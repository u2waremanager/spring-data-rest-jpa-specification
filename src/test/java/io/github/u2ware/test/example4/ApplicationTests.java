package io.github.u2ware.test.example4;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
	
	private @Autowired ManyToOneSample1Repository manyToOneSample1Repository;
	private @Autowired ManyToOneSample2Repository manyToOneSample2Repository;
	private @Autowired ManyToOneSample3Repository manyToOneSample3Repository;
	private @Autowired ManyToOneSample4Repository manyToOneSample4Repository;
	private @Autowired ManyToOneSample5Repository manyToOneSample5Repository;
	
	private @Autowired DomainSampleRepository domainSampleRepository;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		String mto1Link1 = $.POST("/manyToOneSample1s").C(new ManyToOneSample1("mto1Link1")).is2xx().andReturn().path();		
		String mto1Link2 = $.POST("/manyToOneSample1s").C(new ManyToOneSample1("mto1Link2")).is2xx().andReturn().path();		
		ManyToOneSample1 mto1Json1 = manyToOneSample1Repository.save(new ManyToOneSample1("mto1Json1"));	
		ManyToOneSample1 mto1Json2 = manyToOneSample1Repository.save(new ManyToOneSample1("mto1Json2"));	
		
		$.POST("/manyToOneSample2s").C(new ManyToOneSample2("~~~~")).is4xx();
		ManyToOneSample2 mto2Json1 = manyToOneSample2Repository.save(new ManyToOneSample2("mto2Json1"));		
		ManyToOneSample2 mto2Json2 = manyToOneSample2Repository.save(new ManyToOneSample2("mto2Json2"));		

		String mto3Link1 = $.POST("/manyToOneSample3s").C(new ManyToOneSample3("mto3Link1")).is2xx().andReturn().path();
		String mto3Link2 = $.POST("/manyToOneSample3s").C(new ManyToOneSample3("mto3Link2")).is2xx().andReturn().path();
		ManyToOneSample3 mto3Json1 = manyToOneSample3Repository.save(new ManyToOneSample3("mto3Json1"));		
		ManyToOneSample3 mto3Json2 = manyToOneSample3Repository.save(new ManyToOneSample3("mto3Json2"));		

		String mto4Link1 = $.POST("/manyToOneSample4s").C(new ManyToOneSample4("mto4Link1")).is2xx().andReturn().path();
		String mto4Link2 = $.POST("/manyToOneSample4s").C(new ManyToOneSample4("mto4Link2")).is2xx().andReturn().path();
		ManyToOneSample4 mto4Json1 = manyToOneSample4Repository.save(new ManyToOneSample4("mto4Json1"));		
		ManyToOneSample4 mto4Json2 = manyToOneSample4Repository.save(new ManyToOneSample4("mto4Json2"));		
		
		
		String mto5link1 = $.POST("/manyToOneSample5s").C(new ManyToOneSample5("mto5link1")).is2xx().andReturn().path();
		String mto5link2 = $.POST("/manyToOneSample5s").C(new ManyToOneSample5("mto5link2")).is2xx().andReturn().path();
		ManyToOneSample5 mto5Json1 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json1"));		
		ManyToOneSample5 mto5Json2 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json2"));		
		
		
		///////////////////////////////////////////////
		// Insert
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		
		c.put("foo1", mto1Link1);  //c.put("foo1", link); (O)   c.put("foo1", json); (X)  
		c.put("foo2", mto2Json1);  //c.put("foo2", link); (X)   c.put("foo2", json); (O)  
		c.put("foo3", mto3Json1);  //c.put("foo3", link); (X)   c.put("foo3", json); (O)  
		c.put("foo4", mto4Link1);  //c.put("foo4", link); (O)   c.put("foo4", json); (O)  
		c.put("foo5", mto5link1);  //c.put("foo5", link); (O)   c.put("foo5", json); (O)  
		
		
//		c.put("bar1", foo1uri);  //c.put("bar1", foo1uri); (O)   c.put("bar1", foo1json); (O)  
//		c.put("bar2", foo2json); //c.put("bar2", foo2uri); (X)   c.put("bar2", foo2json); (O)  
//		c.put("bar3", foo3uri);  //c.put("bar3", foo3uri); (O)   c.put("bar3", foo3json); (O)  
//		c.put("bar4", foo4uri);  //c.put("bar4", foo4uri); (O)   c.put("bar4", foo4json); (O)  

		$.POST("/domainSamples").C(c).is2xx("d1");

		////////////////////////////////////////////////////
		// Select
		////////////////////////////////////////////////////
		RestMvcResult r = $.GET("d1").is2xx().andReturn();

//		//foo1
//		Assert.assertTrue(r.path("_links.foo1.href").toString().endsWith("/foo1"));
//		$.get(r.path("_links.foo1.href").toString()).is2xx(); //foo1uri
//		$.get(r.path("_links.foo1.href").toString()).is4xx(); //foo1json

//		//foo2
//		Assert.assertFalse(StringUtils.isEmpty(r.path("foo2")));
//		
//		//foo3
//		Assert.assertFalse(StringUtils.isEmpty(r.path("foo3")));
//		
//		//foo4
//		Assert.assertFalse(StringUtils.isEmpty(r.path("foo4")));
		
		
		
		////////////////////////////////////////////////////
		// Update Patch 
		////////////////////////////////////////////////////
		Map<String, Object> u1 = new HashMap<String,Object>();
		u1.put("name", "XXXXXXXXXXXX");
		u1.put("age", 10);
		u1.put("foo1", mto1Link2);  //c.put("foo1", link); (O)   c.put("foo1", json); (X)  
		u1.put("foo2", mto2Json2);  //c.put("foo2", link); (X)   c.put("foo2", json); (X)  
		u1.put("foo3", mto3Link2);  //c.put("foo3", link); (X)   c.put("foo3", json); (X)  
		u1.put("foo4", mto4Link2);  //c.put("foo3", link); (O)   c.put("foo3", json); (X)  
		u1.put("foo5", mto5link2);  //c.put("foo5", link); (O)   c.put("foo5", json); (O)  
		
		$.PATCH("d1").C(u1).is2xx();
		r = $.GET("d1").is2xx().andReturn();
//		$.get(r.path("_links.foo1.href").toString()).is2xx();

		
//		////////////////////////////////////////////////////
//		// Update Put //Null Update
//		////////////////////////////////////////////////////
//		Map<String, Object> u2 = new HashMap<String,Object>();
//		u2.put("name", "XXXXXXXXXXXX");
//		u2.put("age", 10);
//		u2.put("foo1", mto1Link2);  //c.put("foo1", link); (X)   c.put("foo1", json); (X)  
//		u2.put("foo2", null);       //c.put("foo2", foo2uri); (X)   c.put("foo2", foo2json); (O)  
//		u2.put("foo3", null);   //c.put("foo3", foo3uri); (X)   c.put("foo3", foo3json); (O)  
//		u2.put("foo4", mto4Link2);  //c.put("foo3", link); (O)   c.put("foo3", foo4json); (O)  
//		
//		$.PUT("d1").C(u2).is2xx();
//		r = $.GET("d1").is2xx().andReturn();
////		$.get(r.path("_links.foo1.href").toString()).is2xx();
		
		
		////////////////////////////////////////////////////
		// Search EntityGraphics
		////////////////////////////////////////////////////
//		logger.info("-----------------------------");
//		logger.info("-----------------------------");
//		domainSampleRepository.findAll(PageRequest.of(0, 10));
//		logger.info("-----------------------------");
//		logger.info("-----------------------------");
//		domainSampleRepository.findAll(Sort.by("name"));
		
		
	}
}
