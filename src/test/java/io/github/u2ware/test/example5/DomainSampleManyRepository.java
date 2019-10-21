package io.github.u2ware.test.example5;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainSampleManyRepository extends PagingAndSortingRepository<DomainSampleMany, UUID> , 
JpaSpecificationExecutor<DomainSampleMany>{

}
