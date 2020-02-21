package io.github.u2ware.test.example50;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.u2ware.test.RestMockMvc;
import io.github.u2ware.test.RestMockMvc.RestMvcResult;
import io.github.u2ware.test.example50.FooRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected RestMockMvc $;
	
	private @Autowired OneToManySample1Repository manyToOnePhysicalColumn1Repository;
	private @Autowired OneToManySample2Repository manyToOnePhysicalColumn2Repository;
	private @Autowired OneToManySample3Repository manyToOnePhysicalColumn3Repository;
	private @Autowired OneToManySample4Repository manyToOnePhysicalColumn4Repository;
	
	private @Autowired DomainSampleRepository domainSampleRepository;
	private @Autowired FooRepository fooRepository;
	
	private @Autowired ObjectMapper objectMapper;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}
	
	@Test
	public void contextLoads() throws Exception {

		String foo1uri = $.POST("/manyToOnePhysicalColumn1s").C(new OneToManySample1("a", 1)).is2xx().andReturn().path();		
		OneToManySample1 foo1json = manyToOnePhysicalColumn1Repository.save(new OneToManySample1("b", 2));	
		
		$.POST("/manyToOnePhysicalColumn2s").C(new OneToManySample2("c", 3)).is4xx();
		OneToManySample2 foo2json = manyToOnePhysicalColumn2Repository.save(new OneToManySample2("d", 4));		

		String foo3uri = $.POST("/manyToOnePhysicalColumn3s").C(new OneToManySample3("e", 5)).is2xx().andReturn().path();
		OneToManySample3 foo3json = manyToOnePhysicalColumn3Repository.save(new OneToManySample3("f", 6));		

		String foo4uri = $.POST("/manyToOnePhysicalColumn4s").C(new OneToManySample4("e", 5)).is2xx().andReturn().path();
		OneToManySample4 foo4json = manyToOnePhysicalColumn4Repository.save(new OneToManySample4("f", 6));		
		
		
//		ManyToOnePhysicalColumn4 e31 = manyToOnePhysicalColumn3Repository.save(new ManyToOnePhysicalColumn4("e", 5));		
//		ManyToOnePhysicalColumn4 e32 = manyToOnePhysicalColumn3Repository.save(new ManyToOnePhysicalColumn4("f", 6));		
//
//		String e41 = $.POST("/foos").C(new Foo("i", 9)).is2xx().andReturn().path();		
//		Foo e42 = fooRepository.save(new Foo("j", 0));		
		
		///////////////////////////////////////////////
		//
		///////////////////////////////////////////////
		Map<String, Object> c = new HashMap<String,Object>();
		
		c.put("foo1", foo1uri);  //c.put("foo1", foo1uri); (O)   c.put("foo1", foo1json); (X)  
		c.put("foo2", foo2json); //c.put("foo2", foo2uri); (X)   c.put("foo2", foo2json); (O)  
		c.put("foo3", foo3json); //c.put("foo3", foo3uri); (X)   c.put("foo3", foo3json); (O)  
		c.put("foo4", foo4uri); //c.put("foo3", foo4uri); (O)   c.put("foo3", foo4json); (O)  
		
		
//		c.put("foo21", e11);
//		c.put("foo22", e21);
//		c.put("foo23", e31);
//		c.put("foo24", e41);
		
//		c.put("foo21", e12);
//		c.put("foo22", e22);
//		c.put("foo23", e32);
//		c.put("foo24", e42);
//		
//		Map<String, Object> foo11 = new HashMap<String,Object>();
//		foo11.put("seq", e12.getSeq());
//		node.put("age", 6);
//		c.put("foo", UriLinkParser.resolveUuid(uri1));
//		
//		c.put("foo31", Arrays.asList(uri1, uri2));
//		c.put("foo32", Arrays.asList(uri1, uri2));
//		
//		c.put("foo41", Arrays.asList(uri1, uri2));
//		c.put("foo42", Arrays.asList(uri1, uri2));
//		
//		c.put("foo51", Arrays.asList(uri1, uri2));
//		c.put("foo52", Arrays.asList(uri1, uri2));
//
//		c.put("foo61", Arrays.asList(uri1, uri2));
//		c.put("foo62", Arrays.asList(uri1, uri2));
//
//		
//		Map<String, Object> child = new HashMap<String,Object>();
//		child.put("stringValue", "string");
//		child.put("integerValue", 4);
//		c.put("childs", Arrays.asList(child, child));
		
//		Map<String, Object> node = new HashMap<String,Object>();
//		node.put("name", "X");
//		node.put("age", 6);
		
		
//		c.put("foo01", node);  //      node
//		c.put("foo02", node); //       node
//		c.put("foo03", uri1.replaceAll("localhost", "a.com"));  //uri , node
//		c.put("foo04", uri1);  //uri   node
//		c.put("foo05", Arrays.asList(uri1, uri2));  //uri   node
//		c.put("foo06", Arrays.asList(uri1, uri2));  //uri   node
		c.put("name", "John");
		c.put("age", 10);
		

		$.POST("/domainSamples").C(c).is2xx("d1");
//		$.POST("/domainSamples").C(c).is2xx("d2");

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
		
		
		
		
//		////////////////////////////////////////////////////
//		//
//		////////////////////////////////////////////////////
//		DomainSample d = objectMapper.readValue(r.body(), DomainSample.class);
//		logger.info(d);
//		d.setFoo12(null);
//		$.PATCH("d1").C(d).is2xx();
//		$.GET("/domainSamples").is2xx();
//		$.GET("/domainSamples").H("query","true").C().is2xx();
//		$.GET("/domainSamples").H("query","true").C("name", "John").P("unpaged", "true").is2xx();

	}
	private @PersistenceContext EntityManager em;
}
