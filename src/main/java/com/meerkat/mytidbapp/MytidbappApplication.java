package com.meerkat.mytidbapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MytidbappApplication {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SpringApplication.run(MytidbappApplication.class, args);
		log.info("启动完成，用时：{} 秒", (System.currentTimeMillis() - startTime) / 1000.0);
	}

}
