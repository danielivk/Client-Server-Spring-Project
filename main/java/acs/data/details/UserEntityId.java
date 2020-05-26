package acs.data.details;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserEntityId implements Comparable<UserEntityId>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4460660886045140314L;
	@Column(name = "user_domain")
	private String userDomain;
	private String email;

	public UserEntityId() {
	}

	public UserEntityId(String domain, String email) {
		this.userDomain = domain;
		this.email = email;
	}
	
	public String getDomain() {
		return userDomain;
	}

	public void setDomain(String domain) {
		this.userDomain = domain;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userDomain == null) ? 0 : userDomain.hashCode());
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
		UserEntityId other = (UserEntityId) obj;
		if (userDomain == null) {
			if (other.userDomain != null)
				return false;
		} else if (!userDomain.equals(other.userDomain))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "UserId={ domain=" + this.userDomain + ", email=" + this.email + " }";
	}
	
	@Override
	public int compareTo(UserEntityId o) {
		if (this.getDomain().compareTo(o.getDomain()) == 0) {
			return this.getEmail().compareTo(o.getEmail());
		} else {
			return this.getDomain().compareTo(o.getDomain());
		}
	}
}
