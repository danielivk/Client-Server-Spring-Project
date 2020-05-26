package acs.data.details;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ElementEntityId implements Comparable<ElementEntityId>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3993182766965924029L;
	@Column(name = "element_domain")
	private String elementDomain;
	@Column(name = "element_id")
	private String elementId;

	public ElementEntityId() {
	}

	public ElementEntityId(String domain, String id) {
		super();
		this.elementDomain = domain;
		this.elementId = id;
	}

	public String getDomain() {
		return elementDomain;
	}

	public void setDomain(String domain) {
		this.elementDomain = domain;
	}
	
	public String getId() {
		return elementId;
	}

	public void setId(String id) {
		this.elementId = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementDomain == null) ? 0 : elementDomain.hashCode());
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementEntityId other = (ElementEntityId) obj;
		if (elementDomain == null) {
			if (other.elementDomain != null)
				return false;
		} else if (!elementDomain.equals(other.elementDomain))
			return false;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		return true;
	}

	@Override
	public int compareTo(ElementEntityId o) {
		if (this.getDomain().compareTo(o.getDomain()) == 0) {
			return this.getId().compareTo(o.getId());
		} else {
			return this.getDomain().compareTo(o.getDomain());
		}
	}
	
	@Override
	public String toString() {
		return "ElementId={ domain=" + this.elementDomain + ", id=" + this.elementId + " }";
	}
}
