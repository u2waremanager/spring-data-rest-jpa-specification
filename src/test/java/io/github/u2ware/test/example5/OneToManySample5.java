package io.github.u2ware.test.example5;

import javax.persistence.Embeddable;
import javax.persistence.Table;

import lombok.Data;

@Embeddable
@Table(name="one_to_many_sample5")
public @Data class OneToManySample5 {
	
	private String name;

	private Integer age;
	
	public OneToManySample5() {
		
	}
	public OneToManySample5(String name) {
		this.name = name;
	}
}

