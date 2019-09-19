package io.github.u2ware.test.example3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	private @Autowired FooRepository fooRepository; 
	
	private UUID f1 = UUID.randomUUID();
	private UUID f2 = UUID.randomUUID();
	private UUID f3 = UUID.randomUUID();
	private UUID f4 = UUID.randomUUID();
	private UUID f5 = UUID.randomUUID();
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);
		
		fooRepository.save(new Foo(f1, "a", 1));		
		fooRepository.save(new Foo(f2, "a", 2));		
		fooRepository.save(new Foo(f3, "b", 1));		
		fooRepository.save(new Foo(f4, "b", 2));		

	}
	
	@Test
	public void contextLoads() throws Exception {
		
		
		$.OPTIONS("/foos").is2xx();
		$.OPTIONS("/bars").is2xx();
		
		$.HEAD("/foos").is2xx();
		$.HEAD("/bars").is2xx();
		
		$.GET("/foos").is2xx();
		$.GET("/bars").is2xx();

		$.GET("/foos/"+f1).is2xx();
		$.GET("/bars/"+f1).is2xx();
		
		$.GET("/foos/"+UUID.randomUUID()).is4xx();
		$.GET("/bars/"+UUID.randomUUID()).is4xx();
		
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		c.put("id", f5);
		
		$.POST("/foos").C(c).is2xx();
		$.POST("/bars").C(c).is2xx();
		
		$.DELETE("/foos/"+f5).is2xx();
		$.DELETE("/bars/"+f4).is2xx();

		
		Map<String, Object> u = new HashMap<String,Object>();
		u.put("name", "John");
		u.put("age", 10);
		u.put("id", f3);
		
		
		$.PUT("/foos/"+f3).C(u).is2xx();
		$.PUT("/bars/"+f3).C(u).is2xx();
		

		$.OPTIONS("/foos/"+f3).is2xx();
		$.OPTIONS("/bars/"+f3).is2xx();

		$.HEAD("/foos/"+f3).is2xx();
		$.HEAD("/bars/"+f3).is2xx();
		
		
		/////////////////////////////////////
		$.OPTIONS("/bazes").is2xx();
		$.HEAD("/bazes").is2xx();
		$.POST("/bazes").is4xx();
		$.GET("/bazes").is2xx();
	}
}
