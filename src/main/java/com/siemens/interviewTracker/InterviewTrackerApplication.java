package com.siemens.interviewTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InterviewTrackerApplication {
	@GetMapping("/dummyEndPoint")
	public String dummyEndPoint() {
		return "Hello from dummyEndPoint";
	}
	public static void main(String[] args) {
		SpringApplication.run(InterviewTrackerApplication.class, args);
	}

}
