package io.github.u2ware.test.example50;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//	        EntityManagerFactoryBuilder factory, DataSource dataSource,
//	        JpaProperties properties) {
//	    Map<String, Object> jpaProperties = new HashMap<String, Object>();
//	    jpaProperties.putAll(properties.getHibernateProperties(dataSource));
//	    jpaProperties.put("hibernate.ejb.interceptor", hibernateInterceptor());
//	    return factory.dataSource(dataSource).packages("sample.data.jpa")
//	            .properties((Map) jpaProperties).build();
//	}
}
