package com.meerkat.mytidbapp;

import com.meerkat.mytidbapp.controller.SentimentAnalysisController;
import com.meerkat.mytidbapp.util.JsonUtil;
import com.meerkat.mytidbapp.util.SnowFlake;
import com.meerkat.mytidbapp.entity.SentimentAnalysisResult;
import com.meerkat.mytidbapp.repo.SentimentAnalysisResultRepo;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class MytidbappApplicationTests {

    @Autowired
    private SentimentAnalysisResultRepo sentimentAnalysisResultRepo;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {


    }

    void clean() {
        jdbcTemplate.execute("truncate table sentiment_analysis_result");
    }

    /**
     * Deprecated
     *
     * @see SentimentAnalysisController
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    @Deprecated
    public void parseCsv() throws IOException, URISyntaxException {
        this.clean();
        String fileString = Files.readString(Paths.get(MytidbappApplicationTests.class.getClassLoader().getResource("stock_market_tweets_10w.csv").toURI()));

        String[] lineArray = fileString.split("\n");
        List<String> wordsList = Stream.of(lineArray).map(line -> {
            return line.split(",")[4];
        }).toList();
        for (int i = 0; i < wordsList.size(); i++) {
            String words = wordsList.get(i);
            if (words == null) {
                continue;
            }
            words = words.replaceAll("\\\\", "");
            words = words.replaceAll("/", "");
            words = words.replaceAll("<", "");
            words = words.replaceAll(">", "");
            words = words.replaceAll("\\$", "");
            words = words.replaceAll(":", "");
            System.out.println("正在分析第 " + i + " 条：" + words);
            if (words.length() > 256) {
                words = words.substring(0, 256);
            }
            String result = this.getSentimentAnalysisResult(words);
            SentimentAnalysisResponse sentimentAnalysisResult = JsonUtil.fromJson(result, SentimentAnalysisResponse.class);
            SentimentAnalysisResult result1 = new SentimentAnalysisResult();
            result1.setId(SnowFlake.getInstance().nextId());
            result1.setWords(words);
            result1.setSentiment(sentimentAnalysisResult.getSentiment());
            result1.setProbability(sentimentAnalysisResult.getProbability());
            result1.setCreatedDate(LocalDateTime.now());
            sentimentAnalysisResultRepo.save(result1);
        }
    }

    @Test
    public void testRestTemplate() {
        String text = "Today is sunny.";
        String result = this.getSentimentAnalysisResult(text);
        System.out.println("result = " + result);
    }

    public String getSentimentAnalysisResult(String text) {
        String encodeText = URLEncoder.encode(text, Charset.defaultCharset());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://127.0.0.1:8000/analysis/?text=" + encodeText, String.class);
        return responseEntity.getBody();
    }

    @Data
    public static class SentimentAnalysisResponse {
        private String sentiment;
        private BigDecimal probability;
    }

}
