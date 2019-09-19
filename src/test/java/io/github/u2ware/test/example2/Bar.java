package io.github.u2ware.test.example2;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JoinFormula;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.view.EntityViewDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Table(name="example2_bar")
@Entity
public @Data class Bar {

	@Id 
	@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	private String name;

	private Integer age;
	
	public Bar() {}
	public Bar(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
	
	///////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////
	@Transient
	@JsonProperty(access=Access.WRITE_ONLY) 
	private Foo foo01;

	@Transient
	@JsonProperty(access=Access.WRITE_ONLY) 
	private FooView foo02;
	
	@Transient
	@JsonDeserialize(using=EntityViewDeserializer.class)
	@JsonProperty(access=Access.WRITE_ONLY) 
	private Foo foo03;

	@Transient
	@JsonDeserialize(using=EntityViewDeserializer.class)
	@JsonProperty(access=Access.WRITE_ONLY) 
	private FooView foo04;
	
	@Transient
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	@JsonProperty(access=Access.WRITE_ONLY) 
	private List<Foo> foo05;

	@Transient
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	@JsonProperty(access=Access.WRITE_ONLY) 
	private List<FooView> foo06;

	///////////////////////////////////////////////////////////////////
	// @ManyToOne physical foreign key
	//////////////////////////////////////////////////////////////////
	@RestResource(exported=true)/*rendering link only */
	@ManyToOne 
	@JoinColumn(name=/*Bar*/"foo11" , referencedColumnName=/* Foo's primary*/"id")
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private Foo foo11;
	
	
	@RestResource(exported=false) /*rendering data only */
	@ManyToOne 
	@JoinColumn(name=/*Bar*/"foo12" , referencedColumnName=/* Foo's primary*/"id")
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private Foo foo12;
	

	@RestResource(exported=true) /*Don't care*/
	@ManyToOne 
	@JoinColumn(name=/*Bar*/"foo13" , referencedColumnName=/* Foo's primary*/"id")
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private FooView foo13;
	
	@RestResource(exported=false) /*Don't care*/
	@ManyToOne 
	@JoinColumn(name=/*Bar*/"foo14" , referencedColumnName=/* Foo's primary*/"id")
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private FooView foo14;
	
	
	///////////////////////////////////////////////////////////////////
	// @ManyToOne logical foreign column 
	//////////////////////////////////////////////////////////////////
	@ManyToOne
	@JoinColumn(name="foo21", foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private Foo foo21;

	
	@ManyToOne
	@JoinColumn(name="foo22", foreignKey=/* Not exists Foo's column*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(using=EntityViewDeserializer.class)
	private FooView foo22;

	
	@Column(name="foo")
	@JsonProperty(value="foo", access=Access.WRITE_ONLY) 
	private UUID foo;

	@RestResource(exported=true) //
	@ManyToOne
	@JoinFormula("( SELECT t.id FROM example2_foo t WHERE t.id = foo)")
	@JsonProperty(access=Access.READ_ONLY) 
	private Foo foo23;

	
	@ManyToOne
	@JoinFormula("( SELECT t.id FROM example2_foo t WHERE t.id = foo)")
	@JsonProperty(access=Access.READ_ONLY) 
	private FooView foo24;

	///////////////////////////////////////////////////////////////////
	// @OneToMany physical foreign key
	//////////////////////////////////////////////////////////////////
	@RestResource(exported=false) 
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="foo31", referencedColumnName="id")
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<Foo> foo31;	

	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="foo32", referencedColumnName="id")
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<FooView> foo32;	

	
	///////////////////////////////////////////////////////////////////
	// @OneToMany logical foreign column
	//////////////////////////////////////////////////////////////////
	@RestResource(exported=false) 
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="foo41", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<Foo> foo41;	

	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="foo42", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<FooView> foo42;	
	
	
	///////////////////////////////////////////////////////////////////
	// @ManyToMany physical foreign table
	//////////////////////////////////////////////////////////////////
	@RestResource(exported=false) 
	@ManyToMany(fetch=FetchType.EAGER)
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<Foo> foo51;
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<FooView> foo52;
	
	
	///////////////////////////////////////////////////////////////////
	// @ManyToMany physical foreign table + logical foreign column
	//////////////////////////////////////////////////////////////////
	@RestResource(exported=false) 
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="bar_foo61", 
		joinColumns={@JoinColumn(name="bar_id", referencedColumnName="id")},
		foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<Foo> foo61;


	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="bar_foo62", 
		joinColumns={@JoinColumn(name="bar_id", referencedColumnName="id")},
		foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<FooView> foo62;

	
	///////////////////////////////////////////////////////////////////
	// @OneToMany cascade
	//////////////////////////////////////////////////////////////////
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	@RestResource(exported=false)
	protected Set<Child> childs = new LinkedHashSet<Child>();

	public void setChilds(Set<Child> childs) {
		this.childs = new LinkedHashSet<Child>();
		for(Child f : childs) {
			f.setParent(this);
			this.childs.add(f);
		} 
	}

	@Entity
	@Table(name = "Bar_Childs")
	public static class Child {

		@Id @GeneratedValue 
		private @JsonIgnore @Getter @Setter Long seq;

		@RestResource(exported = false)
		@ManyToOne(fetch = FetchType.LAZY) 
		private @JsonIgnore @Getter @Setter Bar parent;

		private @Getter @Setter String stringValue;
		private @Getter @Setter Integer integerValue;
	}
}
