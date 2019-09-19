package io.github.u2ware.test.example0;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.query.PartTreePredicateBuilder;
import org.springframework.data.jpa.repository.query.PartTreeSpecification;
import org.springframework.data.jpa.repository.query.PredicateBuilder;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	private @Autowired FooRepository repository; 
	
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);

		if(repository.count() > 0) return;
		
		repository.save(new Foo("a", 1, "1"));		
		repository.save(new Foo("b", 2, "1"));		
		repository.save(new Foo("c", 2, "1"));		
		repository.save(new Foo("d", 1, "2"));		
		repository.save(new Foo("e", 2, "2"));		
		repository.save(new Foo("f", 3, "2"));		
	}

//	@Test
	public void criteriaBuilderTests() throws Exception{

		List<Foo> foos1 = repository.findAll((root, query, builder)->{

			logger.info(builder.getClass());
			logger.info(builder.getClass());
			logger.info(builder.getClass());
			
//			CriteriaBuilderImpl f;
//			f.equal(x, y);
//			
			
			Expression<?> title = PartTreePredicateBuilder.toExpressionRecursively(root, "title");
			Expression<?> name = PartTreePredicateBuilder.toExpressionRecursively(root, "name");
			Expression<?> age = PartTreePredicateBuilder.toExpressionRecursively(root, "age");
			
			
			Predicate p1 = builder.equal(title, "1");
			Predicate p2 = builder.equal(name, "a");
			Predicate p3 = builder.equal(age, "2");

//			Predicate r1 = builder.and( p1, builder.or(  p2, p3 ) ); //->2
//			Predicate r2 = builder.or( p1, builder.and( p2, p3 ) ) ; //->3
			
			Predicate r3 = builder.and(  p2, p3 );
			r3 = builder.and( p1, r3 ); //->2
			return r3;
			
//			return age.in(new String[] {"1", "2"});
		});
		logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(foos1));

	}
	
	
//	@Test
	public void partTreePredicateBuilderTest() throws Exception{
		
		repository.findAll((root, query, builder)->{
			Part part = new Part("NameNot", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, "b");
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("nameIsLike", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, new String[] {"avvv","bvvv"});
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("nameIsContainingIgnoreCase", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, "d");
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("age", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, "123");
		});
		
		repository.findAll((root, query, builder)->{
			Part part = new Part("ageGreaterThan", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, "a");
		});
		
		repository.findAll((root, query, builder)->{
			Part partTree = new Part("ageIn", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(partTree, new String[] {"2","3"});
		});
		repository.findAll((root, query, builder)->{
			Part part = new Part("ageBetween", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(part, new String[] {"2","3"});
		});
		
		Foo foo = new Foo("a1", 1, "b1");
		repository.findAll((root, query, builder)->{
			PartTree partTree = new PartTree("findByNameIgnoreCase", root.getJavaType());
			return new PartTreePredicateBuilder<>(root, query, builder).build(partTree, foo);
		});
		repository.findAll((root, query, builder)->{
			PartTree partTree = new PartTree("ageIn", Foo.class);
			return new PartTreePredicateBuilder<>(root, query, builder).build(partTree, foo);
		});
	}
	
//	@Test
	public void partTreeSpecificationTests() throws Exception{
		
		//////////////////////////////////////////////////////////////////////////
		// PartTreeSpecification
		///////////////////////////////////////////////////////////////////////////
		Foo params1 = new Foo("a", 1, null);
		
		Foo params2 = new Foo("b", 2, null);
		
		Foo params3 = new Foo("b", null, null);
		
		Foo params4 = new Foo(null, 1, null);
		
		
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params1)).size());
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params2)).size());
		Assert.assertEquals(1, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params3)).size());
		Assert.assertEquals(2, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndAge", params4)).size());
		Assert.assertEquals(2, repository.findAll(new PartTreeSpecification<Foo>("findByNameOrAge", params1)).size());
		try {
			Assert.assertEquals(6, repository.findAll(new PartTreeSpecification<Foo>("findByNameAndXXXX", params1)).size());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void predicateBuilderTest() throws Exception {
		
		///////////////////////////////////////////////////
		//
		////////////////////////////////////////////////////
		repository.findAll((root, query, builder)->{
			
			return new PredicateBuilder<>(root, query, builder)
					.and().eq("name", "1")
					.andStart()
						.eq("name", "a")
						.or()
						.eq("age", 2)
					.andEnd()
					.andStart()
						.eq("name", "a")
						.or()
						.eq("age", 2)
					.andEnd()
					.and().eq("title", null)
					.order().asc("name")
					.order().desc("age")
					.build();
			
		});
		
	}
	
	
}
