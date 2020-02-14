package org.springframework.data.jpa.repository.query;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;

import org.springframework.data.mapping.PropertyPath;

public class ExpressionRecursivelyUtils {

	public static Expression<?> toExpressionRecursively(Root<?> root, String property){
		PropertyPath path = PropertyPath.from(property, root.getJavaType());
		return ExpressionRecursivelyUtils.toExpressionRecursively(root, path);
	}
	
	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {
		return QueryUtils.toExpressionRecursively(from, property, false);
	}
	
}
