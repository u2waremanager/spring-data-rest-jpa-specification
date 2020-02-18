package io.github.u2ware.test.example4;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
public @Data class ManyToOnePhysicalColumn3 {
	
	@Id
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")	
	@Column(name = "id")
	private UUID id;
	
	private String name;

	private Integer age;
	
	public ManyToOnePhysicalColumn3() {
		
	}
	public ManyToOnePhysicalColumn3(UUID id) {
		this.id = id;
	}
	public ManyToOnePhysicalColumn3(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
