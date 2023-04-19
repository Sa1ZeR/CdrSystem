package me.sa1zer.cdrsystem.commondb.repository;

import me.sa1zer.cdrsystem.commondb.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    Optional<Tariff> findByCode(String code);

    Optional<Tariff> findByType(TariffType type);
}
