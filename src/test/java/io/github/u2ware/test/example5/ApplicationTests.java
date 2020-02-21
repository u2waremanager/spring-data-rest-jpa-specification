package io.github.u2ware.test.example5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.test.RestMockMvc;
import io.github.u2ware.test.RestMockMvc.RestMvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	private @Autowired OneToManySample1Repository oneToManySample1Repository;
	private @Autowired OneToManySample2Repository oneToManySample2Repository;
	private @Autowired OneToManySample3Repository oneToManySample3Repository;
	private @Autowired OneToManySample4Repository oneToManySample4Repository;
	
	private @Autowired DomainSampleRepository domainSampleRepository;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		String otm1Link1 = $.POST("/oneToManySample1s").C(new OneToManySample1("otm1Link1")).is2xx().andReturn().path();		
		String otm1Link2 = $.POST("/oneToManySample1s").C(new OneToManySample1("otm1Link2")).is2xx().andReturn().path();		
		OneToManySample1 otm1Json1 = oneToManySample1Repository.save(new OneToManySample1("otm1Json1"));	
		OneToManySample1 otm1Json2 = oneToManySample1Repository.save(new OneToManySample1("otm1Json2"));	
		
		$.POST("/oneToManyColumn2s").C(new OneToManySample2("~~~")).is4xx();
		OneToManySample2 otm2Json1 = oneToManySample2Repository.save(new OneToManySample2("otm2Json1"));		
		OneToManySample2 otm2Json2 = oneToManySample2Repository.save(new OneToManySample2("otm2Json2"));	
		
		String otm3Link1 = $.POST("/oneToManySample3s").C(new OneToManySample3("otm3Link1")).is2xx().andReturn().path();		
		String otm3Link2 = $.POST("/oneToManySample3s").C(new OneToManySample3("otm3Link2")).is2xx().andReturn().path();		
		OneToManySample3 otm3Json1 = oneToManySample3Repository.save(new OneToManySample3("otm3Json1"));	
		OneToManySample3 otm3Json2 = oneToManySample3Repository.save(new OneToManySample3("otm3Json2"));	
		
		String otm4Link1 = $.POST("/oneToManySample4s").C(new OneToManySample4("otm4Link1")).is2xx().andReturn().path();		
		String otm4Link2 = $.POST("/oneToManySample4s").C(new OneToManySample4("otm4Link2")).is2xx().andReturn().path();		
		OneToManySample4 otm4Json1 = oneToManySample4Repository.save(new OneToManySample4("otm4Json1"));	
		OneToManySample4 otm4Json2 = oneToManySample4Repository.save(new OneToManySample4("otm4Json2"));	
		
		
		///////////////////////////////////////////////
		// POST
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		
		c.put("sample1", Arrays.asList(otm1Link1, otm1Link2));  //link(O) json(X)  
		c.put("sample2", Arrays.asList(otm2Json1, otm2Json2));  //link(X) json(O)  
		c.put("sample3", Arrays.asList(otm3Json1, otm3Json2));  //link(X) json(O)  
		c.put("sample4", Arrays.asList(otm4Json1, otm4Json2));  //link(O) json(O)  
		
		
		$.POST("/domainSamples").C(c).is2xx("uri");
		RestMvcResult post = $.GET("uri").is2xx().andReturn();
		$.get(post.path("_links.sample1.href").toString()).is2xx();
		
//		////////////////////////////////////////////////////
//		// Select
//		////////////////////////////////////////////////////
//		
//		////////////////////////////////////////////////////
//		// Update
//		////////////////////////////////////////////////////
//		Map<String, Object> u = new HashMap<String,Object>();
//		u.put("name", "John####");
//		u.put("age", 10);
//		
////		u.put("foo1", Arrays.asList(otm1Link1, otm1Link2));  //c.put("foo1", [link...]); (O)   c.put("foo1", [json...]); (X)  
////		u.put("foo2", Arrays.asList(otm2Json1, otm2Json2));  //c.put("foo2", [link...]); (X)   c.put("foo2", [json...]); (O) 
////		u.put("foo3", Arrays.asList(otm3Json1, otm3Json2));  //c.put("foo3", [link...]); (X)   c.put("foo3", [json...]); (O) 
//		u.put("foo4", Arrays.asList(otm4Link2));             //c.put("foo4", [link...]); (O)   c.put("foo4", [json...]); (O) 
//		
//		$.PATCH("d1").C(u).is2xx();
////		$.PUT("d1").C(u).is2xx();
//		//$.GET("d1").is2xx().andReturn();
//		
//		////////////////////////////////////////////////////
//		// Search Graphic
//		////////////////////////////////////////////////////
//		//$.GET("/domainSamples").is2xx("d1");
	}
}
