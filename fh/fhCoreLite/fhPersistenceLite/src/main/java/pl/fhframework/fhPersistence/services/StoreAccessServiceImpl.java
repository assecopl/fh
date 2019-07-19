package pl.fhframework.fhPersistence.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.datasource.StoreAccessService;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.core.maps.features.geometry.IGeometry;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.fhPersistence.core.model.ModelProxyService;
import pl.fhframework.ReflectionUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-05-10.
 */
@Service
public class StoreAccessServiceImpl implements StoreAccessService {
    @Autowired
    protected EntityManagerRepository entityManagerRepository;

    @Autowired
    protected ModelProxyService modelProxy;

    public <S extends BaseEntity> List<S> storeRead(Class<S> type) {
            EntityManager em = entityManagerRepository.getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery(type);
            Root rootEntry = cq.from(type);
            CriteriaQuery all = cq.select(rootEntry);
            TypedQuery allQuery = em.createQuery(all);
            return allQuery.getResultList();
    }

    @Override
    public <S extends BaseEntity> S storeWrite(S baseEntity) {
        modelProxy.clearEntitiesToCascadePersist();

        S savedEntity = storeWriteInternal(baseEntity);

        Queue<BaseEntity> toPersist = modelProxy.getEntitiesToCascadePersist();
        while (toPersist.size() > 0) {
            storeWriteInternal(toPersist.poll());
        }

        modelProxy.clearEntitiesToCascadePersist();

        return savedEntity;
    }

    protected   <S extends BaseEntity> S storeWriteInternal(S baseEntity) {
        modelProxy.syncObjectCollections(baseEntity);

        EntityManager em = entityManagerRepository.getEntityManager();
        em.persist(baseEntity);

        return baseEntity;
    }

    @Override
    public <S extends BaseEntity> S storeRefresh(S baseEntity) {
        modelProxy.refresh(Arrays.asList(baseEntity), entityManagerRepository.getEntityManager());

        return baseEntity;
    }

    @Override
    public <S extends BaseEntity> void storeDelete(S baseEntity) {
        modelProxy.clearOtherSideRelations(baseEntity);
        if (baseEntity.getEntityId() != null) {
            EntityManager em = entityManagerRepository.getEntityManager();
            if (em.contains(baseEntity)) {
                em.remove(baseEntity);
            }
        }
    }

    public <S extends Object> S storeResult(From from) {
        List<S> resultList = storeFind(from);

        if (resultList.size() == 0) {
            throw new FhException("No result");
        } else if (resultList.size() > 1) {
            throw new FhException("Query returns more than one object");
        }

        return resultList.get(0);
    }

    public <S extends Object> List<S> storeFind(From from) {
        List<Object> queryParams = new ArrayList<>();

        String queryStr = storeFindQueryStr(from, new HashMap<>(), queryParams, new HashMap<>(), true);

        Offset offset = null;
        Limit limit = null;
        Count count = null;
        Collection<DataAccess> conditions = (Collection) from.getStatements();
        Class type = from.getTypeClass();

        for (DataAccess condition : conditions) {
            if (Offset.class.isInstance(condition)) {
                offset = (Offset) condition;
            } else if (Limit.class.isInstance(condition)) {
                limit = (Limit) condition;
            } else if (Count.class.isInstance(condition)) {
                count = (Count) condition;
            }
        }

        Query query = entityManagerRepository.getEntityManager().createQuery(queryStr);
        for (int paramIdx = 1; paramIdx <= queryParams.size(); paramIdx++) {
            Object value = queryParams.get(paramIdx - 1);
            if (LocalDate.class.isInstance(value)) {
                query.setParameter(paramIdx, value);
            } else if (Date.class.isInstance(value)) {
                query.setParameter(paramIdx, (Date) value, TemporalType.TIMESTAMP);
            } else {
                query.setParameter(paramIdx, value);
            }
        }

        if (offset != null && offset.getOffset() > 0) {
            query.setFirstResult(offset.getOffset());
        }
        if (limit != null && limit.getLimit() > 0) {
            query.setMaxResults(limit.getLimit());
        }

        if (count != null) {
            return (List<S>) Collections.singletonList(((Number) query.getSingleResult()).longValue());
        } else {
            return storeFindInternal(query, type);
        }
    }

    protected <S extends Object> List<S> storeFindInternal(Query query, Class type) {
            return query.getResultList();
    }

