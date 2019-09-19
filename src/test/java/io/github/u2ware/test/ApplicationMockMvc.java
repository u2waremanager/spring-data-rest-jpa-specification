package io.github.u2ware.test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponents.UriTemplateVariables;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;



public class ApplicationMockMvc {

	public final MockMvc mvc;
	private final String baseUri;
	private final HashMap<String, ApplicationMvcResult> results;

	public ApplicationMockMvc(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.baseUri = baseUri;
		this.results = new HashMap<String, ApplicationMvcResult>();
	}
	
	public <T> T path(String path) {
		if(path == null) return null;
		if (results.containsKey(path)) {
			return results.get(path).path("$");
		}else {
			int idx = path.indexOf('.');
			if(idx < 0) {
				return null;
			}else {
				String key = path.substring(0, idx);
				String jsonPath = "$"+path.substring(idx);
				if(results.containsKey(key)) {
					return results.get(key).path(jsonPath);
				}else {
					return null;
				}
			}
		}
	}
	
	public String link(String path) {
		if(path == null) return null;
		if (results.containsKey(path)) {
			return results.get(path).path();
		}else {
			int idx = path.indexOf('.');
			if(idx < 0) {
				return baseUri + path;
			}else {
				String key = path.substring(0, idx);
				String jsonPath = "$"+path.substring(idx);
				if(results.containsKey(key)) {
					return results.get(key).path(jsonPath);
				}else {
					return baseUri + path;
				}
			}
		}
	}
	
	public UriComponents uri(String uri) {
		return UriComponentsBuilder.fromUriString(uri).build().expand(new UriTemplateVariables() {
			public Object getValue(String name) {
				return link(name);
			}
		});
	}

	public MultiValueMap<String, String> uriParams(String uri) {
		if(uri == null) return null;
		String queryString = StringUtils.startsWithIgnoreCase(uri, "?") ? uri : "?"+uri;
		return uri(queryString).getQueryParams();
	}	
	
