package org.springframework.data.jpa.repository.query;


import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class JpaSpecification<T> implements Specification<T> {

	private PredicateBuilder predicate;
	
	private Object payload;
	
	private Specification<T> specification;
	private PartTree partTree;
	private String name;
	
	private JpaSpecification() {
	}
	
	
	@SuppressWarnings("unchecked")
	public <C> C getPayload() {
		return (C) payload;
	}

	public PredicateBuilder getPredicateBuilder() {
		if(predicate == null) 
			predicate = new PredicateBuilder();
		return predicate;
	}

	@Override
	public String toString() {
		return name;
	}
	
	//////////////////////////////////////////////
	//
	///////////////////////////////////////////////
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		if(specification != null) {
			return specification.toPredicate(root, query, cb);
		}else if(partTree != null){
			return getPredicateBuilder().with(partTree, getPayload()).build(root, query, cb);
		}else {
			return getPredicateBuilder().build(root, query, cb);
		}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////////////////
//	private static Log logger = LogFactory.getLog(JpaSpecification.class );
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static <X> JpaSpecification<X> of(final MultiValueMap<String, Object> parameter, final String source) {
		
		Assert.notNull(parameter, "parameter must not be null!");
		Assert.notNull(source, "source must not be null!");
			
//		logger.info("source: "+source);
//		logger.info("parameter: "+parameter);
		
		return new JpaSpecification<X>() {
			@Override
			public Predicate toPredicate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				PartTree partTree = new PartTree(source, root.getJavaType());
				return getPredicateBuilder().with(partTree, parameter).build(root, query, cb);
			}
		};
	}
	
	public static <X> JpaSpecification<X> of(final MultiValueMap<String, Object> parameter, final String source, final Class<X> domainClass) {

		Assert.notNull(parameter, "parameter must not be null!");
		Assert.notNull(domainClass, "source must not be null!");
			
//		logger.info("source: "+source);
//		logger.info("parameter: "+parameter);
//		logger.info("domain: "+domainClass);
		
		JpaSpecification<X> specification = new JpaSpecification<X>();
//		specification.type = domainClass;
		
		if (StringUtils.hasLength(source)) {
			Class<? extends Specification<?>> clazz = specificationClass(source);
			if (clazz != null) {
				Specification<X> delegate = specification(parameter, clazz);
				specification.name = delegate.getClass().getName();
				specification.payload = delegate;
				specification.specification = delegate;
				
			} else {
				PartTree partTree = new PartTree(source, domainClass);
				specification.name = source;
				specification.payload = parameter;
				specification.partTree = partTree;
			}

		} else {
			Class<? extends Specification<?>> clazz = specificationClass(domainClass.getName());
			if (clazz != null) {
				Specification<X> delegate = specification(parameter, clazz);
				specification.name = delegate.getClass().getName();
				specification.payload = delegate;
				specification.specification = delegate;
				
			} else {
				X payload = specification(parameter, domainClass);
				specification.name = specification.getClass().getName();
				specification.payload = payload;
			}
		}
		return specification;
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T> Class<? extends Specification<?>> specificationClass(String className) {
		try {
			Class<?> clazz = ClassUtils.forName(className, JpaSpecification.class.getClassLoader());
			if (ClassUtils.isAssignable(Specification.class, clazz)) {
				return (Class<? extends Specification<T>>) clazz;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <O> O specification(MultiValueMap<String, Object> payload, final Class<?> clazz)  {
	
	
		Map<String, Object> temp = new HashMap<String, Object>();
		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);

		for (PropertyDescriptor pd : beanWrapper.getPropertyDescriptors()) {

			String key = pd.getName();
			Class<?> type = pd.getPropertyType();
			boolean access = beanWrapper.isReadableProperty(key) && beanWrapper.isWritableProperty(key);

			if (access && payload.containsKey(key)) {
				Object value = type.isArray() ? payload.get(key) : payload.getFirst(key);
				if (value != null) {
					temp.put(key, value);
				}
			}

		}
		return (O)objectMapper.convertValue(temp, clazz);//(JpaSpecification<T>) ;
	}

	
}
