package com.meerkat.mytidbapp.controller;

import com.meerkat.mytidbapp.repo.BatchDao;
import com.meerkat.mytidbapp.util.JsonUtil;
import com.meerkat.mytidbapp.util.Result;
import com.meerkat.mytidbapp.util.SnowFlake;
import com.meerkat.mytidbapp.util.Tuple3;
import com.meerkat.mytidbapp.dto.req.QueryReq;
import com.meerkat.mytidbapp.dto.res.StockAndProbabilityRes;
import com.meerkat.mytidbapp.dto.res.StockCommentQuantityByDayRes;
import com.meerkat.mytidbapp.entity.SentimentAnalysisResult;
import com.meerkat.mytidbapp.repo.SentimentAnalysisResultRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
public class SentimentAnalysisController {

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private SentimentAnalysisResultRepo sentimentAnalysisResultRepo;

    @Autowired
    private BatchDao batchDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Data
    public static class SentimentAnalysisResponse {
        private String sentiment;
        private BigDecimal probability;
    }

    /**
     * 获取单个句的得分（不持久化）
     *
     * @param text
     * @return
     */
    @GetMapping("/sentimentAnalysis")
    public Result<SentimentAnalysisResponse> sentimentAnalysis(@RequestParam String text) {
        String encodeText = URLEncoder.encode(text, Charset.defaultCharset());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://127.0.0.1:8000/analysis/?text=" + encodeText, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            SentimentAnalysisResponse sentimentAnalysisResponse = JsonUtil.fromJson(responseEntity.getBody(), SentimentAnalysisResponse.class);
            return Result.ok(sentimentAnalysisResponse);
        } else {
            return Result.error("未获取到情感分数");
        }
    }


    /**
     * 获取单个句的得分（持久化）
     *
     * @param text
     * @return
     */
    @GetMapping("/sentimentAnalysisAndSave")
    public Result<SentimentAnalysisResponse> sentimentAnalysisAndSave(@RequestParam String text) {
        Result<SentimentAnalysisResponse> result = this.sentimentAnalysis(text);
        if (result.getSuccess()) {
            SentimentAnalysisResult sentimentAnalysisResult = new SentimentAnalysisResult();
            sentimentAnalysisResult.setId(SnowFlake.getInstance().nextId());
            sentimentAnalysisResult.setWords(text);
            sentimentAnalysisResult.setSentiment(result.getData().getSentiment());
            sentimentAnalysisResult.setProbability(result.getData().getProbability());
            sentimentAnalysisResult.setCreatedDate(LocalDateTime.now());
            sentimentAnalysisResult.setCreatedBy("web");
            sentimentAnalysisResult.setLastModifiedDate(LocalDateTime.now());
            sentimentAnalysisResult.setLastModifiedBy("web");
            sentimentAnalysisResultRepo.save(sentimentAnalysisResult);
        }
        return result;
    }

    @GetMapping("/saveCsvToDb")
    public Result<Integer> saveCsvToDb() throws IOException, URISyntaxException {
        jdbcTemplate.execute("truncate table sentiment_analysis_result");
        String fileString = Files.readString(Paths.get(SentimentAnalysisController.class.getClassLoader().getResource("stock_market_tweets_10w.csv").toURI()));

        String[] lineArray = fileString.split("\n");
        // 评论，股票名，日期
        List<Tuple3<String, String, LocalDate>> wordsList = Stream.of(lineArray).skip(1).map(line -> {
            String[] ItemArray = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            return Tuple3.of(ItemArray[4], ItemArray[8], LocalDate.parse(ItemArray[3]));
        }).toList();
        int count = 0;

        List<List<Tuple3<String, String, LocalDate>>> partitions = ListUtils.partition(wordsList, 1000);
        for (List<Tuple3<String, String, LocalDate>> partition : partitions) {
            List<SentimentAnalysisResult> insertList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            for (Tuple3<String, String, LocalDate> tuple : partition) {
                String words = tuple.getFirst();
                String stockName = tuple.getSecond();
                LocalDate tradeDate = tuple.getThird();
                if (words == null) {
                    continue;
                }
                count++;
                words = words.replaceAll("\\\\", "");
                words = words.replaceAll("/", "");
                words = words.replaceAll("<", "");
                words = words.replaceAll(">", "");
                words = words.replaceAll("\\$", "");
                words = words.replaceAll(":", "");
                if (words.length() > 256) {
                    words = words.substring(0, 256);
                }
                SentimentAnalysisResult result1 = new SentimentAnalysisResult();
                result1.setId(SnowFlake.getInstance().nextId());
                result1.setWords(words);
                result1.setStockName(stockName);
                result1.setTradeDate(tradeDate);
                result1.setCreatedDate(LocalDateTime.now());
                result1.setCreatedBy("init");
                insertList.add(result1);
            }
            batchDao.batchInsert(insertList);
            log.info("写入 {} 条记录成功，耗时：{}", insertList.size(), (System.currentTimeMillis() - startTime) / 1000.0);
        }
        return Result.ok(count);
    }

    /**
     * 把数据库里的数据逐条打分
     *
     * @return
     */
    @GetMapping("/sentimentAnalysisAll")
    public Result<String> sentimentAnalysisAll() {
        List<Long> allIds = sentimentAnalysisResultRepo.findIds();
        List<List<Long>> partitions = ListUtils.partition(allIds, 100);
        for (List<Long> ids : partitions) {
            List<SentimentAnalysisResult> updateList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            Map<Long, SentimentAnalysisResult> dbMap = sentimentAnalysisResultRepo.findByIdIn(ids)
                    .stream().collect(Collectors.toMap(SentimentAnalysisResult::getId, Function.identity()));
            for (Long id : ids) {
                if (dbMap.get(id) != null && dbMap.get(id).getSentiment() == null) {
                    SentimentAnalysisResult sentimentAnalysisResult = dbMap.get(id);
                    Result<SentimentAnalysisResponse> result = this.sentimentAnalysis(dbMap.get(id).getWords());
                    if (result.getSuccess()) {
                        sentimentAnalysisResult.setSentiment(result.getData().getSentiment());
                        sentimentAnalysisResult.setProbability(result.getData().getProbability());
                        sentimentAnalysisResult.setLastModifiedDate(LocalDateTime.now());
                        sentimentAnalysisResult.setLastModifiedBy("system");
                        updateList.add(sentimentAnalysisResult);
                    } else {
                        log.error("获取情感得分失败：{}", sentimentAnalysisResult.getWords());
                    }
                }
            }
            batchDao.batchUpdate(updateList);
            log.info("更新 {} 条记录的情感得分成功，耗时：{} 秒", updateList.size(), (System.currentTimeMillis() - startTime) / 1000.0);
        }
        return Result.ok();
    }

    /**
     * 查询股票类型
     */
    @GetMapping("/getStockName")
    public Result<List<String>> getStockName() {
        List<String> stockNameList = sentimentAnalysisResultRepo.getStockName();
        return Result.ok(stockNameList);
    }

    /**
     * 查询每只股票的评论量
     */
    @GetMapping("/getStockNameAndCommentQuantity")
    public Result<List<Map<String, Object>>> getStockNameAndCommentQuantity() {
        List<Map<String, Object>> mapList = sentimentAnalysisResultRepo.getStockNameAndCommentQuantity();
        return Result.ok(mapList);
    }

    /**
     * 查询每天的评论量
     */
    @PostMapping("/getDayCommentQuantity")
    public Result<List<Map<String, Object>>> getDayCommentQuantity(@RequestBody QueryReq queryReq) {
        LocalDate startTime = queryReq.getStartTime();
        LocalDate endTime = queryReq.getEndTime();
        List<Map<String, Object>> mapList = sentimentAnalysisResultRepo.getDayCommentQuantity(startTime, endTime);
        return Result.ok(mapList);
    }

    /**
     * 查询每天各股票的评论量
     */
    @PostMapping("/getStockCommentQuantityByDay")
    public Result getStockCommentQuantityByDay(@RequestBody QueryReq queryReq) {
        LocalDate startTime = queryReq.getStartTime();
        LocalDate endTime = queryReq.getEndTime();
        List<StockCommentQuantityByDayRes> stockCommentQuantityByDayResList = sentimentAnalysisResultRepo.getStockCommentQuantityByDay(startTime, endTime);
        stockCommentQuantityByDayResList.forEach(stockCommentQuantityByDayRes -> stockCommentQuantityByDayRes.setStockName(stockCommentQuantityByDayRes.getStockName().replace("\r", "")));
        Map<String, List<StockCommentQuantityByDayRes>> stringListMap = stockCommentQuantityByDayResList.stream().collect(Collectors.groupingBy(StockCommentQuantityByDayRes::getStockName));
        return Result.ok(stringListMap);
    }

    /**
     * 查询每只股票的评分
     */
    @PostMapping("/getStockAndProbability")
    public Result<List<StockAndProbabilityRes>> getStockAndProbability(@RequestBody QueryReq queryReq) {
        LocalDate startTime = queryReq.getStartTime();
        LocalDate endTime = queryReq.getEndTime();
        String stockName = queryReq.getStockName();
        List<StockAndProbabilityRes> stockAndProbabilityResList = sentimentAnalysisResultRepo.getStockAndProbability(stockName, startTime, endTime);
        return Result.ok(stockAndProbabilityResList);
    }

    /**
     * 查询每只股票每天的评分均值
     */
    @PostMapping("/getTradeDateAvgProbability")
    public Result<List<StockAndProbabilityRes>> getTradeDateAvgProbability(@RequestBody QueryReq queryReq) {
        LocalDate startTime = queryReq.getStartTime();
        LocalDate endTime = queryReq.getEndTime();
        String stockName = queryReq.getStockName();
        List<StockAndProbabilityRes> stockAndProbabilityResList = sentimentAnalysisResultRepo.getTradeDateAvgProbability(stockName, startTime, endTime);
        return Result.ok(stockAndProbabilityResList);
    }

    /**
     *  查询股票评论数量
     */
    @GetMapping("/getStockCommentTotalCount")
    public Result<Integer> getStockCommentTotalCount() {
        Integer stockCommentTotalCount = sentimentAnalysisResultRepo.getStockCommentTotalCount();
        return Result.ok(stockCommentTotalCount);
    }

}
