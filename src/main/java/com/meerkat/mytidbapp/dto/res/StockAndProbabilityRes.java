package com.meerkat.mytidbapp.dto.res;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockAndProbabilityRes {

    private String stockName;
    private LocalDate tradeDate;
    private double finalProbability;
    private double avgProbability;
    private BigDecimal probability;

    public StockAndProbabilityRes(String stockName, LocalDate tradeDate, double finalProbability, BigDecimal probability) {
        this.stockName = stockName;
        this.tradeDate = tradeDate;
        this.finalProbability = finalProbability;
        this.probability = probability;
    }

    public StockAndProbabilityRes(LocalDate tradeDate, double avgProbability) {
        this.tradeDate = tradeDate;
        this.avgProbability = avgProbability;
    }
}
