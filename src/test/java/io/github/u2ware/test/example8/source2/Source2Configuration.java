package io.github.u2ware.test.example8.source2;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.HibernateAddtionalConfiguration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = {"io.github.u2ware.test.example8.source2"}, 
		entityManagerFactoryRef = "source2-entity-manager-factory", 
		transactionManagerRef = "source2-transaction-manager")
public class Source2Configuration {

	@Bean(name = "source2-datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).url("jdbc:hsqldb:mem:source2").driverClassName("org.hsqldb.jdbcDriver").build();
	}


//	@ConfigurationProperties(prefix = "source2.datasource")
//	public DataSource dataSource() {
//		//return DataSourceBuilder.create().build();
//	}
	
	
	@Bean(name = "source2-entity-manager-factory")
	public LocalContainerEntityManagerFactoryBean emf(@Qualifier("source2-datasource") DataSource ds) {

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(ds);
		emf.setPackagesToScan("io.github.u2ware.test.example8.source2");
		emf.setPersistenceUnitName("source2");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		return emf;
	}

	@Bean(name = "source2-transaction-manager")
	public PlatformTransactionManager tx(@Qualifier("source2-entity-manager-factory") EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
	
	@Bean(name = "source2-hibernateAddtionalConfiguration") 
	public HibernateAddtionalConfiguration hibernateAddtionalConfiguration(@Qualifier("source2-entity-manager-factory") EntityManagerFactory emf) {
		return new HibernateAddtionalConfiguration(emf);
	}
	
}