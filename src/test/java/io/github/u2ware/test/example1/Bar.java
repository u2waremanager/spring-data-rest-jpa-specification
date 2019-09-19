package io.github.u2ware.test.example1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.PredicateBuilder;

import lombok.Data;

@SuppressWarnings("serial")
@Table(name="example1_bar")
@Entity
public @Data class Bar implements Specification<Bar>{

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public Bar() {}
	public Bar(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
	@Override
	public Predicate toPredicate(Root<Bar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return new PredicateBuilder()
				.like("name", name)
				.build(root, query, cb);
	}
	
	
	
}
