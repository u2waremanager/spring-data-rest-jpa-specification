package io.github.u2ware.test.example2;

import java.util.UUID;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Table(name="example2_foo")
@Entity
public @Data class Foo {

	@Id
	private UUID id;
	
	private String name;

	private Integer age;
	
	public Foo() {
		
	}
	public Foo(UUID id, String name, Integer age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	
//	@ManyToOne
//	@JoinFormula("(SELECT t.id FROM example4_bar t WHERE t.name = name)")
//	@JsonProperty(access=Access.READ_ONLY) 
//	private Bar bar;

	
	@Formula("(SELECT t.id FROM example2_bar t WHERE t.name = name)")
	@JsonProperty(access=Access.READ_ONLY) 
	private UUID title;
	
	
	@Formula("(SELECT count(t.id) FROM example2_bar t WHERE t.name = '#{fooStatement.fooStatement1.statement}')")
	@JsonProperty(access=Access.READ_ONLY) 
	private Long count1;

	@Formula("(SELECT count(t.id) FROM example2_bar t WHERE t.name = '#{fooStatement.fooStatement2.statement}')")
	@JsonProperty(access=Access.READ_ONLY) 
	private Long count2;

	
//	@NotFound(action = NotFoundAction.IGNORE)
//	@JsonProperty(access=Access.READ_ONLY) 
//	private String xyz;
	
}
