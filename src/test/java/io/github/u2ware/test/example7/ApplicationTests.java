package io.github.u2ware.test.example7;

import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.test.RestMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	protected @Autowired FooRepository fooRepository;
	
	@Test
	public void contextLoads() throws Exception {
		
		logger.info("------------------------");
		logger.info(StringUtils.arrayToCommaDelimitedString(context.getBeanNamesForType(DataSource.class)));
		logger.info("------------------------");
		
		
		fooRepository.save(new Foo(UUID.randomUUID(), "qq", 1));
		
		try {
			Iterable<Foo> r = fooRepository.findByName("qq");
			Assert.assertNotNull(r);
		}catch(Exception e) {
			//Assert.assertEquals(InvalidDataAccessResourceUsageException.class, e.getClass());
		}
		
		try {
			Iterable<Foo> r = fooRepository.findByAge(1);
			//Assert.assertNotNull(r);
		}catch(Exception e) {
			Assert.assertEquals(InvalidDataAccessResourceUsageException.class, e.getClass());
		}
	}
}
