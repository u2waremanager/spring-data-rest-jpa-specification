package io.github.u2ware.test.example4;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface ManyToOnePhysicalColumn4Repository extends PagingAndSortingRepository<ManyToOnePhysicalColumn4, UUID>{

}
