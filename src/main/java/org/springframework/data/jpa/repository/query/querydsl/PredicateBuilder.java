package org.springframework.data.jpa.repository.query.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

public class PredicateBuilder {	

	public static PredicateBuilder of(Predicate... predicates) {
		return new PredicateBuilder(predicates);
	}

	private BooleanBuilder root;
	
	private PredicateBuilder(Predicate... predicates) {
		this.root = new BooleanBuilder();
		for(Predicate p : predicates){
			root = root.and(p);
		}
	}

	public WhereBuilder where(Class<?> entityType) {			
		return new WhereBuilder(root, new PathBuilderFactory().create(entityType));
	}
	public WhereBuilder where(PathBuilder<?> path) {			
		return new WhereBuilder(root, path);
	}
	
	public static class WhereBuilder extends AbstractCriteriaBuilder<WhereBuilder, Predicate>{
		
		private WhereBuilder(BooleanBuilder root, PathBuilder<?> path) {
			super(root, path);
		}
		
		public Predicate build(){
			return getRoot();
		}
	}
}