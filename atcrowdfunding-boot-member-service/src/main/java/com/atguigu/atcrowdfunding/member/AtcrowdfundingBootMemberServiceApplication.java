package com.atguigu.atcrowdfunding.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@ComponentScan("com.atguigu")
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.atguigu.**.dao")
@SpringBootApplication
public class AtcrowdfundingBootMemberServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtcrowdfundingBootMemberServiceApplication.class, args);
	}
}
