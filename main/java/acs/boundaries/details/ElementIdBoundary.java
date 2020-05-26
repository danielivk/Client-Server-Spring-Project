package acs.boundaries.details;

public class ElementIdBoundary implements Comparable<Object> {
	private String domain;
	private String id;

	public ElementIdBoundary() {
	}

	public ElementIdBoundary(String domain, String id) {
		super();
		this.domain = domain;
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ElementIdBoundary other = (ElementIdBoundary) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof ElementIdBoundary) {
			ElementIdBoundary other = (ElementIdBoundary) o;
			int domainDiff = this.domain.compareTo(other.getDomain());
			int idDiff = this.id.compareTo(other.getId());
			if (domainDiff == 0) {
				return idDiff;
			}
			return domainDiff;
		}
		return -1;
	}
}