	public String uriString(String uri) {
		if(uri == null) return null;
		return uri(uri).toString();
	}	
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public ApplicationMockHttpServletRequestBuilder HEAD(String path) throws Exception {
		return head(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder OPTIONS(String path) throws Exception {
		return options(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder GET(String path) throws Exception {
		return get(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder POST(String path) throws Exception {
		return post(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder PUT(String path) throws Exception {
		return put(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder PATCH(String path) throws Exception {
		return patch(link(path));
	}
	public ApplicationMockHttpServletRequestBuilder DELETE(String path) throws Exception {
		return delete(link(path));
	}
	public ApplicationMockMultipartHttpServletRequestBuilder MULTIPART(String path) throws Exception {
		return multipart(link(path));
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public ApplicationMockHttpServletRequestBuilder head(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.head(path));
	}
	public ApplicationMockHttpServletRequestBuilder options(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.options(path));
	}
	public ApplicationMockHttpServletRequestBuilder get(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.get(path));
	}
	public ApplicationMockHttpServletRequestBuilder post(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.post(path));
	}
	public ApplicationMockHttpServletRequestBuilder put(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.put(path));
	}
	public ApplicationMockHttpServletRequestBuilder patch(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.patch(path));
	}
	public ApplicationMockHttpServletRequestBuilder delete(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.delete(path));
	}
	public ApplicationMockMultipartHttpServletRequestBuilder multipart(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new ApplicationMockMultipartHttpServletRequestBuilder(this, MockMvcRequestBuilders.multipart(path));
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class ApplicationMockHttpServletRequestBuilder {

		private static ObjectMapper mapper = new ObjectMapper();

		private ApplicationMockMvc mvc;
		private MockHttpServletRequestBuilder requestBuilder;
		
		private ApplicationMockHttpServletRequestBuilder(ApplicationMockMvc mvc, MockHttpServletRequestBuilder requestBuilder) {
			this.mvc = mvc;
			this.requestBuilder = requestBuilder;
		}

//		public ApplicationMockHttpServletRequestBuilder A(ApplicationResultActions r) throws Exception {
//			this.requestBuilder.header("Authorization", r.andReturn().header("Authorization"));
//			return this;
//		}
//		public ApplicationMockHttpServletRequestBuilder U(UserDetails auth) throws Exception{
//			this.requestBuilder.with(user(auth));
//			return this;
//		}	
		public ApplicationMockHttpServletRequestBuilder H(String key, String value) throws Exception {
			this.requestBuilder.header(key, value);
			return this;
		}
		public ApplicationMockHttpServletRequestBuilder H(MultiValueMap<String,String> src) throws Exception {
			HttpHeaders headers = new HttpHeaders();
			headers.putAll(src);
			this.requestBuilder.headers(headers);
			return this;
		}
		public ApplicationMockHttpServletRequestBuilder P(String key, Object value) throws Exception {
			this.requestBuilder.param(key, value.toString());
			return this;
		}
		public ApplicationMockHttpServletRequestBuilder P(MultiValueMap<String,String> src) throws Exception {
			this.requestBuilder.params(src);
			return this;
		}
		public ApplicationMockHttpServletRequestBuilder C(Map<String, Object> src) throws Exception {
			String requestContent = mapper.writeValueAsString(src);
			this.requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content(requestContent);
			return this;
		}	

//		public ApplicationMockHttpServletRequestBuilder H(String uri) throws Exception {
//			return H(mvc.uriParams(uri));
//		}
//		public ApplicationMockHttpServletRequestBuilder P(String uri) throws Exception {
//			this.requestBuilder.params(mvc.uriParams(uri));
//			return this;
//		}
//		public ApplicationMockHttpServletRequestBuilder C(String uri) throws Exception {
//			String requestContent = mvc.uriString(uri);
//			this.requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content(requestContent);
//			return this;
//		}	
		
		public ApplicationResultActions is2xx(Object... matcherOrHandlers) throws Exception {
			return new ApplicationResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is2xxSuccessful())).and(matcherOrHandlers);
		}
		public ApplicationResultActions is4xx(Object... matcherOrHandlers) throws Exception {
			return new ApplicationResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is4xxClientError())).and(matcherOrHandlers);
		}
		public ApplicationResultActions is5xx(Object... matcherOrHandlers) throws Exception {
			return new ApplicationResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is5xxServerError())).and(matcherOrHandlers);
		}
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class ApplicationMockMultipartHttpServletRequestBuilder extends ApplicationMockHttpServletRequestBuilder{

		private MockMultipartHttpServletRequestBuilder requestBuilder;
		
		private ApplicationMockMultipartHttpServletRequestBuilder(ApplicationMockMvc mvc, MockMultipartHttpServletRequestBuilder requestBuilder) {
			super(mvc, requestBuilder);
			this.requestBuilder = requestBuilder;
		}
		
		public ApplicationMockMultipartHttpServletRequestBuilder F(String  name) throws Exception {
			requestBuilder.file(name, "Hello World".getBytes());
			return this;
		}
		public ApplicationMockMultipartHttpServletRequestBuilder F(MockMultipartFile file) throws Exception {
			requestBuilder.file(file);
			return this;
		}
		public ApplicationMockMultipartHttpServletRequestBuilder A(ApplicationResultActions r) throws Exception {
			this.requestBuilder.header("Authorization", r.andReturn().header("Authorization"));
			return this;
		}
		
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class ApplicationResultActions{

		private ApplicationMockMvc mvc;
		private ResultActions resultActions;
		
		private ApplicationResultActions(ApplicationMockMvc mvc, ResultActions resultActions) {
			this.mvc = mvc;
			this.resultActions = resultActions;
		}
		
		public ApplicationResultActions and(final Object... matcherOrHandlers) throws Exception {
			for(Object matcherOrHandler : matcherOrHandlers) {
				if(ClassUtils.isAssignableValue(ResultMatcher.class, matcherOrHandler)){
					resultActions.andExpect((ResultMatcher)matcherOrHandler);
					
				}else if(ClassUtils.isAssignableValue(ResultHandler.class, matcherOrHandler)){
					resultActions.andDo((ResultHandler)matcherOrHandler);

				}else if(ClassUtils.isAssignableValue(String.class, matcherOrHandler)){
					final String key = matcherOrHandler.toString();
					resultActions.andDo(new ResultHandler() {
						public void handle(MvcResult result) throws Exception {
							mvc.results.put(key, new ApplicationMvcResult(result));
						}
					});
				}
			}
			return this;
		}		
		public ApplicationMvcResult andReturn() throws Exception {
			return new ApplicationMvcResult(this.resultActions.andReturn());
		}

		public static ResultMatcher valueMatch(String path, Object value) throws Exception {
			return org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(path).value(value);
		}
		public static ResultMatcher sizeMatch(int value) throws Exception {
			return valueMatch("$.page.totalElements", value);
		}
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class ApplicationMvcResult {
		
		private MvcResult mvcResult;
		
		private ApplicationMvcResult(MvcResult mvcResult) {
			this.mvcResult = mvcResult;
		}
		
		public String header(String name) {
			return mvcResult.getResponse().getHeader(name);
		}
		
		public String path()  {
			String uri = null;

			uri = mvcResult.getResponse().getHeader("Location");
			if (uri != null) {
				return uri;
			}
			uri = (String) path("$._links.self.href");
			if (uri != null) {
				return uri;
			}
			uri = mvcResult.getRequest().getRequestURL().toString();
			if (uri != null) {
				return uri;
			}
			return null;
		}
		
		public <T> T path(String path) {
			try {
				String body = mvcResult.getResponse().getContentAsString();
				Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);
				return JsonPath.read(document, path);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}

		public Map<String,Object> body()  {
			return path("$");
		}
	}
}
