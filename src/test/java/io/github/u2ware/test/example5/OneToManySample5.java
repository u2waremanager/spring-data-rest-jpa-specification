package io.github.u2ware.test.example5;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
public @Data class OneToManySample5 {
	
	private String name;

	private Integer age;
	
	public OneToManySample5() {
		
	}
	public OneToManySample5(String name) {
		this.name = name;
	}
}

