package com.meerkat.mytidbapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class SentimentAnalysisResult {

    @Id
    private Long id;

    @Column(columnDefinition = "varchar(500) null")
    private String words;

    @Column(columnDefinition = "varchar(20) null")
    private String sentiment;

    @Column(columnDefinition = "decimal(12,6) null")
    private BigDecimal probability;

    @Column(columnDefinition = "varchar(20) null")
    private String model;

    /**
     * 股票名称
     */
    @Column(columnDefinition = "varchar(100) null")
    private String stockName;

    private LocalDate tradeDate;

    private LocalDateTime createdDate;

    @Column(columnDefinition = "varchar(50) null")
    private String createdBy;

    private LocalDateTime lastModifiedDate;

    @Column(columnDefinition = "varchar(50) null")
    private String lastModifiedBy;

}
