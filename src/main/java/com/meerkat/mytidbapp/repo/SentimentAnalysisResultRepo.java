package com.meerkat.mytidbapp.repo;

import com.meerkat.mytidbapp.dto.res.StockAndProbabilityRes;
import com.meerkat.mytidbapp.dto.res.StockCommentQuantityByDayRes;
import com.meerkat.mytidbapp.entity.SentimentAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SentimentAnalysisResultRepo extends JpaRepository<SentimentAnalysisResult, Long> {

    Integer countBySentiment(String sentiment);

    @Query("select t.id from #{#entityName} t")
    List<Long> findIds();

    List<SentimentAnalysisResult> findByIdIn(List<Long> ids);

    @Query(value = "SELECT DISTINCT stock_name FROM test.sentiment_analysis_result where stock_name is not null", nativeQuery = true)
    List<String> getStockName();

    @Query(value = "SELECT stock_name as name, COUNT(stock_name) value FROM (" +
            "SELECT case when stock_name is not null then stock_name else 'others' end as stock_name FROM test.sentiment_analysis_result " +
            ") tmp group by name ORDER BY value DESC", nativeQuery = true)
    List<Map<String, Object>> getStockNameAndCommentQuantity();

    @Query(value = "SELECT trade_date, COUNT(trade_date) count FROM test.sentiment_analysis_result where trade_date between ?1 and ?2 " +
            " group by trade_date order by trade_date", nativeQuery = true)
    List<Map<String, Object>> getDayCommentQuantity(LocalDate startTime, LocalDate endTime);

    @Query("SELECT new com.meerkat.mytidbapp.dto.res.StockCommentQuantityByDayRes(stockName, tradeDate, COUNT(stockName) count) FROM SentimentAnalysisResult " +
            " where tradeDate between ?1 and ?2 GROUP by stockName, tradeDate ORDER by tradeDate, stockName")
    List<StockCommentQuantityByDayRes> getStockCommentQuantityByDay(LocalDate startTime, LocalDate endTime);

    @Query("SELECT new com.meerkat.mytidbapp.dto.res.StockAndProbabilityRes(stockName, tradeDate, (probability - 0.5) as finalProbability, probability) FROM SentimentAnalysisResult " +
            " WHERE stockName = ?1 and tradeDate between ?2 and ?3 ORDER by tradeDate")
    List<StockAndProbabilityRes> getStockAndProbability(String stockName, LocalDate startTime, LocalDate endTime);

    @Query("SELECT new com.meerkat.mytidbapp.dto.res.StockAndProbabilityRes(tradeDate, AVG(probability) avgProbability) FROM SentimentAnalysisResult " +
            " WHERE stockName = ?1 and tradeDate between ?2 and ?3 GROUP BY tradeDate ORDER by tradeDate")
    List<StockAndProbabilityRes> getTradeDateAvgProbability(String stockName, LocalDate startTime, LocalDate endTime);

}
