package io.github.u2ware.test.example5;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class DomainSampleOne {

	@Id 
	private UUID id;
	private String name;
	private Integer age;
	
	
	public DomainSampleOne() {
		setId(UUID.randomUUID());
		setName("DomainSampleOne-"+UUID.randomUUID());
		setAge((int)System.currentTimeMillis());
	}
	
	@Override
	public String toString() {
		return "DomainSample1One [id=" + id + ", name=" + name + ", age=" + age + "]";
	}

}
