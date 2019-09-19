package io.github.u2ware.test.example0;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name="example0_foo")
public @Data class Foo {

	@Id
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")	
	@Column(name = "id")
	private UUID id;
	
	private String name;

	private String title;
	
	private Integer age;

	@Transient
	private String _name;

	@Transient
	private Integer _age;
	
	public Foo() {
		
	}
	public Foo(UUID id) {
		this.id = id;
	}
	public Foo(String name, Integer age, String sub) {
		this.name = name;
		this.age = age;
		this.title = sub;
	}
}
