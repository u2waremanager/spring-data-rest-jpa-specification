package io.github.u2ware.test.example5;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Entity
public @Data class DomainSample {

	@Id 
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	private String name;

	private Integer age;
	
	public DomainSample() {}
	public DomainSample(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
//	@NotFound(action = NotFoundAction.IGNORE)	
	
	
	///////////////////////////////////////////////////////////////////
	// @OneToMany foreign key
	//////////////////////////////////////////////////////////////////
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
//	@JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Set<OneToManySample1> sample1;	

	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
//	@JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Set<OneToManySample2> sample2;	

	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
//	@JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported = false)
	private Set<OneToManySample3> sample3;	
	
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
//	@JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported = false)
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<OneToManySample4> sample4;	
	
	
	
	
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="foo32", referencedColumnName="id")
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<FooView> foo32;	
//
//	
//	///////////////////////////////////////////////////////////////////
//	// @OneToMany logical foreign column
//	//////////////////////////////////////////////////////////////////
//	@RestResource(exported=false) 
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="foo41", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<Foo> foo41;	
//
//	
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="foo42", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<FooView> foo42;	
//	
//	
//	///////////////////////////////////////////////////////////////////
//	// @ManyToMany physical foreign table
//	//////////////////////////////////////////////////////////////////
//	@RestResource(exported=false) 
//	@ManyToMany(fetch=FetchType.EAGER)
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<Foo> foo51;
//	
//	
//	@ManyToMany(fetch=FetchType.EAGER)
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<FooView> foo52;
//	
//	
//	///////////////////////////////////////////////////////////////////
//	// @ManyToMany physical foreign table + logical foreign column
//	//////////////////////////////////////////////////////////////////
//	@RestResource(exported=false) 
//	@ManyToMany(fetch=FetchType.EAGER)
//	@JoinTable(name="bar_foo61", 
//		joinColumns={@JoinColumn(name="bar_id", referencedColumnName="id")},
//		foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<Foo> foo61;
//
//	@ManyToMany(fetch=FetchType.EAGER)
//	@JoinTable(name="bar_foo62", 
//		joinColumns={@JoinColumn(name="bar_id", referencedColumnName="id")},
//		foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<FooView> foo62;
//	
//
//	///////////////////////////////////////////////////////////////////
//	// @OneToMany cascade
//	//////////////////////////////////////////////////////////////////
//	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
////	@RestResource(exported=false)
//	protected Set<Child> childs = new LinkedHashSet<Child>();
//
//	public void setChilds(Set<Child> childs) {
//		this.childs = new LinkedHashSet<Child>();
//		for(Child f : childs) {
//			f.setParent(this);
//			this.childs.add(f);
//		} 
//	}
//
//	@Entity
//	@Table(name = "Bar_Childs")
//	public static class Child {
//
//		@Id @GeneratedValue 
//		private @JsonIgnore @Getter @Setter Long seq;
//
//		@RestResource(exported = false)
//		@ManyToOne(fetch = FetchType.LAZY) 
//		private @JsonIgnore @Getter @Setter Bar parent;
//
//		private @Getter @Setter String stringValue;
//		private @Getter @Setter Integer integerValue;
//	}
}
