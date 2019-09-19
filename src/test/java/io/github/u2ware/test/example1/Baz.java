package io.github.u2ware.test.example1;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="example1_baz")
@Entity
public @Data class Baz {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public Baz() {}
	public Baz(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
}
