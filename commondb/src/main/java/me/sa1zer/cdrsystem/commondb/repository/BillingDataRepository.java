package me.sa1zer.cdrsystem.commondb.repository;

import me.sa1zer.cdrsystem.commondb.entity.BillingData;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingDataRepository extends JpaRepository<BillingData, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "user.tariff", "reportData"})
    List<BillingData> findAll();
}