    protected String storeFindQueryStr(From from, Map<String, PathAttribute> allAttributes, List<Object> queryParams, Map<String, Class> iterMap, boolean rootSelect) {
        Filter where = null;
        SortBy orderby = null;
        Count count = null;
        List<Join> joins = new LinkedList<>();

        Collection<DataAccess> conditions = (Collection) from.getStatements();
        Class type = from.getTypeClass();
        String iter = from.getIter();
        iterMap.put(iter, type);

        for (DataAccess condition : conditions) {
            if (Filter.class.isInstance(condition)) {
                where = (Filter) condition;
            } else if (SortBy.class.isInstance(condition)) {
                orderby = (SortBy) condition;
            } else if (Count.class.isInstance(condition)) {
                count = (Count) condition;
            } else if (Join.class.isInstance(condition)) {
                joins.add((Join) condition);
            }
        }
        if (count != null) {
            orderby = null;
        }

        Map<String, PathAttribute> currIterAttrs = new HashMap<>();
        getAttrsDescription(where, orderby, joins, iter, type, currIterAttrs, iterMap);

        String rootAlias = pathToAlias(iter);
        List<String> joinClauseList = getJoinClause(currIterAttrs, iter, type, iterMap, where);

        allAttributes.putAll(currIterAttrs);

        String selectClause = count != null ? "count(" + rootAlias + ")" : rootAlias;
        String fromClause;

        fromClause = String.format("%s %s", getEntityName(type), rootAlias);

        String queryStr = String.format("select %s from %s %s where %s%s %s",
                selectClause,
                fromClause,
                joinClauseList.stream().collect(Collectors.joining(" ")),
                getWhereClause(allAttributes, where, queryParams, iter, iterMap),
                "",
                getOrderByClause(orderby, allAttributes));


        return queryStr;
        // todo:
            /*
            StringBuilder sb = new StringBuilder();
            String objectType = dynamicModelConfig.getStaticClassForExtendedClass(type).getName();
            sb.append("select static, (select dyn from DynamicObject dyn where dyn.staticObjectType='");
            sb.append(objectType).append("' and dyn.staticObjectId = static.id) from ");
            sb.append(objectType).append(" static");
            // todo: entity name
            return sb.toString();
            */
    }

    protected String getEntityName(Class type) {
        Entity entityAnnotation = (Entity) type.getAnnotation(Entity.class);
        if (StringUtils.isNullOrEmpty(entityAnnotation.name())) {
            return type.getSimpleName();
        }
        return entityAnnotation.name();
    }

