package io.github.u2ware.test.example4;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Table(name="many_to_one_sample5")
@Entity
public @Data class ManyToOneSample5 {

	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;

	public ManyToOneSample5() {
		
	}
	public ManyToOneSample5(String name) {
		this.name = name;
	}
	public ManyToOneSample5(Long id) {
		this.seq = id;
	}
}
