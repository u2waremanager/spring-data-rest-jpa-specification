package io.github.u2ware.test.example1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.test.ApplicationMockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	private @Autowired FooRepository fooRepository; 
	private @Autowired BarRepository barRepository; 
//	private @Autowired BazRepository bazRepository; 
	
	
	@Before
	public void before() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);
		
		fooRepository.save(new Foo("a", 1));
		fooRepository.save(new Foo("a", 2));
		fooRepository.save(new Foo("b", 1));
		fooRepository.save(new Foo("b", 2));
		
		barRepository.save(new Bar("a", 1));		
		barRepository.save(new Bar("a", 2));		
		barRepository.save(new Bar("b", 1));		
		barRepository.save(new Bar("b", 2));		
	}
	
	
	@Test
	public void contextLoads() throws Exception {

		$.GET("/foos/query").C("_name", "aa").is2xx();
//		$.GET("/foos/read").H("partTree", "findByNameAndAge").C("age", "1").is2xx();
//		$.GET("/foos/read").H("read", "querydsl").C("_name", "bb").is2xx();
	}
}
