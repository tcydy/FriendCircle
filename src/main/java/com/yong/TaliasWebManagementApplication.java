package com.yong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@ServletComponentScan //开启了springboot对servlet的支持
@SpringBootApplication
public class TaliasWebManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaliasWebManagementApplication.class, args);
	}

}
