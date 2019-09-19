package io.github.u2ware.test.example3;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
public interface BarRepository extends PagingAndSortingRepository<Bar, UUID> , JpaSpecificationExecutor<Bar>{

}
