package acs.data;

import java.util.Map;
import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import acs.data.details.ElementEntityId;
import acs.data.details.UserEntityId;

@Entity
@Table(name = "ELEMENTS")
public class ElementEntity implements Comparable<ElementEntity> { // ELEMENTS

	@EmbeddedId
	private ElementEntityId elementId; // DOMAIN VARCHAR(255)
										// ID VARCHAR(255)
	private String type; // TYPE VARCHAR(255)

	private String name; // NAME VARCHAR(255)

	private Boolean active; // ACTIVE BOOLEAN

	private String createdTimestamp; // CREATION_TIMESTAMP VARCHAR(255)

	@Embedded
	private UserEntityId createdBy; // DOMAIN VARCHAR(255)
									// EMAIL VARCHAR(255)
	private Double lat; // LAT DOUBLE

	private Double lng; // LNG DOUBLE

	@Lob
	@Convert(converter = acs.data.MapToJsonConverter.class)
	private Map<String, Object> elementAttributes; // ATTRIBUTES CLOB

	// add another entity collection related to this one using ONE-TO-MANY
	// relationship
	@OneToMany(mappedBy = "father", fetch = FetchType.LAZY)
	private Set<ElementEntity> children;

	// add another entity related to this one using MANY-TO-ONE relationship
	@ManyToOne(fetch = FetchType.LAZY)
	private ElementEntity father;

	public ElementEntity() {
	}

	public ElementEntity(ElementEntityId elementId, String type, String name, Boolean active, String createdTimestamp,
			UserEntityId createdBy, Double lat, Double lng, Map<String, Object> elementAttributes) {
		this.elementId = elementId;
		this.type = type;
		this.name = name;
		this.active = active;
		this.createdTimestamp = createdTimestamp;
		this.createdBy = createdBy;
		this.lat = lat;
		this.lng = lng;
		this.elementAttributes = elementAttributes;
	}

	public ElementEntityId getElementId() {
		return elementId;
	}

	public void setElementId(ElementEntityId elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public UserEntityId getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserEntityId createdBy) {
		this.createdBy = createdBy;
	}

	public Double getLat() {
		return this.lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return this.lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Map<String, Object> getElementAttributes() {
		return elementAttributes;
	}

	public void setElementAttributes(Map<String, Object> elementAttributes) {
		this.elementAttributes = elementAttributes;
	}

	public Set<ElementEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<ElementEntity> children) {
		this.children = children;
	}

	public void addChild(ElementEntity child) {
		this.children.add(child);
		child.setFather(this);
	}

	public ElementEntity getFather() {
		return father;
	}

	public void setFather(ElementEntity origin) {
		this.father = origin;
	}

	@Override
	public String toString() {
		return "ElementEntity [elementId=" + elementId + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", createdBy=" + createdBy + ", lat=" + lat + ", lng="
				+ lng + ", elementAttributes=" + elementAttributes + ", children=" + children + "]";
	}

	// TODO IS THIS THE RIGHT IMPLEMENTATION??
	@Override
	public int compareTo(ElementEntity o) {
		return this.elementId.compareTo(o.getElementId());
	}

}
