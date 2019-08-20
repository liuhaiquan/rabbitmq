package com.kavin.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 本项目主要对Spring整合AMQP的一些知识点,主要都在RabbitMQConfig类中
 *
 */


@SpringBootApplication
public class Application {

	public static void main(String[] args) {

	    SpringApplication.run(Application.class, args);
	}
}
