package acs.logic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import acs.boundaries.ElementBoundary;
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.Location;
import acs.boundaries.details.UserId;
import acs.data.ElementEntity;
import acs.data.details.ElementEntityId;
import acs.data.details.UserEntityId;

@Component
public class ElementConverter implements ElementConverterInterface {

	@Override
	public ElementBoundary toBoundary(ElementEntity elementEntity) {
		validateElementEntity(elementEntity);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.pattern);
		Date date = null;
		try {
			date = simpleDateFormat.parse(elementEntity.getCreatedTimestamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new ElementBoundary(
				new ElementId(elementEntity.getElementId().getDomain(), elementEntity.getElementId().getId()),
				elementEntity.getType(), elementEntity.getName(), elementEntity.getActive(), date,
				new CreatedBy(
						new UserId(elementEntity.getCreatedBy().getDomain(), elementEntity.getCreatedBy().getEmail())),
				new Location(elementEntity.getLat(), elementEntity.getLng()),
				new TreeMap<String, Object>(elementEntity.getElementAttributes()));
	}

	@Override
	public ElementEntity toEntity(ElementBoundary elementBoundary) {
		validateElementBoundary(elementBoundary);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.pattern);
		return new ElementEntity(
				new ElementEntityId(elementBoundary.getElementId().getDomain(), elementBoundary.getElementId().getId()),
				elementBoundary.getType(), elementBoundary.getName(), elementBoundary.getActive(),
				simpleDateFormat.format(elementBoundary.getCreatedTimestamp()),
				new UserEntityId(elementBoundary.getCreatedBy().getUserId().getDomain(),
						elementBoundary.getCreatedBy().getUserId().getEmail()),
				elementBoundary.getLocation().getLat(), elementBoundary.getLocation().getLng(),
				new TreeMap<String, Object>(elementBoundary.getElementAttributes()));
	}

	public ElementEntityId toEntityId(ElementId elementChildrenId) {
		String baseErrMsg = "An error occurred:";
		ElementEntityId elementId = new ElementEntityId(elementChildrenId.getDomain(), elementChildrenId.getId());
		if (!validateEntityId(elementId)) {
			throw new RuntimeException(
					String.format("%s %s is an invalid ElementId.", baseErrMsg, elementId.toString()));
		}
		return elementId;
	}

	@Override
	public void validateElementBoundary(Object o) {
		String baseErrMsg = "An error occurred:";
		ElementBoundary elementBoundary;
		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof ElementBoundary))
			throw new RuntimeException(
					String.format("%s %s is not an instance of ElementBoundary class.", baseErrMsg, o.toString()));
		elementBoundary = (ElementBoundary) o;

		if (!validateActive(elementBoundary.getActive()))
			throw new RuntimeException(
					String.format("%s %s is an invalid active status for the ElementBoundary object: %s.", baseErrMsg,
							elementBoundary.getActive(), elementBoundary.toString()));
		if (!validateType(elementBoundary.getType()))
			throw new RuntimeException(String.format("%s %s is an invalid type for the ElementBoundary object: %s.",
					baseErrMsg, elementBoundary.getType(), elementBoundary.toString()));
		if (!validateLocation(elementBoundary.getLocation()))
			throw new RuntimeException(String.format("%s %s is an invalid location for the ElementBoundary object: %s.",
					baseErrMsg, elementBoundary.getLocation(), elementBoundary.toString()));
		if (!validateName(elementBoundary.getName()))
			throw new RuntimeException(String.format("%s %s is an invalid name for the ElementBoundary object: %s.",
					baseErrMsg, elementBoundary.getName(), elementBoundary.toString()));
		if (!validateAttributes(elementBoundary.getElementAttributes()))
			throw new RuntimeException(String.format("%s %s are invalid attributes for the ElementBoundary object: %s.",
					baseErrMsg, elementBoundary.getElementAttributes(), elementBoundary.toString()));
	}

	@Override
	public void validateElementEntity(Object o) {
		String baseErrMsg = "An error occurred:";
		ElementEntity elementEntity;
		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof ElementEntity))
			throw new RuntimeException(
					String.format("%s %s is not an instance of ElementEntity class.", baseErrMsg, o.toString()));
		elementEntity = (ElementEntity) o;

		if (!validateDate(elementEntity.getCreatedTimestamp()))
			throw new RuntimeException(
					String.format("%s %s is an invalid creation timestamp for the UserEntity object: %s.", baseErrMsg,
							elementEntity.getCreatedTimestamp(), elementEntity.toString()));
		if (!validateCreatedBy(elementEntity.getCreatedBy()))
			throw new RuntimeException(
					String.format("%s %s is an invalid UserEntityId for the ElementEntity object: %s.", baseErrMsg,
							elementEntity.getCreatedBy().toString(), elementEntity.toString()));
		if (!validateActive(elementEntity.getActive()))
			throw new RuntimeException(
					String.format("%s %s is an invalid active status for the ElementEntity object: %s.", baseErrMsg,
							elementEntity.getActive(), elementEntity.toString()));
		if (!validateType(elementEntity.getType()))
			throw new RuntimeException(String.format("%s %s is an invalid type for the ElementEntity object: %s.",
					baseErrMsg, elementEntity.getType(), elementEntity.toString()));
		if (!validateLatAndLng(elementEntity.getLat(), elementEntity.getLng()))
			throw new RuntimeException(
					String.format("%s (%f, %f) is an invalid latitude and longitude of the ElementEntity object: %s.",
							baseErrMsg, elementEntity.getLat(), elementEntity.getLng(), elementEntity.toString()));
		if (!validateName(elementEntity.getName()))
			throw new RuntimeException(String.format("%s %s is an invalid name for the ElementEntity object: %s.",
					baseErrMsg, elementEntity.getName(), elementEntity.toString()));
		if (!validateEntityId(elementEntity.getElementId()))
			throw new RuntimeException(String.format("%s %s is an invalid ElementId for the ElementEntity object: %s.",
					baseErrMsg, elementEntity.getElementId(), elementEntity.toString()));
		if (!validateAttributes(elementEntity.getElementAttributes()))
			throw new RuntimeException(String.format("%s %s are invalid attributes for the ElementEntity object: %s.",
					baseErrMsg, elementEntity.getElementAttributes(), elementEntity.toString()));
	}

	private boolean validateCreatedBy(UserEntityId createdBy) {
		if (validateNotNull(createdBy))
			return validateNotNull(createdBy.getDomain()) && validateNotNull(createdBy.getEmail());
		return false;
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

	private boolean validateActive(Boolean active) {
		return validateNotNull(active);
	}

	private boolean validateType(String type) {
		return validateNotNull(type);
	}

	private boolean validateLocation(Location location) {
		if (validateNotNull(location)) {
			return validateLatAndLng(location.getLat(), location.getLng());
		}
		return false;
	}

	private boolean validateLatAndLng(Double lat, Double lng) {
		if (validateNotNull(lat) && validateNotNull(lng)) {
			return true;
		}
		return false;
	}

	private boolean validateName(String name) {
		return validateNotNullOrEmptyString(name);
	}

	private boolean validateEntityId(ElementEntityId elementEntityId) {
		if (validateNotNull(elementEntityId))
			return validateNotNullOrEmptyString(elementEntityId.getId())
					&& validateNotNullOrEmptyString(elementEntityId.getDomain());
		return false;
	}

	private boolean validateAttributes(Map<String, Object> attributes) {
		return validateNotNull(attributes);
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
