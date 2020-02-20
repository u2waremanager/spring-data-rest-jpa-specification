package io.github.u2ware.test.example5;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;


@RestResource(exported = false)
public interface OneToManyColumn2Repository extends PagingAndSortingRepository<OneToManyColumn2, Long>{

}
