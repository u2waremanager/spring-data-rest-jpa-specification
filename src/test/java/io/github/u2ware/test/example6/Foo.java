package io.github.u2ware.test.example6;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Table(name="example6_foo")
public @Data class Foo {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	@Enumerated(EnumType.STRING)
	private FooType type;

	@DateTimeFormat(pattern = "yyyy-MM-dd") 
	private Date date;
	
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
