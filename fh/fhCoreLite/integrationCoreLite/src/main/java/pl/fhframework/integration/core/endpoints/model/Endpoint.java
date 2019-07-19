package pl.fhframework.integration.core.endpoints.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.model.Hidden;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by pawel.ruta on 2018-04-12.
 */
@Getter
@Setter
@Entity
@Hidden
@Table(name = "INTGR_ENDPOINT")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SEC_INTGR_CONFIG", allocationSize = 5)
public class Endpoint extends BasePersistentObject {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column
    private EndpointSecurityTypeEnum securityType;

    @Column
    private String username;

    @Column
    private String password; // todo: should be encrypted
}
