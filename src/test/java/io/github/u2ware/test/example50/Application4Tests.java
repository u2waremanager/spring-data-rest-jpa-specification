package io.github.u2ware.test.example50;

import java.util.ArrayList;
import java.util.Collection;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.query.JPAQueryLagycyBuilder;
import org.springframework.data.jpa.repository.query.PartTreeQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.querydsl.jpa.impl.JPAQuery;

//import static io.github.u2ware.test.ApplicationMockMvc.ApplicationResultActions.sizeMatch;
import io.github.u2ware.test.ApplicationMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Application4Tests {

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
		Collection<String> collection = new ArrayList<>();
		collection.add("aa");
		collection.add("bb");
		
		String[] array = StringUtils.commaDelimitedListToStringArray("a,b,c");
		
		
		logger.info("-------------------------------------------");
		logger.info("-------------ZZZZZ-------------------------");
		logger.info("-------------------------------------------");
		JPAQuery<DomainSample> query = JPAQueryLagycyBuilder.of(DomainSample.class, em)
			.join("one")
//			.join("many")
			.where()
				.and().like("name", "CCfffGGGGc")
				.and().gt("age", 1)
				.and().gte("age", 2)
				.and().lt("age", 3)
				.and().lte("age", 4)
				
				.and().in("name", collection)
				.and().in("name", array)
				.and().in("name", "aa")
				
				.and().between("name", collection)
				.and().between("name", array)
				//.and().between("name", "aa")
				
				
				.andStart()
					.eq("name", "a")
					.or()
					.eq("age", 1)
				.andEnd()
				.andStart()
					.eq("name", "b")
					.or()
					.eq("age", 2)
				.andEnd()
				.and().eq("age", 5)
				.pageable(PageRequest.of(0, 2, Direction.ASC, "age"))
				.build();
//			.order()
//				.asc("name")
//				.desc("name")
				;
		
		query.fetch().forEach(r->{
			logger.info(r);
		});
		
		
		logger.info("-------------------------------------------");
		logger.info("-------------YYYYYY-------------------------");
		logger.info("-------------------------------------------");
		logger.info("-------------------------------------------");

		
		domainSampleRepository.findAll( (r, q, b) -> {
			return PredicateQueryBuilder.of(r, q, b)
					.where()
						.and().like("name", "CCfffGGGGc")
						.and().gt("age", 1)
						.and().gte("age", 2)
						.and().lt("age", 3)
						.and().lte("age", 4)
						
						.and().in("name", collection)
						.and().in("name", array)
						.and().in("name", "aa")
						
						.and().between("name", collection)
						.and().between("name", array)
						//.and().between("name", "aa")
						
						
						.andStart()
							.eq("name", "a")
							.or()
							.eq("age", 1)
						.andEnd()
						.andStart()
							.eq("name", "b")
							.or()
							.eq("age", 2)
						.andEnd()
						.and().eq("age", 5)
					.build();
		}).forEach(r->{
			logger.info(r);
		});
		
	}
}


