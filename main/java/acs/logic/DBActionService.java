package acs.logic;

import java.util.Collection;

import acs.boundaries.ActionBoundary;

public interface DBActionService extends ActionService{

	Collection<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page);

}
