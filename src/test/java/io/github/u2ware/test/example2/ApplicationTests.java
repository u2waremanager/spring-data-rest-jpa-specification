package io.github.u2ware.test.example2;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.rest.webmvc.config.HibernateAddtionalConfiguration;
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
	
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new RestMockMvc(mvc, springDataRestBasePath);
	}

	protected @Autowired FooRepository fooRepository;
	protected @Autowired BarRepository barRepository;
	protected @Autowired FooStatement fooStatement;
	protected @Autowired HibernateAddtionalConfiguration AddedHibernateConfiguration;
	
	
//	protected @Autowired HibernateInterceptor hibernateInterceptor;
	
	private UUID f1 = UUID.randomUUID();
	private UUID f2 = UUID.randomUUID();
	private UUID f3 = UUID.randomUUID();
	private UUID f4 = UUID.randomUUID();
	private UUID f5 = UUID.randomUUID();
	
	@Test
	public void contextLoads() throws Exception {

		fooRepository.save(new Foo(f1, "a", 1));		
		fooRepository.save(new Foo(f2, "b", 2));		
		fooRepository.save(new Foo(f3, "c", 3));		

		barRepository.save(new Bar(f4, "a", 4));		
		barRepository.save(new Bar(f5, "b", 5));		
		

		
		logger.info(fooRepository.findAll((root, query, builder) -> {
			return PredicateBuilder.of(root, query, builder).where().and().eq("name", "a").build();
		}));
		

	}
	
	
}
