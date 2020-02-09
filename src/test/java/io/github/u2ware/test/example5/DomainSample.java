package io.github.u2ware.test.example5;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
public @Data class DomainSample {

	@Id 
	private UUID id;
	private String name;
	private Integer age;

	@JsonIgnore
	private String typesData;
	
	public Set<String> getTypes(){
		return StringUtils.commaDelimitedListToSet(getTypesData());
	}
	
	public void setTypes(Set<String> types) {
		setTypesData(StringUtils.collectionToCommaDelimitedString(types));
	}


	
	//@Convert(converter = StringArrayConverter.class)
//	private String[] types;
	
//	@ElementCollection(fetch = FetchType.EAGER)
//	private List<String> types;
	
//	@Column(columnDefinition = "varchar(255)[]")
//	private String[] types;
	
	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
//	private List<Integer> types;
	
//	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
//    public List<Integer> getTypes() {
//        return types;
//    }   
	
	public DomainSample() {
		setId(UUID.randomUUID());
		setName("DomainSample-"+UUID.randomUUID()+"a,b,c,d");
		setAge((int)System.currentTimeMillis());
	}

	////////////////////////////////////////////////////////
	//
	///////////////////////////////////////////////////////
//	@Formula("( SELECT count(t.id) FROM domain_sample1many t where t.name = name)")
//	@JsonProperty(access=Access.READ_ONLY) 
//	private Long count;
	
	
	
	////////////////////////////////////////////////////////
	// physical foreign, left join , N+1  
	///////////////////////////////////////////////////////
//	@OneToMany(fetch = FetchType.EAGER)
//	@JoinColumn(name = "many")
//	private Set<DomainSampleMany> many;
	
	
	////////////////////////////////////////////////////////
	// logical foreign, left join , N+1
	///////////////////////////////////////////////////////
//	@OneToMany(fetch = FetchType.EAGER)
//	@JoinColumn(name = "many", foreignKey=@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	private Set<DomainSample1Many> many;

	////////////////////////////////////////////////////////
	// physical foreign, left join , 1+1
	///////////////////////////////////////////////////////
//	@OneToMany(fetch = FetchType.EAGER)
//	@JoinColumn(name = "many")
//	@BatchSize(size = 1000)
//	private Set<DomainSampleMany> many;
	
	
	////////////////////////////////////////////////////////
	// logical foreign,  select , 1+1
	///////////////////////////////////////////////////////
//	@OneToMany(fetch = FetchType.EAGER)
//	@JoinColumn(name = "many", foreignKey=@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
//	@Fetch(FetchMode.JOIN) //left join , N+1  
//	private Set<DomainSample1Many> many;

	
	////////////////////////////////////////////////////////
	// physical foreign, left join , N+1  
	///////////////////////////////////////////////////////
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "one")
//	private DomainSampleOne one;


	////////////////////////////////////////////////////////
	// logical foreign, left join , N+1  
	///////////////////////////////////////////////////////
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "one")
//	private DomainSample1One one;
	
	////////////////////////////////////////////////////////
	// physical foreign, left join , N+1  
	///////////////////////////////////////////////////////
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "one")
	private DomainSampleOne one;
//
//	
//	
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumnOrFormula(
//		column = @JoinColumn(name = "one"),
//		formula = @JoinFormula(value = "one", referencedColumnName = "id")
//	)
//	private DomainSample1One one;
//
	
	
	
//	@ManyToOne
//	@JoinFormula("( SELECT t.id FROM domain_sample1one t WHERE t.id = one1)")
//	@Fetch(FetchMode.SELECT)
//	@BatchSize(size = 1000)
//	@JoinFormula("( SELECT t.id FROM domain_sample1one t WHERE t.id = one1)")
	
	
}