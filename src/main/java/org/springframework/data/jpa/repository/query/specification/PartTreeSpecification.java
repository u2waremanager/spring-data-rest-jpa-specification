package org.springframework.data.jpa.repository.query.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanWrapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.MultiValueMap;

@SuppressWarnings("serial")
public class PartTreeSpecification<T> implements Specification<T>{

	private String source;
	private BeanWrapper params;
	
	public PartTreeSpecification(String source, T params) {
		this.source = source;
		this.params = BeanWrapperFactory.getInstance(params);
	}
	public PartTreeSpecification(String source, Object... params) {
		this.source = source;
		this.params = BeanWrapperFactory.getInstance(params);
	}
	public PartTreeSpecification(String source, MultiValueMap<String,Object> params) {
		this.source = source;
		this.params = BeanWrapperFactory.getInstance(params);
	}	
	
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		PartTree partTree = new PartTree(source, root.getJavaType());
		return new PartTreePredicateBuilder<>(root, query, builder).build(partTree, params);
	}
}
