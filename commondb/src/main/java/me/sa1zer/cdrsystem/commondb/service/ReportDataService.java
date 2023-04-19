package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import me.sa1zer.cdrsystem.commondb.entity.ReportData;
import me.sa1zer.cdrsystem.commondb.repository.ReportDataRepository;

@Service
@RequiredArgsConstructor
public class ReportDataService {

    private final ReportDataRepository repository;

    public ReportData save(ReportData data) {
        return repository.save(data);
    }
}
