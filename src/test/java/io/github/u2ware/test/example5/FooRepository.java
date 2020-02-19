package io.github.u2ware.test.example5;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long>, JpaSpecificationExecutor<Foo>, QuerydslPredicateExecutor<Foo>{

}
