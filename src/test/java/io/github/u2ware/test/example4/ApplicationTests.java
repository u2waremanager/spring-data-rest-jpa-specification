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
	
	private @Autowired ManyToOnePhysicalColumn1Repository manyToOnePhysicalColumn1Repository;
	private @Autowired ManyToOnePhysicalColumn2Repository manyToOnePhysicalColumn2Repository;
	private @Autowired ManyToOnePhysicalColumn3Repository manyToOnePhysicalColumn3Repository;
	private @Autowired ManyToOnePhysicalColumn4Repository manyToOnePhysicalColumn4Repository;
	private @Autowired DomainSampleRepository domainSampleRepository;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		String foo1uri = $.POST("/manyToOnePhysicalColumn1s").C(new ManyToOnePhysicalColumn1("a", 1)).is2xx().andReturn().path();		
		ManyToOnePhysicalColumn1 foo1json = manyToOnePhysicalColumn1Repository.save(new ManyToOnePhysicalColumn1("b", 2));	
		
		$.POST("/manyToOnePhysicalColumn2s").C(new ManyToOnePhysicalColumn2("c", 3)).is4xx();
		ManyToOnePhysicalColumn2 foo2json = manyToOnePhysicalColumn2Repository.save(new ManyToOnePhysicalColumn2("d", 4));		

		String foo3uri = $.POST("/manyToOnePhysicalColumn3s").C(new ManyToOnePhysicalColumn3("e", 5)).is2xx().andReturn().path();
		ManyToOnePhysicalColumn3 foo3json = manyToOnePhysicalColumn3Repository.save(new ManyToOnePhysicalColumn3("f", 6));		

		String foo4uri = $.POST("/manyToOnePhysicalColumn4s").C(new ManyToOnePhysicalColumn4("e", 5)).is2xx().andReturn().path();
		ManyToOnePhysicalColumn4 foo4json = manyToOnePhysicalColumn4Repository.save(new ManyToOnePhysicalColumn4("f", 6));		
		
		///////////////////////////////////////////////
		//
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		
		c.put("foo1", foo1uri);  //c.put("foo1", foo1uri); (O)   c.put("foo1", foo1json); (X)  
		c.put("foo2", foo2json); //c.put("foo2", foo2uri); (X)   c.put("foo2", foo2json); (O)  
		c.put("foo3", foo3json); //c.put("foo3", foo3uri); (X)   c.put("foo3", foo3json); (O)  
		c.put("foo4", foo4uri);  //c.put("foo3", foo4uri); (O)   c.put("foo3", foo4json); (O)  
		
		
		c.put("bar1", foo1uri);  //c.put("bar1", foo1uri); (O)   c.put("bar1", foo1json); (O)  
		c.put("bar2", foo2json); //c.put("bar2", foo2uri); (X)   c.put("bar2", foo2json); (O)  
		c.put("bar3", foo3uri);  //c.put("bar3", foo3uri); (O)   c.put("bar3", foo3json); (O)  
		c.put("bar4", foo4uri);  //c.put("bar4", foo4uri); (O)   c.put("bar4", foo4json); (O)  

		$.POST("/domainSamples").C(c).is2xx("d1");
		$.POST("/domainSamples").C(c).is2xx("d2");

		////////////////////////////////////////////////////
		//
		////////////////////////////////////////////////////
		RestMvcResult r = $.GET("d1").is2xx().andReturn();

		//foo1
		Assert.assertTrue(r.path("_links.foo1.href").toString().endsWith("/foo1"));
//		$.get(r.path("_links.foo1.href").toString()).is2xx(); //foo1uri
//		$.get(r.path("_links.foo1.href").toString()).is4xx(); //foo1json

		//foo2
		Assert.assertFalse(StringUtils.isEmpty(r.path("foo2")));
		
		//foo3
		Assert.assertFalse(StringUtils.isEmpty(r.path("foo3")));
		
		//foo4
		Assert.assertFalse(StringUtils.isEmpty(r.path("foo4")));
		
		
		
		////////////////////////////////////////////////////
		//
		////////////////////////////////////////////////////
//		Map<String, Object> u = new HashMap<String,Object>();
//		u.put("name", "XXXXXXXXXXXX");
//		u.put("age", 10);
//		u.put("foo1", null);  //c.put("foo1", foo1uri); (O)   c.put("foo1", foo1json); (X)  
//		u.put("foo2", null); //c.put("foo2", foo2uri); (X)   c.put("foo2", foo2json); (O)  
//		u.put("foo3", foo3json); //c.put("foo3", foo3uri); (X)   c.put("foo3", foo3json); (O)  
//		u.put("foo4", foo4uri);  //c.put("foo3", foo4uri); (O)   c.put("foo3", foo4json); (O)  
//		
//		$.PATCH("d1").C(u).is2xx();
//		$.PUT("d1").C(u).is2xx();
//		$.GET("d1").is2xx();
		
		
		////////////////////////////////////////////////////
		//
		////////////////////////////////////////////////////
		logger.info("-----------------------------");
		logger.info("-----------------------------");
		domainSampleRepository.findAll(PageRequest.of(0, 10));
		logger.info("-----------------------------");
		logger.info("-----------------------------");
		domainSampleRepository.findAll(Sort.by("name"));
		
		
	}
}
