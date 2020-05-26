package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ElementBoundary;
import acs.boundaries.details.ElementId;
import acs.logic.DBElementService;

@RestController
@RequestMapping("/acs/elements")
public class ElementController {
	private DBElementService elementService;

	@Autowired
	public ElementController(DBElementService elementService) {
		this.elementService = elementService;
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElements(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		// GETs all elements created by a given user according to the URL
		return elementService.getAll(userDomain, userEmail, size, page).toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/{elementDomain}/{elementId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary getSpecificElement(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId) {
		return elementService.getSpecificElement(userDomain, userEmail, elementDomain, elementId);
	}

	// POST new element
	@RequestMapping(path = "/{managerDomain}/{managerEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary creatNewElement(@PathVariable("managerDomain") String managerDomain,
			@PathVariable("managerEmail") String managerEmail, @RequestBody ElementBoundary elementBoundary) {
		return elementService.create(managerDomain, managerEmail, elementBoundary);
	}

	// PUT update an element
	@RequestMapping(path = "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateAnElement(@PathVariable("managerDomain") String managerDomain,
			@PathVariable("managerEmail") String managerEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId, @RequestBody ElementBoundary elementBoundry) {
		elementService.update(managerDomain, managerEmail, elementDomain, elementId, elementBoundry);
	}

	// PUT update an element
	@RequestMapping(path = "/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void bindElementToChild(@PathVariable("managerDomain") String managerDomain,
			@PathVariable("managerEmail") String managerEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId, @RequestBody ElementId elementChildrenId) {

		// Binds an existing element by existing manager, according to the URL, to an
		// existing child element,
		// given as the elementIdBoundary argument

		this.elementService.bindExistingElementToAnExistingChildElement(managerDomain, managerEmail, elementDomain,
				elementId, elementChildrenId);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllChildrenElements(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// GETs all children of an existing element, created by a given user according
		// to the URL
		return this.elementService.getAllChildren(userDomain, userEmail, elementDomain, elementId, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllParentElements(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("elementDomain") String elementDomain,
			@PathVariable("elementId") String elementId,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {

		// If we decide a non-many-to-many relationships, then:

		return this.elementService.getParent(userDomain, userEmail, elementDomain, elementId, size, page)
				.toArray(new ElementBoundary[0]);
	}

	// search
	@RequestMapping(path = "/{userDomain}/{userEmail}/search/byName/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsWithElementName(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("name") String name,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElementsWithElementName(userDomain, userEmail, name, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/search/byType/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsWithElementType(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("type") String type,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElementsWithElementType(userDomain, userEmail, type, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsNearBy(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("lat") double lat,
			@PathVariable("lng") double lng, @PathVariable("distance") double distance,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.getElementsNearBy(userDomain, userEmail, lat, lng, distance, size, page)
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(path = "/{userDomain}/{userEmail}/search/typeNearby/{lat}/{lng}/{distance}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementsWithElementTypeNearBy(@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail, @PathVariable("lat") double lat,
			@PathVariable("lng") double lng, @PathVariable("distance") double distance,
			@PathVariable("type") String type,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService
				.getElementsWithElementTypeNearBy(userDomain, userEmail, lat, lng, distance, type, size, page)
				.toArray(new ElementBoundary[0]);
	}
}
