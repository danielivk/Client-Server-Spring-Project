package acs.data;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import acs.data.details.ActionEntityId;
import acs.data.details.ElementEntityId;
import acs.data.details.UserEntityId;

@Entity
@Table(name = "ACTIONS")
public class ActionEntity implements Comparable<Object> {
	@EmbeddedId
	private ActionEntityId actionId; // EMBEDDED

	private String type; // VARCHAR(255)
	
	@Embedded
	private ElementEntityId element; // EMBEDDED

	private String createdTimestamp; // CREATION_TIMESTAMP - VARCHAR(255)

	@Embedded
	private UserEntityId invokedBy; // INVOKED_BY - EMBEDDED

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	private Map<String, Object> actionAttributes; // ATTRIBUTES - CLOB

	public ActionEntity() {
	}

	public ActionEntity(ActionEntityId actionId, String type, ElementEntityId element, String createdTimestamp,
			UserEntityId invokedBy, Map<String, Object> actionAttributes) {
		this.actionId = actionId;
		this.type = type;
		this.element = element;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.actionAttributes = actionAttributes;
	}

	public ActionEntityId getActionId() {
		return actionId;
	}

	public void setActionId(ActionEntityId actionId) {
		this.actionId = actionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ElementEntityId getElement() {
		return element;
	}

	public void setElement(ElementEntityId element) {
		this.element = element;
	}

	
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public UserEntityId getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(UserEntityId invokedBy) {
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
		return "ActionEntity [actionId=" + actionId + ", type=" + type + ", element=" + element + ", createdTimestamp="
				+ createdTimestamp + ", invokedBy=" + invokedBy + ", actionAttributes=" + actionAttributes + "]";
	}

	// TODO is this the right implementation of compareTo?
	@Override
	public int compareTo(Object o) {
		ActionEntity a = (ActionEntity) o;
		return this.actionId.compareTo(a.getActionId());
	}
}
