package io.github.u2ware.test.example4;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ManyToOneSample5Repository extends PagingAndSortingRepository<ManyToOneSample5, Long>, JpaSpecificationExecutor<ManyToOneSample5>, QuerydslPredicateExecutor<ManyToOneSample5>{

}
