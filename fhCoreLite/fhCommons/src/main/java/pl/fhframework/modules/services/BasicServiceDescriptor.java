package pl.fhframework.modules.services;

import lombok.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicServiceDescriptor {
    private String label;

    private String icon;

    private String description;

    @Singular("group")
    private List<String> groupedBy;
}
