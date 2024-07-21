package seatek.e3dc.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class E3dcToMqtt {

	public static void main(final String[] args) {
		SpringApplication.run(E3dcToMqtt.class, args);
	}



}
