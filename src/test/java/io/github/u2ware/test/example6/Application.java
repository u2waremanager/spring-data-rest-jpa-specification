package io.github.u2ware.test.example6;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	
	private static final String WRITE_TYPE = "write";
	private static final String READ_TYPE = "read";
	
	
	@Bean(name="masterDataSource")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).url("jdbc:hsqldb:mem:master").driverClassName("org.hsqldb.jdbcDriver").build();
	}
	
	@Bean(name="slaveDataSource")
	public DataSource slaveDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).url("jdbc:hsqldb:mem:slave").driverClassName("org.hsqldb.jdbcDriver").build();
	}
	
	@Bean(name="routingDataSource")
	public DataSource routingDataSource() {
	    ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

	    Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
	    dataSourceMap.put(WRITE_TYPE, masterDataSource());
	    dataSourceMap.put(READ_TYPE, slaveDataSource());
	    routingDataSource.setTargetDataSources(dataSourceMap);
	    routingDataSource.setDefaultTargetDataSource(masterDataSource());

	    return routingDataSource;
	}

	@Primary
	@Bean(name="dataSource")
	public DataSource dataSource() {
	    return new LazyConnectionDataSourceProxy(routingDataSource());
	}	
	

	public static class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
		
		protected Log logger = LogFactory.getLog(getClass());
		
	    @Override
	    protected Object determineCurrentLookupKey() {
	        String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? READ_TYPE : WRITE_TYPE;
	        logger.info("dataSourceType : "+dataSourceType);
	        return dataSourceType;
	    }
	}
}
