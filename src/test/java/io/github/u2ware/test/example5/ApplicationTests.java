package io.github.u2ware.test.example5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import io.github.u2ware.test.example5.DomainSample.OneToManySample5;

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

		String otm1Link1 = $.POST("/oneToManySample1s").C(new OneToManySample1("otm1Link1")).is2xx().andReturn().link();		
		String otm1Link2 = $.POST("/oneToManySample1s").C(new OneToManySample1("otm1Link2")).is2xx().andReturn().link();		
		String otm1Link3 = $.POST("/oneToManySample1s").C(new OneToManySample1("otm1Link3")).is2xx().andReturn().link();		
		OneToManySample1 otm1Json1 = oneToManySample1Repository.save(new OneToManySample1("otm1Json1"));	
		OneToManySample1 otm1Json2 = oneToManySample1Repository.save(new OneToManySample1("otm1Json2"));	
		OneToManySample1 otm1Json3 = oneToManySample1Repository.save(new OneToManySample1("otm1Json3"));	
		
		String otm2Link1 = $.POST("/oneToManySample2s").C(new OneToManySample1("otm2Link1")).is2xx().andReturn().link();		
		String otm2Link2 = $.POST("/oneToManySample2s").C(new OneToManySample1("otm2Link2")).is2xx().andReturn().link();		
		String otm2Link3 = $.POST("/oneToManySample2s").C(new OneToManySample1("otm2Link3")).is2xx().andReturn().link();
		OneToManySample2 otm2Json1 = oneToManySample2Repository.save(new OneToManySample2("otm2Json1"));		
		OneToManySample2 otm2Json2 = oneToManySample2Repository.save(new OneToManySample2("otm2Json2"));	
		OneToManySample2 otm2Json3 = oneToManySample2Repository.save(new OneToManySample2("otm2Json3"));	
		
		String otm3Link1 = $.POST("/oneToManySample3s").C(new OneToManySample3("otm3Link1")).is2xx().andReturn().link();		
		String otm3Link2 = $.POST("/oneToManySample3s").C(new OneToManySample3("otm3Link2")).is2xx().andReturn().link();		
		String otm3Link3 = $.POST("/oneToManySample3s").C(new OneToManySample3("otm3Link3")).is2xx().andReturn().link();		
		OneToManySample3 otm3Json1 = oneToManySample3Repository.save(new OneToManySample3("otm3Json1"));	
		OneToManySample3 otm3Json2 = oneToManySample3Repository.save(new OneToManySample3("otm3Json2"));	
		OneToManySample3 otm3Json3 = oneToManySample3Repository.save(new OneToManySample3("otm3Json3"));	
		
		String otm4Link1 = $.POST("/oneToManySample4s").C(new OneToManySample4("otm4Link1")).is2xx().andReturn().link();		
		String otm4Link2 = $.POST("/oneToManySample4s").C(new OneToManySample4("otm4Link2")).is2xx().andReturn().link();		
		String otm4Link3 = $.POST("/oneToManySample4s").C(new OneToManySample4("otm4Link3")).is2xx().andReturn().link();		
		OneToManySample4 otm4Json1 = oneToManySample4Repository.save(new OneToManySample4("otm4Json1"));	
		OneToManySample4 otm4Json2 = oneToManySample4Repository.save(new OneToManySample4("otm4Json2"));	
		OneToManySample4 otm4Json3 = oneToManySample4Repository.save(new OneToManySample4("otm4Json3"));	
		
				
		
		///////////////////////////////////////////////
		// POST
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
//		c.put("sample1", Arrays.asList(otm1Link1, otm1Json1));  //link(X-> O, EntityViewDeserializer) json(O)
		c.put("sample2", Arrays.asList(otm2Link1, otm2Json1));  //link(O) json(O)
//		c.put("sample3", Arrays.asList(otm3Link1,otm3Json1));  //link(X-> O, EntityViewDeserializer) json(O)
//		c.put("sample4", Arrays.asList(otm4Link1, otm4Json1));  //link(O) json(O)
		c.put("sample5", Arrays.asList(new OneToManySample5("otm5Json1"), new OneToManySample5("otm5Json2")));  //link(X) json(O)

		
		$.POST("/domainSamples").C(c).is2xx().andReturn("uri");
		$.GET("{uri}").is2xx().andReturn("post");
		//$.GET("{post._links.sample1.href}").is2xx();
		
		
		////////////////////////////////////////////////////
		// PATCH
		////////////////////////////////////////////////////
		Map<String, Object> u1 = new HashMap<String,Object>();
		u1.put("name", "PATCH");
		u1.put("age", 10);
//		u1.put("sample1", Arrays.asList(otm1Link2, otm1Link3)); //link(X-> O, EntityViewDeserializer) json(X) null(O)
//		u1.put("sample2", Arrays.asList(otm2Link2, otm2Json2)); //link(O) json(O) null(O)
//		u1.put("sample3",  Arrays.asList(otm3Link2, otm3Link3)); //link(O -> O, EntityViewDeserializer) json(X) null(O)
//		u1.put("sample4", Arrays.asList(otm4Link2, otm4Json2)); //link(O) json(O) null(O)
//		u1.put("sample5", Arrays.asList(new OneToManySample5("otm5Json3")));  //link(X) json(O)

		$.PATCH("{uri}").C(u1).is2xx();
		$.GET("{uri}").is2xx().andReturn("patch");

		////////////////////////////////////////////////////
		// PUT // -> Null Update
		////////////////////////////////////////////////////
		Map<String, Object> u2 = new HashMap<String,Object>();
		u2.put("name", "PUT");
		u2.put("age", 10);
//		u2.put("sample1", null);                                  //link(X) json(X) null(O)
//		u2.put("sample2", Arrays.asList(otm2Link3, otm2Json3));   //link(O) json(O) null(O)
//		u1.put("sample3",  Arrays.asList(otm3Link2, otm3Link3));  //link(X) json(X) null(O)
//		u2.put("sample4", Arrays.asList(otm4Link3, otm4Json3));   //link(O) json(O) null(O)
//		u2.put("sample5", Arrays.asList(new OneToManySample5("otm5Json3"), new OneToManySample5("otm5Json4"), new OneToManySample5("otm5Json6")));  //link(X) json(O)

//		$.PUT("{uri}").C(u2).is2xx();
//		$.GET("{uri}").is2xx().andReturn("put");
		

		////////////////////////////////////////////////////
		// DELETE
		////////////////////////////////////////////////////
//		$.DELETE("{uri}").is2xx();
		
		
		////////////////////////////////////////////////////
		// Search EntityGraphics
		////////////////////////////////////////////////////
//		$.GET("/domainSamples").is2xx();
//		$.GET("/domainSamples").H("query","true").C("names", Arrays.asList("PUT","GET")).is2xx();
//		$.GET("/domainSamples").H("query","true").C("_sample4", Arrays.asList(otm4Link3)).is2xx();
	}
	
	@PersistenceContext
	private EntityManager em;
}
