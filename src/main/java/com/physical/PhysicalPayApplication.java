package com.physical;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhysicalPayApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(PhysicalPayApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.print("server ok");
	}
}
