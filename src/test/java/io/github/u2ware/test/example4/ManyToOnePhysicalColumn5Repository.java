package io.github.u2ware.test.example4;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ManyToOnePhysicalColumn5Repository extends PagingAndSortingRepository<ManyToOnePhysicalColumn5, Long>, JpaSpecificationExecutor<ManyToOnePhysicalColumn5>, QuerydslPredicateExecutor<ManyToOnePhysicalColumn5>{

}
