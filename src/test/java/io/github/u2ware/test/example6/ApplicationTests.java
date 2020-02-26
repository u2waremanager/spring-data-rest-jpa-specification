package io.github.u2ware.test.example6;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.u2ware.test.RestMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	protected @Autowired FooRepository fooRepository;
	protected @Autowired ObjectMapper objectMapper;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		
//		DateTimeFormatterFactory Factory
//		Foo f = new Foo();
//		f.setDate(new DateTime());
//		f.setType(FooType.AA);
		
		$.POST("/foos").C("date", "2010-01-01").is2xx().andReturn("foo1");
		$.GET("{foo1}").is2xx().andReturn("foo1");
		
		
	}
}
