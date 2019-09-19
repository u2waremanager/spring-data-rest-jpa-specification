package org.springframework.data.jpa.repository.query;

import static org.springframework.data.jpa.repository.query.QueryUtils.toExpressionRecursively;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_CONTAINING;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_LIKE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.repository.query.parser.PartTree.OrPart;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class PredicateBuilder {

	protected static Log logger = LogFactory.getLog(PredicateBuilder.class);
	
	private List<List<Matcher>> orParts = new ArrayList<List<Matcher>>();
	private List<Matcher> parts;
	
	public PredicateBuilder() {
		or();
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////
	public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Predicate predicate = null;
		
		for (List<Matcher> andPart : orParts) {
			
			Predicate or = null;
			
			for (Matcher p : andPart) {
				if(p.isPresent()) {
					Predicate criteria = p.toPredicate(root, cb);
					if (criteria != null) {
						or = or == null ? criteria : cb.and(or, criteria);
					}
				}
			}
			
			if (or != null) {
				predicate = predicate == null ? or : cb.or(predicate, or);
			}
		}
		
		return predicate;
	}
	

	/////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////
	public static enum Handler{
		NULL_INCLUDE,
		STARTING,
		ENDING,
		CONTAINING,
		IGNORE_CASE;
	}
	
	public PredicateBuilder or() {
		ArrayList<Matcher> andParts = new ArrayList<Matcher>();
		this.orParts.add(andParts);
		this.parts = andParts;
		return this;
	}
	
	private PredicateBuilder and(String property, Type type, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, type, value, handlers));
		return this;
	}
	
	public PredicateBuilder with(PartTree partTree, MultiValueMap<String, Object> params) {

		for (OrPart node : partTree) {
			or();
			
			for (Part part : node) {
				// logger.info("toPredicate 3 "+part);
				if (part.getProperty() != null) {
					
					String property = part.getProperty().getSegment();
					Type type = part.getType();
					Object value = Type.BETWEEN.equals(type) ? params.get(property) : params.getFirst(property);

					Handler[] handlers = new Handler[4];
					if(params.containsKey(property)) {
						handlers[0] = Handler.NULL_INCLUDE;
					}
					if(! IgnoreCaseType.NEVER.equals(part.shouldIgnoreCase())) {
						handlers[1] = Handler.IGNORE_CASE;
					}
					if(Type.CONTAINING.equals(type)) {
						handlers[2] = Handler.CONTAINING;
					}
					if(Type.STARTING_WITH.equals(type)) {
						handlers[3] = Handler.STARTING;
					}
					if(Type.ENDING_WITH.equals(type)) {
						handlers[4] = Handler.ENDING;
					}
					
					and(property, type, value, handlers);
				}
			}
		}
		
		
		return this;
	}
	
	/*
	private static class Parameters {

		private Map<String, ?> map;
		private BeanWrapper bean;
		
		@SuppressWarnings("unchecked")
		private Parameters(Object source) {
			if(ClassUtils.isAssignableValue(Map.class, source)){
//				logger.info("Map "+source);
				this.map = (Map<String, ?>)source;
			} else if(ClassUtils.isAssignableValue(BeanWrapper.class, source)) {
//				logger.info("BeanWrapper "+source);
				this.bean = (BeanWrapper)source;
			} else {
//				logger.info("BeanWrapperImpl "+source);
				this.bean = new BeanWrapperImpl(source);
			}
		}

		private Object get(String key) {
			Object value = null;
			if(map != null) { value = map.get(key); }
			if(bean != null) { value = bean.getPropertyValue(key);}
			return value;
		}

		private Object getFirst(String key) {
			Object value = get(key);
			if(ClassUtils.isAssignableValue(List.class, value)) {
				List<?> list = (List<?>)value; 
				return (list != null && list.size() > 0 ) ? list.get(0) : null;
			}else if(ObjectUtils.isArray(value)){
				//AAAAAAAAAAAAAAAA
				//return value;
				throw new RuntimeException(value.getClass().getName());
			}else {
				return value;
			}
		}

		private boolean contains(String key) {
			if(map != null) { return map.containsKey(key);}
			if(bean != null) {return bean.isReadableProperty(key);}
			return false;
		}
		
		@Override
		public String toString() {
			if(map != null) { return map.toString();}
			if(bean != null) {return bean.toString();}
			return "???";
		}
	}
	*/
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////
	public PredicateBuilder isNull(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.IS_NULL, value, handlers));
		return this;
	}

	public PredicateBuilder isNotNull(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.IS_NOT_NULL, value, handlers));
		return this;
	}

	public PredicateBuilder eq(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.SIMPLE_PROPERTY, value, handlers));
		return this;
	}

	public PredicateBuilder notEq(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.NEGATING_SIMPLE_PROPERTY, value, handlers));
		return this;
	}

	public PredicateBuilder in(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.IN, value, handlers));
		return this;
	}

	public PredicateBuilder notIn(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.NOT_IN, value, handlers));
		return this;
	}

	public PredicateBuilder like(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.LIKE, value, handlers));
		return this;
	}

	public PredicateBuilder notLike(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.NOT_LIKE, value, handlers));
		return this;
	}

	public PredicateBuilder between(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.BETWEEN, value, handlers));
		return this;
	}

	public PredicateBuilder gt(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.GREATER_THAN, value, handlers));
		return this;
	}

	public PredicateBuilder gte(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.GREATER_THAN_EQUAL, value, handlers));
		return this;
	}

	public PredicateBuilder lt(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.LESS_THAN, value, handlers));
		return this;
	}

	public PredicateBuilder lte(String property, Object value, Handler... handlers) {
		this.parts.add(new Matcher(property, Type.LESS_THAN_EQUAL, value, handlers));
		return this;
	}
	
	/////////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////////
	private static class Matcher {
		
		private String property;
		private Type type;
		private Object value;
		private Handler[] matchers;
		
		private Matcher(String property, Type type, Object value, Handler... matchers) {
			this.property = property;
			this.type = type;
			this.value = value;
			this.matchers = matchers;
		}

		private boolean contains(Handler matcher) {
			for(Handler m : matchers) {
				if(matcher.equals(m)) {
					return true;
				}
			}
			return false;
		}
		
		private boolean isPresent() {
			if(contains(Handler.NULL_INCLUDE)) {
				return true;
			}else {
				if(StringUtils.isEmpty(value)) {
					return false;
				}else{
					
					if("...".equals(value.toString())) {
						return false;
					}else {
						return true;
					}
				}
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Predicate toPredicate(Root<?> root, CriteriaBuilder builder) {

			if(! isPresent()) return null;
			
			PropertyPath path = PropertyPath.from(property, root.getJavaType());
			
			switch (type) {
				case BETWEEN:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					Iterator<?> v = ((Iterable)value).iterator();
					return builder.between(getComparablePath(root, path), (Comparable) v.next(), (Comparable) v.next());
				case AFTER:
				case GREATER_THAN:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return builder.greaterThan(getComparablePath(root, path), (Comparable)value);

				case GREATER_THAN_EQUAL:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return builder.greaterThanOrEqualTo(getComparablePath(root, path), (Comparable)value);
					
				case BEFORE:
				case LESS_THAN:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return builder.lessThan(getComparablePath(root, path), (Comparable)value);

				case LESS_THAN_EQUAL:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return builder.lessThanOrEqualTo(getComparablePath(root, path), (Comparable)value);
				case IS_NULL:
					logger.info(path.getSegment()+" ["+type.name()+"] ");
					return getTypedPath(root, path).isNull();
				case IS_NOT_NULL:
					logger.info(path.getSegment()+" ["+type.name()+"] ");
					return getTypedPath(root, path).isNotNull();
				case NOT_IN:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return getTypedPath(root, path).in((Collection)value).not();
				case IN:
					logger.info(path.getSegment()+" ["+type.name()+"] "+value);
					return getTypedPath(root, path).in((Collection)value);
				case STARTING_WITH:
				case ENDING_WITH:
				case CONTAINING:
				case NOT_CONTAINING:
					if (path.getLeafProperty().isCollection()) {
						logger.info(path.getSegment()+" ["+type.name()+"] "+value);
						Expression<Collection<Object>> propertyExpression = traversePath(root, path);
						// Can't just call .not() in case of negation as EclipseLink chokes on that.
						return type.equals(NOT_CONTAINING) ? builder.isNotMember(value, propertyExpression) : builder.isMember(value, propertyExpression);
					}
				case LIKE:
				case NOT_LIKE:
					Expression<String> stringPath = upperIfIgnoreCase(getTypedPath(root, path), builder);
					String stringValue = resolveLikeValue(stringPath);
					logger.info(path.getSegment()+" ["+type.name()+"] "+stringValue);
					Predicate like = builder.like(stringPath, stringValue);
					return type.equals(NOT_LIKE) || type.equals(NOT_CONTAINING) ? like.not() : like;
					
				case TRUE:
					Expression<Boolean> truePath = getTypedPath(root, path);
					logger.info(path.getSegment()+" ["+type.name()+"] ");
					return builder.isTrue(truePath);
				case FALSE:
					Expression<Boolean> falsePath = getTypedPath(root, path);
					logger.info(path.getSegment()+" ["+type.name()+"] ");
					return builder.isFalse(falsePath);
					
				case SIMPLE_PROPERTY:
				case NEGATING_SIMPLE_PROPERTY:
					Expression<Object> equalsPath = upperIfIgnoreCase(getTypedPath(root, path), builder);
					Object equalsValue = resolveEqualsValue(equalsPath);
					logger.info(path.getSegment()+" ["+type.name()+"] "+equalsValue);
					Predicate eq = builder.equal(equalsPath, equalsValue);
					return type.equals(Type.NEGATING_SIMPLE_PROPERTY) ? eq.not() : eq;

				default:
					throw new IllegalArgumentException("Unsupported keyword " + type);
			}
		}	
		
		@SuppressWarnings("unchecked")
		private <T> T resolveEqualsValue(Expression<?> expression) {
			if(value == null) return null;
			
			if(String.class.equals(expression.getJavaType())) {
				String result = value.toString();
				if(contains(Handler.IGNORE_CASE)) {
					result = result.toUpperCase();
				}
				return (T)result;
			}
			return (T)value;
		}
		
		private String resolveLikeValue(Expression<?> expression) {
			
			if(String.class.equals(expression.getJavaType())) {

				String result = resolveEqualsValue(expression).toString();
				if(contains(Handler.CONTAINING)) {
					result = ("%" + result + "%");
				}else if(contains(Handler.STARTING)) {
					result = (result + "%");
				}else if(contains(Handler.ENDING)) {
					result = ("%" + result);
				}
				return result;
			}
			return null;
		}
		
		@SuppressWarnings({ "unchecked"})
		private <T> Expression<T> upperIfIgnoreCase(Expression<?> expression, CriteriaBuilder builder) {
			if(canUpperCase(expression)) {
				return (Expression<T>) builder.upper((Expression<String>) expression);
			}else {
				return (Expression<T>) expression;
			}
		}

		private boolean canUpperCase(Expression<?> expression) {
			return contains(Handler.IGNORE_CASE) && String.class.equals(expression.getJavaType());
		}
		
		@SuppressWarnings({ "unchecked"})
		private <T> Expression<T> traversePath(Path<?> root, PropertyPath path) {
			Path<Object> result = root.get(path.getSegment());
			return (Expression<T>) (path.hasNext() ? traversePath(result, path.next()) : result);
		}
		
		private <T> Expression<T> getTypedPath(Root<?> root, PropertyPath path) {
			return toExpressionRecursively(root, path);
		}

		@SuppressWarnings({"rawtypes"})
		private <T extends Comparable> Expression<T> getComparablePath(Root<?> root, PropertyPath path) {
			return getTypedPath(root, path);
		}
	}
}
