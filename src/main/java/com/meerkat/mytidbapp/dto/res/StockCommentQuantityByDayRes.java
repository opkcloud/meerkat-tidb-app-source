package com.meerkat.mytidbapp.dto.res;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StockCommentQuantityByDayRes {

    private String stockName;
    private LocalDate tradeDate;
    private double count;

    public StockCommentQuantityByDayRes(String stockName, LocalDate tradeDate, double count) {
        this.stockName = stockName;
        this.tradeDate = tradeDate;
        this.count = count;
    }

}
