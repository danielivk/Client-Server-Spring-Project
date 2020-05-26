package acs.logic;

import java.util.List;

import acs.boundaries.ElementBoundary;

public interface ElementService {
	public ElementBoundary create(String managerDomain, String managerEmail, ElementBoundary elementBoundary);

	public ElementBoundary update(String managerDomain, String managerEmail, String elementDomain, String elementId,
			ElementBoundary update);

	public List<ElementBoundary> getAll(String userDomain, String userEmail);

	public ElementBoundary getSpecificElement(String userDomain, String userEmail, String elementDomain,
			String elementId);

	public void deleteAllElements(String adminDomain, String adminEmail);
}
