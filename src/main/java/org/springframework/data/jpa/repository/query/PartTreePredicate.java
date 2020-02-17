package org.springframework.data.jpa.repository.query;

import static org.springframework.data.repository.query.parser.Part.Type.IS_NOT_EMPTY;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_CONTAINING;
import static org.springframework.data.repository.query.parser.Part.Type.NOT_LIKE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.Part.IgnoreCaseType;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.repository.query.parser.PartTree.OrPart;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class PartTreePredicate<X> {

	protected Log logger = LogFactory.getLog(getClass());

	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Root<X> root;
	//private final CriteriaQuery<?> query;
	private final CriteriaBuilder builder;
	
	public PartTreePredicate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		
		Assert.notNull(root, "root is requried");
		Assert.notNull(query, "query is requried");
		Assert.notNull(builder, "builder is requried");
		
		this.root = root;
		//this.query = query;
		this.builder = builder;
	}
	
	
//	public Predicate build(PartTree partTree, X params){
//		return toPredicate(partTree, BeanWrapperFactory.getInstance(params));
//	}
//	public Predicate build(PartTree partTree, Object... params){
//		return toPredicate(partTree, BeanWrapperFactory.getInstance(params));
//	}
//	public Predicate build(PartTree partTree, MultiValueMap<String,Object> params){
//		return toPredicate(partTree,  BeanWrapperFactory.getInstance(params));
//	}
	public Predicate build(PartTree partTree, BeanWrapper parameter){
		return toPredicate(partTree,  parameter);
	}
	

	private Predicate toPredicate(PartTree tree, BeanWrapper parameter) {

		Predicate base = null;

		for (OrPart node : tree) {

			Iterator<Part> parts = node.iterator();

			if (!parts.hasNext()) {
				throw new IllegalStateException(String.format("No part found in PartTree %s!", tree));
			}

			Predicate criteria = create(parts.next(), parameter);
			
			while (parts.hasNext()) {
				criteria = and(parts.next(), criteria, parameter);
			}

			base = base == null ? criteria : or(base, criteria);
		}
		
		return base;
	}
	
	private Predicate and(Part part, Predicate base, BeanWrapper parameter) {
		Predicate criteria = create(part, parameter);
		return criteria != null ? ( (base != null) ? builder.and(base, criteria) : criteria ): base;
	}

	private Predicate or(Predicate base, Predicate predicate) {
		return builder.or(base, predicate);
	}

	private Predicate create(Part part, BeanWrapper parameter) {
		return build(part, parameter.getPropertyValue(part.getProperty().getSegment()));
	}
	

	public Predicate build(Part part, Object parameter) {
		try {
			Predicate p = toPredicate(part, parameter);
			logger.info(part);
			return p;
		}catch(Exception e) {
			logger.info(part+" -> "+e.getMessage());
			return null;
		}
	}
	
	private Predicate toPredicate(Part part, Object value) {
		
		Assert.notNull(value, "value is null");

		PropertyPath property = part.getProperty();
		Type type = part.getType();

		switch (type) {
			case BETWEEN:
//				ParameterMetadata<Comparable> first = provider.next(part);
//				ParameterMetadata<Comparable> second = provider.next(part);
//				return builder.between(getComparablePath(root, part), first.getExpression(), second.getExpression());
				Collection<Comparable> parameters = getCollectionValue(root, part, value);
				Iterator<Comparable> iterator = parameters.iterator();
				return builder.between(getComparablePath(root, part), iterator.next(), iterator.next());
			case AFTER:
			case GREATER_THAN:
//				return builder.greaterThan(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.greaterThan(getComparablePath(root, part), getSingleLiteral(root, part, value));
			case GREATER_THAN_EQUAL:
//				return builder.greaterThanOrEqualTo(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.greaterThanOrEqualTo(getComparablePath(root, part), getSingleLiteral(root, part, value));
			case BEFORE:
			case LESS_THAN:
//				return builder.lessThan(getComparablePath(root, part), provider.next(part, Comparable.class).getExpression());
				return builder.lessThan(getComparablePath(root, part), getSingleLiteral(root, part, value));
			case LESS_THAN_EQUAL:
//				return builder.lessThanOrEqualTo(getComparablePath(root, part),
//						provider.next(part, Comparable.class).getExpression());
				return builder.lessThanOrEqualTo(getComparablePath(root, part), getSingleLiteral(root, part, value));
			case IS_NULL:
				return getTypedPath(root, part).isNull();
			case IS_NOT_NULL:
				return getTypedPath(root, part).isNotNull();
			case NOT_IN:
//				return getTypedPath(root, part).in(provider.next(part, Collection.class).getExpression()).not();
				return getTypedPath(root, part).in(getCollectionValue(root, part, value)).not();
			case IN:
//				return getTypedPath(root, part).in(provider.next(part, Collection.class).getExpression());
				return getTypedPath(root, part).in(getCollectionValue(root, part, value));
				
			case STARTING_WITH:
			case ENDING_WITH:
			case CONTAINING:
			case NOT_CONTAINING:

				if (property.getLeafProperty().isCollection()) {

//					Expression<Collection<Object>> propertyExpression = traversePath(root, property);
//					ParameterExpression<Object> parameterExpression = provider.next(part).getExpression();
//					// Can't just call .not() in case of negation as EclipseLink chokes on that.
//					return type.equals(NOT_CONTAINING) ? isNotMember(builder, parameterExpression, propertyExpression)
//							: isMember(builder, parameterExpression, propertyExpression);
					
				}

			case LIKE:
			case NOT_LIKE:
//				Expression<String> stringPath = getTypedPath(root, part);
//				Expression<String> propertyExpression = upperIfIgnoreCase(stringPath);
//				Expression<String> parameterExpression = upperIfIgnoreCase(provider.next(part, String.class).getExpression());
//				Predicate like = builder.like(propertyExpression, parameterExpression, escape.getEscapeCharacter());
//				return type.equals(NOT_LIKE) || type.equals(NOT_CONTAINING) ? like.not() : like;
				Predicate like = builder.like(getIgnoreCasedPath(root, part), getSingleLiteral(root, part, value));
				return type.equals(NOT_LIKE) || type.equals(NOT_CONTAINING) ? like.not() : like;
				
				
			case TRUE:
				Expression<Boolean> truePath = getTypedPath(root, part);
				return builder.isTrue(truePath);
			case FALSE:
				Expression<Boolean> falsePath = getTypedPath(root, part);
				return builder.isFalse(falsePath);
			case SIMPLE_PROPERTY:
//				ParameterMetadata<Object> expression = provider.next(part);
//				Expression<Object> path = getTypedPath(root, part);
//				return expression.isIsNullParameter() ? path.isNull()
//						: builder.equal(upperIfIgnoreCase(path), upperIfIgnoreCase(expression.getExpression()));
				return builder.equal(getIgnoreCasedPath(root, part), getSingleLiteral(root, part, value));
				
			case NEGATING_SIMPLE_PROPERTY:
//				return builder.notEqual(upperIfIgnoreCase(getTypedPath(root, part)),
//						upperIfIgnoreCase(provider.next(part).getExpression()));
				return builder.notEqual(getIgnoreCasedPath(root, part), getSingleLiteral(root, part, value));
			case IS_EMPTY:
			case IS_NOT_EMPTY:

				if (!property.getLeafProperty().isCollection()) {
					throw new IllegalArgumentException("IsEmpty / IsNotEmpty can only be used on collection properties!");
				}

				Expression<Collection<Object>> collectionPath = traversePath(root, property);
				return type.equals(IS_NOT_EMPTY) ? builder.isNotEmpty(collectionPath) : builder.isEmpty(collectionPath);

			default:
				throw new IllegalArgumentException("Unsupported keyword " + type);
		}
	}	

	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	private <T> Expression<T> getSingleLiteral(Root<?> root, Part part, Object value) {
		
		if(value == null) return null;

		Object result = value;
		if (value instanceof Collection) {
			result = ((Collection) value).iterator().next();
		}
		if (ObjectUtils.isArray(value)) {
			result = ((Collection)Arrays.asList(ObjectUtils.toObjectArray(value))).iterator().next();
		}

		Expression<? extends T> expression = getTypedPath(root, part);
		if(! ClassUtils.isAssignableValue(expression.getJavaType(), result)) {
			result = objectMapper.convertValue(result, expression.getJavaType()) ;
		}
		
		if (String.class.equals(expression.getJavaType())) {
			
			String stringValue = (String)result;

			IgnoreCaseType shouldIgnoreCase = part.shouldIgnoreCase();
			Type type = part.getType();
			
			if(IgnoreCaseType.ALWAYS.equals(shouldIgnoreCase)) {
				stringValue = stringValue.toUpperCase();
			}else if(IgnoreCaseType.WHEN_POSSIBLE.equals(shouldIgnoreCase)) {
				if (canUpperCase(expression)) {
					stringValue = stringValue.toUpperCase();
				}
			}
			
			if(Type.STARTING_WITH.equals(type)) {
				stringValue = String.format("%s%%", stringValue);
			}else if(Type.ENDING_WITH.equals(type)) {
				stringValue = String.format("%%%s", stringValue);
			}else if(Type.CONTAINING.equals(type)) {
				stringValue = String.format("%%%s%%", stringValue);
			}else if(Type.NOT_CONTAINING.equals(type)) {
				stringValue = String.format("%%%s%%", stringValue);
			}
			
			result = stringValue;
		} 
		return (Expression<T>) builder.literal(result);
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public <T> Collection<T> getCollectionValue(Root<?> root, Part part, Object value) {

		if(value == null) return null;
		
		Collection collection = null;
		if (value instanceof Collection) {
			collection = (Collection) value;
		}else if (ObjectUtils.isArray(value)) {
			collection = (Collection)Arrays.asList(ObjectUtils.toObjectArray(value));
		}else {
			collection = (Collection) Collections.singleton(value);
		}
		
		Expression<? extends T> expression = getTypedPath(root, part);

		Collection<T> result = new ArrayList<T>();
		collection.forEach((v)->{ 
			if(! ClassUtils.isAssignableValue(expression.getJavaType(), v)) {
				result.add(objectMapper.convertValue(v, expression.getJavaType()));
			}else {
				result.add((T)v);
			}
		});
		
		return result;
	}
	
	private <T> Expression<T> traversePath(Path<?> root, PropertyPath path) {
		Path<Object> result = root.get(path.getSegment());
		return (Expression<T>) (path.hasNext() ? traversePath(result, path.next()) : result);
	}
	
	private Expression<? extends Comparable> getComparablePath(Root<?> root, Part part) {
		return getTypedPath(root, part);
	}
	
	private <T> Expression<T> getIgnoreCasedPath(Root<?> root, Part part) {

		Expression<? extends T> expression = getTypedPath(root, part);
		
		switch (part.shouldIgnoreCase()) {

			case ALWAYS:

				Assert.state(canUpperCase(expression), "Unable to ignore case of " + expression.getJavaType().getName()
						+ " types, the property '" + part.getProperty().getSegment() + "' must reference a String");
				return (Expression<T>) builder.upper((Expression<String>) expression);

			case WHEN_POSSIBLE:

				if (canUpperCase(expression)) {
					return (Expression<T>) builder.upper((Expression<String>) expression);
				}

			case NEVER:
			default:

				return (Expression<T>) expression;
		}
	}
	private boolean canUpperCase(Expression<?> expression) {
		return String.class.equals(expression.getJavaType());
	}
	
	
	//////////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////////
	public static <T> Expression<T> getTypedPath(Root<?> root, String property) {
		return getTypedPath(root, new Part(property, root.getJavaType()));
	}
	
	public static <T> Expression<T> getTypedPath(Root<?> root, Part part) {
		return QueryUtils.toExpressionRecursively(root, part.getProperty());
//		return ExpressionRecursivelyUtils.toExpressionRecursively(root, part.getProperty());
	}
	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {
		return QueryUtils.toExpressionRecursively(from, property, false);
	}
	
	public static Expression<?> toExpressionRecursively(Root<?> root, String property){
		PropertyPath path = PropertyPath.from(property, root.getJavaType());
		return QueryUtils.toExpressionRecursively(root, path);
	}
	
	
}
