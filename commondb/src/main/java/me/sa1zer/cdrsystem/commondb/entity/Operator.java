package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.sa1zer.cdrsystem.commondb.entity.BaseEntity;

@Entity
@Table(name = "api_operators")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "name")
public class Operator extends BaseEntity<Integer> {

    private String name;
}
