package io.github.u2ware.test.example7;

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
import org.springframework.data.jpa.repository.query.JPAQueryBuilder;
import org.springframework.data.jpa.repository.query.PartTreeSpecification;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);
	}
	
	protected @PersistenceContext EntityManager em;
	protected @Autowired FooRepository fooRepository;
	
	@Test
	public void contextLoads() throws Exception {
		
		logger.info("------------------------");
		logger.info("------------------------");
		logger.info("------------------------");
		
		JPAQueryBuilder<Foo> path = new JPAQueryBuilder<>(Foo.class);

		BooleanExpression w0 = path.get("name").eq("aa").and(path.get("age").eq(1));
		BooleanExpression w1 = path.get("name").eq("aa").or(path.get("age").eq(1));
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(w1).and(w1);
		
		
		logger.info("1------------------------");
		new JPAQuery<Foo>().from(path.from()).where(path.get("name").eq("aa").and(path.get("age").eq(1))).clone(em).fetch();
		
		logger.info("2------------------------");
		new JPAQuery<Foo>().from(path.from()).where(w0.and(w0)).clone(em).fetch();

		logger.info("3------------------------");
		new JPAQuery<Foo>().from(path.from()).where(w0.or(w0)).clone(em).fetch();

		logger.info("4------------------------");
		new JPAQuery<Foo>().from(path.from()).where(w1.and(w1)).clone(em).fetch();
		
		logger.info("5------------------------");
		new JPAQuery<Foo>().from(path.from()).where(w1.or(w1)).clone(em).fetch();
		
		logger.info("6------------------------");
		new JPAQuery<Foo>().from(path.from()).where(builder).clone(em).fetch();
		
		
		logger.info("7------------------------");
		new JPAQuery<Foo>().from(
				path.from()
			).where(
				path.where()
					.andStart()
						.eq("name", "aaa")
						.or().eq("age", 1)
					.andEnd()
					.andStart()
						.eq("name", "aaa")
						.or().eq("age", 1)
					.andEnd()
					.and().eq("name", "aaa")
					.build()
			).orderBy(
				path.orderBy()
					.order("name", Order.ASC)
					.order("age", Order.DESC)
					.build()
			).clone(em).fetch();
		
		
		logger.info("8------------------------");

		
		$.GET("/foos").H("u2ware", "u2ware").is2xx();
	}
}
