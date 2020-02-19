package io.github.u2ware.test.example5;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.rest.webmvc.support.EntityView;

import lombok.Data;

//@Table(name="example4_foo")
//@Entity
public @Data class ManyToOnePhysicalColumn6 implements EntityView<Foo, Long>{
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	public ManyToOnePhysicalColumn6() {
		
	}
	public ManyToOnePhysicalColumn6(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	

	@Override
	public Long getId() {
		return seq;
	}
	@Override
	public void deserialize(Foo source) {
		PropertyMapper.get().from(source::getSeq).to(this::setSeq);
		PropertyMapper.get().from(source::getName).to(this::setName);
	}
	@Override
	public Foo serialize() {
		return PropertyMapper.get().from(this::getId).toInstance(id -> new Foo(id));
	}
}
