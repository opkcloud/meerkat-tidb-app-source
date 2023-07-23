package com.meerkat.mytidbapp.dto.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QueryReq {

    private LocalDate startTime;
    private LocalDate endTime;
    private String stockName;

}
