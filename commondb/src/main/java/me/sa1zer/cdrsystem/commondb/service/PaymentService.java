package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.commondb.entity.Payment;
import me.sa1zer.cdrsystem.commondb.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {

    private final PaymentRepository repository;

    @Transactional
    public Payment save(Payment payment) {
        return repository.save(payment);
    }
}
