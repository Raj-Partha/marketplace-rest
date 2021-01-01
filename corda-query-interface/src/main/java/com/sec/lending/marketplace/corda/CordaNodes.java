package com.sec.lending.marketplace.corda;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;


@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class CordaNodes {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String shortName;
    private String cordaName;
    private String restEndPoint;
    @ElementCollection(targetClass = Roles.class)
    @CollectionTable(joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "roles_id")
    private Collection<Roles> roles;

}
