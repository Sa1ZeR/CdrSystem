package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "api_billing_data")
@Data
@EqualsAndHashCode(callSuper = false, of = {"user", "totalCost"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingData extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    private Set<ReportData> reportData = new HashSet<>();

    @Column(nullable = false)
    double totalCost;
}
