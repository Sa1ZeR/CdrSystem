package me.sa1zer.cdrsystem.commondb.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.commondb.entity.Operator;
import me.sa1zer.cdrsystem.commondb.repository.OperatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository repository;

    public List<Operator> findAll() {
        return repository.findAll();
    }
}
