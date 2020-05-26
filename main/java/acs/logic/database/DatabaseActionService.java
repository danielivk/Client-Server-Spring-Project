package acs.logic.database;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.ActionId;
import acs.boundaries.details.InvokedBy;
import acs.boundaries.details.Location;
import acs.boundaries.details.UserRole;
import acs.dal.ActionDao;
import acs.data.ActionEntity;
import acs.logic.DBActionService;
import acs.logic.DBElementService;
import acs.logic.DBUserService;
import acs.logic.util.ActionConverter;

@Service
public class DatabaseActionService implements DBActionService {

	@Value("${spring.application.name:default}")
	private String appDomain;

	private ActionDao actionDao;

	private ActionConverter actionConverter;

	private DBElementService elementService;

	private DBUserService userService;

	@Autowired
	public DatabaseActionService(ActionDao actionDao, ActionConverter actionConverter, DBElementService elementService,
			DBUserService userService) {
		super();
		this.actionDao = actionDao;
		this.actionConverter = actionConverter;
		this.elementService = elementService;
		this.userService = userService;
	}

	@PostConstruct
	public void init() {
	}

	@Override
	@Transactional
	public Object invokeAction(ActionBoundary action) {
		if (action.getActionId() == null
				|| (action.getActionId().getDomain() == null && action.getActionId().getId() == null)) {
			if (invokedActionIsValid(action)) {
				ActionId actionId = new ActionId(appDomain, UUID.randomUUID().toString());
				action.setActionId(actionId);
				action.setCreatedTimestamp(new Date());
				ActionEntity actionEntity = this.actionConverter.toEntity(action);
				actionDao.save(actionEntity);
				return this.actionConverter.toBoundary(actionEntity);
			} else {
				throw new RuntimeException(action.getInvokedBy().getUserId().getEmail() + " is not a player.");
			}
		} else {
			throw new RuntimeException("this action allready exsits in the database");
		}
	}

	public boolean invokedActionIsValid(ActionBoundary actionBoundary) {
		InvokedBy invoked = actionBoundary.getInvokedBy();
		if (invoked == null)
			return false;
		String userDomain = invoked.getUserId().getDomain();
		String userEmail = invoked.getUserId().getEmail();
		UserBoundary userBoundary = this.userService.login(userDomain, userEmail);
		if (userBoundary != null && userBoundary.getRole() == UserRole.PLAYER) {
			ElementBoundary elementBoundary = getElementBoundaryFromActionBoundary(actionBoundary);
			if (elementBoundary != null) {
				return true;
			}
		}
		return false;
	}

	private ElementBoundary getElementBoundaryFromActionBoundary(ActionBoundary actionBoundary) {
		String actionType = actionBoundary.getType();

		switch (actionType) {
		case "add-food_bowl": {
			return createFoodBowl(actionBoundary);
		}
		case "add-water_bowl": {
			return createWaterBowl(actionBoundary);
		}
		case "add-feeding_area": {
			return createFeedingArea(actionBoundary);
		}
		case "refill-food_bowl": {
			return refillFoodBowl(actionBoundary);
		}
		case "refill-water_bowl": {
			return refillWaterBowl(actionBoundary);
		}
		case "remove-food_bowl": {
			return removeFoodBowl(actionBoundary);
		}
		case "remove-water_bowl": {
			return removeWaterBowl(actionBoundary);
		}
		case "remove-feeding_area": {
			return removeFeedingArea(actionBoundary);
		}
		default:
			return null;
		}
	}

	/*
	 * For a creation of ElementBoundary, this function uses the "actionAttributes"
	 * field of the input: actionBoundary.
	 * 
	 */
	private ElementBoundary createElementBoundary(ActionBoundary actionBoundary) {

		Map<String, Object> attributes = actionBoundary.getActionAttributes();
		ElementBoundary element = findElementBoundaryByElementIdField(actionBoundary);

		if (element.getActive() == true) {
			String elementType = actionBoundary.getType().split("-")[1];
			String elementName = attributes.get("elementName").toString();
			String managerDomain = attributes.get("managerDomain").toString();
			String managerEmail = attributes.get("managerEmail").toString();
			Double lat = Double.parseDouble(attributes.get("elementLat").toString());
			Double lng = Double.parseDouble(attributes.get("elementLng").toString());
			Location location = new Location(lat, lng);

			Map<String, Object> elementAttributes = new TreeMap<>(attributes);
			elementAttributes.remove("managerDomain");
			elementAttributes.remove("managerEmail");
			elementAttributes.remove("elementName");
			elementAttributes.remove("elementLat");
			elementAttributes.remove("elementLng");

			ElementBoundary elementBoundary = new ElementBoundary(null, elementType, elementName, true, null, null,
					location, elementAttributes);

			ElementBoundary createdElement = elementService.create(managerDomain, managerEmail, elementBoundary);
			if (createdElement != null) {
				this.elementService.bindExistingElementToAnExistingChildElement(managerDomain, managerEmail,
						actionBoundary.getElement().getElementId().getDomain(),
						actionBoundary.getElement().getElementId().getId(), createdElement.getElementId());
				return createdElement;
			}
		}
		return null;
	}

