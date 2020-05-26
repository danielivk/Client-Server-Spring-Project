package acs.logic;

import java.util.List;

import acs.boundaries.ActionBoundary;

public interface ActionService {
	public Object invokeAction(ActionBoundary action);

	public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail);

	public void deleteAllActions(String adminDomain, String adminEmail);
}
