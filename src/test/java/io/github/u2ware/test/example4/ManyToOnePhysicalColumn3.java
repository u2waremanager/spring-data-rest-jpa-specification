package io.github.u2ware.test.example4;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class ManyToOnePhysicalColumn3 {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public ManyToOnePhysicalColumn3() {
		
	}
	public ManyToOnePhysicalColumn3(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
