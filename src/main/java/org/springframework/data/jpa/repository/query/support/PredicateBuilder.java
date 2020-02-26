package org.springframework.data.jpa.repository.query.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.query.support.AbstractWhereBuilder.BaseBuilder;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

public class PredicateBuilder {	

	public static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	public static PredicateBuilder of(Class<?> entityType) {
		return new PredicateBuilder(new BaseBuilder(new PathBuilderFactory().create(entityType)));
	}
	public static PredicateBuilder of(PathBuilder<?> path) {
		return new PredicateBuilder(new BaseBuilder(path));
	}
	public static PredicateBuilder of() {
		return new PredicateBuilder(new BaseBuilder(null));
	}

	public static PredicateBuilder of(BooleanBuilder base, Class<?> entityType) {
		return new PredicateBuilder(new BaseBuilder(base, new PathBuilderFactory().create(entityType)));
	}
	public static PredicateBuilder of(BooleanBuilder base, PathBuilder<?> path) {
		return new PredicateBuilder(new BaseBuilder(base, path));
	}
	public static PredicateBuilder of(BooleanBuilder base) {
		return new PredicateBuilder(new BaseBuilder(base, null));
	}
	
	
	
	private BaseBuilder builder;

	private PredicateBuilder(BaseBuilder builder) {
		this.builder = builder;
	}
	
	public WhereBuilder where() {			
		return new WhereBuilder(builder);
	}
	
	public static class WhereBuilder extends AbstractWhereBuilder<WhereBuilder>{
		
		private WhereBuilder(BaseBuilder builder) {
			super(builder);
		}
		
		public Predicate build(){
			return builder.getBase();
		}
	}
}