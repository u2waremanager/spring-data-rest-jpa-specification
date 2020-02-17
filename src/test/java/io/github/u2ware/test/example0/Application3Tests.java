package io.github.u2ware.test.example0;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.SpecificationBuilder;
import org.springframework.data.jpa.repository.query.PredicateQueryBuilder;
import org.springframework.data.jpa.repository.support.PredicateBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.core.BooleanBuilder;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application3Tests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	private @Autowired FooRepository fooRepository; 
	
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);

		if(fooRepository.count() > 0) return;
		
		fooRepository.save(new Foo("a", 1, "1"));		
		fooRepository.save(new Foo("b", 2, "1"));		
		fooRepository.save(new Foo("c", 2, "1"));		
		fooRepository.save(new Foo("d", 1, "2"));		
		fooRepository.save(new Foo("e", 2, "2"));		
		fooRepository.save(new Foo("f", 3, "2"));		
	}

	@Test
	public void contextLoads() throws Exception {
		
//		BooleanBuilder p = new BooleanBuilder();
//		p.and(PredicateBuilder.of(Foo.class).where().and().eq("name", "a").build())
//		//;p
//		.and(PredicateBuilder.of(Foo.class).where().and().eq("age", 1).build());
//		fooRepository.findAll(p);
//		
		
		SpecificationBuilder<Foo> s = new SpecificationBuilder<>();
		s.and((r,q,b)->{return PredicateQueryBuilder.of(r, q, b).where().and().eq("name", "a").build();})
//		;s
		.and((r,q,b)->{return PredicateQueryBuilder.of(r, q, b).where().and().eq("age", 1).build();});
		fooRepository.findAll(s);
	}
	
	
}
