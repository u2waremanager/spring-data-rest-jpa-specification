package io.github.u2ware.test.example1;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long> , JpaSpecificationExecutor<Foo>{

}
