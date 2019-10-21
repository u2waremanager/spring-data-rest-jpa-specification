package io.github.u2ware.test.example5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractQueryBuilder<T> {

	protected Log logger = LogFactory.getLog(getClass());
	
	protected AbstractOrderBuilder<T> orderBuilder;
	protected AbstractWhereBuilder<T> whereBuilder;
	
	public AbstractQueryBuilder<T> join(String property) {
		return this;
	}

	public AbstractWhereBuilder<T> where() {
		return whereBuilder;
	}
	
	public AbstractOrderBuilder<T> order() {
		return orderBuilder;
	}
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public abstract static class AbstractWhereBuilder<T>{
		
		protected AbstractCriteriaBuilder<T> predicateBuilder;
		protected AbstractOrderBuilder<T> orderBuilder;
		
		protected enum State{ AND, AND_START, AND_END, OR, OR_START, OR_END }
		protected State state;
		
		
		public AbstractWhereBuilder(AbstractOrderBuilder<T> orderBuilder) {
			this.orderBuilder = orderBuilder;
		}

		public AbstractOrderBuilder<T> order() { 
			return orderBuilder; 
		}
		
		public AbstractCriteriaBuilder<T> and() { 
			this.state = State.AND; 
			return predicateBuilder;
		}
		public AbstractCriteriaBuilder<T> andStart() { 
			this.state = State.AND_START; 
			return predicateBuilder;
		}
		public AbstractWhereBuilder<T> andEnd() { 
			this.state = State.AND_START; 
			return chain(null);
		}
		public AbstractCriteriaBuilder<T> or() { 
			this.state = State.OR; 
			return predicateBuilder;
		}
		public AbstractCriteriaBuilder<T> orStart() { 
			this.state = State.OR_START; 
			return predicateBuilder;
		}
		public AbstractWhereBuilder<T> orEnd() { 
			this.state = State.OR_END; 
			return chain(null);
		}
		
		
		private <X> AbstractWhereBuilder<T> chain(X criteria) { 
			if(criteria == null) return this;
			switch (this.state) {
				case AND: break;
				case AND_START: break;
				case AND_END: break;
				case OR: break;
				case OR_START: break;
				case OR_END: break;
			}
			return this;
		}
	}
	
	public abstract static class AbstractCriteriaBuilder<T>{
		
		protected AbstractWhereBuilder<T> whereBuilder;
		
		public AbstractCriteriaBuilder(AbstractWhereBuilder<T> whereBuilder) {
			this.whereBuilder = whereBuilder;
		}
		
		public abstract AbstractWhereBuilder<T> isNull(String property);
		public abstract AbstractWhereBuilder<T> isNotNull(String property);
		public abstract AbstractWhereBuilder<T> eq(String property, Object value);
		public abstract AbstractWhereBuilder<T> notEq(String property, Object value);
		public abstract AbstractWhereBuilder<T> like(String property, Object value);
		public abstract AbstractWhereBuilder<T> notLike(String property, Object value);
		public abstract AbstractWhereBuilder<T> between(String property, Object value);
		public abstract AbstractWhereBuilder<T> gt(String property, Object value);
		public abstract AbstractWhereBuilder<T> gte(String property, Object value);
		public abstract AbstractWhereBuilder<T> lt(String property, Object value);
		public abstract AbstractWhereBuilder<T> lte(String property, Object value);
		public abstract AbstractWhereBuilder<T> in(String property, Object value);
		public abstract AbstractWhereBuilder<T> notIn(String property, Object value);
	}
	
	////////////////////////////////////////////
	//
	////////////////////////////////////////////
	public abstract static class AbstractOrderBuilder<T>{
		
		public abstract AbstractOrderBuilder<T> asc(String property);
		
		public abstract AbstractOrderBuilder<T> desc(String property) ;
	}
	
}
