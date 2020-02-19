package io.github.u2ware.test.example50;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application3Tests {

	protected Log logger = LogFactory.getLog(getClass());

	
	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Autowired WebApplicationContext context;
	protected ApplicationMockMvc $;
	
	
	@Before
	public void before() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(context).build();
		this.$ = new ApplicationMockMvc(mvc, springDataRestBasePath);
	}

	protected @Autowired DomainSampleRepository domainSampleRepository;
	protected @Autowired DomainSampleManyRepository domainSampleManyRepository;
	protected @Autowired DomainSampleOneRepository domainSampleOneRepository;
	
	protected @PersistenceContext EntityManager em;

	@Test
	public void contextLoads() throws Exception {

		DomainSampleOne o1 = new DomainSampleOne();
		DomainSampleOne o2 = new DomainSampleOne();
		DomainSampleOne o3 = new DomainSampleOne();
		
		DomainSampleMany m1 = new DomainSampleMany();
		DomainSampleMany m2 = new DomainSampleMany();
		DomainSampleMany m3 = new DomainSampleMany();
		
		DomainSample e1 = new DomainSample();
		DomainSample e2 = new DomainSample();
		DomainSample e3 = new DomainSample();
//		e1.setMany(Sets.newLinkedHashSet(m1));
//		e2.setMany(Sets.newLinkedHashSet(m2));
//		e3.setMany(Sets.newLinkedHashSet(m3));
		e1.setOne(o1);
		e2.setOne(o2);
		e3.setOne(o3);
		
		domainSampleOneRepository.save(o1);		
		domainSampleOneRepository.save(o2);		
		domainSampleOneRepository.save(o3);		
		domainSampleManyRepository.save(m1);
		domainSampleManyRepository.save(m2);
		domainSampleManyRepository.save(m3);
		domainSampleRepository.save(e1);
		domainSampleRepository.save(e2);
		domainSampleRepository.save(e3);

		
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");
		CriteriaBuilder b = em.getCriteriaBuilder();
		CriteriaQuery<DomainSample> q = b.createQuery(DomainSample.class);
		Root<DomainSample> r = q.from(DomainSample.class);
		r.join("one", JoinType.LEFT);
//		Join<DomainSample,DomainSampleOne> j = r.join("one", JoinType.LEFT);
		q.select(r);
		
		TypedQuery<DomainSample> tq = em.createQuery(q);
		tq.getResultList().forEach(d->{
			logger.info(d);
		});
		
		
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");
		EntityPath<DomainSample> entity = new PathBuilderFactory().create(DomainSample.class);
		PathBuilder<DomainSample> path = new PathBuilder<>(entity.getType(), entity.getMetadata());
		JPAQuery<DomainSample> query = new JPAQuery<>(em);
		query.from(entity)
			.leftJoin(path.get("one")).fetchJoin()
			.fetch().forEach(d->{
			logger.info(d);
		});
		
		
		logger.info("-------------------------------------------");
		logger.info("-----------------sss1--------------------------");
		logger.info("-------------------------------------------");
		query.from(entity)
			.leftJoin(path.get("one")).fetchJoin()
			.where(path.get("one.name").eq(o3.getName()))
			.fetch().forEach(d->{
				logger.info(d);
			});

		logger.info("-------------------------------------------");
		logger.info("-----------------sss2--------------------------");
		logger.info("-------------------------------------------");
		new JPAQuery<>(em).from(entity)
			.leftJoin(path.get("one")).fetchJoin()
			.where(path.get("one.name").eq(o3.getName()))
			.fetch().forEach(d->{
				logger.info(d);
			});
		
		
		logger.info("-------------------------------------------");
		logger.info("-----------------sss3--------------------------");
		logger.info("-------------------------------------------");
		BooleanBuilder where = new BooleanBuilder();
		where.and(new BooleanBuilder().and(path.get("name").eq("a").or(path.get("age").eq(1))));
		where.and(new BooleanBuilder().and(path.get("name").eq("a").or(path.get("age").eq(1))));
		
		new JPAQuery<>(em).from(entity)
				.leftJoin(path.get("one")).fetchJoin()
				//.leftJoin(path.get("many")).fetchJoin()
				.select(path)
				.offset(0)
				.limit(2)
				.orderBy(new OrderSpecifier(Order.ASC, path.get("name")))
				
//				.where(path.get("name").eq("a"))
//				.where(path.get("age").eq(1))
				
//				.where(
//						path.get("name").eq("a").and(
//								path.get("age").eq(1).or(path.get("name").eq("2"))
//						)
//				)
				
				.where( where)
				
				.fetch();
		
		
		
		
		
		logger.info("-------------------------------------------");
		logger.info("-------------ZZZZZ-------------------------");
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");
		Querydsl querydsl = new Querydsl(em, path);
		List<DomainSample> domainSamples = querydsl.createQuery(entity)
				.leftJoin(path.get("one")).fetchJoin()
//				.leftJoin(path.get("many")).fetchJoin()
				.select(path).fetch();
		domainSamples.forEach(domainSample->{
			logger.info(domainSample);
		});
	}
}


