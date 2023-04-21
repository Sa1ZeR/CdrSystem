package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UnlimitedCalculatorTest {

    @Autowired
    UnlimitedCalculator calculator;

    @Test
    void calculate() {
        List<CdrPlusDto> CdrPlusDtoList = new ArrayList<>();

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 10, 18, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 10, 18, 53, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 10, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 10, 14, 53, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 5, 11, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 5, 13, 53, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 7, 11, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 7, 15, 53, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 11, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 11, 13, 43, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 13, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 13, 13, 43, 1),
                CallType.INCOMING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 15, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 15, 13, 43, 1),
                CallType.INCOMING, TariffType.UNLIMITED, ""));

        List<ReportDto> calculated = calculator.getReportData(CdrPlusDtoList);

        double sum = calculated.stream().mapToDouble(ReportDto::cost).sum();

        Assertions.assertEquals(230D, sum); //230 because 300 min = 100 rub, but here they are not included
    }

    @Test
    void calculate1() {
        List<CdrPlusDto> CdrPlusDtoList = new ArrayList<>();

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 10, 18, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 10, 18, 53, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));
        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 10, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 10, 14, 53, 1),
                CallType.INCOMING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 11, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 11, 13, 43, 1),
                CallType.OUTGOING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 13, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 13, 13, 43, 1),
                CallType.INCOMING, TariffType.UNLIMITED, ""));

        CdrPlusDtoList.add(new CdrPlusDto("+79033333333",
                LocalDateTime.of(2023, Month.JANUARY, 15, 13, 33, 1),
                LocalDateTime.of(2023, Month.JANUARY, 15, 13, 43, 1),
                CallType.INCOMING, TariffType.UNLIMITED, ""));

        List<ReportDto> calculated = calculator.getReportData(CdrPlusDtoList);

        double sum = calculated.stream().mapToDouble(ReportDto::cost).sum();

        Assertions.assertEquals(0D, sum); //0 because 300 min = 100 rub, but here they are not included
    }
}