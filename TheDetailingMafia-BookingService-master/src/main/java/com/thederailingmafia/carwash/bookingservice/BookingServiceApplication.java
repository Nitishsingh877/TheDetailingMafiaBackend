package com.thederailingmafia.carwash.bookingservice;

//import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients
public class BookingServiceApplication {

    public static void main(String[] args) {
            SpringApplication.run(BookingServiceApplication.class, args);
        }

    }




