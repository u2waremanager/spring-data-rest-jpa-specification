package io.github.u2ware.test.example0;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.JPAQueryBuilder;
import org.springframework.data.jpa.repository.support.PredicateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application2Tests {

	protected Log logger = LogFactory.getLog(getClass());

	
//	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
//	protected @Autowired WebApplicationContext context;
//	protected RestMockMvc $;
	
	@Before
	public void before() throws Exception {
//		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
//		this.$ = new RestMockMvc(mvc, springDataRestBasePath);

		if(fooRepository.count() > 0) return;
		
		fooRepository.save(new Foo("a", 1, "1"));		
		fooRepository.save(new Foo("b", 2, "1"));		
		fooRepository.save(new Foo("c", 2, "1"));		
		fooRepository.save(new Foo("d", 1, "2"));		
		fooRepository.save(new Foo("e", 2, "2"));		
		fooRepository.save(new Foo("f", 3, "2"));		
	}

	private @Autowired FooRepository fooRepository; 
	private @PersistenceContext EntityManager em;
	
//	@Test
	public void queryDslTest1() {
		
		PathBuilder<Foo> t = new PathBuilderFactory().create(Foo.class);

		
		JPAQuery<Foo> query1 = new JPAQuery<>(em);
		query1.select(t);
		
		logger.info(query1.getType());
		logger.info(query1.getMetadata().getProjection());
		Assert.assertEquals(Foo.class, query1.getType());
		Assert.assertEquals("foo", query1.getMetadata().getProjection().toString());
		
		
		JPAQuery<Foo> query2 = new JPAQuery<>(em);
		query2.from(t);
		
		logger.info(query2.getType());
		logger.info(query2.getMetadata().getProjection());
		Assert.assertEquals(Void.class, query2.getType());
		Assert.assertNull(query2.getMetadata().getProjection());
		

	}

	
//	@Test
	public void queryDslTest2() {
		
		JPAQuery<Foo> query = new JPAQuery<>(em);
		PathBuilder<Foo> t = new PathBuilderFactory().create(Foo.class);
		
		query.from(
			t
		).where(
			t.get("name").eq("a").and(
				t.get("age").eq(2).or(
					t.get("name").eq("a").and(
						t.get("age").eq(2)
					)
				)
			)
			
		).orderBy(
			new OrderSpecifier<>(Order.DESC, t.getComparable("age", Integer.class))
		).orderBy(
			new OrderSpecifier<>(Order.ASC, t.getComparable("name", String.class))
		).fetch();
	}
	
		
	


	//@Test
	public void predicateQuery1() {
		PathBuilder<Foo> foo = new PathBuilderFactory().create(Foo.class);
		
		BooleanBuilder p = new BooleanBuilder();
		p.and(foo.get("age").eq(1));

		BooleanBuilder p2 = new BooleanBuilder();
		p2.and(foo.get("name").eq("a"));
		p.and(p2);
		
		p.and(PredicateBuilder.of(Foo.class).where().and(foo.get("name").eq("b")).build());
		fooRepository.findAll(p);
	}
	
//	@Test
	public void predicateQuery2() {
		
		PathBuilder<Foo> foo = new PathBuilderFactory().create(Foo.class);
	
		Predicate p = PredicateBuilder.of()
				.where()
				.and(foo.get("name").eq("a"))
				.andStart()
					.andStart()
						.and(foo.get("age").eq(1))
						.or(foo.get("name").eq("b"))
					.andEnd()
					.andStart()
						.and(foo.get("age").eq(2))
						.or(foo.get("name").eq("c"))
					.andEnd()
				.andEnd()
				.and(foo.get("name").eq("c"))
				.build();
		fooRepository.findAll(p);
	}
		
//	@Test
	public void predicateQuery3() {
		
		Predicate p = PredicateBuilder.of(Foo.class)
			.where()
			.and().eq("name", "a")
			.andStart()
				.andStart()
					.and().eq("age",null)
					.or().eq("name",null)
				.andEnd()
				.andStart()
					.and().eq("age", 2)
					.or().eq("name", "c")
				.andEnd()
			.andEnd()
			.and().eq("name", "c")
		.build();
		
		fooRepository.findAll(p);

	}
	
	@Test
	public void jqpQuery() {
		
		PathBuilder<Foo> foo = new PathBuilderFactory().create(Foo.class);
		
		BooleanExpression p1 = foo.get("title").eq("1");
		BooleanExpression p2 = foo.get("name").eq("a");
		BooleanExpression p3 = foo.get("age").eq(1);
		
		

		JPAQueryBuilder.of(em)
				.from(Foo.class)
				.where()
				
//					.and(p1)
//					.or(p3)
				
//					.and().eq("name", "1")
//					.or().eq("title", "a")
				
//					.andStart()
//						.and(p1)
//						.or(p2)
//					.andEnd()
//					.andStart()
//						.and(p2)
//						.or(p3)
//					.andEnd()
					
//					.andStart()
//						.and().eq("name", "a")
//						.or().eq("age", 2)
//					.andEnd()
//					.andStart()
//						.and().eq("name", "a")
//						.or().eq("age", 2)
//					.andEnd()
					
					
					
					.and().eq("name", "a")
					.andStart()
						.andStart()
							.and().eq("age",1)
							.or().eq("name","b")
						.andEnd()
						.andStart()
							.and().eq("age",2)
							.or().eq("name", "c")
						.andEnd()
					.andEnd()
					.and().eq("name", "c")
					
					
				.orderBy()
				.build()
			.fetch()
		.forEach(i->{
			logger.info(i);
		});;	

	}
	
	

	
}
