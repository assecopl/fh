package pl.fhframework.dp.commons.services.searchTemplate;


import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.transport.searchtemplate.LogicalCondition;
import pl.fhframework.dp.transport.searchtemplate.OperatorType;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateForQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SearchTemplateQueryBuilder {

    private Logger log = LoggerFactory.getLogger(SearchTemplateQueryBuilder.class);
    //zestaw znaków do escapowania; nie escapuje * i ? dopuszczając te znaki jako wildcardy w zapytaniu LIKE i DONT_LIKE
    private static Map<String, String> escapeCharsMap = getEscapeCharsMap();



    public BoolQueryBuilder getQueryFromTemplate(BoolQueryBuilder builder, List<SearchTemplateForQuery> searchTemplateForQuery) {
        for (SearchTemplateForQuery filterDef : searchTemplateForQuery) {
            if (SearchTemplateForQuery.Type.CONDITION.equals(filterDef.getType())) { //warunek
                if (LogicalCondition.AND.equals(filterDef.getLogicalCondition())) { //logiczne I
                    builder.must(getBasicQuery(filterDef.getColumnName(), filterDef.getValues(), filterDef.getOperator()));
                }

                if (LogicalCondition.OR.equals(filterDef.getLogicalCondition())) { //logiczne lub
                    builder.should(getBasicQuery(filterDef.getColumnName(), filterDef.getValues(), filterDef.getOperator()));
                }
            }

            if (SearchTemplateForQuery.Type.BRACKETS.equals(filterDef.getType())) { //nawiasy

                if (LogicalCondition.AND.equals(filterDef.getLogicalCondition())) {
                    BoolQueryBuilder q =  QueryBuilders.boolQuery();
                    getQueryFromTemplate(q, filterDef.getInner());
                    builder.must(q);
                }

                if (LogicalCondition.OR.equals(filterDef.getLogicalCondition())) {
                    BoolQueryBuilder q =  QueryBuilders.boolQuery();
                    getQueryFromTemplate(q, filterDef.getInner());
                    builder.should(q);
                }
            }
        }

        return builder;
    }

    private QueryBuilder getBasicQuery(String columnName, List<Object> values, OperatorType operatorType) {
        switch (operatorType) {
            case EQUAL: {
                return  QueryBuilders.matchQuery(columnName, values.get(0).toString()).operator(Operator.AND);
            }
            case NOT_EQUAL:{
                return QueryBuilders.boolQuery().mustNot(QueryBuilders.matchQuery(columnName, values.get(0).toString()).operator(Operator.AND));
            }
            case GT: {
                return QueryBuilders.rangeQuery(columnName).gt(values.get(0).toString());
            }
            case LT:{
                return QueryBuilders.rangeQuery(columnName).lt(values.get(0).toString());
            }
            case GTE:{
                return QueryBuilders.rangeQuery(columnName).gte(values.get(0).toString());
            }
            case LTE:{
                return QueryBuilders.rangeQuery(columnName).lte(values.get(0).toString());
            }

            case IN:{
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                for(Object item: values){
                    query.should(QueryBuilders.matchQuery(columnName,item.toString()).operator(Operator.AND)); //match query - wykonuje analizę tekstu lower case, tokeny, itp.
                }
                return query;
            }

            case NOT_IN:{
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                for(Object item: values){
                    query.mustNot(QueryBuilders.matchQuery(columnName,item.toString()).operator(Operator.AND)) ;
                }
                return query;
            }
            case LIKE:{
                String escapedQuery = escapeSpecialCharacters(values.get(0).toString());
                return QueryBuilders.queryStringQuery(escapedQuery) //queryStringquery  - daje możliwość stosowania wildcards
                        .field(columnName)
                        .analyzeWildcard(true)
                        .allowLeadingWildcard(true);
            }

            case DONT_LIKE:{
                String escapedQuery = escapeSpecialCharacters(values.get(0).toString());
                BoolQueryBuilder query =  QueryBuilders.boolQuery();
                query.mustNot(QueryBuilders.queryStringQuery(escapedQuery) //queryStringquery  - daje możliwość stosowania wildcards
                        .field(columnName)
                        .analyzeWildcard(true)
                        .allowLeadingWildcard(true));
                return query;
            }
        }
        return null;
    }

    private static Map<String, String> getEscapeCharsMap() {
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("+", "\\+");
        replacementMap.put("-", "\\-");
        replacementMap.put("=", "\\=");
        replacementMap.put("&&", "\\&&");
        replacementMap.put("||", "\\||");
        replacementMap.put("!", "\\!");
        replacementMap.put("(", "\\(");
        replacementMap.put(")", "\\)");
        replacementMap.put("{", "\\{");
        replacementMap.put("}", "\\}");
        replacementMap.put("[", "\\[");
        replacementMap.put("]", "\\]");
        replacementMap.put("^", "\\^");
        replacementMap.put("\"", "\\\"");
        replacementMap.put("~", "\\~");
        replacementMap.put(":", "\\:");
        replacementMap.put("/", "\\/");
        return replacementMap;
    }

    //dokonuje wyescapowania znaków specjalnych zgodnie z mapą;
    private String escapeSpecialCharacters(String input){
        input = input.replace( "\\", "\\\\" );  //escape znaku escapującego escapowaniem innych znaków
        int charsCount = escapeCharsMap.keySet().size();
        String[] chars = Arrays.copyOf(escapeCharsMap.keySet().toArray(), charsCount, String[].class);
        String[] escapedChars = Arrays.copyOf(escapeCharsMap.values().toArray(), charsCount, String[].class);
        input = StringUtils.replaceEach(input, chars, escapedChars);
        return input;
    }
}
