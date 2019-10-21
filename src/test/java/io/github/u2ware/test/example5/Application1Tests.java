package io.github.u2ware.test.example5;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
		
	}
}


