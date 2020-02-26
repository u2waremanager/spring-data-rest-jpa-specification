package io.github.u2ware.test.example5;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.querydsl.core.types.Predicate;

public interface DomainSampleRepository extends PagingAndSortingRepository<DomainSample, UUID>, QuerydslPredicateExecutor<DomainSample>{

//	@Override
//	@EntityGraph(value = "io.github.u2ware.test.example5.DomainSampleGraph")
//	Page<DomainSample> findAll(Pageable pageable);
//	
//	
//	@Override
//	@EntityGraph(value = "io.github.u2ware.test.example5.DomainSampleGraph")
//	Iterable<DomainSample> findAll(Sort sort);
	
	
	
	@Override
	@EntityGraph(value = "io.github.u2ware.test.example5.DomainSampleGraph")
	Page<DomainSample> findAll(Predicate predicate, Pageable pageable) ;
	
	@Override
	@EntityGraph(value = "io.github.u2ware.test.example5.DomainSampleGraph")
	Iterable<DomainSample> findAll(Predicate predicate, Sort sort);

}
