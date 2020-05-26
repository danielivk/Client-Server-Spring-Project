package acs.logic.database;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.UserId;
import acs.boundaries.details.UserRole;
import acs.dal.ElementDao;
import acs.data.ElementEntity;
import acs.data.details.ElementEntityId;
import acs.logic.DBElementService;
import acs.logic.DBUserService;
import acs.logic.util.ElementConverter;

@Service
public class DatabaseElementService implements DBElementService {
	private ElementDao elementDao;
	private ElementConverter elementConverter;
	private DBUserService userService;

	@Value("${spring.application.name:default}")
	private String appDomain;

	@Autowired
	public DatabaseElementService(ElementDao elementDao, ElementConverter elementConverter, DBUserService userService) {
		super();
		this.elementDao = elementDao;
		this.elementConverter = elementConverter;
		this.userService = userService;
	}

	@PostConstruct
	public void init() {
	}

	public boolean isManagerValidation(String managerDomain, String managerEmail) {
		UserBoundary userBoundary = this.userService.login(managerDomain, managerEmail);
		if (userBoundary != null && userBoundary.getRole() == UserRole.MANAGER) {
			return true;
		}
		return false;
	}

	public boolean isAdminValidation(String userDomain, String userEmail) {
		UserBoundary userBoundary = this.userService.login(userDomain, userEmail);
		if (userBoundary != null && userBoundary.getRole() == UserRole.ADMIN) {
			return true;
		}
		return false;
	}

	public boolean isPlayerValidation(String userDomain, String userEmail) {
		UserBoundary userBoundary = this.userService.login(userDomain, userEmail);
		if (userBoundary != null && userBoundary.getRole() == UserRole.PLAYER) {
			return true;
		}
		return false;
	}

	public boolean isElementActive(ElementBoundary elementBoundary) {
		if (elementBoundary != null && elementBoundary.getActive() == true) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary elementBoundary) {
		if (isManagerValidation(managerDomain, managerEmail)) {
			if (elementBoundary.getElementId() == null) {
				CreatedBy created = new CreatedBy(new UserId(managerDomain, managerEmail));
				elementBoundary.setCreatedBy(created);
				elementBoundary.setCreatedTimestamp(new Date());
				elementBoundary.setElementId(new ElementId(appDomain, UUID.randomUUID().toString()));
				ElementEntity elementEntity = this.elementConverter.toEntity(elementBoundary);
				this.elementDao.save(elementEntity);
				return this.elementConverter.toBoundary(elementEntity);
			}
			throw new RuntimeException(String.format(
					"Cannot create an ElementEntity with ElementId: %s. ElementId must be defined as null.",
					elementBoundary.getElementId().toString()));
		}
		return null;
	}

