package me.sa1zer.cdrsystem.commondb.repository;

import me.sa1zer.cdrsystem.commondb.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
