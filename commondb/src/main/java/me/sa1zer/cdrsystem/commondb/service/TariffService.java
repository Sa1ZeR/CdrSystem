package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.commondb.entity.Tariff;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import me.sa1zer.cdrsystem.commondb.repository.TariffRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TariffService {

    private final TariffRepository repository;

    public Tariff findByCode(String code) {
        Optional<Tariff> tariff = repository.findByCode(code);
        if(tariff.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Status with code %s not found", code));
            //throw new ErrorMessageException(HttpStatus.NOT_FOUND, String.format("Status with code %s not found", code));
        return tariff.get();
    }

    public List<Tariff> findAll() {
        return repository.findAll();
    }
}