    protected void getAttrsDescription(Filter where, SortBy orderby, List<Join> joins, String iter, Class type, Map<String, PathAttribute> attrsDescription, Map<String, Class> iterMap) {
        if (!joins.isEmpty()) {
            // todo: normal join, now it's only for "exists in" collection
            joins.forEach(join -> {
                String root = join.getPath().split("\\.")[0];
                if (!root.equals(iter)) {
                    Class rootClass = iterMap.get(root);
                    PathAttribute pathAttribute = extractPathAttribute(join.getPath(), root, rootClass);
                    attrsDescription.put(join.getPath(), pathAttribute);
                }
            });
        }
        if (orderby != null) {
            orderby.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).forEach(sortField -> {
                PathAttribute pathAttribute = extractPathAttribute(sortField.getValue(), iter, type);
                if (pathAttribute != null) {
                    attrsDescription.put(sortField.getValue(), pathAttribute);
                }
            });
        }
        if (where != null) {
            getAttrsDescription(where.getStatements(), attrsDescription, iter, type);
        }
    }

    protected List<String> getJoinClause(Map<String, PathAttribute> attrsName, String iter, Class type, Map<String, Class> iterMap, Filter where) {
        List<String> joinStrList = new LinkedList<>();
        // <path, alias>
        Map<String, String> joinedAssociation = new HashMap<>();
        joinedAssociation.put(iter, pathToAlias(iter));
        new ArrayList<>(attrsName.values()).forEach(pathAttribute -> {
            if (!joinedAssociation.containsKey(pathAttribute.getParentPathWithType())) {
                PathAttribute parentAttribute = extractPathAttribute(pathAttribute.getParentPath(), iter, type);
                if (parentAttribute != null) {
                    joinWithAssociation(pathAttribute, parentAttribute, iter, type, joinStrList, joinedAssociation, true, false);
                    if (pathAttribute.isDynamic()) {
                        addJoinClause(joinStrList, pathAttribute, joinedAssociation);
                    }
                } else {
                    for (String iterEl : iterMap.keySet()) {
                        if (!joinedAssociation.containsKey(iterEl)) {
                            joinedAssociation.put(iterEl, pathToAlias(iterEl));
                        }
                    }
                    joinWithExistsIn(pathAttribute, iter, type, joinStrList, joinedAssociation, attrsName, iterMap, where);
                }
            } else if (pathAttribute.isDynamic()) {
                addJoinClause(joinStrList, pathAttribute, joinedAssociation);
            }
        });

        return joinStrList;
    }

    protected void addJoinClause(List<String> joinStrList, PathAttribute pathAttribute, Map<String, String> joinedAssociation) {
        if (pathAttribute.isChildAttribute()) {
            joinStrList.add(String.format("LEFT JOIN %s.attributes %s on %s.name = '%s'", joinedAssociation.get(pathAttribute.getParentPath()), pathAttribute.getAlias(), pathAttribute.getAlias(), pathAttribute.getName()));
        }
    }

    protected void joinWithAssociation(PathAttribute pathAttribute, PathAttribute parentAttribute, String iter, Class type, List<String> joinStrList, Map<String, String> joinedAssociation, boolean outer, boolean reverse) {
        if (!reverse) {
            if (!joinedAssociation.containsKey(parentAttribute.getParentPathWithType())) {
                joinWithAssociation(parentAttribute, extractPathAttribute(parentAttribute.getParentPath(), iter, type), iter, type, joinStrList, joinedAssociation, outer, reverse);
            }
        }

        String joinType = outer ? "LEFT JOIN" : "JOIN";

        //if (pathAttribute.isHybrid() && !joinedAssociation.containsKey())
        if (!parentAttribute.isDynamic()) {
            if (!pathAttribute.isHybrid()) {
                if (!joinedAssociation.containsKey(parentAttribute.getPath())) {
                    joinedAssociation.put(parentAttribute.getPath(), parentAttribute.getAlias());
                    if (!reverse || !parentAttribute.getParentPath().equals(pathAttribute.getRootPath())) {
                            joinStrList.add(String.format("%s %s %s on %s.%s = %s", joinType, getEntityName(pathAttribute.getParentType()), parentAttribute.getAlias(),
                                    parentAttribute.getParentAlias(), parentAttribute.getName(), parentAttribute.getAlias()));
                    }
                    else {
                            pathAttribute.setWhereCondition(String.format("%s.%s = %s",
                                    parentAttribute.getParentAlias(), parentAttribute.getName(), parentAttribute.getAlias()));
                    }
                }
            } else {
                if (!joinedAssociation.containsKey(pathAttribute.getParentPathWithType())) {
                    joinedAssociation.put(pathAttribute.getParentPathWithType(), pathAttribute.getParentAlias());
                    joinStrList.add(String.format("%s %s %s on %s.%s = %s", joinType, getEntityName(pathAttribute.getParentType()), pathAttribute.getParentAlias(),
                            parentAttribute.getParentAlias(), parentAttribute.getName(), pathAttribute.getParentAlias()));
                }
            }
        } else {
            String fkAlias = parentAttribute.getAlias() + "fk";
            String parentAlias = joinedAssociation.get(parentAttribute.getParentPath());
            String alias = parentAttribute.getAlias() + "dyn";
            if (!reverse) {
                joinStrList.add(String.format("%s %s.attributes %s on %s.name = '%s'", joinType, parentAlias, fkAlias, fkAlias, parentAttribute.getName()));
                joinStrList.add(String.format("%s %s.objValue %s", joinType, fkAlias, alias));
                joinedAssociation.put(parentAttribute.getPath(), alias);

                if (pathAttribute.isHybrid()) {
                    joinStrList.add(String.format("%s %s %s on %s.id = %s.objectStaticId", joinType, getEntityName(pathAttribute.getParentType()), pathAttribute.getParentAlias(),
                            pathAttribute.getParentAlias(), fkAlias));
                    joinedAssociation.put(pathAttribute.getParentPathWithType(), pathAttribute.getParentAlias());
                }
            } else {
                String nameFk = parentAttribute.getParentAlias() + parentAttribute.getName() + "_fk";
                joinStrList.add(String.format("%s DynamicObjectAttribute %s on %s.objValue = %s and %s.name='%s'", joinType, nameFk, nameFk, alias, nameFk, parentAttribute.getName()));
                if (parentAttribute.getParentPath().equals(parentAttribute.getRootPath())) {
                    pathAttribute.setWhereCondition(String.format("%s = %s.owner", parentAttribute.getParentAlias(), nameFk));
                }
            }
        }

        if (reverse) {
            if (!joinedAssociation.containsKey(parentAttribute.getParentPathWithType())) {
                joinWithAssociation(parentAttribute, extractPathAttribute(parentAttribute.getParentPath(), iter, type), iter, type, joinStrList, joinedAssociation, outer, reverse);
            }
        }
    }

    protected void joinWithExistsIn(PathAttribute pathAttribute, String iter, Class type, List<String> joinStrList, Map<String, String> joinedAssociation, Map<String, PathAttribute> attrsName, Map<String, Class> iterMap, Filter where) {
        if (Collection.class.isAssignableFrom(pathAttribute.getType())) {

            PathAttribute rootChild = pathAttribute;
            boolean complexPath = false;
            while (!rootChild.getParentPath().equals(pathAttribute.getRootPath())) {
                complexPath = true;
                rootChild = extractPathAttribute(rootChild.getParentPath(), rootChild.getRootPath(), rootChild.getRootType());
            }
            PathAttribute parentAttribute = extractPathAttribute(pathAttribute.getParentPath(), pathAttribute.getRootPath(), pathAttribute.getRootType());

            joinedAssociation.put(rootChild.getParentPathWithType(), rootChild.getParentAlias());

            String alias = iter;
            if (pathAttribute.isHybrid()) {
                alias = pathForStatic(iter);
            }

            appendAndCondition(where, CompareCondition.of(alias, CompareOperatorEnum.MemberOf,
                    pathAttribute.getPath(), null, false, null));

            attrsName.put(alias, extractPathAttribute(alias, alias, type));

            return;
        }
        throw new FhException("Incorrect exists in clause");
    }

    protected void complexPathInExistsJoin(PathAttribute pathAttribute, PathAttribute parentAttribute, List<String> joinStrList, Map<String, String> joinedAssociation) {
        if (!joinedAssociation.containsKey(pathAttribute.getParentPathWithType())) {
            joinWithAssociation(pathAttribute,
                    parentAttribute,
                    pathAttribute.getRootPath(), pathAttribute.getRootType(), joinStrList, joinedAssociation, false, true);
        }
    }

    protected void appendAndCondition(Filter where, DefinedCondition condition) {
        if (where.getStatements().size() == 0) {
            where.getStatements().add(condition);
        }
        else {
            DefinedCondition presentCondition = (DefinedCondition) where.getStatements().get(0);
            where.getStatements().clear();
            where.getStatements().add(AndCondition.of(condition, presentCondition));
        }
    }

    protected void getAttrsDescription(List<Statement> statements, Map<String, PathAttribute> attrsDescription, String iter, Class type) {
        statements.stream().filter(DefinedCondition.class::isInstance).map(DefinedCondition.class::cast).forEach(definedCondition -> {
            if (CompareCondition.class.isInstance(definedCondition)) {
                CompareCondition compareCondition = CompareCondition.class.cast(definedCondition);
                String path = compareCondition.getLeft().getValue();
                PathAttribute pathAttribute = extractPathAttribute(path, iter, type);

                boolean spatial = CompareOperatorEnum.fromString(compareCondition.getOperator()).isSpatial() || compareCondition.getDistance() != null;
                if (pathAttribute != null) {
                    if (spatial) {
                        pathAttribute = updateSpatialPathAttribute(pathAttribute, path, iter, type, compareCondition.getLeft()::setValue);
                        path = pathAttribute.getPath();
                    }
                    attrsDescription.put(path, pathAttribute);
                }

                if (compareCondition.getRight() != null && !StringUtils.isNullOrEmpty(compareCondition.getRight().getValue())) {
                    path = compareCondition.getRight().getValue();
                    pathAttribute = extractPathAttribute(path, iter, type);
                    if (pathAttribute != null) {
                        if (spatial) {
                            pathAttribute = updateSpatialPathAttribute(pathAttribute, path, iter, type, compareCondition.getRight()::setValue);
                            path = pathAttribute.getPath();
                        }
                        attrsDescription.put(path, pathAttribute);
                    }
                }
                if (spatial && compareCondition.getRightValue() != null) {
                    updateSpatialCondition(compareCondition::getRightValue, compareCondition::setRightValue);
                }
                if (spatial && compareCondition.getLeftValue() != null) {
                    updateSpatialCondition(compareCondition::getLeftValue, compareCondition::setLeftValue);
                }
            } else if (ExistsInCondition.class.isInstance(definedCondition)) {
                ExistsInCondition existsInCondition = (ExistsInCondition) definedCondition;
                if (!existsInCondition.isQuery()) {
                    PathAttribute pathAttribute = extractPathAttribute(existsInCondition.getCollection(), iter, type);
                    if (pathAttribute != null && !pathAttribute.isDynamic() && !pathAttribute.getParentPath().equals(pathAttribute.getRootPath())) {
                        attrsDescription.put(existsInCondition.getCollection(), pathAttribute);
                    }
                }
            } else {
                getAttrsDescription(ComplexCondition.class.cast(definedCondition).getStatements(), attrsDescription, iter, type);
            }
        });
        statements.stream().filter(ExistsInCondition.class::isInstance).map(ExistsInCondition.class::cast).forEach(existsInCondition -> {
            getAttrsDescription(getFilter(existsInCondition).getStatements(), attrsDescription, iter, type);
        });
    }

    private PathAttribute updateSpatialPathAttribute(PathAttribute pathAttribute, String path, String iter, Class type, Consumer<String> valueSetter) {
        if (IGeometry.class.isAssignableFrom(pathAttribute.getType())) {
            path = path + ".geometry";
            valueSetter.accept(path);
            return extractPathAttribute(path, iter, type);
        } else if (IFeature.class.isAssignableFrom(pathAttribute.getType())) {
            path = path + ".geometry.geometry";
            valueSetter.accept(path);
            return extractPathAttribute(path, iter, type);
        }

        return pathAttribute;
    }

    private void updateSpatialCondition(Supplier valueGetter, Consumer valueSetter) {
        if (valueGetter.get() instanceof IGeometry) {
            valueSetter.accept(((IGeometry) valueGetter.get()).getGeometry());
        }
        else if (valueGetter.get() instanceof IFeature && ((IFeature) valueGetter.get()).getGeometry() instanceof IGeometry) {
            valueSetter.accept(((IFeature) valueGetter.get()).getGeometry().getGeometry());
        }

    }

    protected Filter getFilter(ExistsInCondition existsInCondition) {
        if (!existsInCondition.isQuery()) {
            return Filter.of((DefinedCondition) existsInCondition.getWith().getPredicate());
        } else {
            Optional<Filter> filter = ((From) existsInCondition.getStatements().get(0)).getStatements().stream().filter(Filter.class::isInstance).map(Filter.class::cast).findFirst();
            if (filter.isPresent()) {
                return filter.get();
            }
            return Filter.of();
        }
    }

    protected From getFrom(ExistsInCondition existsInCondition, String iter, Map<String, Class> iterMap) {
        if (!existsInCondition.isQuery()) {
            String root = existsInCondition.getCollection().split("\\.")[0];
            Class rootClass = iterMap.get(root);
            PathAttribute pathAttribute = extractPathAttribute(existsInCondition.getCollection(), root, rootClass);

            return From.of(pathAttribute.getElementType(), existsInCondition.getIter(),
                    Join.of(existsInCondition.getCollection(), pathAttribute.getAlias()),
                    Filter.of((DefinedCondition) existsInCondition.getWith().getPredicate()));
        } else {
            return (From) existsInCondition.getStatements().get(0);
        }
    }

    protected String getWhereClause(Map<String, PathAttribute> attributes, Filter where, List<Object> queryParams, String iter, Map<String, Class> iterMap) {
        String valueWhere = "1 = 1";

        if (where != null && where.getStatements().size() > 0) {
            String newValueWhere = generateWhereCondition((DefinedCondition) where.getStatements().get(0), queryParams, attributes, iter, iterMap);
            if (!StringUtils.isNullOrEmpty(newValueWhere)) {
                valueWhere = newValueWhere;
            }
        }

        String additionalConditions = attributes.values().stream().filter(attr -> !StringUtils.isNullOrEmpty(attr.getWhereCondition())).map(PathAttribute::getWhereCondition).collect(Collectors.joining(" and "));

        if (!StringUtils.isNullOrEmpty(additionalConditions)) {
            valueWhere = String.format("%s and (%s)", valueWhere, additionalConditions);
        }

        return valueWhere;
    }

    protected String getOrderByClause(SortBy orderby, Map<String, PathAttribute> attributes) {
        if (orderby != null) {
            List<SortField> orderByFields = orderby.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).collect(Collectors.toList());
            if (orderByFields.size() > 0) {
                return String.format("order by %s", orderByFields.stream().map(
                        sortField -> {
                            PathAttribute pathAttribute = attributes.get(sortField.getValue());
                            String fieldAccessor = getFieldAccessor(pathAttribute);
                            return String.format("%s %s NULLS LAST", fieldAccessor, sortField.getDirection());
                        }).collect(Collectors.joining(", ")));
            }
        }
        return "";
    }

    protected String generateWhereCondition(DefinedCondition definedCondition, List<Object> queryParams, Map<String, PathAttribute> attributes, String iter, Map<String, Class> iterMap) {
        GenerationContext code = new GenerationContext();

        if (CompareCondition.class.isInstance(definedCondition)) {
            CompareCondition condition = CompareCondition.class.cast(definedCondition);

            CompareOperatorEnum operatorEnum = CompareOperatorEnum.fromString(condition.getOperator());
            PathAttribute pathAttribute = attributes.get(condition.getLeft().getValue());
            String fieldAccessor = getFieldAccessor(pathAttribute);
            if (operatorEnum.isEmptinessCheck()) {
                switch (operatorEnum) {
                    case IsNull:
                        code.addCode("%s is null", fieldAccessor);
                        break;
                    case IsNotNull:
                        code.addCode("%s is not null", fieldAccessor);
                        break;
                    case IsEmpty:
                        code.addCode("(%s is null or TRIM(BOTH FROM %s) = '')", fieldAccessor, fieldAccessor);
                        break;
                    case IsNotEmpty:
                        code.addCode("(%s is not null and TRIM(BOTH FROM %s) <> '')", fieldAccessor, fieldAccessor);
                        break;
                }
            } else if (operatorEnum.isSpatial()) {
                String codeSyntax;

                Object value;

                if (condition.getRightValue() != null) {
                    value = condition.getRightValue();

                    codeSyntax =  "%s(%s, ?%s) = true";
                    code.addCode(codeSyntax, getJpaOperator(operatorEnum), fieldAccessor, Integer.toString(queryParams.size() + 1));

                    queryParams.add(value);
                } else if (!StringUtils.isNullOrEmpty(condition.getRight().getValue())) {
                    PathAttribute paramAttribute = attributes.get(condition.getRight().getValue());
                    String rightFieldAccessor = getFieldAccessor(paramAttribute);
                    value = String.format("%s", rightFieldAccessor);

                    codeSyntax = "%s(%s, %s) = true";

                    code.addCode(codeSyntax, fieldAccessor, getJpaOperator(operatorEnum), (String) value);
                }
            } else if (condition.getDistance() != null) {
                String codeSyntax;

                Object value;

                if (condition.getRightValue() != null) {
                    value = condition.getRightValue();

                    codeSyntax =  "distance(transform(%s, 3035), transform(?%s, 3035)) %s ?%s";
                    code.addCode(codeSyntax, fieldAccessor, Integer.toString(queryParams.size() + 1), getJpaOperator(operatorEnum), Integer.toString(queryParams.size() + 2));

                    queryParams.add(value);
                } else if (!StringUtils.isNullOrEmpty(condition.getRight().getValue())) {
                    PathAttribute paramAttribute = attributes.get(condition.getRight().getValue());
                    String rightFieldAccessor = getFieldAccessor(paramAttribute);
                    value = String.format("%s", rightFieldAccessor);

                    codeSyntax =  "distance(transform(%s, 3035), transform(%s, 3035)) %s ?%s";

                    code.addCode(codeSyntax, fieldAccessor, (String) value, getJpaOperator(operatorEnum), Integer.toString(queryParams.size() + 1));
                }
                queryParams.add(Double.valueOf(condition.getDistance()));
            } else {
                String codeSyntax;

                Object value;

                if (condition.getRightValue() != null) {
                    value = condition.getRightValue();

                    if (Boolean.TRUE.equals(condition.getIgnoreCase())) {
                        codeSyntax = "UPPER(%s) %s ?%s";
                        value = ((String) value).toUpperCase();
                    } else {
                        codeSyntax = "%s %s ?%s";
                    }
                    code.addCode(codeSyntax, fieldAccessor, getJpaOperator(operatorEnum), Integer.toString(queryParams.size() + 1));

                    if (operatorEnum == CompareOperatorEnum.StartsWith || operatorEnum == CompareOperatorEnum.NotStartsWith) {
                        value = value + "%";
                    } else if (operatorEnum == CompareOperatorEnum.EndsWith || operatorEnum == CompareOperatorEnum.NotEndsWith) {
                        value = "%" + value;
                    } else if (operatorEnum == CompareOperatorEnum.Contains || operatorEnum == CompareOperatorEnum.NotContains) {
                        value = "%" + value + "%";
                    }

                    if (pathAttribute.isDynamic() && value instanceof Enum) {
                        value = ((Enum) value).name();
                    }
                    queryParams.add(value);
                } else if (!StringUtils.isNullOrEmpty(condition.getRight().getValue())) {
                    PathAttribute paramAttribute = attributes.get(condition.getRight().getValue());
                    String rightFieldAccessor = getFieldAccessor(paramAttribute);
                    value = String.format("%s", rightFieldAccessor);

                    if (Boolean.TRUE.equals(condition.getIgnoreCase())) {
                        codeSyntax = "UPPER(%s) %s UPPER(%s)";
                    } else {
                        codeSyntax = "%s %s %s";
                    }

                    if (operatorEnum == CompareOperatorEnum.StartsWith || operatorEnum == CompareOperatorEnum.NotStartsWith) {
                        value = value + " || '%'";
                    } else if (operatorEnum == CompareOperatorEnum.EndsWith || operatorEnum == CompareOperatorEnum.NotEndsWith) {
                        value = " || '%'" + value;
                    } else if (operatorEnum == CompareOperatorEnum.Contains || operatorEnum == CompareOperatorEnum.NotContains) {
                        value = "'%' || " + value + " || '%'";
                    }

                    code.addCode(codeSyntax, fieldAccessor, getJpaOperator(operatorEnum), (String) value);
                }
            }
        } else if (ExistsInCondition.class.isInstance(definedCondition)) {
            return String.format("exists (%s)", storeFindQueryStr(getFrom(ExistsInCondition.class.cast(definedCondition), iter, iterMap), new HashMap<>(attributes), queryParams, new HashMap<>(iterMap), false)); // subselect
        } else if (ComplexCondition.class.isInstance(definedCondition)) {
            generateComplexCondition(ComplexCondition.class.cast(definedCondition), code, queryParams, attributes, iter, iterMap);
        } else if (definedCondition != null) {
            throw new FhException(String.format("Unknown filter condition: %s", definedCondition.getClass().getSimpleName()));
        }

        return code.resolveCode();
    }

    protected String getFieldAccessor(PathAttribute pathAttribute) {
        if (pathAttribute.getPath().equals(pathAttribute.getRootPath())) {
            return pathAttribute.getAlias();
        }
        return pathAttribute.isDynamic() ?
                String.format("%s.%s", pathAttribute.getAlias(), getTypeField(pathAttribute)) :
                String.format("%s%s", pathAttribute.getParentAlias(), StringUtils.isNullOrEmpty(pathAttribute.getName()) ? "" : "." + pathAttribute.getName());
    }

    protected void generateComplexCondition(ComplexCondition complexCondition, GenerationContext conditionSection, List<Object> queryParams, Map<String, PathAttribute> attributes, String iter, Map<String, Class> iterMap) {
        List<String> innerCondition = new LinkedList<>();

        complexCondition.getStatements().stream().filter(DefinedCondition.class::isInstance).map(DefinedCondition.class::cast).forEach(inner -> {
            String innerStr = generateWhereCondition(inner, queryParams, attributes, iter, iterMap);
            if (!StringUtils.isNullOrEmpty(innerStr)) {
                innerCondition.add(innerStr);
            }
        });

        if (innerCondition.size() > 0) {
            String operator = null;
            if (NotCondition.class.isInstance(complexCondition)) {
                conditionSection.addCode("not");
                operator = " ";
            } else if (AndCondition.class.isInstance(complexCondition)) {
                operator = " and ";
            } else if (OrCondition.class.isInstance(complexCondition)) {
                operator = " or ";
            }

            conditionSection.addCode("(");

            conditionSection.addCode(innerCondition.stream().collect(Collectors.joining(operator)));

            conditionSection.addLine(")");
        }
    }

    protected String getTypeField(PathAttribute pathAttribute) {
        if (!pathAttribute.isChildAttribute()) {
            return pathAttribute.getName();
        }

        Class fieldType = pathAttribute.getType();

        if (Boolean.class.isAssignableFrom(fieldType)) {
            return "booleanValue";
        } else if (String.class.isAssignableFrom(fieldType)) {
            return "stringValue";
        } else if (Short.class.isAssignableFrom(fieldType)) {
            return "shortValue";
        } else if (Integer.class.isAssignableFrom(fieldType)) {
            return "integerValue";
        } else if (Long.class.isAssignableFrom(fieldType)) {
            return "longValue";
        } else if (BigDecimal.class.isAssignableFrom(fieldType)) {
            return "numberValue";
        } else if (Double.class.isAssignableFrom(fieldType)) {
            return "doubleValue";
        } else if (Float.class.isAssignableFrom(fieldType)) {
            return "floatValue";
        } else if (LocalDate.class.isAssignableFrom(fieldType)) {
            return "dateValue";
        } else if (Date.class.isAssignableFrom(fieldType)) {
            return "dateAndTimeValue";
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return "stringValue";
        }
        throw new FhException(String.format("Uknown type of attribute on path: ", fieldType.getSimpleName()));
    }

    protected String getJpaOperator(CompareOperatorEnum operatorEnum) {
        switch (operatorEnum) {
            case Equal:
                return "=";
            case NotEqual:
                return "<>";
            case GreaterThan:
                return ">";
            case GreaterOrEqual:
                return ">=";
            case LessThan:
                return "<";
            case LessOrEqual:
                return "<=";
            case In:
                return "IN";
            case NotIn:
                return "NOT IN";
            case StartsWith:
            case EndsWith:
            case Contains:
                return "LIKE";
            case NotStartsWith:
            case NotEndsWith:
            case NotContains:
                return "NOT LIKE";
            case MemberOf:
                return "member of";
            case Intersects:
                return "intersects";
            case Touches:
                return "touches";
            case Crosses:
                return "crosses";
            case SpatialContains:
                return "contains";
            case Disjoint:
                return "disjoint";
        }
        throw new FhException(String.format("Unknown operator: %s", operatorEnum.getOperator()));
    }

    protected PathAttribute extractPathAttribute(String path, String iter, Class type) {
        String[] parts = path.split("\\.");
        String name = "";
        String alias = pathToAlias(path);

        if (parts[0].equals(iter)) {
            if (parts.length > 1) {
                name = parts[parts.length - 1];
            }
        } else {
            return null;
        }

        Type parentType = type;
        Type attrType = type;
        for (int i = 1; i < parts.length; i++) {
            parentType = attrType;
            attrType = getPropertyType(parts[i], parentType);
        }
        Class parentClass = ReflectionUtils.getRawClass(parentType);
        String parentPath = Arrays.stream(parts).limit(parts.length - 1).collect(Collectors.joining("."));
        String parentAlias = pathToAlias(parentPath);

        Type elementType = null;
        if (ReflectionUtils.isAssignablFrom(Collection.class, attrType)) {
            elementType = ReflectionUtils.getGenericArguments(attrType)[0];
        }
        if (!PathAttribute.isChildAttribute(name)) {
            return PathAttribute.of(name, path, pathToAlias(parentPath, true), ReflectionUtils.getRawClass(attrType), ReflectionUtils.getRawClass(elementType), parentPath, parentAlias, parentClass, iter, type, false, false);
        } else {
            return PathAttribute.of(name, path, alias, ReflectionUtils.getRawClass(attrType), ReflectionUtils.getRawClass(elementType), parentPath, parentAlias, parentClass, iter, type, false, false);
        }
    }

    protected String pathToAlias(String path) {
        return pathToAlias(path, true);
    }

    protected String pathToAlias(String path, boolean fromAttributes) {
        if (fromAttributes || !path.contains(".")) {
            return String.format("%s_", path.replaceAll("\\.", "_"));
        } else {
            return String.format("%s_dyn", path.replaceAll("\\.", "_"));
        }
    }

    protected String pathForStatic(String path) {
        return path + "_static";
    }

    protected String aliasForStatic(String alias) {
        return alias + "static_";
    }

    @Getter
    @Setter
    protected static class PathAttribute {
        private String name; // name of attribute

        private String path; // path to attribute

        private String alias; // alias for attribute row (when attribute is from dynamic class or dynamic part of hybrid class)

        private Class type; // type of attribute

        private Class elementType; // type of element in collection

        private String parentPath;

        private String parentAlias;

        private Class parentType;

        private String rootPath;

        private Class rootType;

        private boolean dynamic; // is attribute from dynamic class or dynamic part of hybrid class

        private boolean hybrid;

        private String whereCondition; // additional where condition

        public static PathAttribute of(String name, String path, String alias, Class type, Class elementType,
                                       String parentPath, String parentAlias, Class parentType,
                                       String rootPath, Class rootType,
                                       boolean dynamic, boolean hybrid) {
            PathAttribute pathAttribute = new PathAttribute();
            pathAttribute.name = name;
            pathAttribute.path = path;
            pathAttribute.alias = alias;
            pathAttribute.type = type;
            pathAttribute.elementType = elementType;
            pathAttribute.parentPath = parentPath;
            pathAttribute.parentAlias = parentAlias;
            pathAttribute.parentType = parentType;
            pathAttribute.rootPath = rootPath;
            pathAttribute.rootType = rootType;
            pathAttribute.dynamic = dynamic;
            pathAttribute.hybrid = hybrid;

            return pathAttribute;
        }

        public boolean isChildAttribute() {
            return isChildAttribute(name);
        }

        public static boolean isChildAttribute(String name) {
            return !"id".equals(name);
        }

        public String getParentPathWithType() {
            if (hybrid && !dynamic) {
                return parentPath + "_static";
            }

            return parentPath;
        }
    }

    private Type getPropertyType(PropertyOrFieldReference expression, Type baseType) {
        String property = expression.getName();

        return getPropertyType(property, baseType);
    }

    private Type getPropertyType(String property, Type baseType) {
        Class<?> baseTypeClass = ReflectionUtils.getRawClass(baseType);

        Optional<Field> field = ReflectionUtils.getPrivateField(baseTypeClass, property);

        // field found
        if (field.isPresent()) {
            return ReflectionUtils.extractTypeVariable(field.get().getGenericType(), baseType);
        } else { // no field found
            throw new FhBindingException(String.format("No property %s in %s", property, baseType.getTypeName()));
        }
    }
}
