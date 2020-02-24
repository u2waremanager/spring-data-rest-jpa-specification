package io.github.u2ware.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class RestMockMvc {

	protected static void test(MockMvc mvc) throws Exception{
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(".....");
		ResultHandler handler = null;
		ResultMatcher matcher = null;
		ResultActions actions = mvc.perform(requestBuilder).andDo(handler).andExpect(matcher);
		MvcResult result = actions.andReturn();
		result.getResponse();
	}
	
	protected Log logger = LogFactory.getLog(getClass());

	private MockMvc mvc;
	private String baseUri;
	private MockMvcUriTemplateVariables variables;

	public RestMockMvc(MockMvc mvc, String baseUri) {
		this.mvc = mvc;
		this.baseUri = baseUri;
		this.variables = new MockMvcUriTemplateVariables();
	}

	public UriTemplateVariables variables() {
		return variables;
	}
	
	public String getUrlTemplate(String uri) throws Exception{
		String convert = UriComponentsBuilder.fromUriString(uri).build().expand(variables()).toUriString();
		return uri.equals(convert) ? baseUri+uri : convert;
	}

	public MockMvcRequestSupport GET(String uri) throws Exception{
		return get(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport POST(String uri) throws Exception {
		return post(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport PUT(String uri) throws Exception{
		return put(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport PATCH(String uri) throws Exception{
		return patch(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport DELETE(String uri) throws Exception{
		return delete(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport OPTIONS(String uri) throws Exception {
		return options(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport HEAD(String uri) throws Exception {
		return head(getUrlTemplate(uri));
	}
	public MockMvcRequestSupport MULTIPART(String uri) throws Exception {
		return multipart(getUrlTemplate(uri));
	}
	
	
	public MockMvcRequestSupport get(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.get(uri), variables, mvc);
	}
	public MockMvcRequestSupport post(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.post(uri), variables, mvc);
	}
	public MockMvcRequestSupport put(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.put(uri), variables, mvc);
	}
	public MockMvcRequestSupport patch(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.patch(uri), variables, mvc);
	}
	public MockMvcRequestSupport delete(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.delete(uri), variables, mvc);
	}
	public MockMvcRequestSupport options(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.options(uri), variables, mvc);
	}
	public MockMvcRequestSupport head(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.head(uri), variables, mvc);
	}
	public MockMvcRequestSupport multipart(String uri) throws Exception{
		return new MockMvcRequestSupport(MockMvcRequestBuilders.multipart(uri), variables, mvc);
	}
	
	
	
	
	@SuppressWarnings("serial")
	public static class MockMvcUriTemplateVariables extends HashMap<String, MvcResultSupport> implements UriTemplateVariables{

		@Override
		public Object getValue(String name) {

			if (containsKey(name)) {
				return get(name).link();
			}else {
				int idx = name.indexOf('.');
				if(idx < 0) {
					return null;
				}else {
					String key = name.substring(0, idx);
					String jsonPath = "$"+name.substring(idx);

					if(containsKey(key)) {
						return get(key).body(jsonPath);
					}else {
						return null;
					}
				}
			}
		}
	}
	
	
	
	public static class MockMvcRequestSupport{
		
		private MockHttpServletRequestBuilder builder;
		private MockMvcUriTemplateVariables variables;
		private MockMvc mvc;
		
		
		private ObjectMapper mapper = new ObjectMapper();
		private Map<String,Object> content = new HashMap<>();
		private Object contentValue;
		
		private MockMvcRequestSupport(MockHttpServletRequestBuilder builder, MockMvcUriTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		private MockMvcRequestSupport(MockMultipartHttpServletRequestBuilder builder, MockMvcUriTemplateVariables variables, MockMvc mvc) {
			this.builder = builder;
			this.variables = variables;
			this.mvc = mvc;
		}
		
		public UriTemplateVariables variables() {
			return variables;
		}

		
		public MockMvcRequestSupport H(String key, String value) throws Exception{
			builder.header(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport H(HttpHeaders headers) throws Exception{
			builder.headers(headers); return this;
		}
		public MockMvcRequestSupport P(String key, String value) throws Exception{
			builder.param(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport P(MultiValueMap<String,String> params) throws Exception{
			builder.params(params); return this;
		}
		public MockMvcRequestSupport C(String key, String value) throws Exception{
			content.put(key, UriComponentsBuilder.fromUriString(value).build().expand(variables()).toUriString()); return this;
		}
		public MockMvcRequestSupport C(Object contentValue) throws Exception{
			this.contentValue = contentValue; return this;
		}
		public MockMvcRequestSupport C() throws Exception{
			this.contentValue = mvc; return this;
		}
		
		public MockMvcRequestSupport F(String key, File file) throws Exception{
			MockMultipartHttpServletRequestBuilder r = (MockMultipartHttpServletRequestBuilder)builder;
			r.file(new MockMultipartFile(key, FileCopyUtils.copyToByteArray(file)));
			return this;
		}
		public MockMvcRequestSupport F(MockMultipartFile file) throws Exception{
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
			
			return new ResultActionsSupport(mvc.perform(builder), variables).andDo(MockMvcResultHandlers.print());
		}
		
		public ResultActionsSupport is2xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
		}
		public ResultActionsSupport is4xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is4xxClientError());
		}
		public ResultActionsSupport is5xx() throws Exception {
			return perform().andExpect(MockMvcResultMatchers.status().is5xxServerError());
		}
	}
	
	
	
	public static class ResultActionsSupport{
		
		private ResultActions actions;
		private MockMvcUriTemplateVariables variables;
		
		private ResultActionsSupport(ResultActions actions, MockMvcUriTemplateVariables variables) {
			this.actions = actions;
			this.variables = variables;
		}
		
		public UriTemplateVariables variables() {
			return variables;
		}

		public ResultActionsSupport andDo(ResultHandler... resultHandlers) throws Exception{
			for(ResultHandler resultHandler : resultHandlers) {
				actions.andDo(resultHandler);
			}
			return this;
		}
		
		public ResultActionsSupport andExpect(ResultMatcher... resultMatchers) throws Exception{
			for(ResultMatcher resultMatcher : resultMatchers) {
				actions.andExpect(resultMatcher);
			}
			return this;
		}
		
		public ResultActionsSupport andExpect(String path, Object value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath(path).value(value));
			return this;
		}
		public ResultActionsSupport andExpect(int value) throws Exception {
			actions.andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(value));
			return this;
		}
		
		public MvcResultSupport andReturn() throws Exception {
			return new MvcResultSupport(actions.andReturn());
		}
		public MvcResultSupport andReturn(String key) throws Exception {
			return variables.put(key, new MvcResultSupport(actions.andReturn()));
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
