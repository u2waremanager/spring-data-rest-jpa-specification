package io.github.u2ware.test.example4;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="example4_foo")
public @Data @EqualsAndHashCode(callSuper=false) class FooView {

	@Id 
	private UUID id;

	private String name;

	private Integer age;
	
//	@Override
//	public void deserialize(Foo source) {
//		PropertyMapper.get().from(source::getId).to(this::setId);
//		PropertyMapper.get().from(source::getName).to(this::setName);
//	}
//
//	@Override
//	public Foo serialize() {
//		return PropertyMapper.get().from(this::getId).toInstance(id -> new Foo(id));
//	}
}
