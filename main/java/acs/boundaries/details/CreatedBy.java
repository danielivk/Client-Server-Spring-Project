package acs.boundaries.details;

public class CreatedBy {
	
	private UserId userId;

	public CreatedBy() {
	}
	public CreatedBy(UserId userId) {
		this.userId = userId;
	}
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		CreatedBy other = (CreatedBy) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "CreatedBy={ UserId={ domain=" + this.userId.domain + ", email=" + this.userId.email + " } }";
	}
}
