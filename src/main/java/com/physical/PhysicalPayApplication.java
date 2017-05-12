package com.physical;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class PhysicalPayApplication  extends SpringBootServletInitializer implements CommandLineRunner  {

	public static void main(String[] args) {
		SpringApplication.run(PhysicalPayApplication.class, args);
	}

	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(PhysicalPayApplication.class);
	}
	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.print("server ok");
	}
}
