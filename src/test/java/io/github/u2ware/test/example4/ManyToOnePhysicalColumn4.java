package io.github.u2ware.test.example4;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class ManyToOnePhysicalColumn4 {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public ManyToOnePhysicalColumn4() {
		
	}
	public ManyToOnePhysicalColumn4(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
