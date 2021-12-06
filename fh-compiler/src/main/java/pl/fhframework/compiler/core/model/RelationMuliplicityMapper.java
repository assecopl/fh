package pl.fhframework.compiler.core.model;

import org.springframework.data.util.Pair;

public class RelationMuliplicityMapper {

    public static String calculateRelationType(String sourceMultiplicity, String targetMultiplicity) {
        if (MultiplicityType.ONE.getType().equals(sourceMultiplicity)
                && MultiplicityType.MULTIPLE.getType().equals(targetMultiplicity)) {
            return RelationType.ONE_TO_MANY.getType();
        } else if (MultiplicityType.ONE.getType().equals(sourceMultiplicity)
                && MultiplicityType.ONE.getType().equals(targetMultiplicity)) {
            return RelationType.ONE_TO_ONE.getType();
        } else if (MultiplicityType.MULTIPLE.getType().equals(sourceMultiplicity)
                && MultiplicityType.ONE.getType().equals(targetMultiplicity)) {
            return RelationType.MANY_TO_ONE.getType();
        } else if (MultiplicityType.MULTIPLE.getType().equals(sourceMultiplicity)
                && MultiplicityType.MULTIPLE.getType().equals(targetMultiplicity)) {
            return RelationType.MANY_TO_MANY.getType();
        } else {
            throw new IllegalArgumentException("calculateMultiplicity - Type not supported");
        }
    }

    public static Pair<String, String> calculateMultiplicity(String relationType) {
        if (RelationType.ONE_TO_MANY.getType().equals(relationType)) {
            return Pair.of(MultiplicityType.ONE.getType(), MultiplicityType.MULTIPLE.getType());
        } else if (RelationType.ONE_TO_ONE.getType().equals(relationType)) {
            return Pair.of(MultiplicityType.ONE.getType(), MultiplicityType.ONE.getType());
        } else if (RelationType.MANY_TO_MANY.getType().equals(relationType)) {
            return Pair.of(MultiplicityType.MULTIPLE.getType(), MultiplicityType.MULTIPLE.getType());
        } else if (RelationType.MANY_TO_ONE.getType().equals(relationType)) {
            return Pair.of(MultiplicityType.MULTIPLE.getType(), MultiplicityType.ONE.getType());
        } else {
            throw new IllegalArgumentException("calculateMultiplicity - Type not supported");
        }
    }
}
