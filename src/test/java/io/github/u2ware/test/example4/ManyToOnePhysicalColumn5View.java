package io.github.u2ware.test.example4;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.rest.webmvc.support.EntityView;

import lombok.Data;

@Table(name="many_to_one_physical_column5")
@Entity
public @Data class ManyToOnePhysicalColumn5View implements EntityView<ManyToOnePhysicalColumn5, Long>{
	
	@Id @GeneratedValue
	private Long seq;
	
	private String name;

	private Integer age;
	
	private @Transient String addon;
	
	public ManyToOnePhysicalColumn5View() {
		
	}
	public ManyToOnePhysicalColumn5View(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	

	@Override
	public Long getId() {
		return seq;
	}
	@Override
	public void deserialize(ManyToOnePhysicalColumn5 source) {
		PropertyMapper.get().from(source::getSeq).to(this::setSeq);
		PropertyMapper.get().from(source::getName).to(this::setName);
	}
	@Override
	public ManyToOnePhysicalColumn5 serialize() {
		return PropertyMapper.get().from(this::getId).toInstance(id -> new ManyToOnePhysicalColumn5(id));
	}
}
