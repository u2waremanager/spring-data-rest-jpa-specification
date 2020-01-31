package io.github.u2ware.test.example5;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.util.Sets;
import org.hibernate.dialect.HSQLDialect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.query.JPAQueryBuilderFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.sql.HSQLDBTemplates;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application1Tests {

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
		
//		e1.setTypes(new String[] {DomainType.AA.name(),DomainType.BB.name()});
//		e2.setTypes(new String[] {DomainType.BB.name(),DomainType.CC.name()});
//		e3.setTypes(new String[] {DomainType.CC.name(),DomainType.DD.name()});

//		e1.setTypes(new Integer[] {1, 2});
//		e2.setTypes(new Integer[] {2, 3});
//		e3.setTypes(new Integer[] {3, 4});

		e1.setTypes(Sets.newLinkedHashSet(DomainType.AA.name(), DomainType.BB.name()));
		e2.setTypes(Sets.newLinkedHashSet(DomainType.BB.name(), DomainType.CC.name()));
		e3.setTypes(Sets.newLinkedHashSet(DomainType.CC.name(), DomainType.DD.name()));
		
		
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
		logger.info("------------READ---------------------------");
		logger.info("-------------------------------------------");
		
		Optional<DomainSample> r1 = domainSampleRepository.findById(e1.getId());
		logger.info(r1.get());
		
		logger.info("-------------------------------------------");
		logger.info("-------------FIND--------------------------");
		logger.info("-------------------------------------------");
		domainSampleRepository.findAll(PageRequest.of(0, 10)).forEach(d->{
			logger.info(d);
		});
		
		
		
		
//		
//		logger.info("-------------------------------------------");
//		logger.info("-------------SSSSS-------------------------");
//		logger.info("-------------------------------------------");
//		logger.info("-------------------------------------------");
//		domainSample1Repository.findByExample1();
//		
//		logger.info("-------------------------------------------");
//		logger.info("-------------SSSSS-------------------------");
//		logger.info("-------------------------------------------");
//		logger.info("-------------------------------------------");
//		domainSample1Repository.findByExample2(PageRequest.of(0, 1));
//		
//		logger.info("-------------------------------------------");
//		logger.info("-------------SSSSS-------------------------");
//		logger.info("-------------------------------------------");
//		logger.info("-------------------------------------------");
//		domainSample1Repository.findByExample3(PageRequest.of(0, 1));
		
		
		logger.info("-------------------------------------------");
		logger.info("-------------OOPS--------------------------");
		logger.info("-------------------------------------------");

		
		JPAQuery<?> query = new JPAQuery<>();
		JPAQueryBuilderFactory path1 = new JPAQueryBuilderFactory(DomainSample.class);	
//		JPAQueryPath path2 = new JPAQueryPath(DomainSampleOne.class);	
		//where(path1.getArray("types", Integer.class).in(1))
		
		EntityPath<DomainSample> e = new PathBuilderFactory().create(DomainSample.class);
		PathBuilder<DomainSample> b = new PathBuilder<>(e.getType(), e.getMetadata());

		query.from(path1.get()).clone(em).fetch().forEach(a->{
			logger.info("/1/ "+a);
		});
		
//		Predicate p1 = path1.getArray("types").in(new Integer[] {1,2});
//		Predicate p2 = path1.getArray("types").in(new Integer[] {new Integer[] {1,2}});
//		Predicate p1 = path1.get("types").in(new String[] {DomainType.AA.name()});
		
		
//		Predicate p1 = path1.get("types").in(new String[][] {new String[] {DomainType.AA.name(), DomainType.BB.name()}});
//		Predicate p1 = path1.get("types").in(new String[] {DomainType.AA.name(), DomainType.BB.name()});

		logger.info("---------------------------------------------");
		logger.info("------------1111-----------------------------");
		logger.info("---------------------------------------------");
		em.createNativeQuery("select * from domain_sample where REGEXP_MATCHES( types_data , '.*BB.*')", DomainSample.class).getResultList().forEach(a->{
			logger.info("/3/ "+a);
		});
		
		logger.info("---------------------------------------------");
		logger.info("-------------2222----------------------------");
		logger.info("---------------------------------------------");
//		Predicate p2 = b.getString("typesData").contains("BB");
//		Predicate p3 = b.getString("typesData").contains("CC");

		
//		em.createQuery("from io.github.u2ware.test.example5.DomainSample a where REGEXP_MATCHES ( a.typesData , '.*BB.*')", DomainSample.class).getResultList().forEach(a->{
//			logger.info("/44/ "+a);
//		});
		
		//select (regexp_matches('aassdd', 'a', 'g'))[1]
		
//		Expressions.stringTemplate(template, args)<T>

		
//		Predicate pp = SQLExpressions.relationalFunctionCall(String.class, "REGEXP_MATCHES");

//		Predicate pp = Expressions.booleanTemplate("function('REGEXP_LIKE',{0},{1})", path1.get("typesData"), "'.*BB.*'");
        
//		Predicate pp = Expressions.stringTemplate("REGEXP_MATCHES").;

//		Predicate pp = Expressions.booleanTemplate(" 'REGEXP_MATCHES(', {0}, {1})", path1.get("typesData"), "'.*BB.*'");
//		Predicate pp = Expressions.booleanTemplate(" REGEXP_MATCHES typesData, '.*BB.*' ");
		//ExpressionUtils.s
		
//		HSQLDBTemplates d;
//		HSQLDialect d1;
		
//		query.from(e).where(pp).clone(em).fetch().forEach(a->{
//			logger.info("/4/ "+a);
//		});
		
		
//		query.from(e).where(Expressions.predicate(Ops.MATCHES, Expressions.constant("AA"), path1.get("typesData"))).clone(em).fetch().forEach(a->{
//			logger.info("/1/ "+a);
//		});
		
		;

//		SQLTemplates templates = null;//new DerbyTemplates();
//		JPASQLQuery q = new JPASQLQuery<>(em, sqlTemplates) 
		
		
		
//		em.createNativeQuery("select * from domain_sample where REGEXP_MATCHES( types_data , 'AA|BB')").getResultList().forEach(a->{
//			logger.info("/3/ "+a);
//		});
		
		//StringExpressions.ma
		
		
//		Expressions.boo
		
//		query.from(path1.get()).where(path1.getString("name").like("%a%b")).clone(em).fetch().forEach(a->{
//			logger.info("/2/ "+a);
//		});
	}
}


