package org.springframework.data.jpa.repository.query.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

@SuppressWarnings({"unchecked","rawtypes"})
abstract class AbstractCriteriaBuilder<X,Y> {

	private BooleanBuilder root;
	private PathBuilder<?> path;

	protected AbstractCriteriaBuilder(BooleanBuilder root, PathBuilder<?> path){
		this.root = root;
		this.path = path;
	}
	
	public X and(Predicate right) {
		root.and(right); return (X)this;
	}
	
	public X or(Predicate right) {
		root.and(right); return (X)this;
	}

	public AndBuilder<X> and() {
		return new AndBuilder(this, root, path) {};
	}

	public OrBuilder<X> or() {
		return new OrBuilder(this, root, path) {};
	}
	
	public AndStartBuilder<X> andStart() {
		return new AndStartBuilder(this, root, path) {};
	}

	public OrStartBuilder<X> orStart() {
		return new OrStartBuilder(this, root, path) {};
	}

	protected Predicate getRoot(){
		return root;
	}

	public abstract Y build();
	
	
	
	////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////
	public abstract static class AndStartBuilder<Z>{
		
		private Z builder;
		private PathBuilder<?> path;
		private BooleanBuilder base;
		private BooleanBuilder sub;
		
		private AndStartBuilder(Z builder, BooleanBuilder base, PathBuilder<?> path) {
			this.builder = builder;
			this.base = base;
			this.path = path;
			this.sub = new BooleanBuilder();
		}
		
		public AndStartBuilder<Z> and(Predicate right) {
			sub.and(right); return this;
		}
		
		public AndStartBuilder<Z> or(Predicate right) {
			sub.or(right); return this;
		}
		
		public AndBuilder<AndStartBuilder<Z>> and() {
			return new AndBuilder<AndStartBuilder<Z>>(this, sub, path) {};
		}

		public OrBuilder<AndStartBuilder<Z>> or() {
			return new OrBuilder<AndStartBuilder<Z>>(this, sub, path) {};
		}
		
		public AndStartBuilder<AndStartBuilder<Z>> andStart() {
			return new AndStartBuilder<AndStartBuilder<Z>>(this, sub, path) {};
		}
		public OrStartBuilder<AndStartBuilder<Z>> orStart() {
			return new OrStartBuilder<AndStartBuilder<Z>>(this, sub, path) {};
		}
		
		public Z andEnd() {
			base.and(sub);
			return builder;
		}
	}

	
	public static class OrStartBuilder<Z>{
		
		private Z builder;
		private PathBuilder<?> path;
		private BooleanBuilder base;
		private BooleanBuilder sub;
		
		private OrStartBuilder(Z builder, BooleanBuilder base, PathBuilder<?> path) {
			this.builder = builder;
			this.base = base;
			this.path = path;
			this.sub = new BooleanBuilder();
		}

		public OrStartBuilder<Z> and(Predicate right) {
			sub.and(right); return this;
		}
		
		public OrStartBuilder<Z> or(Predicate right) {
			sub.or(right); return this;
		}
		
		public AndBuilder<OrStartBuilder<Z>> and() {
			return new AndBuilder<OrStartBuilder<Z>>(this, sub, path) {};
		}

		public OrBuilder<OrStartBuilder<Z>> or() {
			return new OrBuilder<OrStartBuilder<Z>>(this, sub, path) {};
		}

		public OrStartBuilder<OrStartBuilder<Z>> orStart() {
			return new OrStartBuilder<OrStartBuilder<Z>>(this, sub, path) {};
		}
		public AndStartBuilder<OrStartBuilder<Z>> andStart() {
			return new AndStartBuilder<OrStartBuilder<Z>>(this, sub, path) {};
		}
		
		public Z orEnd() {
			base.or(sub);
			return builder;
		}
	}

	
	public abstract static class AndBuilder<Z> extends OperationBuilder<Z>{
		
		private Z builder;
		private BooleanBuilder base;
		
		private AndBuilder(Z builder, BooleanBuilder base, PathBuilder<?> path) {
			super(path);
			this.builder = builder;
			this.base = base;
		}
		
		protected Z add(Predicate right) {
			base.and(right);
			return builder;
		}
	}
	
	public abstract static class OrBuilder<Z> extends OperationBuilder<Z>{
	
		private Z builder;
		private BooleanBuilder base;
		
		private OrBuilder(Z builder, BooleanBuilder base, PathBuilder<?> path) {
			super(path);
			this.builder = builder;
			this.base = base;
		}
		
		protected Z add(Predicate right) {
			base.or(right);
			return builder;
		}
	}
	
	
	private abstract static class OperationBuilder<Z>{
		
		private PathBuilder<?> path;
		
		private OperationBuilder(PathBuilder<?> path) {
			this.path = path;
		}
		
		protected abstract Z add(Predicate right);
		
		public Z eq(String property, Comparable<?> right) {
			return add(path.get(property).eq(right));
		}
		public Z goe(String property, Comparable<?> right) {
			return add(path.getComparable(property, Comparable.class).goe(right));
		}
		public Z like(String property, String right) {
			return add(path.getString(property).like(right));
		}
	}
	
}
