package acs.logic;

import java.util.Collection;

import acs.boundaries.UserBoundary;

public interface DBUserService extends UserService{

	Collection<UserBoundary> getAllUsers(String adminDomain, String adminEmail, int size, int page);

}
