package io.github.u2ware.test.example60;

import java.util.UUID;

import javax.persistence.Transient;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FooRepository extends PagingAndSortingRepository<Foo, UUID> , JpaSpecificationExecutor<Foo>{

	
	//@Transactional(readOnly = true)
	Iterable<Foo> findAll() ;
	
}
