package acs.boundaries.details;

public class InvokedBy implements Comparable<Object> {
	private UserId userId;

	public InvokedBy() {
	}

	public InvokedBy(UserId userId) {
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
		return this.userId.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof InvokedBy) {
			InvokedBy other = (InvokedBy) o;
			return this.userId.equals(other.getUserId());
		}
		return false;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof InvokedBy) {
			InvokedBy other = (InvokedBy) o;
			return this.userId.compareTo(other.getUserId());
		}
		return -1;
	}

	@Override
	public String toString() {
		return "InvokedBy [domain=" + this.userId.domain + ", email=" + this.userId.email + "]";
	}

}
