package io.github.u2ware.test.example50;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainSampleOneRepository extends PagingAndSortingRepository<DomainSampleOne, UUID> , JpaSpecificationExecutor<DomainSampleOne>{

}
