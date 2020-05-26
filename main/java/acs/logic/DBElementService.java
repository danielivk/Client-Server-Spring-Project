package acs.logic;

import java.util.Collection;

import acs.boundaries.ElementBoundary;
import acs.boundaries.details.ElementId;

public interface DBElementService extends ElementService {
	public void bindExistingElementToAnExistingChildElement(String managerDomain, String managerEmail,
			String elementDomain, String elementId, ElementId elementChildrenId);

	public Collection<ElementBoundary> getAllChildren(String userDomain, String userEmail, String elementDomain,
			String elementId, int size, int page);

	public Collection<ElementBoundary> getParent(String userDomain, String userEmail, String elementDomain,
			String elementId, int size, int page);

	public Collection<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page);

	public Collection<ElementBoundary> getElementsWithElementName(String userDomain, String userEmail, String name,
			int size, int page);

	public Collection<ElementBoundary> getElementsWithElementType(String userDomain, String userEmail, String type,
			int size, int page);

	public Collection<ElementBoundary> getElementsNearBy(String userDomain, String userEmail, double lat, double lng,
			double distance, int size, int page);

	public Collection<ElementBoundary> getElementsWithElementTypeNearBy(String userDomain, String userEmail, double lat,
			double lng, double distance, String type, int size, int page);
}
