package io.github.u2ware.test.example5;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
public @Data class OneToManyColumn2 {
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public OneToManyColumn2() {
		
	}
	public OneToManyColumn2(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

}
