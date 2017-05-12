package com.physical.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class WebConfig {

	@Bean
	public JdbcTemplate getJdbcTemplate(DataSource ds){
		return new JdbcTemplate(ds);
	}
}
