package io.github.u2ware.test.example4;

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
import io.github.u2ware.test.RestMockMvc.RestMvcResult;
import io.github.u2ware.test.RestMockMvc.RestResultActions;

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
		String mto5Link2 = $.POST("/manyToOneSample5s").C(new ManyToOneSample5("mto5link2")).is2xx().andReturn().path();
		ManyToOneSample5 mto5Json1 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json1"));		
		ManyToOneSample5 mto5Json2 = manyToOneSample5Repository.save(new ManyToOneSample5("mto5Json2"));		
		
		
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
		

		$.POST("/domainSamples").C(c).is2xx("uri");
		RestMvcResult post = $.GET("uri").is2xx().andReturn();
		$.get(post.path("_links.sample1.href").toString()).is2xx();
		
		////////////////////////////////////////////////////
		// PATCH
		////////////////////////////////////////////////////
		Map<String, Object> u1 = new HashMap<String,Object>();
		u1.put("name", "PATCH");
		u1.put("age", 10);
		
		u1.put("sample1", mto1Link2);  //link(O)  json(X) null(O)
//		u1.put("sample2", null);       //link(X)  json(X) null(O)
//		u1.put("sample3", null);       //link(X)  json(X) null(O) 
		u1.put("sample4", mto4Link2);  //link(O)  json(X) null(O) 
		u1.put("sample5", mto5Link2);  //link(O)  json(X) null(O) 

		$.PATCH("uri").C(u1).is2xx();
		RestMvcResult patch = $.GET("uri").is2xx().andReturn();
		$.get(patch.path("_links.sample1.href").toString()).is2xx();

		
		////////////////////////////////////////////////////
		// PUT // -> Null Update
		////////////////////////////////////////////////////
		Map<String, Object> u2 = new HashMap<String,Object>();
		u2.put("name", "PUT");
		u2.put("age", 10);
//		u2.put("sample1", null);       //link(X) json(X) null(X)
		u2.put("sample2", mto2Json2);  //link(X) json(O) null(O) 
		u2.put("sample3", mto3Json2);  //link(X) json(O) null(O) 
		u2.put("sample4", mto4Link2);  //link(O) json(O) null(O) 
		u2.put("sample5", mto5Link2);  //link(O) json(O) null(O) 

		$.PUT("uri").C(u2).is2xx();
		RestMvcResult put = $.GET("uri").is2xx().andReturn();
		$.get(put.path("_links.sample1.href").toString()).is2xx();
		
		
		////////////////////////////////////////////////////
		// Search EntityGraphics
		////////////////////////////////////////////////////
		$.GET("/domainSamples").H("query","true").C("sample4", mto4Link2).is2xx(RestResultActions.sizeMatch(1));
		$.GET("/domainSamples").H("query","true").C("sample3Name", "mto3Json2").is2xx(RestResultActions.sizeMatch(1));
	}
}
