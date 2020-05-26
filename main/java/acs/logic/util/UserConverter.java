package acs.logic.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import acs.boundaries.UserBoundary;
import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserId;
import acs.boundaries.details.UserRole;
import acs.data.UserEntity;
import acs.data.details.UserEntityId;

@Component
public class UserConverter implements UserConverterInterface {

	@Value("${spring.application.name:default}")
	private String appDomain;

	@Override
	public UserBoundary toBoundary(UserEntity userEntity) {
		validateUserEntity(userEntity);
		UserRole role = UserRole.valueOf(userEntity.getRole().name());
		return new UserBoundary(new UserId(userEntity.getId().getDomain(), userEntity.getId().getEmail()), role,
				userEntity.getUserName(), userEntity.getAvatar());
	}

	@Override
	public UserBoundary toBoundary(NewUserDetails newUserDetails) {
		validateNewUserDetails(newUserDetails);
		return new UserBoundary(new UserId(this.appDomain, newUserDetails.getEmail()), newUserDetails.getRole(),
				newUserDetails.getUsername(), newUserDetails.getAvatar());
	}

	@Override
	public UserEntity toEntity(UserBoundary userBoundary) {
		validateUserBoundary(userBoundary);
		UserRole role = userBoundary.getRole() == null ? null : UserRole.valueOf(userBoundary.getRole().name());
		return new UserEntity(new UserEntityId(this.appDomain, userBoundary.getUserId().getEmail()),
				acs.data.UserRole.valueOf(role.name()), userBoundary.getUsername(), userBoundary.getAvatar());
	}

	@Override
	public void validateNewUserDetails(Object o) {
		String baseErrMsg = "An error occurred:";
		NewUserDetails nud;

		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof UserBoundary))
			throw new RuntimeException(
					String.format("%s %s is not an instance of NewUserDetails class.", baseErrMsg, o.toString()));
		nud = (NewUserDetails) o;

		if (!validateEmail(nud.getEmail()))
			throw new RuntimeException(
					String.format("%s %s is an invalid email address for the NewUserDetails object: %s.", baseErrMsg,
							nud.getEmail(), nud.toString()));
		if (!validateRole(nud.getRole().toString()))
			throw new RuntimeException(String.format("%s %s is an invalid role for the NewUserDetails object: %s.",
					baseErrMsg, nud.getRole().toString(), nud.toString()));
		if (!validateUsername(nud.getUsername()))
			throw new RuntimeException(String.format("%s %s is an invalid username for the NewUserDetails object: %s.",
					baseErrMsg, nud.getUsername(), nud.toString()));
		if (!validateAvatar(nud.getAvatar()))
			throw new RuntimeException(String.format("%s %s is an invalid avatar for the NewUserDetails object: %s.",
					baseErrMsg, nud.getAvatar(), nud.toString()));
	}

	@Override
	public void validateUserBoundary(Object o) {
		String baseErrMsg = "An error occurred:";
		UserBoundary ub;

		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof UserBoundary))
			throw new RuntimeException(
					String.format("%s %s is not an instance of UserBoundary class.", baseErrMsg, o.toString()));
		ub = (UserBoundary) o;

		if (!validateEmail(ub.getUserId().getEmail()))
			throw new RuntimeException(
					String.format("%s %s is an invalid email address for the UserBoundary object: %s.", baseErrMsg,
							ub.getUserId().getEmail(), ub.toString()));
		if (!validateRole(ub.getRole().toString()))
			throw new RuntimeException(String.format("%s %s is an invalid role for the UserBoundary object: %s.",
					baseErrMsg, ub.getRole(), ub.toString()));
		if (!validateUsername(ub.getUsername()))
			throw new RuntimeException(String.format("%s %s is an invalid username for the UserBoundary object: %s.",
					baseErrMsg, ub.getUsername(), ub.toString()));
		if (!validateAvatar(ub.getAvatar()))
			throw new RuntimeException(String.format("%s %s is an invalid avatar for the UserBoundary object: %s.",
					baseErrMsg, ub.getAvatar(), ub.toString()));
	}

	@Override
	public void validateUserEntity(Object o) {
		String baseErrMsg = "The given object is invalid:";
		UserEntity ue;

		if (o == null)
			throw new RuntimeException(String.format("%s It is not initiated (null value).", baseErrMsg));

		if (!(o instanceof UserEntity))
			throw new RuntimeException(
					String.format("%s %s is not an instance of UserEntity class.", baseErrMsg, o.toString()));
		ue = (UserEntity) o;

		if (!validateDomain(ue.getId().getDomain()))
			throw new RuntimeException(String.format("%s %s is an invalid domain for the UserEntity object: %s.",
					baseErrMsg, ue.getId().getDomain(), ue.toString()));
		if (!validateEmail(ue.getId().getEmail()))
			throw new RuntimeException(String.format("%s %s is an invalid email address for the UserEntity object: %s.",
					baseErrMsg, ue.getId().getEmail(), ue.toString()));
		if (!validateRole(ue.getRole().toString()))
			throw new RuntimeException(String.format("%s %s is an invalid role for the UserEntity object: %s.",
					baseErrMsg, ue.getRole().toString(), ue.toString()));
		if (!validateUsername(ue.getUserName()))
			throw new RuntimeException(String.format("%s %s is an invalid username for the UserEntity object: %s.",
					baseErrMsg, ue.getUserName(), ue.toString()));
		if (!validateAvatar(ue.getAvatar()))
			throw new RuntimeException(String.format("%s %s is an invalid avatar for the UserEntity object: %s.",
					baseErrMsg, ue.getAvatar(), ue.toString()));
	}

	private boolean validateEmail(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.find())
			return false;
		return true;
	}

	private boolean validateDomain(String domain) {
		if (this.appDomain.equals(domain))
			return true;
		return false;
	}

	private boolean validateRole(String role) {
		try {
			UserRole.valueOf(role);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private boolean validateUsername(String name) {
		return validateNotNullOrEmptyString(name);
	}

	private boolean validateAvatar(String name) {
		return validateNotNullOrEmptyString(name);
	}

	private boolean validateNotNullOrEmptyString(String str) {
		if (str == null || str.equals(""))
			return false;
		return true;

	}

}
