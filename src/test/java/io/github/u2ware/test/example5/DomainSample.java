package io.github.u2ware.test.example5;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.*;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.support.EntityViewDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Entity
//@NamedEntityGraph(name = "io.github.u2ware.test.example5.DomainSampleGraph", 
//attributeNodes = {
//		@NamedAttributeNode("sample1"),
//		@NamedAttributeNode("sample2"),
//		@NamedAttributeNode("sample3"),
//		@NamedAttributeNode("sample4"),
//}
//)
@Data
public class DomainSample {
	
	@Transient @JsonIgnore
	protected Log logger = LogFactory.getLog(getClass());
	
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
		sample2Response = new HashSet<>(sample2Request != null ? sample2Request : Collections.emptySet());
		sample4Response = new HashSet<>(sample4Request != null ? sample4Request : Collections.emptySet());
		sample5Response = new HashSet<>(sample5Request != null ? sample5Request : Collections.emptySet());
		
	}

	@Transient @JsonIgnore
	private AtomicLong seq = new AtomicLong(0);
	
	@PreUpdate
	private void handlePreUpdate(){
		sample2Response.clear(); sample2Response.addAll(sample2Request != null ? sample2Request : Collections.emptySet());
		sample4Response.clear(); sample4Response.addAll(sample4Request != null ? sample4Request : Collections.emptySet());
		sample5Response.clear(); sample5Response.addAll(Collections.emptySet());
		
	}
	
	
	///////////////////////////////////////////////////////////////////
	// @OneToMany join table #1
	///////////////////////////////////////////////////////////////////
	@OneToMany(fetch = FetchType.EAGER)
	@RestResource(exported = false)
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<OneToManySample1> sample1;
	
	
	///////////////////////////////////////////////////////////////////
	// @OneToMany join table #2
	///////////////////////////////////////////////////////////////////
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY, value = "sample2")
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<OneToManySample2> sample2Request;	
	//	
	//	@JoinTable(
	//		name="domain_sample_onetomany" 
	//		,joinColumns={@JoinColumn(name="a", referencedColumnName="id")}
	//		,inverseJoinColumns={@JoinColumn(name="b", referencedColumnName="seq")}
	//	
	//		,foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
	//		,inverseForeignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
	//	)
	//
	//1.
	//	create table domain_sample_onetomany (
	//		domain_sample_id binary(255) not null,
	//		sample1response_seq bigint not null,
	//		primary key (domain_sample_id, sample1response_seq)
	//	)		
	//	alter table domain_sample_onetomany add constraint UK_7xjv4sxc7houfhqec9i9fqphy unique (sample1response_seq)
	//	alter table domain_sample_onetomany add constraint FKf8qwlh6q1s6l7y8lnqsxntuat  foreign key (sample1response_seq) references one_to_many_sample1
	//	alter table domain_sample_onetomany add constraint FKblolk2f91bxbdnohl435wlij7 foreign key (domain_sample_id) references domain_sample	
	//
	//2.
	//	create table domain_sample_onetomany (
	//		a binary(255) not null,
	//		b bigint not null,
	//		primary key (a, b)
	//	)
	//	alter table domain_sample_onetomany add constraint UK_tel74hgw66p75vo4vjjqi5984 unique (b)
	//	alter table domain_sample_onetomany add constraint FKje5dm6ojrvw2ym3docuwrihip foreign key (b) references one_to_many_sample1
	//	alter table domain_sample_onetomany add constraint FK1h7vnltqal10v97m6tgpqe7vl foreign key (a) references domain_sample	
	@OneToMany(fetch = FetchType.EAGER)
	@JsonProperty(access = Access.READ_ONLY, value = "sample2")
	@RestResource(exported = false)
	private Set<OneToManySample2> sample2Response;


	///////////////////////////////////////////////////////////////////
	// @OneToMany join column #1
	///////////////////////////////////////////////////////////////////
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
	// or @JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@RestResource(exported = false)
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<OneToManySample3> sample3;
	
	
	///////////////////////////////////////////////////////////////////
	// @OneToMany join column #2
	///////////////////////////////////////////////////////////////////
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY, value = "sample4")
	@JsonDeserialize(contentUsing=EntityViewDeserializer.class)
	private Set<OneToManySample4> sample4Request;	
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="domainSample")
	// or @JoinColumn(name="domainSample", foreignKey=@ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@JsonProperty(access = Access.READ_ONLY, value = "sample4")
	@RestResource(exported = false)
	private Set<OneToManySample4> sample4Response;
	
	
	
	///////////////////////////////////////////////////////////////////
	// @OneToMany cascade 
	///////////////////////////////////////////////////////////////////
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY, value = "sample5")
	protected Set<OneToManySample5> sample5Request;
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, orphanRemoval = true)
//	@JoinColumn(name = "domainSample", referencedColumnName = "id")
//	@JoinColumn(name = "domainSample", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@JoinColumn(name = "domainSample")
	@JsonProperty(access = Access.READ_ONLY, value = "sample5")
	@RestResource(exported = false)
	private Set<OneToManySample5> sample5Response;
	
	
	
	@Entity
	@Table(name = "one_to_many_sample5")
	public @Data static class OneToManySample5 {

//		@EqualsAndHashCode(exclude = {"domainSample"}) @ToString(exclude = {"domainSample"})  
//		
//		@EmbeddedId
//		private ID id = new ID();
//
//		@Embeddable
//		@SuppressWarnings("serial")
//		public static @Data  class ID implements Serializable{
//
//			@ManyToOne(fetch = FetchType.LAZY) 
//			@JsonIgnore
//			@RestResource(exported = false)
//			private DomainSample domainSample;
//			
//			
//			private Long seq;
//			
//		}

		@Id @GeneratedValue
		private Long seq;
		
//		@ManyToOne(fetch = FetchType.LAZY) 
//		@JsonIgnore
//		@RestResource(exported = false)
//		private DomainSample domainSample;
//		
		private String name;

		private Integer age;

		
		public OneToManySample5() {
			
		}
		public OneToManySample5(String name) {
			this.name = name;
		}
	}

	
	
	
	
	

	
	
	
		
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
