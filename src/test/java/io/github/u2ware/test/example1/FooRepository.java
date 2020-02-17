package io.github.u2ware.test.example1;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FooRepository extends PagingAndSortingRepository<Foo, Long> , QuerydslPredicateExecutor<Foo>{

}
