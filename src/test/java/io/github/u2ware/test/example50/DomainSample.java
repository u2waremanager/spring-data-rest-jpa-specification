package io.github.u2ware.test.example50;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Entity
//@NamedEntityGraph(name = "io.github.u2ware.test.example4.DomainSampleGraph", 
//	attributeNodes = {
//			@NamedAttributeNode("foo11"),
//			@NamedAttributeNode("foo12"),
//			@NamedAttributeNode("foo13"),
//			@NamedAttributeNode("foo14"),
//	}
//)
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
	
	
//	///////////////////////////////////////////////////////////////////
//	// Transient ManyToOne...
//	//////////////////////////////////////////////////////////////////
//	@Transient
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private Foo foo01;
//
//	@Transient
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private FooView foo02;
//	
//	@Transient
//	@JsonDeserialize(using=UriLinkDeserializer.class)
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private Foo foo03;
//
//	@Transient
//	@JsonDeserialize(using=UriLinkDeserializer.class)
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private FooView foo04;
//	
//	
//	///////////////////////////////////////////////////////////////////
//	// Transient OneToMany...
//	//////////////////////////////////////////////////////////////////
//	@Transient
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private List<Foo> foo05;
//
//	@Transient
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	@JsonProperty(access=Access.WRITE_ONLY) 
//	private List<FooView> foo06;

	
	///////////////////////////////////////////////////////////////////
	// @ManyToOne physical foreign key
	//////////////////////////////////////////////////////////////////
	@ManyToOne
	@JoinColumn(name=/*DomainSample*/"foo1" , referencedColumnName=/*ManyToOnePhysicalColumn1 primary*/"seq")
	private OneToManyColumn1 foo1; /*request: uri only. response:  link */

	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo2" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	private OneToManyColumn2 foo2; /*request: json only. response:  body */

	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo3" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	@RestResource(exported=false) 
	private OneToManyColumn3 foo3; /*request: json only. response:  body */
	
	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo4" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	@RestResource(exported=false) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private OneToManyColumn4 foo4; /*request: uri & json , response:  body */

	
	
	
	
	
	
	
//
//	@ManyToOne 
//	@JoinColumn(name=/*DomainSample*/"foo13" , referencedColumnName=/* ManyToOnePhysicalColumn3 primary*/"seq")
//	private ManyToOnePhysicalColumn4 foo13;
//	
//
//	@ManyToOne 
//	@JoinColumn(name=/*DomainSample*/"foo14" , referencedColumnName=/* ManyToOnePhysicalColumn3 primary*/"seq")
//	private ManyToOnePhysicalColumn5 foo14;
//	
//	
//	@ManyToOne 
//	@JoinColumn(name=/*DomainSample*/"foo15" , referencedColumnName=/* ManyToOnePhysicalColumn3 primary*/"seq")
//	@JsonDeserialize(using=EntityViewDeserializer.class) /*request: uri*/ 
//	private ManyToOnePhysicalColumn6 foo15;
	
	
	
	
	///////////////////////////////////////////////////////////////////
	// @ManyToOne logical foreign column 
	//////////////////////////////////////////////////////////////////
//	@RestResource(exported=true)
//	@ManyToOne
//	@JoinColumn(name="foo21", foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(using=UriLinkDeserializer.class)
//	private ManyToOnePhysicalColumn1 foo21;
//
//	
//	@RestResource(exported=false)
//	@ManyToOne
//	@JoinColumn(name="foo22", foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	@JsonDeserialize(using=UriLinkDeserializer.class)
//	private ManyToOnePhysicalColumn2 foo22;
//	
//	@RestResource(exported=true) /*Don't care*/
//	@ManyToOne 
//	@JoinColumn(name="foo23" , foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	private ManyToOnePhysicalColumn3 foo23;
//	
//	@RestResource(exported=false) /*Don't care*/
//	@ManyToOne 
//	@JoinColumn(name="foo24" , foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	private ManyToOnePhysicalColumn4 foo24;
	
	
	
//	
//	
//	///////////////////////////////////////////////////////////////////
//	// @OneToMany physical foreign key
//	//////////////////////////////////////////////////////////////////
//	@RestResource(exported=false) 
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="foo31", referencedColumnName="id")
//	@JsonDeserialize(contentUsing=UriLinkDeserializer.class)
//	private Set<Foo> foo31;	
//
//	
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
