package io.github.u2ware.test.example7;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FooRepository extends PagingAndSortingRepository<Foo, UUID> , JpaSpecificationExecutor<Foo>{

	
	@Transactional(readOnly = true)
	Iterable<Foo> findByAge(Integer age) ;
	
	
	@Transactional(readOnly = false)
	Iterable<Foo> findByName(String name) ;
	
}
