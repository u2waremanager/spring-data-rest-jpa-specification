package io.github.u2ware.test.example4;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
	// @ManyToOne physical foreign key
	//////////////////////////////////////////////////////////////////
	@ManyToOne
	@JoinColumn(name=/*DomainSample*/"foo1" , referencedColumnName=/*ManyToOnePhysicalColumn1 primary*/"seq")
	private ManyToOnePhysicalColumn1 foo1; /*request: uri only. response:  link */

	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo2" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	private ManyToOnePhysicalColumn2 foo2; /*request: json only. response:  body */

	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo3" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	@RestResource(exported=false) 
	private ManyToOnePhysicalColumn3 foo3; /*request: json only. response:  body */
	
	
	@ManyToOne 
	@JoinColumn(name=/*DomainSample*/"foo4" , referencedColumnName=/* ManyToOnePhysicalColumn2 primary*/"seq")
	@RestResource(exported=false) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOnePhysicalColumn4 foo4; /*request: uri & json , response:  body */


	
	///////////////////////////////////////////////////////////////////
	// @ManyToOne Transient
	////////////////////////////////////////////////////////////////////
	@Transient @JsonProperty(access=Access.WRITE_ONLY) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOnePhysicalColumn1 bar1;

	@Transient @JsonProperty(access=Access.WRITE_ONLY) 
	private ManyToOnePhysicalColumn2 bar2;
	
	@Transient @JsonProperty(access=Access.WRITE_ONLY) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOnePhysicalColumn3 bar3;

	@Transient @JsonProperty(access=Access.WRITE_ONLY) 
	@JsonDeserialize(using=EntityViewDeserializer.class) 
	private ManyToOnePhysicalColumn4 bar4;
	
}
