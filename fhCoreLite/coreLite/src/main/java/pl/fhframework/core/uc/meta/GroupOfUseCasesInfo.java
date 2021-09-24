package pl.fhframework.core.uc.meta;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of use cases infos
 */
public class GroupOfUseCasesInfo {
    @Getter
    @Setter
    private List<UseCaseInfo> useCaseInfos = new ArrayList<>();
}
