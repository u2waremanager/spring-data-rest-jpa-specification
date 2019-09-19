package org.springframework.data.rest.webmvc;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.support.DefaultedPageable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

public abstract class AbstractRestController<T,ID extends Serializable> extends AbstractRepositoryController{

	private final Class<?> DOMAIN_TYPE = GenericTypeResolver.resolveTypeArguments(getClass(), AbstractRestController.class)[0];

	@SuppressWarnings("unchecked")
	protected Class<T> getDomainType(){
		return (Class<T>) DOMAIN_TYPE;
	}
	protected ResourceMetadata getResourceMetadata() {
		return mappings.getMetadataFor(getDomainType());
	}
	protected PersistentEntity<?,?> getPersistentEntity() {
		return entities.getPersistentEntity(getDomainType()).get();
	}
	
	public ResponseEntity<?> optionsForCollectionResource() {
		return super.optionsForCollectionResource(getResourceMetadata());
	}
	public ResponseEntity<?> headForCollectionResource(DefaultedPageable pageable) throws HttpRequestMethodNotSupportedException {
		return super.headForCollectionResource(pageable, getResourceMetadata());
	}
	public Resources<?> getCollectionResource(DefaultedPageable pageable, PersistentEntityResourceAssembler assembler, Iterable<?> results) throws ResourceNotFoundException, HttpRequestMethodNotSupportedException {
		return super.getCollectionResource(pageable, assembler, results, getResourceMetadata());
	}
	public ResponseEntity<ResourceSupport> postCollectionResource(PersistentEntityResourceAssembler assembler, HttpHeaders headers, Object result) throws HttpRequestMethodNotSupportedException {
		return super.postCollectionResource(assembler, headers.getFirst(ACCEPT_HEADER), result);
	}
	public ResponseEntity<?> optionsForItemResource() {
		return super.optionsForItemResource(getResourceMetadata());
	}
	public ResponseEntity<?> headForItemResource(PersistentEntityResourceAssembler assembler, Optional<?> result) throws HttpRequestMethodNotSupportedException {
		return super.headForItemResource(assembler, result, getPersistentEntity());
	}
	public ResponseEntity<Resource<?>> getItemResource(PersistentEntityResourceAssembler assembler, HttpHeaders headers, Optional<?> result) throws HttpRequestMethodNotSupportedException {
		return super.getItemResource(assembler, headers, result, getPersistentEntity());
	}
	public ResponseEntity<? extends ResourceSupport> putItemResource(PersistentEntityResourceAssembler assembler,HttpHeaders headers, Object result) throws HttpRequestMethodNotSupportedException {
		return super.putItemResource(assembler, headers.getFirst(ACCEPT_HEADER), result);
	}
	public ResponseEntity<? extends ResourceSupport> patchItemResource(PersistentEntityResourceAssembler assembler,HttpHeaders headers, Object result) throws HttpRequestMethodNotSupportedException {
		return super.patchItemResource(assembler, headers.getFirst(ACCEPT_HEADER), result);
	}
	public ResponseEntity<?> deleteItemResource(Object result)  {
		return super.deleteItemResource(result);
	}
}
