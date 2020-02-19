package io.github.u2ware.test.example50;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class DomainSampleMany {

	@Id 
	private UUID id;
	private String name;
	private Integer age;
	
	
	public DomainSampleMany() {
		setId(UUID.randomUUID());
		setName("DomainSample-"+UUID.randomUUID());
		setAge((int)System.currentTimeMillis());
	}
}
