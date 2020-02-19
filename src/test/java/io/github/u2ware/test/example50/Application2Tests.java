package io.github.u2ware.test.example50;

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
import org.springframework.data.jpa.repository.query.JPAQueryLagycyBuilder;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

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
//		e1.setOne(o1);
//		e2.setOne(o2);
//		e3.setOne(o3);
		
//		domainSampleOneRepository.save(o1);		
//		domainSampleOneRepository.save(o2);		
//		domainSampleOneRepository.save(o3);		
		domainSampleManyRepository.save(m1);		
		domainSampleManyRepository.save(m2);		
		domainSampleManyRepository.save(m3);		
//		domainSampleRepository.save(e1);
//		domainSampleRepository.save(e2);
//		domainSampleRepository.save(e3);
		
		logger.info("-------------------------------------------");
		domainSampleManyRepository.findAll( (r, q, b) -> {return PredicateQueryBuilder.of(r, q, b).build();} );
		logger.info("-------------------------------------------");
		domainSampleManyRepository.findAll( (r, q, b) -> {return PredicateQueryBuilder.of(r, q, b).where().build();} );
		logger.info("-------------------------------------------");
		domainSampleManyRepository.findAll( (r, q, b) -> {return PredicateQueryBuilder.of(r, q, b).where().and().eq("name", "a").build();} );
		logger.info("-------------------------------------------");
		domainSampleManyRepository.findAll( (r, q, b) -> {return PredicateQueryBuilder.of(r, q, b).order().build();} );
		logger.info("-------------------------------------------");
		domainSampleManyRepository.findAll( (r, q, b) -> {return PredicateQueryBuilder.of(r, q, b).order().desc("name").build();} );
		
		
		
		logger.info("----222---------------------------------------");
		JPAQueryLagycyBuilder.of(DomainSampleMany.class, em).build().fetch();
		logger.info("-------------------------------------------");
		JPAQueryLagycyBuilder.of(DomainSampleMany.class, em).where().build().fetch();
		logger.info("-------------------------------------------");
		JPAQueryLagycyBuilder.of(DomainSampleMany.class, em).where().and().eq("name", "a").build().fetch();
		logger.info("-------------------------------------------");
		JPAQueryLagycyBuilder.of(DomainSampleMany.class, em).order().build().fetch();
		logger.info("-------------------------------------------");
		JPAQueryLagycyBuilder.of(DomainSampleMany.class, em).order().desc("name").build().fetch();
	}
}


