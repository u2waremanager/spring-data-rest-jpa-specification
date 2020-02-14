package io.github.u2ware.test.example0;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, UUID>, JpaSpecificationExecutor<Foo>, QuerydslPredicateExecutor<Foo>{

}