	private ElementBoundary updateElementBoundary(ActionBoundary actionBoundary) {
		ElementBoundary elementBoundary = findElementBoundaryByElementIdField(actionBoundary);
		if (elementBoundary != null && elementBoundary.getActive() == true) {
			Map<String, Object> attributes = actionBoundary.getActionAttributes();
			String managerDomain = attributes.get("managerDomain").toString();
			String managerEmail = attributes.get("managerEmail").toString();
			Map<String, Object> elementAttributes = new TreeMap<>(attributes);
			elementAttributes.remove("managerDomain");
			elementAttributes.remove("managerEmail");
			ElementBoundary updatedElementBoundary = elementService.update(managerDomain, managerEmail,
					elementBoundary.getElementId().getDomain(), elementBoundary.getElementId().getId(),
					elementBoundary);
			return updatedElementBoundary;
		}
		return null;
	}

	private ElementBoundary findElementBoundaryByElementIdField(ActionBoundary actionBoundary) {
		String userDomain = actionBoundary.getInvokedBy().getUserId().getDomain();
		String userEmail = actionBoundary.getInvokedBy().getUserId().getEmail();
		String elementDomain = actionBoundary.getElement().getElementId().getDomain();
		String elementId = actionBoundary.getElement().getElementId().getId();
		return elementService.getSpecificElement(userDomain, userEmail, elementDomain, elementId);
	}

	private ElementBoundary removeElementBoundary(ActionBoundary actionBoundary) {
		ElementBoundary elementBoundary = findElementBoundaryByElementIdField(actionBoundary);
		if (elementBoundary != null && elementBoundary.getActive() == true) {
			elementBoundary.setActive(false);
			Map<String, Object> attributes = actionBoundary.getActionAttributes();
			String managerDomain = attributes.get("managerDomain").toString();
			String managerEmail = attributes.get("managerEmail").toString();

			ElementBoundary updatedElementBoundary = elementService.update(managerDomain, managerEmail,
					elementBoundary.getElementId().getDomain(), elementBoundary.getElementId().getId(),
					elementBoundary);

			if (updatedElementBoundary != null) {
				String userDomain = actionBoundary.getInvokedBy().getUserId().getDomain();
				String userEmail = actionBoundary.getInvokedBy().getUserId().getEmail();
				removeChildrenOf(userDomain, userEmail, managerDomain, managerEmail, updatedElementBoundary);
			}
			return updatedElementBoundary;
		}
		return null;

	}

	private void removeChildrenOf(String userDomain, String userEmail, String managerDomain, String managerEmail,
			ElementBoundary updatedElementBoundary) {
		int page = 0;
		String elementDomain = updatedElementBoundary.getElementId().getDomain();
		String elementId = updatedElementBoundary.getElementId().getId();
		while (true) {
			Collection<ElementBoundary> children = elementService.getAllChildren(userDomain, userEmail, elementDomain,
					elementId, 20, page);
			if (children.isEmpty()) {
				break;
			} else {
				children.forEach(child -> {
					child.setActive(false);
					this.elementService.update(managerDomain, managerEmail, child.getElementId().getDomain(),
							child.getElementId().getId(), child);
				});
			}
			page += 1;
		}
	}

	private ElementBoundary createFoodBowl(ActionBoundary actionBoundary) {
		if (isFoodBowl(actionBoundary.getActionAttributes())) {
			return createElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary createWaterBowl(ActionBoundary actionBoundary) {
		if (isWaterBowl(actionBoundary.getActionAttributes())) {
			return createElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary createFeedingArea(ActionBoundary actionBoundary) {
		if (isFeedingArea(actionBoundary.getActionAttributes())) {
			return createElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary refillFoodBowl(ActionBoundary actionBoundary) {
		if (isFoodBowl(actionBoundary.getActionAttributes())) {
			return updateElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary refillWaterBowl(ActionBoundary actionBoundary) {
		if (isWaterBowl(actionBoundary.getActionAttributes())) {
			return updateElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary removeFoodBowl(ActionBoundary actionBoundary) {
		if (isFoodBowl(actionBoundary.getActionAttributes())) {
			return removeElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary removeWaterBowl(ActionBoundary actionBoundary) {
		if (isWaterBowl(actionBoundary.getActionAttributes())) {
			return removeElementBoundary(actionBoundary);
		}
		return null;
	}

	private ElementBoundary removeFeedingArea(ActionBoundary actionBoundary) {
		if (isFeedingArea(actionBoundary.getActionAttributes())) {
			return removeElementBoundary(actionBoundary);
		}
		return null;
	}

	private boolean isFoodBowl(Map<String, Object> actionAttributes) {
		// only validate x, y, z and not managerDomain, managerEmail
		if (!actionAttributes.containsKey("state"))
			return false;
		if (!actionAttributes.containsKey("animal"))
			return false;
		if (!actionAttributes.containsKey("weight"))
			return false;
		if (!actionAttributes.containsKey("brand"))
			return false;
		if (!actionAttributes.containsKey("lastFillDate"))
			return false;
		return true;
	}

	private boolean isWaterBowl(Map<String, Object> actionAttributes) {
		if (!actionAttributes.containsKey("state"))
			return false;
		if (!actionAttributes.containsKey("waterQuality"))
			return false;
		return true;
	}

	private boolean isFeedingArea(Map<String, Object> actionAttributes) {
		if (!actionAttributes.containsKey("fullFoodBowl"))
			return false;

		if (!actionAttributes.containsKey("fullWaterBowl"))
			return false;
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail) {
		return StreamSupport.stream(this.actionDao.findAll().spliterator(), false).map(this.actionConverter::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page) {
		return this.actionDao.findAll(PageRequest.of(page, size, Direction.ASC, "actionId")).getContent().stream()
				.map(this.actionConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAllActions(String adminDomain, String adminEmail) {
		this.actionDao.deleteAll();
	}

}
