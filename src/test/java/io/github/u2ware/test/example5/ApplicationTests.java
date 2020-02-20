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
	
	private @Autowired OneToManyColumn1Repository oneToManyColumn1Repository;
	private @Autowired OneToManyColumn2Repository oneToManyColumn2Repository;
	private @Autowired OneToManyColumn3Repository oneToManyColumn3Repository;
	private @Autowired OneToManyColumn4Repository oneToManyColumn4Repository;
	
	private @Autowired DomainSampleRepository domainSampleRepository;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {


		String otm1Link1 = $.POST("/oneToManyColumn1s").C(new OneToManyColumn1("a", 1)).is2xx().andReturn().path();		
		String otm1Link2 = $.POST("/oneToManyColumn1s").C(new OneToManyColumn1("b", 1)).is2xx().andReturn().path();		
		OneToManyColumn1 otm1Json1 = oneToManyColumn1Repository.save(new OneToManyColumn1("c", 2));	
		OneToManyColumn1 otm1Json2 = oneToManyColumn1Repository.save(new OneToManyColumn1("d", 2));	
		
		$.POST("/oneToManyColumn2s").C(new OneToManyColumn2("c", 3)).is4xx();
		OneToManyColumn2 otm2Json1 = oneToManyColumn2Repository.save(new OneToManyColumn2("e", 4));		
		OneToManyColumn2 otm2Json2 = oneToManyColumn2Repository.save(new OneToManyColumn2("f", 4));	
		
		
		String otm3Link1 = $.POST("/oneToManyColumn3s").C(new OneToManyColumn3("a", 1)).is2xx().andReturn().path();		
		String otm3Link2 = $.POST("/oneToManyColumn3s").C(new OneToManyColumn3("b", 1)).is2xx().andReturn().path();		
		OneToManyColumn3 otm3Json1 = oneToManyColumn3Repository.save(new OneToManyColumn3("c", 2));	
		OneToManyColumn3 otm3Json2 = oneToManyColumn3Repository.save(new OneToManyColumn3("d", 2));	
		
		String otm4Link1 = $.POST("/oneToManyColumn4s").C(new OneToManyColumn4("a", 1)).is2xx().andReturn().path();		
		String otm4Link2 = $.POST("/oneToManyColumn4s").C(new OneToManyColumn4("b", 1)).is2xx().andReturn().path();		
		OneToManyColumn4 otm4Json1 = oneToManyColumn4Repository.save(new OneToManyColumn4("c", 2));	
		OneToManyColumn4 otm4Json2 = oneToManyColumn4Repository.save(new OneToManyColumn4("d", 2));	
		
		
		///////////////////////////////////////////////
		// Insert
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		
		c.put("foo1", Arrays.asList(otm1Link1, otm1Link2));  //c.put("foo1", [link...]); (O)   c.put("foo1", [json...]); (X)  
		c.put("foo2", Arrays.asList(otm2Json1, otm2Json2));  //c.put("foo2", [link...]); (X)   c.put("foo2", [json...]); (O) 
		c.put("foo3", Arrays.asList(otm3Json1, otm3Json2));  //c.put("foo3", [link...]); (X)   c.put("foo3", [json...]); (O) 
		c.put("foo4", Arrays.asList(otm4Link1, otm4Link2));  //c.put("foo4", [link...]); (O)   c.put("foo4", [json...]); (O) 
		

		$.POST("/domainSamples").C(c).is2xx("d1");

		////////////////////////////////////////////////////
		// Select
		////////////////////////////////////////////////////
		RestMvcResult r = $.GET("d1").is2xx().andReturn();
		
		//foo1
		Assert.assertTrue(r.path("_links.foo1.href").toString().endsWith("/foo1"));
//		$.get(r.path("_links.foo1.href").toString()).is2xx(); //foo1uri
//		$.get(r.path("_links.foo1.href").toString()).is4xx(); //foo1json
		
		//foo2
		Assert.assertFalse(StringUtils.isEmpty(r.path("foo2")));
		
		////////////////////////////////////////////////////
		// Update
		////////////////////////////////////////////////////
		Map<String, Object> u = new HashMap<String,Object>();
		u.put("name", "John####");
		u.put("age", 10);
		
//		u.put("foo1", Arrays.asList(otm1Link1, otm1Link2));  //c.put("foo1", [link...]); (O)   c.put("foo1", [json...]); (X)  
//		u.put("foo2", Arrays.asList(otm2Json1, otm2Json2));  //c.put("foo2", [link...]); (X)   c.put("foo2", [json...]); (O) 
//		u.put("foo3", Arrays.asList(otm3Json1, otm3Json2));  //c.put("foo3", [link...]); (X)   c.put("foo3", [json...]); (O) 
		u.put("foo4", Arrays.asList(otm4Link2));             //c.put("foo4", [link...]); (O)   c.put("foo4", [json...]); (O) 
		
		$.PATCH("d1").C(u).is2xx();
//		$.PUT("d1").C(u).is2xx();
		//$.GET("d1").is2xx().andReturn();
		
		////////////////////////////////////////////////////
		// Search Graphic
		////////////////////////////////////////////////////
		//$.GET("/domainSamples").is2xx("d1");
	}
}
