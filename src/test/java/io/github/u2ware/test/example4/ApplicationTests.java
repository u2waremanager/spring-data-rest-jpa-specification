package io.github.u2ware.test.example4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
	
	protected @Autowired ManyToOneSample1Repository manyToOneSample1Repository;
	protected @Autowired ManyToOneSample2Repository manyToOneSample2Repository;
	protected @Autowired ManyToOneSample3Repository manyToOneSample3Repository;
	protected @Autowired ManyToOneSample4Repository manyToOneSample4Repository;
	protected @Autowired ManyToOneSample5Repository manyToOneSample5Repository;
	protected @Autowired ManyToOneSample6Repository manyToOneSample6Repository;
	
	protected @Autowired DomainSampleRepository domainSampleRepository;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		String mto1Link1 = $.POST("/manyToOneSample1s").C(new ManyToOneSample1("mto1Link1")).is2xx().andReturn().link();		
		String mto1Link2 = $.POST("/manyToOneSample1s").C(new ManyToOneSample1("mto1Link2")).is2xx().andReturn().link();		
		ManyToOneSample1 mto1Json1 = manyToOneSample1Repository.save(new ManyToOneSample1("mto1Json1"));	
		ManyToOneSample1 mto1Json2 = manyToOneSample1Repository.save(new ManyToOneSample1("mto1Json2"));	
		
		$.POST("/manyToOneSample2s").C(new ManyToOneSample2("~~~~")).is4xx();
		ManyToOneSample2 mto2Json1 = manyToOneSample2Repository.save(new ManyToOneSample2("mto2Json1"));		
		ManyToOneSample2 mto2Json2 = manyToOneSample2Repository.save(new ManyToOneSample2("mto2Json2"));		

		String mto3Link1 = $.POST("/manyToOneSample3s").C(new ManyToOneSample3("mto3Link1")).is2xx().andReturn().link();
		String mto3Link2 = $.POST("/manyToOneSample3s").C(new ManyToOneSample3("mto3Link2")).is2xx().andReturn().link();
		ManyToOneSample3 mto3Json1 = manyToOneSample3Repository.save(new ManyToOneSample3("mto3Json1"));		
		ManyToOneSample3 mto3Json2 = manyToOneSample3Repository.save(new ManyToOneSample3("mto3Json2"));		

		String mto4Link1 = $.POST("/manyToOneSample4s").C(new ManyToOneSample4("mto4Link1")).is2xx().andReturn().link();
		String mto4Link2 = $.POST("/manyToOneSample4s").C(new ManyToOneSample4("mto4Link2")).is2xx().andReturn().link();
		ManyToOneSample4 mto4Json1 = manyToOneSample4Repository.save(new ManyToOneSample4("mto4Json1"));		
		ManyToOneSample4 mto4Json2 = manyToOneSample4Repository.save(new ManyToOneSample4("mto4Json2"));		
		
		
		String mto5link1 = $.POST("/manyToOneSample5s").C(new ManyToOneSample5("mto5link1")).is2xx().andReturn().link();
		String mto5Link2 = $.POST("/manyToOneSample5s").C(new ManyToOneSample5("mto5link2")).is2xx().andReturn().link();
		ManyToOneSample5 mto5Json1 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json1"));		
		ManyToOneSample5 mto5Json2 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json2"));		
		
		String mto6link1 = $.POST("/manyToOneSample6s").C(new ManyToOneSample6("mto6link1")).is2xx().andReturn().link();
		String mto6Link2 = $.POST("/manyToOneSample6s").C(new ManyToOneSample6("mto6link2")).is2xx().andReturn().link();
		ManyToOneSample6 mto6Json1 = manyToOneSample6Repository.save(new ManyToOneSample6("mto6Json1"));		
		ManyToOneSample6 mto6Json2 = manyToOneSample6Repository.save(new ManyToOneSample6("mto6Json2"));		
		
		///////////////////////////////////////////////
		// POST
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);

		c.put("sample1", mto1Link1);  //link(O) json(X)  
		c.put("sample2", mto2Json1);  //link(X) json(O)  
		c.put("sample3", mto3Json1);  //link(X) json(O)  
		c.put("sample4", mto4Link1);  //link(O) json(O)  
		c.put("sample5", mto5link1);  //link(O) json(O)  
		c.put("sample6", mto6Json1);  //link(O) json(O)  
		

		$.POST("/domainSamples").C(c).is2xx().andReturn("uri");
		$.GET("{uri}").is2xx().andReturn("post");
		$.GET("{post._links.sample1.href}").is2xx();
		
		////////////////////////////////////////////////////
		// PATCH
		////////////////////////////////////////////////////
		Map<String, Object> u1 = new HashMap<String,Object>();
		u1.put("name", "PATCH");
		u1.put("age", 10);
		
		u1.put("sample1", mto1Link2);  //link(O)  json(X) null(O)
		u1.put("sample2", null);       //link(X)  json(X) null(O)
		u1.put("sample3", null);       //link(X)  json(X) null(O) 
		u1.put("sample4", mto4Link2);  //link(O)  json(X) null(O) 
		u1.put("sample5", mto5Link2);  //link(O)  json(X) null(O) 
		u1.put("sample6", mto6Json2);  //link(O)  json(O) null(O) 

		$.PATCH("{uri}").C(u1).is2xx();
		$.GET("{uri}").is2xx().andReturn("patch");
		$.GET("{patch._links.sample1.href}").is2xx();

		
		////////////////////////////////////////////////////
		// PUT // -> Null Update
		////////////////////////////////////////////////////
		Map<String, Object> u2 = new HashMap<String,Object>();
		u2.put("name", "PUT");
		u2.put("age", 10);
		u2.put("sample1", null);       //link(X) json(X) null(X)
		u2.put("sample2", mto2Json2);  //link(X) json(O) null(O) 
		u2.put("sample3", mto3Json2);  //link(X) json(O) null(O) 
		u2.put("sample4", mto4Link2);  //link(O) json(O) null(O) 
		u2.put("sample5", mto5Link2);  //link(O) json(O) null(O) 
		u2.put("sample6", mto6Json1);  //link(O) json(O) null(O) 

		$.PUT("{uri}").C(u2).is2xx();
		$.GET("{uri}").is2xx().andReturn("put");
		$.GET("{put._links.sample1.href}").is2xx();
		
		
		////////////////////////////////////////////////////
		// Search EntityGraphics
		////////////////////////////////////////////////////
		$.GET("/domainSamples").H("query","true").C("sample4", mto4Link2).is2xx().andExpect(1);
		$.GET("/domainSamples").H("query","true").C("sample4", mto4Link1).is2xx().andExpect(0);
		$.GET("/domainSamples").H("query","true").C("sample4", mto4Json1).is2xx().andExpect(0);
		$.GET("/domainSamples").H("query","true").C("sample3Names", Arrays.asList("mto3Json2")).is2xx().andExpect(1);
		$.GET("/domainSamples").H("query","true").C("sample3Name", "mto3Json2").is2xx().andExpect(1);
		$.GET("/domainSamples").H("query","true").C("sample3Name", "mto3Json2ttt").is2xx().andExpect(0);

		
		////////////////////////////////////////////////////
		// DELETE
		////////////////////////////////////////////////////
		$.DELETE("{uri}").is2xx();
		
		
		
	}
}
