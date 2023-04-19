package me.sa1zer.cdrsystem.commondb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import me.sa1zer.cdrsystem.common.object.enums.CallType;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_repot_data")
@Data
@EqualsAndHashCode(callSuper = false, of = {"callType", "cost", "duration", "startTime", "endTime"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportData extends BaseEntity<Long> {

    @Enumerated
    @Column(nullable = false)
    private CallType callType;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private long duration;
    @Column(nullable = false)
    private double cost;
}
