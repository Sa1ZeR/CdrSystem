package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "api_tariff")
public class Tariff extends BaseEntity<Integer> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffType type;

    @Column(nullable = false, columnDefinition = "varchar(3)")
    private String code;


}
