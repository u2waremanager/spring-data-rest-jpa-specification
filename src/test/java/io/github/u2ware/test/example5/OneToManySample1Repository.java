package io.github.u2ware.test.example5;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = true) //default
public interface OneToManySample1Repository extends PagingAndSortingRepository<OneToManySample1, Long>{

}
