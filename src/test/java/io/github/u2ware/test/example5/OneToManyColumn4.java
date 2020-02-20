package io.github.u2ware.test.example5;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class OneToManyColumn4 {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public OneToManyColumn4() {
		
	}
	public OneToManyColumn4(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
