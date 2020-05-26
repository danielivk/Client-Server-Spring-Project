package acs.boundaries.details;

public class UserId implements Comparable<Object> {
	
	protected String domain;
	protected String email;
	
	public UserId() {
	}
	
	public UserId(String domain, String email) {
		this.domain = domain;
		this.email = email;
	}
	
	public UserId(String email) {
		this.domain = "";
		this.email = email;
	}
	
	public String getDomain() {
		return domain;
	}
	public String getEmail() {
		return email;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "UserID [domain=" + domain + ", email=" + email + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		UserId other = (UserId) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof UserId) {
			UserId other = (UserId) o;
			int domainDiff = this.domain.compareTo(other.getDomain());
			int emailDiff = this.email.compareTo(other.getEmail());
			if (domainDiff == 0) {
				return emailDiff;
			}
			return domainDiff;
		}
		return -1;
	}


}
