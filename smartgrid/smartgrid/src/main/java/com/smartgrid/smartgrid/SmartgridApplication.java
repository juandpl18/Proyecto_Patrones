package com.smartgrid.smartgrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartgridApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartgridApplication.class, args);
	}


}
