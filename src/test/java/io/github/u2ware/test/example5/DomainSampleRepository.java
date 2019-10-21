package io.github.u2ware.test.example5;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainSampleRepository extends PagingAndSortingRepository<DomainSample, UUID> , 
JpaSpecificationExecutor<DomainSample>{

	
//	@Query("select a from DomainSample1 a join fetch a.one1")
//	Iterable<DomainSample1> findAll() ;
//
//	
//	@Query("select a from DomainSample1 a join fetch a.one1")
//	Page<DomainSample1> findAll(Pageable pageable);
	
//	@Query("select a from DomainSample1 a join fetch a.one where a.name=:name")
//	List<DomainSample1> findByName(@Param("name") String name);
	
	
//	@Query("select a from DomainSample a join fetch a.one")
//	Iterable<DomainSample> findByExample1();
//	
//	@Query(value="select a from DomainSample a join fetch a.one", countQuery = "select count(a) from DomainSample a")
//	Page<DomainSample> findByExample2(Pageable pageable);
//	
//	@Query(value="select a from DomainSample a")
//	Page<DomainSample> findByExample3(Pageable pageable);
}
