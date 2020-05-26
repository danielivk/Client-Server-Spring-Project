package acs.logic.util;

import acs.boundaries.UserBoundary;
import acs.boundaries.details.NewUserDetails;
import acs.data.UserEntity;;


public interface UserConverterInterface {
	public UserBoundary toBoundary(UserEntity userEntity);
	public UserBoundary toBoundary(NewUserDetails newUserDetails);
	public UserEntity toEntity(UserBoundary userBoundary);
	public void validateNewUserDetails(Object o);
	public void validateUserBoundary(Object o);
	public void validateUserEntity(Object o);
}