	@Override
	@Transactional
	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update) {
		if (isManagerValidation(managerDomain, managerEmail)) {
			ElementEntity elementEntity = this.getEntityElementFromDB(elementDomain, elementId);
			if (elementEntity != null) {
				if (update.getType() != null) {
					elementEntity.setType(update.getType());
				}
				if (update.getName() != null) {
					elementEntity.setName(update.getName());
				}
				if (update.getActive() != null) {
					elementEntity.setActive(update.getActive());
				}
				if (update.getLocation() != null) {
					elementEntity.setLat(update.getLocation().getLat());
					elementEntity.setLng(update.getLocation().getLng());
				}
				if (update.getElementAttributes() != null) {
					elementEntity.setElementAttributes(update.getElementAttributes());
					;
				}
				this.elementDao.save(elementEntity);
				return this.elementConverter.toBoundary(elementEntity);
			} else {
				throw new RuntimeException(elementId + " is not in the database.");
			}
		} else {
			throw new RuntimeException(managerEmail + " is not a manager.");
		}
	}

	private ElementEntity getEntityElementFromDB(String elementDomain, String elementId) {
		return this.elementDao.findById(new ElementEntityId(elementDomain, elementId))
				.orElseThrow(() -> new RuntimeException("could not find message by id"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getAll(String userDomain, String userEmail) {
		if (isManagerValidation(userDomain, userEmail)) {
			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false)
					.map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			return StreamSupport.stream(this.elementDao.findAll().spliterator(), false)
					.map(this.elementConverter::toBoundary).filter(e -> isElementActive(e) == true)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			return this.elementDao
					.findAll(
							PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"))
					.getContent().stream().map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			return this.elementDao
					.findAll(
							PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"))
					.getContent().stream().map(this.elementConverter::toBoundary)
					.filter(e -> isElementActive(e) == true).collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");
		}

	}

	@Override
	@Transactional(readOnly = true)
	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId) {
		if (isManagerValidation(userDomain, userEmail)) {
			return this.elementConverter.toBoundary(getEntityElementFromDB(elementDomain, elementId));
		} else if (isPlayerValidation(userDomain, userEmail)) {
			ElementBoundary elementBoundary = this.elementConverter
					.toBoundary(getEntityElementFromDB(elementDomain, elementId));
			if (isElementActive(elementBoundary)) {
				return elementBoundary;
			} else {
				throw new EntityNotFoundException("Element " + elementBoundary + "is not found.");
			}
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");
		}

	}

	@Override
	@Transactional
	public void deleteAllElements(String adminDomain, String adminEmail) {
		// check if Admin Credentials are correct.
		if (isAdminValidation(adminDomain, adminEmail)) {
			this.elementDao.deleteAll();
		} else {
			throw new RuntimeException(adminDomain + " is not an admin.");
		}
	}

	@Override
	@Transactional
	public void bindExistingElementToAnExistingChildElement(String managerDomain, String managerEmail,
			String elementDomain, String elementId, ElementId elementChildId) {
		if (isManagerValidation(managerDomain, managerEmail)) {
			ElementEntityId fatherId = new ElementEntityId(elementDomain, elementId);
			ElementEntityId childId = this.elementConverter.toEntityId(elementChildId);

			ElementEntity father = this.elementDao.findById(fatherId)
					.orElseThrow(() -> new EntityNotFoundException("could not find father by id: " + fatherId));

			ElementEntity child = this.elementDao.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException("could not find child by id: " + childId));

			father.addChild(child);
			this.elementDao.save(father);
		} else {
			throw new RuntimeException(managerEmail + " is not a manager.");
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ElementBoundary> getAllChildren(String userDomain, String userEmail, String elementDomain,
			String elementId, int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			ElementEntityId fatherId = new ElementEntityId(elementDomain, elementId);

			return this.elementDao
					.findAllByFather_ElementId(fatherId,
							PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId")) // Set<MessageEntity>
					.stream() // Stream<MessageEntity>
					.map(this.elementConverter::toBoundary) // Stream<MessageBoundaryWithId>
					.collect(Collectors.toSet()); // Set<MessageBoundaryWithId> return null;
		} else if (isPlayerValidation(userDomain, userEmail)) {
			ElementEntityId fatherId = new ElementEntityId(elementDomain, elementId);

			return this.elementDao
					.findAllByFather_ElementId(fatherId,
							PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId")) // Set<MessageEntity>
					.stream() // Stream<MessageEntity>
					.map(this.elementConverter::toBoundary) // Stream<MessageBoundaryWithId>
					.filter(e -> isElementActive(e) == true).collect(Collectors.toSet()); // Set<MessageBoundaryWithId>
																							// return null;
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Collection<ElementBoundary> getParent(String userDomain, String userEmail, String elementDomain,
			String elementId, int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			ElementEntityId childId = this.elementConverter.toEntityId(new ElementId(elementDomain, elementId));

			if (size <= 0) {
				throw new RuntimeException("size must be at least 1");
			}

			if (page < 0) {
				throw new RuntimeException("page must not be negative");
			}

			ElementEntity child = this.elementDao.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException("could not find reply by id: " + childId));

			ElementEntity father = child.getFather();
			Set<ElementBoundary> rv = new HashSet<>();

			if (father != null && page == 0) {
				rv.add(this.elementConverter.toBoundary(father));
			}

			return rv;
		} else if (isPlayerValidation(userDomain, userEmail)) {
			ElementEntityId childId = this.elementConverter.toEntityId(new ElementId(elementDomain, elementId));
			ElementBoundary elementBoundary;

			if (size <= 0) {
				throw new RuntimeException("size must be at least 1");
			}

			if (page < 0) {
				throw new RuntimeException("page must not be negative");
			}

			ElementEntity child = this.elementDao.findById(childId)
					.orElseThrow(() -> new EntityNotFoundException("could not find reply by id: " + childId));

			ElementEntity father = child.getFather();
			Set<ElementBoundary> rv = new HashSet<>();

			if (father != null && page == 0) {
				elementBoundary = this.elementConverter.toBoundary(father);
				if (elementBoundary.getActive()) {
					rv.add(elementBoundary);
				} else {
					throw new RuntimeException("404 ERROR");
				}

			}

			return rv;
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");

		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementBoundary> getElementsWithElementName(String userDomain, String userEmail, String name, int size,
			int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findAllByNameLike(name,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findAllByNameLike(name,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).filter(e -> isElementActive(e) == true)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");

		}

	}

	@Override
	public Collection<ElementBoundary> getElementsWithElementType(String userDomain, String userEmail, String type,
			int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findAllByTypeLike(type,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findAllByTypeLike(type,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).filter(e -> isElementActive(e) == true)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");

		}

	}

	@Override
	public Collection<ElementBoundary> getElementsNearBy(String userDomain, String userEmail, double lat, double lng,
			double distance, int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findByLatBetweenAndLngBetween(lat - distance, lat + distance,
					lng - distance, lng + distance,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findByLatBetweenAndLngBetween(lat - distance, lat + distance,
					lng - distance, lng + distance,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).filter(e -> isElementActive(e) == true)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");

		}
	}

	@Override
	public Collection<ElementBoundary> getElementsWithElementTypeNearBy(String userDomain, String userEmail, double lat,
			double lng, double distance, String type, int size, int page) {
		if (isManagerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findByLatBetweenAndLngBetweenAndTypeLike(lat - distance,
					lat + distance, lng - distance, lng + distance, type,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).collect(Collectors.toList());
		} else if (isPlayerValidation(userDomain, userEmail)) {
			List<ElementEntity> entities = this.elementDao.findByLatBetweenAndLngBetweenAndTypeLike(lat - distance,
					lat + distance, lng - distance, lng + distance, type,
					PageRequest.of(page, size, Direction.ASC, "elementId.elementDomain", "elementId.elementId"));

			return entities.stream().map(this.elementConverter::toBoundary).filter(e -> isElementActive(e) == true)
					.collect(Collectors.toList());
		} else {
			throw new RuntimeException(userDomain + " is not a manager or a player.");

		}
	}

}
