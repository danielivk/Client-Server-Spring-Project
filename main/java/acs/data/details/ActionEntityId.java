package acs.data.details;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ActionEntityId implements Comparable<ActionEntityId>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2935613451938050078L;
	@Column(name = "action_domain")
	private String actionDomain;
	@Column(name = "action_id")
	private String actionId;

	public ActionEntityId() {
	}

	public ActionEntityId(String domain, String id) {
		super();
		this.actionDomain = domain;
		this.actionId = id;
	}

	public String getDomain() {
		return actionDomain;
	}

	public void setDomain(String actionDomain) {
		this.actionDomain = actionDomain;
	}

	public String getId() {
		return actionId;
	}

	public void setId(String actionId) {
		this.actionId = actionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionDomain == null) ? 0 : actionDomain.hashCode());
		result = prime * result + ((actionId == null) ? 0 : actionId.hashCode());
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
		ActionEntityId other = (ActionEntityId) obj;
		if (actionDomain == null) {
			if (other.actionDomain != null)
				return false;
		} else if (!actionDomain.equals(other.actionDomain))
			return false;
		if (actionId == null) {
			if (other.actionId != null)
				return false;
		} else if (!actionId.equals(other.actionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ActionId= [domain=" + this.actionDomain + ", id=" + this.actionId + "]";
	}
	
	@Override
	public int compareTo(ActionEntityId o) {
		if (this.getDomain().compareTo(o.getDomain()) == 0) {
			return this.getId().compareTo(o.getId());
		} else {
			return this.getDomain().compareTo(o.getDomain());
		}
	}
}
