package acs.logic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import acs.boundaries.ActionBoundary;
import acs.boundaries.details.ActionId;
import acs.boundaries.details.Element;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.InvokedBy;
import acs.boundaries.details.UserId;
import acs.data.ActionEntity;
import acs.data.details.ActionEntityId;
import acs.data.details.ElementEntityId;
import acs.data.details.UserEntityId;

@Component
public class ActionConverter implements ActionConverterIterface {

	@Override
	public ActionBoundary toBoundary(ActionEntity actionEntity) {
		validateActionEntity(actionEntity);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.pattern);
		Date date = null;
		try {
			date = simpleDateFormat.parse(actionEntity.getCreatedTimestamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new ActionBoundary(
				new ActionId(actionEntity.getActionId().getDomain(), actionEntity.getActionId().getId()),
				actionEntity.getType(),
				new Element(new ElementId(actionEntity.getElement().getDomain(), actionEntity.getElement().getId())),
				date,
				new InvokedBy(
						new UserId(actionEntity.getInvokedBy().getDomain(), actionEntity.getInvokedBy().getEmail())),
				new HashMap<String, Object>(actionEntity.getActionAttributes()));
	}

	@Override
	public ActionEntity toEntity(ActionBoundary actionBoundary) {
		validateActionBoundary(actionBoundary);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.pattern);
		System.out.println(actionBoundary);
		return new ActionEntity(
				new ActionEntityId(actionBoundary.getActionId().getDomain(), actionBoundary.getActionId().getId()),
				actionBoundary.getType(),
				new ElementEntityId(actionBoundary.getElement().getElementId().getDomain(),
						actionBoundary.getElement().getElementId().getId()),
				simpleDateFormat.format(actionBoundary.getCreatedTimestamp()),
				new UserEntityId(actionBoundary.getInvokedBy().getUserId().getDomain(),
						actionBoundary.getInvokedBy().getUserId().getEmail()),
				actionBoundary.getActionAttributes());
	}

	@Override
	public void validateActionBoundary(Object o) {
		String baseErrMsg = "An error occurred:";
		ActionBoundary actionBoundary;
		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof ActionBoundary))
			throw new RuntimeException(
					String.format("%s %s is not an instance of ActionBoundary class.", baseErrMsg, o.toString()));
		actionBoundary = (ActionBoundary) o;

		if (!validateType(actionBoundary.getType()))
			throw new RuntimeException(String.format("%s %s is an invalid type for the ActionBoundary object: %s.",
					baseErrMsg, actionBoundary.getType(), actionBoundary.toString()));
		if (!validateElementId(actionBoundary.getElement()))
			throw new RuntimeException(String.format("%s %s is an invalid ElementId for the ActionBoundary object: %s.",
					baseErrMsg, actionBoundary.getElement(), actionBoundary.toString()));
		if (!validateInvokedBy(actionBoundary.getInvokedBy()))
			throw new RuntimeException(String.format("%s %s is an invalid InvokedBy for the ActionBoundary object: %s.",
					baseErrMsg, actionBoundary.getInvokedBy(), actionBoundary.toString()));
		if (!validateAttributes(actionBoundary.getActionAttributes()))
			throw new RuntimeException(String.format("%s %s are invalid attributes for the ActionBoundary object: %s.",
					baseErrMsg, actionBoundary.getActionAttributes(), actionBoundary.toString()));
	}

	@Override
	public void validateActionEntity(Object o) {
		String baseErrMsg = "An error occurred:";
		ActionEntity actionEntity;
		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof ActionEntity))
			throw new RuntimeException(
					String.format("%s %s is not an instance of ActionEntity class.", baseErrMsg, o.toString()));
		actionEntity = (ActionEntity) o;

		if (!validateActionId(actionEntity.getActionId()))
			throw new RuntimeException(String.format("%s %s is an invalid ActionId for the ActionEntity object: %s.",
					baseErrMsg, actionEntity.getActionId(), actionEntity.toString()));
		if (!validateDate(actionEntity.getCreatedTimestamp()))
			throw new RuntimeException(
					String.format("%s %s is an invalid creation timestamp for the ActionEntity object: %s.", baseErrMsg,
							actionEntity.getCreatedTimestamp(), actionEntity.toString()));
		if (!validateType(actionEntity.getType()))
			throw new RuntimeException(String.format("%s %s is an invalid type for the ActionEntity object: %s.",
					baseErrMsg, actionEntity.getType(), actionEntity.toString()));
		if (!validateElementEntityId(actionEntity.getElement()))
			throw new RuntimeException(String.format("%s %s is an invalid ElementId for the ActionEntity object: %s.",
					baseErrMsg, actionEntity.getElement(), actionEntity.toString()));
		if (!validateUserEntityId(actionEntity.getInvokedBy()))
			throw new RuntimeException(String.format("%s %s is an invalid InvokedBy for the ActionEntity object: %s.",
					baseErrMsg, actionEntity.getInvokedBy(), actionEntity.toString()));
		if (!validateAttributes(actionEntity.getActionAttributes()))
			throw new RuntimeException(String.format("%s %s are invalid attributes for the ActionEntity object: %s.",
					baseErrMsg, actionEntity.getActionAttributes(), actionEntity.toString()));
	}

	private boolean validateDate(String createdTimestamp) {
		return true;
//		try {
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.pattern);
//			simpleDateFormat.format(createdTimestamp);
//			return true;
//		} catch (Exception e) {
//			try {
//				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.UTC_PATTERN);
//				simpleDateFormat.format(createdTimestamp);
//				return true;
//			} catch (Exception ee) {
//			}
//			return false;
//		}
	}

	private boolean validateActionId(ActionEntityId actionId) {
		if (validateNotNull(actionId))
			return validateNotNullOrEmptyString(actionId.getDomain()) && validateNotNullOrEmptyString(actionId.getId());
		return false;
	}

	private boolean validateUserEntityId(UserEntityId userEntityId) {
		if (validateNotNull(userEntityId))
			return validateNotNullOrEmptyString(userEntityId.getDomain())
					&& validateNotNullOrEmptyString(userEntityId.getEmail());
		return false;
	}

	private boolean validateAttributes(Map<String, Object> actionAttributes) {
		return validateNotNull(actionAttributes);
	}

	private boolean validateInvokedBy(InvokedBy invokedBy) {
		if (validateNotNull(invokedBy))
			if (validateNotNull(invokedBy.getUserId()))
				return validateNotNullOrEmptyString(invokedBy.getUserId().getDomain())
						&& validateNotNullOrEmptyString(invokedBy.getUserId().getEmail());
		return false;
	}

	private boolean validateElementId(Element element) {
		if (validateNotNull(element) && validateNotNull(element.getElementId()))
			return validateNotNullOrEmptyString(element.getElementId().getId())
					&& validateNotNullOrEmptyString(element.getElementId().getDomain());
		return false;
	}

	private boolean validateElementEntityId(ElementEntityId elementEntityId) {
		if (validateNotNull(elementEntityId))
			return validateNotNullOrEmptyString(elementEntityId.getId())
					&& validateNotNullOrEmptyString(elementEntityId.getDomain());
		return false;
	}

	private boolean validateType(String type) {
		return validateNotNullOrEmptyString(type);
	}

	private boolean validateNotNull(Object o) {
		if (o == null)
			return false;
		return true;
	}

	private boolean validateNotNullOrEmptyString(String str) {
		if (validateNotNull(str))
			if (!str.equals(""))
				return true;
		return false;
	}
}
