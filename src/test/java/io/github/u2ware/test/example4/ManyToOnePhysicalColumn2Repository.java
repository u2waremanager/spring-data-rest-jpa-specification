package io.github.u2ware.test.example4;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false) 
public interface ManyToOnePhysicalColumn2Repository extends PagingAndSortingRepository<ManyToOnePhysicalColumn2, Long>{

}
