package io.github.u2ware.test.example5;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class OneToManySample3 {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public OneToManySample3() {
		
	}
	public OneToManySample3(String name) {
		this.name = name;
	}

}
