package acs.boundaries;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import acs.boundaries.details.ActionId;
import acs.boundaries.details.Element;
import acs.boundaries.details.InvokedBy;

public class ActionBoundary implements Comparable<Object> {
	private ActionId actionId;
	private String type;
	private Element element;
	private Date createdTimestamp;
	private InvokedBy invokedBy;
	private Map<String, Object> actionAttributes;

	public ActionBoundary() {

	}

	public ActionBoundary(ActionId actionId, String type, Element element, Date createdTimestamp, InvokedBy invokedBy,
			Map<String, Object> actionAttributes) {
		this.actionId = actionId;
		this.type = type;
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}

	public ActionId getActionId() {
		return actionId;
	}

	public void setActionId(ActionId actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Element getElement() {
		return element;
	}
	
	public void setElement(Element element) {
		this.element = element;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public InvokedBy getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String, Object> getActionAttributes() {
		return actionAttributes;
	}

	public void setActionAttributes(Map<String, Object> actionAttributes) {
		this.actionAttributes = actionAttributes;
	}

	@Override
	public String toString() {
		return "ActionBoundary [actionId=" + actionId + ", type=" + type + ", element=" + element
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy + ", actionAttributes="
				+ actionAttributes + "]";
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof ActionBoundary) {
			ActionBoundary other = (ActionBoundary) o;
			int actionIdDiff = this.actionId.compareTo(other.getActionId());
			int elemDiff = this.actionId.compareTo(other.getElement());
			int typeDiff = this.type.compareTo(other.getType());
			int invokedByDiff = this.actionId.compareTo(other.getInvokedBy());
			int actionAttrDiff = -1;
			if (this.actionAttributes != null && other.getActionAttributes() != null) {
				Set<Entry<String, Object>> s = new HashSet<>(this.actionAttributes.entrySet());
				s.removeAll(other.getActionAttributes().entrySet());
				actionAttrDiff = s.size();
			}
			if (actionIdDiff == 0) {
				if (elemDiff == 0) {
					if (typeDiff == 0) {
						if (invokedByDiff == 0) {
							return actionAttrDiff;
						}
						return invokedByDiff;
					}
					return typeDiff;
				}
				return elemDiff;
			}
			return actionIdDiff;
		}
		return -1;
	}

}
