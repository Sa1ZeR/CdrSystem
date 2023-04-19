package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.*;
import lombok.*;
import me.sa1zer.cdrsystem.common.object.enums.OperationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_payments")
@Data
@EqualsAndHashCode(callSuper = false, of = {"user"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment extends BaseEntity<Long> {

    @ManyToOne()
    private User user;
    private double amount;
    private LocalDateTime dateTime;
    @Enumerated
    private OperationType operationType;
}
