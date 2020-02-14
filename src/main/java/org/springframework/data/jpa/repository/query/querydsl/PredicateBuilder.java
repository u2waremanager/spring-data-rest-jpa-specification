package org.springframework.data.jpa.repository.query.querydsl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

public class PredicateBuilder {	

	public static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	private BooleanBuilder root;
	private WhereBuilder where;

	public static PredicateBuilder of(Class<?> entityType) {
		return new PredicateBuilder(new PathBuilderFactory().create(entityType));
	}
	public static PredicateBuilder of(PathBuilder<?> path) {
		return new PredicateBuilder(path);
	}
	public static PredicateBuilder of() {
		return new PredicateBuilder(null);
	}
	
	private PredicateBuilder(PathBuilder<?> path) {
		this.root = new BooleanBuilder();
		this.where = new WhereBuilder(root, path);
	}
	
	public WhereBuilder where() {			
		return where;
	}
	
	public static class WhereBuilder extends AbstractCriteriaBuilder<WhereBuilder, Predicate>{
		
		private BooleanBuilder root;
		
		private WhereBuilder(BooleanBuilder root, PathBuilder<?> path) {
			super(new BooleanBuilder(), path);
			this.root = root;
		}
		
		public Predicate build(){
			return root.and(getBase());
		}
	}
}