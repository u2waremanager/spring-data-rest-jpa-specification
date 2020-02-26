package io.github.u2ware.test.example2;

import javax.persistence.EntityManagerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.config.HibernateAddtionalConfiguration;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean 
	public HibernateAddtionalConfiguration hibernateAddtionalConfiguration(EntityManagerFactory emf) {
		return new HibernateAddtionalConfiguration(emf);
	}

}
