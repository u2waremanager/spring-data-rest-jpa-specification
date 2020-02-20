package io.github.u2ware.test.example70;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<ManyToOnePhysicalColumn5, Long> , JpaSpecificationExecutor<ManyToOnePhysicalColumn5>{

	
}
