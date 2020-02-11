package io.github.u2ware.test.example4;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Table(name="example4_foo")
@Entity
public @Data class Foo {

	@Id
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")	
	@Column(name = "id")
	private UUID id;
	
	private String name;

	private Integer age;
	
	public Foo() {
		
	}
	public Foo(UUID id) {
		this.id = id;
	}
	public Foo(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
}
