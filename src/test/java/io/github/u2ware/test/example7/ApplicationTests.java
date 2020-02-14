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
import org.springframework.data.jpa.repository.query.specification.SpecificationBuilder;
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
	protected @Autowired ObjectMapper objectMapper;
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
		
//
//		
//		fooRepository.findAll((root, query, builder)->{
//			PartTree partTree = new PartTree("findByNameAndAge", Foo.class);
//			
//			Part part = new Part("name", Foo.class);
//			
//			Foo foo = new Foo();
//			foo.setAge(1);
//			//foo.setName("aaa");
//			
////			return new PredicateBuilder<>(root,query,builder).build(partTree, foo);
//			return new PartTreePredicate<>(root,query,builder).build(part, "aaa");
//		});
//
//		
		fooRepository.findAll((root, query, builder)->{
			
			return SpecificationBuilder.of(root, query, builder)
					.where()
//					.and().eq("age", 1)
						.andStart()
							.eq("age", 1)
							.or().eq("name", "aaa")
						.andEnd()
						.andStart()
							.eq("age", 1)
							.or().eq("name", "aaa")
						.andEnd()
					.orderBy()
						.asc("name")
						.desc("age")
					.build();
			
		});
		
		
		
		
		
		
		
//		logger.info("1------------------------");
//		$.POST("/foos").C("name", "name1").is2xx();
//		$.POST("/foos").C("name", "name2").is2xx();
//		
//		logger.info("2------------------------");
//		$.GET("/foos/1")
//		.H("u2ware", "u2ware")
//		.is2xx();
//		
//		
//		logger.info("2------------------------");
//		
//		$.GET("/foos")
//			.H("u2ware", "u2ware")
////			.P("unpaged", "true")
//			.P("page", "1")
//			.P("size", "1")
//			.P("sort", "name")
//			.C("name", "name")
//			.C("id", "1")
//			.C("url", "http://google.com")
//			.C("dateTime", "2019-01-01")
//			.is2xx();
//		
////		logger.info("3------------------------");
//		JPAQuery<Foo> query = new JPAQuery<>(em);
//		JPAQueryBuilder<Foo> b = new JPAQueryBuilder<>(Foo.class);
//		
//		query
//			.from(
//				b.from()
//					.build()
//			).where(
//				b.where()
//					.andStart()
//						.eq("name", "aaa")
//						.or().eq("age", 1)
//					.andEnd()
//					.andStart()
//						.eq("name", "aaa")
//						.or().eq("age", 1)
//					.andEnd()
//					.and().eq("name", "aaa")
//					.build()
//			).orderBy(
//				b.orderBy()
//					.asc("age")
//					.desc("name")
//					.build()
//			).fetch();
//			
//		
//		
//		
//		
//		
//		JPAQueryBuilder<Foo> path = new JPAQueryBuilder<>(Foo.class);
//
//		BooleanExpression w0 = path.get("name").eq("aa").and(path.get("age").eq(1));
//		BooleanExpression w1 = path.get("name").eq("aa").or(path.get("age").eq(1));
//		
//		BooleanBuilder builder = new BooleanBuilder();
//		builder.and(w1).and(w1);
//		
//		
//		logger.info("1------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(path.get("name").eq("aa").and(path.get("age").eq(1))).clone(em).fetch();
//		
//		logger.info("2------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(w0.and(w0)).clone(em).fetch();
//
//		logger.info("3------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(w0.or(w0)).clone(em).fetch();
//
//		logger.info("4------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(w1.and(w1)).clone(em).fetch();
//		
//		logger.info("5------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(w1.or(w1)).clone(em).fetch();
//		
//		logger.info("6------------------------");
//		new JPAQuery<Foo>().from(path.from()).where(builder).clone(em).fetch();
//		
//		
//		logger.info("7------------------------");
//		new JPAQuery<Foo>().from(
//				path.from()
//			).where(
//				path.where()
//					.andStart()
//						.eq("name", "aaa")
//						.or().eq("age", 1)
//					.andEnd()
//					.andStart()
//						.eq("name", "aaa")
//						.or().eq("age", 1)
//					.andEnd()
//					.and().eq("name", "aaa")
//					.build()
//			).orderBy(
//				path.orderBy()
//					.order("name", Order.ASC)
//					.order("age", Order.DESC)
//					.build()
//			).clone(em).fetch();
//		
//		
//		logger.info("8------------------------");
//		$.POST("/foos").H("u2ware", "u2ware").C("name", "name").is2xx();
//		
		
	}
}
