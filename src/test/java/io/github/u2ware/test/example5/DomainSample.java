package io.github.u2ware.test.example5;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
public @Data class DomainSample {

	@Id 
	private UUID id;
	private String name;
	private Integer age;
	
	public DomainSample() {
		setId(UUID.randomUUID());
		setName("DomainSample-"+UUID.randomUUID()+"a,b,c,d");
		setAge((int)System.currentTimeMillis());
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "one")
	private DomainSampleOne one;

	
	
}
