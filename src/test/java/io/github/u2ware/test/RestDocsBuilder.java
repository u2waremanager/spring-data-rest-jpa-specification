package io.github.u2ware.test;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.snippet.Snippet;

public class RestDocsBuilder {
	
	public interface Callback {
		public void document(RestDocsBuilder builder);
	}
	
	
	public static RestDocumentationResultHandler document(String identifier, Callback descriptor){
		
		RestDocsBuilder builder = new RestDocsBuilder(identifier);
		
		descriptor.document(builder);

		List<Snippet> snippets = new ArrayList<>();
		if(builder.requestParameters.size() >  0){snippets.add(RequestDocumentation.requestParameters(builder.requestParameters));}
		if(builder.requestParts.size()      >  0){snippets.add(RequestDocumentation.requestParts(builder.requestParts));}
		if(builder.requestHeaders.size()    > -1){snippets.add(HeaderDocumentation.requestHeaders(builder.requestHeaders));}
		if(builder.requestFields.size()     >  0){snippets.add(PayloadDocumentation.requestFields(builder.requestFields));}
		if(builder.responseHeaders.size()   > -1){snippets.add(HeaderDocumentation.responseHeaders(builder.responseHeaders));}
		if(builder.responseFields.size()    >  0){snippets.add(PayloadDocumentation.responseFields(builder.responseFields));}
		
		return MockMvcRestDocumentation.document(identifier,
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				snippets.toArray(new Snippet[0])
		);
	}

	private final ParameterDescriptors requestParameters = new ParameterDescriptors();
	private final RequestPartDescriptors requestParts= new RequestPartDescriptors();
	private final HeaderDescriptors requestHeaders = new HeaderDescriptors();
	private final FieldDescriptors requestFields = new FieldDescriptors();
	private final HeaderDescriptors responseHeaders = new HeaderDescriptors();
	private final FieldDescriptors responseFields = new FieldDescriptors();
	private final String identifier;

	private RestDocsBuilder(String identifier) {
		this.identifier = identifier;
	}

	public ParameterDescriptors requestParameters() {return requestParameters;}
	public RequestPartDescriptors requestParts() {return requestParts;}
	public HeaderDescriptors requestHeaders() {return requestHeaders;}
	public FieldDescriptors requestFields() {return requestFields;}
	public HeaderDescriptors responseHeaders() {return responseHeaders;}
	public FieldDescriptors responseFields() {return responseFields;}
	public String identifier() {return identifier;}
	
	@SuppressWarnings("serial")
	public static class HeaderDescriptors extends ArrayList<HeaderDescriptor>{
		public HeaderDescriptor headerWithName(String name){
			HeaderDescriptor descriptor = HeaderDocumentation.headerWithName(name);
			this.add(descriptor);
			return descriptor;
		}
	}
	
	@SuppressWarnings("serial")
	public static class FieldDescriptors extends ArrayList<FieldDescriptor>{
		public FieldDescriptor fieldWithPath(String path){
			FieldDescriptor descriptor = PayloadDocumentation.fieldWithPath(path);
			this.add(descriptor);
			return descriptor;
		}

		public FieldDescriptor subsectionWithPath(String path){
			FieldDescriptor descriptor = PayloadDocumentation.subsectionWithPath(path);
			this.add(descriptor);
			return descriptor;
		}
	}
	
	@SuppressWarnings("serial")
	public static class ParameterDescriptors extends ArrayList<ParameterDescriptor>{
		public ParameterDescriptor parameterWithName(String name){
			ParameterDescriptor descriptor = RequestDocumentation.parameterWithName(name);
			this.add(descriptor);
			return descriptor;
		}
	}
	
	@SuppressWarnings("serial")
	public static class RequestPartDescriptors extends ArrayList<RequestPartDescriptor>{
		public RequestPartDescriptor partWithName(String name){
			RequestPartDescriptor descriptor = RequestDocumentation.partWithName(name);
			this.add(descriptor);
			return descriptor;
		}
	}
}