package org.springframework.data.jpa.repository.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

public class SpecificationUtils {

	
	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {
		return QueryUtils.toExpressionRecursively(from, property, false);
	}
	
	public static <T> Specification<T> of(Specification<T> other){
		return other;
	}
	
	public static <T> Specification<T> createSpecificationBuffer(){
		return new SpecificationBuffer<T>();
	}
	public static <T> Specification<T> createSpecificationBuffer(Specification<T> other){
		return new SpecificationBuffer<T>(other);
	}
	
	@SuppressWarnings("serial")
	public static class SpecificationBuffer<T> implements Specification<T>{

		private Specification<T> specification;
		
		private SpecificationBuffer() {
			
		}
		private SpecificationBuffer(Specification<T> other) {
			specification = Specification.where(other);
		}
		
		
		@Override
		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			if(specification == null) return null;
			return specification.toPredicate(root, query, criteriaBuilder);
		}
		
		@Override
		public Specification<T> and(Specification<T> other) {
			if(specification == null) {
				specification = Specification.where(other);
			}else {
				specification = specification.and(other);
			}
			return specification;
		}

		@Override
		public Specification<T> or(Specification<T> other) {
			if(specification == null) {
				specification = Specification.where(other);
			}else {
				specification = specification.or(other);
			}
			return specification;
		}
	}
	
}
