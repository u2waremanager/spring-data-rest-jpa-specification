package io.github.u2ware.test.example22;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.rest.webmvc.view.EntityView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="example2_foo")
public @Data @EqualsAndHashCode(callSuper=false) class FooView implements EntityView<Foo, UUID>{

	@Id 
	private UUID id;

	private String name;

	private Integer age;
	
	@Override
	public void deserialize(Foo source) {
		PropertyMapper.get().from(source::getId).to(this::setId);
		PropertyMapper.get().from(source::getName).to(this::setName);
	}

	@Override
	public Foo serialize() {
		return PropertyMapper.get().from(this::getId).toInstance(id -> new Foo(id));
	}
}
