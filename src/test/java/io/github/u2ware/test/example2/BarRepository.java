package io.github.u2ware.test.example2;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BarRepository extends PagingAndSortingRepository<Bar, UUID> , JpaSpecificationExecutor<Bar>{

}
