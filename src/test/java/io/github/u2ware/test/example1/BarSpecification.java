package io.github.u2ware.test.example1;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.PredicateBuilder;

import lombok.Data;

@SuppressWarnings("serial")
public @Data class BarSpecification implements Specification<Bar>{

	private String name;
	
	
	@Override
	public Predicate toPredicate(Root<Bar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return new PredicateBuilder()
				.eq("name", name)
				.eq("age", 1)
				.build(root, query, cb);
	}

}
