package acs.boundaries.details;

public class ActionId implements Comparable<Object> {
	private String domain;
	private String id;

	public ActionId() {

	}

	public ActionId(String domain, String id) {
		super();
		this.domain = domain;
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		if (this != null) {
			this.domain = domain;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (this != null) {
			this.id = id;
		}
	}

	@Override
	public String toString() {
		return "ActionId= [domain=" + this.domain + ", id=" + this.id + "]";
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof ActionId) {
			ActionId other = (ActionId) o;
			if (this.domain != null && other.getDomain() != null) {
				int domainDiff = this.domain.compareTo(other.getDomain());
				int idDiff = this.id.compareTo(other.getId());
				if (domainDiff == 0) {
					return idDiff;
				}
				return domainDiff;
			}
		}
		return -1;
	}

}
