package org.springframework.data.jpa.repository.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.support.AbstractWhereBuilder.BaseBuilder;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;

public class PredicateBuilder {	

	public static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	public static PredicateBuilder of(Class<?> entityType) {
		return new PredicateBuilder(new PathBuilderFactory().create(entityType));
	}
	public static PredicateBuilder of(PathBuilder<?> path) {
		return new PredicateBuilder(path);
	}
	public static PredicateBuilder of() {
		return new PredicateBuilder(null);
	}
	
	private PathBuilder<?> path;

	private PredicateBuilder(PathBuilder<?> path) {
		this.path = path;
	}
	
	public WhereBuilder where() {			
		return new WhereBuilder(new BaseBuilder(path));
	}
	
	public static class WhereBuilder extends AbstractWhereBuilder<WhereBuilder>{
		
		private WhereBuilder(BaseBuilder builder) {
			super(builder);
		}
		
		public Predicate build(){
			return getBuilder().getBase();
		}
	}
}