package org.springframework.data.jpa.repository.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.parser.PartTree;

@SuppressWarnings("serial")
public class PartTreeSpecification<T> implements Specification<T> {

	private String source;
	private T params;
	
	public PartTreeSpecification(String source, T params) {
		this.source = source;
		this.params = params;
	}
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		
		PartTree partTree = new PartTree(source, root.getJavaType());
		return new PartTreePredicateBuilder<T>(root, query, builder).build(partTree, params);
	}
}
