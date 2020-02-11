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
import org.springframework.data.rest.webmvc.support.UriToEntityParser;
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
	private @Autowired BarRepository barRepository; 
	
//	private @Autowired UriToEntityConverter uriToEntityConverter;
	
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);
		
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


		String uri1 = $.GET("/foos").H("read","specification").H("partTree", "findByNameAndAge").C("name", "a").C("age","1").is2xx().andReturn().path("_embedded.foos[0]._links.self.href");
		logger.info(uri1);

		String uri2 = $.GET("/foos").H("read","specification").H("partTree", "findByNameAndAge").C("name", "b").C("age","2").is2xx().andReturn().path("_embedded.foos[0]._links.self.href");
		logger.info(uri2);
		
		
		
		
		Foo foo1 = UriToEntityParser.resolveEntity(uri1.replaceAll("localhost", "a.com"), Foo.class);
		logger.info(foo1);
		
		FooView foo2 = UriToEntityParser.resolveEntity(uri1, FooView.class);
		logger.info(foo2);
		
//		
//		logger.info(uriToEntityConverter);
//		logger.info(uriToEntityConverter);
//		logger.info(uriToEntityConverter);
//		
		
		
		
		
		
		
		
		
		
		
		
		
		
		Map<String, Object> node = new HashMap<String,Object>();
		node.put("name", "X");
		node.put("age", 6);
		
		
//		
//		
		Map<String, Object> c = new HashMap<String,Object>();
		c.put("name", "John");
		c.put("age", 10);
		c.put("foo01", node);  //      node
		c.put("foo02", node); //       node
		c.put("foo03", uri1.replaceAll("localhost", "a.com"));  //uri , node
		c.put("foo04", uri1);  //uri   node
		c.put("foo05", Arrays.asList(uri1, uri2));  //uri   node
		c.put("foo06", Arrays.asList(uri1, uri2));  //uri   node
//		
//		c.put("foo11", uri1);
//		c.put("foo12", uri1);
//		c.put("foo13", uri2);
//		c.put("foo14", uri2);
//		
//		c.put("foo21", uri1);
//		c.put("foo22", uri1);
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
//		
		$.POST("/bars").H("read","specification").C(c).is2xx("bar");
//
//		$.GET("bar").H("read","specification").is2xx();
	}
}
