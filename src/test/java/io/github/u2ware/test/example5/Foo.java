package io.github.u2ware.test.example5;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="example4_foo")
public @Data class Foo {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	public Foo() {
		
	}
	public Foo(Long seq) {
		this.seq = seq;
	}
	public Foo(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
}
