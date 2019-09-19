package io.github.u2ware.test.example1;

import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.query.JpaSpecification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	private @Autowired FooRepository fooRepository; 
	private @Autowired BarRepository barRepository; 
	private @Autowired BazRepository bazRepository; 
	
	
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

		bazRepository.save(new Baz("a", 1));		
		bazRepository.save(new Baz("a", 2));		
		bazRepository.save(new Baz("b", 1));		
		bazRepository.save(new Baz("b", 2));		
	}
	
	//@Test
	public void contextLoads2() throws Exception {
		
	}
	
	@Test
	public void contextLoads() throws Exception {

		//////////////////////////////////////////////////////////////////////////
		// JpaSpecification.of(final String queryMethod, final Object parameters)
		///////////////////////////////////////////////////////////////////////////
		MultiValueMap<String,Object> params1 = new  LinkedMultiValueMap<>();
		params1.add("name", "a");
		params1.add("age", 1);
		
		MultiValueMap<String,Object> params2 = new  LinkedMultiValueMap<>();
		params2.add("name", "b");
		params2.add("age", 2);
		
		MultiValueMap<String,Object> params3 = new  LinkedMultiValueMap<>();
		params3.add("name", "a");
		
		MultiValueMap<String,Object> params4 = new  LinkedMultiValueMap<>();
		params4.add("age", 1);
		
		logger.info("--------------------------------------------------------------");
		Assert.assertEquals(1, fooRepository.findAll(JpaSpecification.of(params1, "findByNameAndAge")).size());
		Assert.assertEquals(1, fooRepository.findAll(JpaSpecification.of(params2, "findByNameAndAge")).size());
		Assert.assertEquals(2, fooRepository.findAll(JpaSpecification.of(params3, "findByNameAndAge")).size());
		Assert.assertEquals(2, fooRepository.findAll(JpaSpecification.of(params4, "findByNameAndAge")).size());
		logger.info("--------------------------------------------------------------");
		try {
			fooRepository.findAll(JpaSpecification.of(params1, "findByNameAndXXXX"));
		}catch(Exception e) {
			//e.printStackTrace();
			Assert.assertEquals(PropertyReferenceException.class, e.getClass());
		}
		logger.info("--------------------------------------------------------------");
		Assert.assertEquals(3, fooRepository.findAll(JpaSpecification.of(params1, "findByNameOrAge")).size());

		
		//////////////////////////////////////////////////////////////////////////
		// 
		///////////////////////////////////////////////////////////////////////////
		$.GET("/foos/!q").is2xx(sizeMatch(4));
		$.GET("/foos/!q").P("name", "a").is2xx(sizeMatch(4));
		$.GET("/foos/!q/findByNameAndAge").P("name", "a").is2xx(sizeMatch(2));
		$.GET("/foos/!q/findByNameAndAge").P("name", "a").P("age","1").is2xx(sizeMatch(1));
		$.GET("/foos/!q/findByNameOrAge").P("name", "a").is2xx(sizeMatch(2));
		$.GET("/foos/!q/findByNameOrAge").P("name", "a").P("age","1").is2xx(sizeMatch(3));
		
		$.GET("/bars/!q").is2xx(sizeMatch(4));
		$.GET("/bars/!q").P("name", "a").is2xx(sizeMatch(2));
		$.GET("/bars/!q/"+BarSpecification.class.getName()+"XXXX").is4xx();
		$.GET("/bars/!q/"+BarSpecification.class.getName()).P("name", "a").is2xx(sizeMatch(1));
		$.GET("/bars/!q").P("!q", BarSpecification.class.getName()).P("name", "a").is2xx(sizeMatch(1));
		
		
		$.GET("/bazes/!q").is2xx(sizeMatch(4));
		$.GET("/bazes/!q").P("name", "a").is2xx(sizeMatch(2));
		$.GET("/bazes/!q").P("name", "a").P("age", "1").is2xx(sizeMatch(1));
		
		
		//////////////////////////////////////////////////////////////////////////
		// 
		///////////////////////////////////////////////////////////////////////////
		$.POST("/foos").C(params1.toSingleValueMap()).is2xx();
		$.POST("/bars").C(params1.toSingleValueMap()).is2xx();
		$.POST("/bazes").C(params1.toSingleValueMap()).is2xx();
		
		
	}
}
