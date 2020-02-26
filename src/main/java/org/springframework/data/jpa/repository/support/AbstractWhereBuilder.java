package org.springframework.data.jpa.repository.support;

import java.util.Collection;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

@SuppressWarnings({"unchecked","rawtypes"})
public abstract class AbstractWhereBuilder<X> {

	public static class BaseBuilder {
		
		private PathBuilder<?> path;
		private BooleanBuilder predicate;
		
		private BaseBuilder(BaseBuilder builder) {
			this(new BooleanBuilder(), builder.getPath());
		}
		
		public BaseBuilder(PathBuilder<?> path) {
			this(new BooleanBuilder(), path);
		}

		public BaseBuilder(BooleanBuilder predicate, PathBuilder<?> path) {
			this.predicate = predicate;
			this.path = path;
		}
		
		
		public BaseBuilder and(Predicate right) {
			predicate.and(right);
			return this;
		}
		public BaseBuilder or(Predicate right) {
			predicate.or(right);
			return this;
		}
		
		public PathBuilder<?> getPath() {
			return path;
		}

		public BooleanBuilder getBase() {
			return predicate;
		}
	}
	
	
	
	protected BaseBuilder builder;

	protected AbstractWhereBuilder(BaseBuilder builder){
		this.builder = builder;
	}

	public X and(Predicate right) {
		builder.and(right); return (X)this;
	}
	
	public X or(Predicate right) {
		builder.or(right); return (X)this;
	}

	public AndBuilder<X> and() {
		return new AndBuilder(this, builder) {};
	}

	public OrBuilder<X> or() {
		return new OrBuilder(this, builder) {};
	}
	
	public AndStartBuilder<X> andStart() {
		return new AndStartBuilder(this, builder) {};
	}

	public OrStartBuilder<X> orStart() {
		return new OrStartBuilder(this, builder) {};
	}
	
//	protected BaseBuilder getBuilder() {
//		return builder;
//	}
	
	////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////
	public abstract static class AndStartBuilder<W>{
		
		private W where;
		private BaseBuilder builder;
		private BaseBuilder sub;
		
		private AndStartBuilder(W where, BaseBuilder builder) {
			this.where = where;
			this.builder = builder;
			this.sub = new BaseBuilder(builder);
		}
		
		public AndStartBuilder<W> and(Predicate right) {
			sub.and(right); return this;
		}
		
		public AndStartBuilder<W> or(Predicate right) {
			sub.or(right); return this;
		}
		
		public AndBuilder<AndStartBuilder<W>> and() {
			return new AndBuilder<AndStartBuilder<W>>(this, sub) {};
		}

		public OrBuilder<AndStartBuilder<W>> or() {
			return new OrBuilder<AndStartBuilder<W>>(this, sub) {};
		}
		
		public AndStartBuilder<AndStartBuilder<W>> andStart() {
			return new AndStartBuilder<AndStartBuilder<W>>(this, sub) {};
		}
		public OrStartBuilder<AndStartBuilder<W>> orStart() {
			return new OrStartBuilder<AndStartBuilder<W>>(this, sub) {};
		}
		
		public W andEnd() {
			builder.and(sub.getBase()); return where;
		}
	}

	
	public abstract static class OrStartBuilder<Z>{
		
		private Z where;
		private BaseBuilder builder;
		private BaseBuilder sub;
		
		private OrStartBuilder(Z where, BaseBuilder builder) {
			this.where = where;
			this.builder = builder;
			this.sub = new BaseBuilder(builder);
		}


		public OrStartBuilder<Z> and(Predicate right) {
			sub.and(right); return this;
		}
		
		public OrStartBuilder<Z> or(Predicate right) {
			sub.or(right); return this;
		}
		
		public AndBuilder<OrStartBuilder<Z>> and() {
			return new AndBuilder<OrStartBuilder<Z>>(this, sub) {};
		}

		public OrBuilder<OrStartBuilder<Z>> or() {
			return new OrBuilder<OrStartBuilder<Z>>(this, sub) {};
		}

		public AndStartBuilder<OrStartBuilder<Z>> andStart() {
			return new AndStartBuilder<OrStartBuilder<Z>>(this, sub) {};
		}
		public OrStartBuilder<OrStartBuilder<Z>> orStart() {
			return new OrStartBuilder<OrStartBuilder<Z>>(this, sub) {};
		}
		
		public Z orEnd() {
			builder.or(sub.getBase()); return where;
		}
	}

	
	public abstract static class AndBuilder<W> extends OperationBuilder<W>{
		
		private AndBuilder(W where, BaseBuilder builder) {
			super(where, builder);
		}
		
		protected W add(Predicate right) {
			builder.and(right); return where;
		}
	}
	
	public abstract static class OrBuilder<W> extends OperationBuilder<W>{
	
		private OrBuilder(W where, BaseBuilder builder) {
			super(where, builder);
			this.where = where;
		}
		
		protected W add(Predicate right) {
			builder.or(right); return where;
		}
	}
	
	
	private abstract static class OperationBuilder<W>{
		
		protected W where;
		protected BaseBuilder builder;
		
		private OperationBuilder(W where, BaseBuilder builder) {
			this.where = where;
			this.builder = builder;
		}
		
		protected abstract W add(Predicate right);
		
		
		public W isNull(String property) {
			return add(builder.getPath().get(property).isNull());
		}
		public W isNotNull(String property) {
			return add(builder.getPath().get(property).isNotNull());
		}
		public W eq(String property, Object right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().get(property).eq(right));
		}
		public W ne(String property, Object right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().get(property).ne(right));
		}

		public W like(String property, String right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getString(property).like(right));
		}
		
		public W notLike(String property, String right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getString(property).notLike(right));
		}

		
		public W between(String property, Comparable<?> from, Comparable<?> to) {
			if(StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) return where;
			return add(builder.getPath().getComparable(property, Comparable.class).between(from, to));
		}
		
		
		public W gt(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getComparable(property, Comparable.class).gt(right));
		}
		
		public W goe(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getComparable(property, Comparable.class).goe(right));
		}
		
		public W lt(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getComparable(property, Comparable.class).lt(right));
		}
		
		public W loe(String property, Comparable<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().getComparable(property, Comparable.class).loe(right));
		}
		
		public W in(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().get(property).in(right));
		}
		
		public W notIn(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			return add(builder.getPath().get(property).notIn(right));
		}
		
		public W containsAll(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			BooleanBuilder b = new BooleanBuilder();
			right.forEach(r->{
				b.and(builder.getPath().getCollection(property, Object.class).contains(r));
			});
			return add(b);
		}
		
		public W containsAny(String property, Collection<?> right) {
			if(StringUtils.isEmpty(right)) return where;
			BooleanBuilder b = new BooleanBuilder();
			right.forEach(r->{
				b.or(builder.getPath().getCollection(property, Object.class).contains(r));
			});
			return add(b);
		}
	}
}
