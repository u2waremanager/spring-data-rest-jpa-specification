package io.github.u2ware.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents.UriTemplateVariables;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class MockMvcSupport {

	void test() throws Exception{
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("");
		ResultHandler handler = null;
		ResultMatcher matcher = null;
		ResultActions actions = mvc.perform(requestBuilder).andDo(handler).andExpect(matcher);
		MvcResult result = actions.andReturn();
		result.getModelAndView();
	}
	
	private MockMvc mvc;
	private MvcResultTemplateVariables variables;

	public MockMvcSupport(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.variables = new MvcResultTemplateVariables(baseUri);
	}

	public UriTemplateVariables variables() {
		return variables;
	}

	public MvcRequestSupport GET(String uri) throws Exception{
		return get(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport POST(String uri) throws Exception {
		return post(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport PUT(String uri) throws Exception{
		return put(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport PATCH(String uri) throws Exception{
		return patch(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport DELETE(String uri) throws Exception{
		return delete(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport OPTIONS(String uri) throws Exception {
		return options(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport HEAD(String uri) throws Exception {
		return head(variables.getValue(uri, true).toString());
	}
	public MvcRequestSupport MULTIPART(String uri) throws Exception {
		return multipart(variables.getValue(uri, true).toString());
	}
	
	
	public MvcRequestSupport get(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.get(uri), variables, mvc);
	}
	public MvcRequestSupport post(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.post(uri), variables, mvc);
	}
	public MvcRequestSupport put(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.put(uri), variables, mvc);
	}
	public MvcRequestSupport patch(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.patch(uri), variables, mvc);
	}
	public MvcRequestSupport delete(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.delete(uri), variables, mvc);
	}
	public MvcRequestSupport options(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.options(uri), variables, mvc);
	}
	public MvcRequestSupport head(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.head(uri), variables, mvc);
	}
	public MvcRequestSupport multipart(String uri) throws Exception{
		return new MvcRequestSupport(MockMvcRequestBuilders.multipart(uri), variables, mvc);
	}
	
	
	@SuppressWarnings("serial")
	public static class MvcResultTemplateVariables extends HashMap<String, MvcResultSupport> implements UriTemplateVariables{

		private String baseUri;
		
		private MvcResultTemplateVariables(String baseUri) {
			this.baseUri = baseUri;
		}
		
		@Override
		public Object getValue(String name) {
			return getValue(name, false);
		}
		
		private Object getValue(String name, boolean isPath) {
			if (containsKey(name)) {
				return get(name).link();
			}else {
				int idx = name.indexOf('.');
				if(idx < 0) {
					return isPath ? baseUri + name : name;
				}else {
					String key = name.substring(0, idx);
					String jsonPath = "$"+name.substring(idx);
					System.err.println(jsonPath);
					
					if(containsKey(key)) {
						
						System.err.println(key);
						
						
						return get(key).body(jsonPath);
					}else {
						return isPath ? baseUri + name : name;
					}
				}
			}
		}
	}
	
	
	
	public static class MvcRequestSupport{
		
		private MockHttpServletRequestBuilder builder;
		private MvcResultTemplateVariables variables;
		private MockMvc mvc;
		
		
		private ObjectMapper mapper = new ObjectMapper();
		private Map<String,Object> content = new HashMap<>();
		private Object contentValue;
		
		private MvcRequestSupport(MockHttpServletRequestBuilder builder, MvcResultTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		private MvcRequestSupport(MockMultipartHttpServletRequestBuilder builder, MvcResultTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		
		
		public MvcRequestSupport H(String key, Object... values) throws Exception{
			builder.header(key, values); return this;
		}
		public MvcRequestSupport H(HttpHeaders headers) throws Exception{
			builder.headers(headers); return this;
		}
		public MvcRequestSupport P(String key, String... value) throws Exception{
			builder.param(key, value); return this;
		}
		public MvcRequestSupport P(MultiValueMap<String,String> params) throws Exception{
			builder.params(params); return this;
		}
		public MvcRequestSupport C(String key, Object value) throws Exception{
			content.put(key, value); return this;
		}
		public MvcRequestSupport C(Object contentValue) throws Exception{
			this.contentValue = contentValue; return this;
		}
		public MvcRequestSupport C() throws Exception{
			this.contentValue = mvc; return this;
		}
		
		public MvcRequestSupport F(String key, File file) throws Exception{
			MockMultipartHttpServletRequestBuilder r = (MockMultipartHttpServletRequestBuilder)builder;
			r.file(new MockMultipartFile(key, FileCopyUtils.copyToByteArray(file)));
			return this;
		}
		public MvcRequestSupport F(MockMultipartFile file) throws Exception{
			MockMultipartHttpServletRequestBuilder r = (MockMultipartHttpServletRequestBuilder)builder;
			r.file(file);
			return this;
		}
		

		
		private ResultActionsSupport perform() throws Exception {
			
			if(content.size() > 0) {
				builder.contentType(MediaType.APPLICATION_JSON_UTF8);
				builder.content(mapper.writeValueAsString(content));
			}
			if(contentValue != null) {
				
				if(ClassUtils.isAssignableValue(MockMvc.class, contentValue)) {
					builder.contentType(MediaType.APPLICATION_JSON_UTF8);
					builder.content("{}");
				}else {
					builder.contentType(MediaType.APPLICATION_JSON_UTF8);
					builder.content(mapper.writeValueAsString(contentValue));
				}
			}
			
			return new ResultActionsSupport(mvc.perform(builder), variables);
		}
		
		public ResultActionsSupport is2xx() throws Exception {
			return perform().and(HttpStatus.OK);
		}
		public ResultActionsSupport is4xx() throws Exception {
			return perform().and(HttpStatus.BAD_REQUEST);
		}
		public ResultActionsSupport is5xx() throws Exception {
			return perform().and(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static class ResultActionsSupport{
		
		private ResultActions actions;
		private MvcResultTemplateVariables variables;
		
		private ResultActionsSupport(ResultActions actions, MvcResultTemplateVariables variables) {
			this.actions = actions;
			this.variables = variables;
		}
		
		public ResultActionsSupport andDo(ResultHandler... resultHandlers) throws Exception{
			for(ResultHandler resultHandler : resultHandlers) {
				actions.andDo(resultHandler);
			}
			return this;
		}
		
		private ResultActionsSupport and(HttpStatus status) throws Exception{
			actions.andDo(MockMvcResultHandlers.print());
			if(status.is2xxSuccessful()) {
				actions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
			}else if(status.is4xxClientError()) {
				actions.andExpect(MockMvcResultMatchers.status().is4xxClientError());
			}else if(status.is5xxServerError()) {
				actions.andExpect(MockMvcResultMatchers.status().is5xxServerError());
			}
			return this;
		}
		
		public ResultActionsSupport andExpect(ResultMatcher... resultMatchers) throws Exception{
			for(ResultMatcher resultMatcher : resultMatchers) {
				actions.andExpect(resultMatcher);
			}
			return this;
		}
		
		public ResultActionsSupport andExpectPath(String path, Object value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath(path).value(value));
			return this;
		}
		public ResultActionsSupport andExpectPageTotalElements(int value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(value));
			return this;
		}
		
		public MvcResultSupport andReturn() throws Exception {
			return new MvcResultSupport(actions.andReturn());
		}
		public MvcResultSupport andReturn(String key) throws Exception {
			MvcResultSupport a = new MvcResultSupport(actions.andReturn());
			
			System.err.println("3333: "+a);
			
			variables.put(key, a);
			return a;
		}
		public String andReturnLink() throws Exception {
			return new MvcResultSupport(actions.andReturn()).link();
		}
	}
	
	public static class MvcResultSupport {
		
		private MvcResult mvcResult;

		private MvcResultSupport(MvcResult mvcResult) {
			this.mvcResult = mvcResult;
		}
		
		public MvcResult get() {
			return mvcResult;
		}
		
		public String link()  {
			String uri = null;
			uri = mvcResult.getResponse().getHeader("Location");
			if (uri != null) {
				return uri;
			}
			uri = (String) body("$._links.self.href");
			if (uri != null) {
				return uri;
			}
			uri = mvcResult.getRequest().getRequestURL().toString();
			if (uri != null) {
				return uri;
			}
			return null;
		}
		
		public String body()  {
			try {
				return mvcResult.getResponse().getContentAsString();
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		public <T> T body(String path) {
			try {
				String body = mvcResult.getResponse().getContentAsString();
				Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);
				return JsonPath.read(document, path);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
