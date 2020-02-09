package io.github.u2ware.test.example7;

import java.net.URL;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Table(name="example7_foo")
@Entity
public @Data class Foo {

	@Id @GeneratedValue
	private Long id;
	
	private String name;

	private Integer age;
	
	public Foo() {
		
	}
	public Foo(Long id, String name, Integer age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
	
	
	
	private @Transient @JsonProperty(access = Access.WRITE_ONLY) URL url;
	private @Transient @JsonProperty(access = Access.WRITE_ONLY) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTime;
}
