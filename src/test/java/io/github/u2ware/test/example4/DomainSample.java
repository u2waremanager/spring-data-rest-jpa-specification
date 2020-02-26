package io.github.u2ware.test.example4;

import java.util.Set;
import java.util.UUID;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Entity
@NamedEntityGraph(name = "io.github.u2ware.test.example4.DomainSampleGraph", 
	attributeNodes = {
			@NamedAttributeNode("sample1"),
			@NamedAttributeNode("sample2"),
			@NamedAttributeNode("sample3"),
			@NamedAttributeNode("sample4"),
			@NamedAttributeNode("sample5"),
			@NamedAttributeNode("sample6Response"),
	}
)
@Data 
public class DomainSample {

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
	
	@PrePersist 
	private void handlePrePersist(){
		sample6Response = sample6Request;
	}

	@PreUpdate
	private void handlePreUpdate(){
		sample6Response = sample6Request;
	}
	

	
	
	
	///////////////////////////////////////////////////////////////////
	// @ManyToOne physical or logical foreign key
	//////////////////////////////////////////////////////////////////
	@ManyToOne
	@JoinColumn(name=/*DomainSample*/"sample1" , referencedColumnName=/*ManyToOnePhysicalColumn1 primary*/"seq")
//	@JoinColumn(name=/*DomainSample*/"sample1" , foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	private ManyToOneSample1 sample1; /*request: uri only. response:  link */

	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"sample2" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
//	@JoinColumn(name=/*DomainSample*/"sample2" , foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	private ManyToOneSample2 sample2; /*request: json only. response:  body */

	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"sample3" , referencedColumnName=/* ManyToOnePhysicalColumn3 primary*/"seq")
//	@JoinColumn(name=/*DomainSample*/"sample3" , foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported=false) 
	private ManyToOneSample3 sample3; /*request: json only. response:  body */
	
	
	@ManyToOne 
//	@JoinColumn(name=/*DomainSample*/"sample4" , referencedColumnName=/* ManyToOnePhysicalColumn4 primary*/"seq")
	@JoinColumn(name=/*DomainSample*/"sample4" , foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported=false) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOneSample4 sample4; /*request: uri & json , response:  body */

	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"sample5" , referencedColumnName=/* ManyToOnePhysicalColumn5 primary*/"seq")
//	@JoinColumn(name=/*DomainSample*/"foo5" , foreignKey=/* Not exists*/@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOneSample5View sample5; /*request: uri & json , response:  body */

	
	///////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////
	@Transient
	@JsonDeserialize(using=EntityViewDeserializer.class)
	@JsonProperty(access = Access.WRITE_ONLY, value = "sample6")
	private ManyToOneSample6 sample6Request; 
	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"sample6" , referencedColumnName=/* ManyToOnePhysicalColumn5 primary*/"seq")
	@JsonProperty(access = Access.READ_ONLY, value = "sample6")
	private ManyToOneSample6 sample6Response; 
	
	
	///////////////////////////////////////////////////////////////////
	// Parameters
	///////////////////////////////////////////////////////////////////
	@Transient
	private String sample3Name;
	
	@Transient
	private Set<String> sample3Names;
}
