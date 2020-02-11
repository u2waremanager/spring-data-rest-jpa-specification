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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.JPAQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;
import io.github.u2ware.test.example5.DomainSample;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application2Tests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
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

	private @Autowired FooRepository repository; 
	private @PersistenceContext EntityManager em;
	
	@Test
	public void queryDslTest() {
		
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
	public void jpaQueryTypeTest() {
		
		JPAQuery<Foo> query = new JPAQuery<>(em);
		PathBuilder<Foo> t = new PathBuilderFactory().create(Foo.class);
		
		query.from(
			t
//		).where(
//			t.get("name").eq("1")
//		).where(
//			t.get("age").eq(1)

		).where(
			new BooleanBuilder().and(t.get("age").eq(1)).or(t.get("age").eq(2))
			
		).orderBy(
			new OrderSpecifier<>(Order.DESC, t.getComparable("age", Integer.class))
		).orderBy(
			new OrderSpecifier<>(Order.ASC, t.getComparable("name", String.class))
		).fetch();
	}
	
	//@Test
	public void jpaQueryBuilderTest() {
		
		JPAQueryBuilder.of(em)
			.from(Foo.class)
			.where()
				.and().eq("name", "1")
				.andStart()
					.eq("name", "1")
					.or().eq("age", 1)
				.andEnd()
				.andStart()
					.eq("name", "1")
					.or().eq("age", 1)
				.andEnd()
				.or().eq("age", 1)
			.orderBy()
				.desc("name")
				.asc("age")
			.build()
			.fetch();
	}
	
	
	
}
