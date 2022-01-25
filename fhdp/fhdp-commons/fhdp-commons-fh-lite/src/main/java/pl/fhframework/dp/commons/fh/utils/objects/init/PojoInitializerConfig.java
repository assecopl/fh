package pl.fhframework.dp.commons.fh.utils.objects.init;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class PojoInitializerConfig {
    private Set<String> skipByType = new HashSet<>();
    private Set<String> skipByLocation = new HashSet<>();
    public PojoInitializerConfig() {
        addToSkipByType(Long.class);
        addToSkipByType(Integer.class);
        addToSkipByType(String.class);
        addToSkipByType(Boolean.class);
        addToSkipByType(Double.class);
        addToSkipByType(Float.class);
        addToSkipByType(Byte.class);
        addToSkipByType(BigDecimal.class);
        addToSkipByType(BigInteger.class);
        addToSkipByType(Integer.class);
        addToSkipByType(List.class);
        addToSkipByType(Set.class);
        addToSkipByType(Map.class);
        addToSkipByType(LocalDateTime.class);
        addToSkipByType(LocalDate.class);
    }
    public void addToSkipByType(Class type) {
        skipByType.add(type.getCanonicalName());
    }
    public void removeFromSkipByType(Class type) {
        skipByType.remove(type.getCanonicalName());
    }
}
