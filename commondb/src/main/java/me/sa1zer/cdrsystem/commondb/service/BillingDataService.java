package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import me.sa1zer.cdrsystem.commondb.entity.BillingData;
import me.sa1zer.cdrsystem.commondb.repository.BillingDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BillingDataService {

    private final BillingDataRepository repository;

    @Transactional
    public BillingData save(BillingData data) {
        return repository.save(data);
    }

    public List<BillingData> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void saveAll(List<BillingData> dataList) {
        repository.saveAll(dataList);
    }

    @Transactional
    @Modifying
    public void deleteAll() {
        repository.deleteAll();
    }
}
