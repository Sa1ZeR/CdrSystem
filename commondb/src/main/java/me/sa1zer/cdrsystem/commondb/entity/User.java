package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.*;
import lombok.*;
import me.sa1zer.cdrsystem.common.object.enums.UserRole;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "api_users",
    indexes = {
        @Index(name = "phone_index", columnList = "phone")
    })
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"phone, balance"})
@Builder
public class User extends BaseEntity<Long> {

    @Column(nullable = false, unique = true, columnDefinition = "varchar(12)")
    private String phone;

    @Column(nullable = false, columnDefinition = "varchar(64)")
    private String password;

    private double balance;

    @ElementCollection(targetClass = UserRole.class)
    @CollectionTable(name = "api_users_to_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated
    private Set<UserRole> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

}
