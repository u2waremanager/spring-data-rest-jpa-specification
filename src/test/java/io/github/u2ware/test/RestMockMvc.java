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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponents.UriTemplateVariables;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;




public class RestMockMvc {

	public final MockMvc mvc;
	private final String baseUri;
	private final HashMap<String, RestMvcResult> results;

	public RestMockMvc(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.baseUri = baseUri;
		this.results = new HashMap<String, RestMvcResult>();
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
	public RestMockHttpServletRequestBuilder HEAD(String path) throws Exception {
		return head(link(path));
	}
	public RestMockHttpServletRequestBuilder OPTIONS(String path) throws Exception {
		return options(link(path));
	}
	public RestMockHttpServletRequestBuilder GET(String path) throws Exception {
		return get(link(path));
	}
	public RestMockHttpServletRequestBuilder POST(String path) throws Exception {
		return post(link(path));
	}
	public RestMockHttpServletRequestBuilder PUT(String path) throws Exception {
		return put(link(path));
	}
	public RestMockHttpServletRequestBuilder PATCH(String path) throws Exception {
		return patch(link(path));
	}
	public RestMockHttpServletRequestBuilder DELETE(String path) throws Exception {
		return delete(link(path));
	}
	public RestMockMultipartHttpServletRequestBuilder MULTIPART(String path) throws Exception {
		return multipart(link(path));
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public RestMockHttpServletRequestBuilder head(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.head(path));
	}
	public RestMockHttpServletRequestBuilder options(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.options(path));
	}
	public RestMockHttpServletRequestBuilder get(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.get(path));
	}
	public RestMockHttpServletRequestBuilder post(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.post(path));
	}
	public RestMockHttpServletRequestBuilder put(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.put(path));
	}
	public RestMockHttpServletRequestBuilder patch(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.patch(path));
	}
	public RestMockHttpServletRequestBuilder delete(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockHttpServletRequestBuilder(this, MockMvcRequestBuilders.delete(path));
	}
	public RestMockMultipartHttpServletRequestBuilder multipart(String path) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
		return new RestMockMultipartHttpServletRequestBuilder(this, MockMvcRequestBuilders.multipart(path));
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class RestMockHttpServletRequestBuilder {

		private static ObjectMapper mapper = new ObjectMapper();

		private RestMockMvc mvc;
		private MockHttpServletRequestBuilder requestBuilder;
		private HttpHeaders headers = new HttpHeaders();
		private MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
		private Map<String,Object> content = new HashMap<>();
		
		private RestMockHttpServletRequestBuilder(RestMockMvc mvc, MockHttpServletRequestBuilder requestBuilder) {
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
		
		public RestMockHttpServletRequestBuilder H(String key, Object value) throws Exception {
			headers.add(key, value.toString());
			return this;
		}
		public RestMockHttpServletRequestBuilder H(Map<String,Object> src) throws Exception {
			src.forEach((key, value)->{
				if(value != null) {
					headers.add(key, value.toString());
				}
			});
			return this;
		}
		public RestMockHttpServletRequestBuilder H(MultiValueMap<String,String> src) throws Exception {
			headers.addAll(src);
			return this;
		}
		public RestMockHttpServletRequestBuilder P(String key, Object value) throws Exception {
			params.add(key, value.toString());
			return this;
		}
		public RestMockHttpServletRequestBuilder P(Map<String,Object> src) throws Exception {
			src.forEach((key, value)->{
				if(value != null) {
					params.add(key, value.toString());
				}
			});
			return this;
		}
		public RestMockHttpServletRequestBuilder P(MultiValueMap<String,String> src) throws Exception {
			params.addAll(src);
			return this;
		}
		public RestMockHttpServletRequestBuilder C(String key, Object value) throws Exception {
			content.put(key, value);
			return this;
		}
		public RestMockHttpServletRequestBuilder C(Map<String, Object> src) throws Exception {
			content.putAll(src);
			return this;
		}
		public RestMockHttpServletRequestBuilder C(MultiValueMap<String,String> src) throws Exception {
			src.forEach((key, value)->{ 
				if(value.size() > 0) { 
					content.put(key, value.size() > 1 ? value : value.get(0));
				}
			});
			content.putAll(src.toSingleValueMap());
			return this;
		}

		public RestMockHttpServletRequestBuilder C(Object src) throws Exception {
			String requestContent = mapper.writeValueAsString(src);
			System.err.println();
			System.err.println(requestContent);
			System.err.println();
			this.requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content(requestContent);
			return this;
		}

		public RestMockHttpServletRequestBuilder C() throws Exception {
			this.requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content("{}");
			return this;
		}
		
		
		private void build() throws Exception {
			this.requestBuilder.headers(headers);
			this.requestBuilder.params(params);
			if(content.size() > 0) {
				String requestContent = mapper.writeValueAsString(content);
				System.err.println();
				System.err.println(requestContent);
				System.err.println();
				this.requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8).content(requestContent);
			}
		}
		
		public RestResultActions is2xx(Object... matcherOrHandlers) throws Exception {
			build();
			return new RestResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is2xxSuccessful())).and(matcherOrHandlers);
		}
		public RestResultActions is3xx(Object... matcherOrHandlers) throws Exception {
			build();
			return new RestResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is3xxRedirection())).and(matcherOrHandlers);
		}
		public RestResultActions is4xx(Object... matcherOrHandlers) throws Exception {
			build();
			return new RestResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is4xxClientError())).and(matcherOrHandlers);
		}
		public RestResultActions is5xx(Object... matcherOrHandlers) throws Exception {
			build();
			return new RestResultActions(mvc, mvc.mvc.perform(requestBuilder).andDo(print()).andExpect(status().is5xxServerError())).and(matcherOrHandlers);
		}
	}
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class RestMockMultipartHttpServletRequestBuilder extends RestMockHttpServletRequestBuilder{

		private MockMultipartHttpServletRequestBuilder requestBuilder;
		
		private RestMockMultipartHttpServletRequestBuilder(RestMockMvc mvc, MockMultipartHttpServletRequestBuilder requestBuilder) {
			super(mvc, requestBuilder);
			this.requestBuilder = requestBuilder;
		}
		
		public RestMockMultipartHttpServletRequestBuilder F(String  name) throws Exception {
			requestBuilder.file(name, "Hello World".getBytes());
			return this;
		}
		public RestMockMultipartHttpServletRequestBuilder F(MockMultipartFile file) throws Exception {
			requestBuilder.file(file);
			return this;
		}
		public RestMockMultipartHttpServletRequestBuilder A(RestResultActions r) throws Exception {
			this.requestBuilder.header("Authorization", r.andReturn().header("Authorization"));
			return this;
		}
		
	}
	
	
	///////////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////////////
	public static class RestResultActions{

		private RestMockMvc mvc;
		private ResultActions resultActions;
		
		private RestResultActions(RestMockMvc mvc, ResultActions resultActions) {
			this.mvc = mvc;
			this.resultActions = resultActions;
		}
		
		public RestResultActions and(final Object... matcherOrHandlers) throws Exception {
			for(Object matcherOrHandler : matcherOrHandlers) {
				if(ClassUtils.isAssignableValue(ResultMatcher.class, matcherOrHandler)){
					resultActions.andExpect((ResultMatcher)matcherOrHandler);
					
				}else if(ClassUtils.isAssignableValue(ResultHandler.class, matcherOrHandler)){
					resultActions.andDo((ResultHandler)matcherOrHandler);

				}else if(ClassUtils.isAssignableValue(String.class, matcherOrHandler)){
					final String key = matcherOrHandler.toString();
					resultActions.andDo(new ResultHandler() {
						public void handle(MvcResult result) throws Exception {
							mvc.results.put(key, new RestMvcResult(result));
						}
					});
				}
			}
			return this;
		}		
		public RestMvcResult andReturn() throws Exception {
			return new RestMvcResult(this.resultActions.andReturn());
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
	public static class RestMvcResult {
		
		private MvcResult mvcResult;
		
		private RestMvcResult(MvcResult mvcResult) {
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

		public String body()  {
			try {
				return mvcResult.getResponse().getContentAsString();
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
	}
}