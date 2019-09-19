package io.github.u2ware.test.example3;

import java.util.UUID;

import org.springframework.hateoas.ResourceSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

public @Data @EqualsAndHashCode(callSuper = true) class Baz extends ResourceSupport{

	private String name;

	private Integer age;
	
	public Baz() {
		
	}
	public Baz(UUID id, String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
}
