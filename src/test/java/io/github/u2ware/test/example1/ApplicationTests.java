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

import io.github.u2ware.test.RestMockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	private @Autowired FooRepository fooRepository; 
	private @Autowired BarRepository barRepository; 
	
	
	@Before
	public void before() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
		
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

		$.POST("/foos").C(new Foo("hello", 11)).is2xx().andReturn("foo1");
		$.GET("{foo1}").is2xx();
		$.GET("{foo1}").H("query","true").is2xx();
		
		$.POST("/bars").C(new Bar("world", 22)).is2xx().andReturn("bar1");
		$.GET("{bar1}").is2xx();
		$.GET("{bar1}").H("query","true").is2xx();
		
		$.GET("/foos").H("query","true").C("_name", "a").is2xx();
		$.GET("/bars").H("query","true").C("name", "a").is2xx();
		

		$.GET("/bars").H("query","true").C("","").P("page", "0").P("size", "2").is2xx();
		$.GET("/bars").H("query","true").C("age","2").P("unpaged", "true").P("page", "0").P("size", "1").P("sort","name").P("sort","age,desc").is2xx();
		$.GET("/bars").H("query","true").C("age","2").P("unpaged", "false").P("page", "0").P("size", "2").P("sort","name").P("sort","age,desc").is2xx();
		
		
		$.GET("/bars").H("query","true").H("partTree", "findByNameAndAge").C("age", "1").is2xx();
	}
}
