package com.meerkat.mytidbapp.controller;

import com.meerkat.mytidbapp.util.JsonUtil;
import com.meerkat.mytidbapp.util.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootTest
class SentimentAnalysisControllerTest {

    @Autowired
    private SentimentAnalysisController sentimentAnalysisController;

    @Test
    void sentimentAnalysis() {
        long startTime = System.currentTimeMillis();
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result = sentimentAnalysisController.sentimentAnalysis("top trades");
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result2 = sentimentAnalysisController.sentimentAnalysis("Today is raining");
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result3 = sentimentAnalysisController.sentimentAnalysis("Today is sunny");
        System.out.println("三次调用总耗时：" + (System.currentTimeMillis() - startTime) / 1000.0 + " 秒");
        System.out.println(JsonUtil.toJson(result, true));
        System.out.println(JsonUtil.toJson(result2, true));
        System.out.println(JsonUtil.toJson(result3, true));
    }

    @Test
    void sentimentAnalysisAndSave() {
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result = sentimentAnalysisController.sentimentAnalysisAndSave("top trades");
        System.out.println(JsonUtil.toJson(result, true));
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result2 = sentimentAnalysisController.sentimentAnalysisAndSave("Today is raining");
        System.out.println(JsonUtil.toJson(result2, true));
        Result<SentimentAnalysisController.SentimentAnalysisResponse> result3 = sentimentAnalysisController.sentimentAnalysisAndSave("Today is sunny");
        System.out.println(JsonUtil.toJson(result3, true));
    }

    @Test
    void sentimentAnalysisAll() {
        Result<String> result = sentimentAnalysisController.sentimentAnalysisAll();
        System.out.println(JsonUtil.toJson(result, true));
    }

    @Test
    void saveCsvToDb() {
        try {
            Result<Integer> result = sentimentAnalysisController.saveCsvToDb();
            System.out.println(JsonUtil.toJson(result, true));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}