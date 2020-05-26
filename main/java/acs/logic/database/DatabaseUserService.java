package acs.logic.database;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acs.boundaries.UserBoundary;
import acs.dal.UserDao;
import acs.data.UserEntity;
import acs.data.UserRole;
import acs.data.details.UserEntityId;
import acs.logic.DBUserService;
import acs.logic.util.UserConverter;

@Service
public class DatabaseUserService implements DBUserService {

	private UserConverter userConverter;
	private UserDao userDao;
	@Value("${spring.application.name:default}")
	private String appDomain;

	@Autowired
	public DatabaseUserService(UserDao userDao, UserConverter userConverter) {
		this.userDao = userDao;
		this.userConverter = userConverter;
	}

	// all UPSET queries
	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		UserEntityId key = new UserEntityId(this.appDomain, user.getUserId().getEmail());
		if (getEntityUserFromDatabase(key) != null) {
			throw new RuntimeException("The user already exists:");
		}

		UserEntity newUserEntity = this.userConverter.toEntity(user);

		this.userDao.save(newUserEntity);
		return this.userConverter.toBoundary(getEntityUserFromDatabase(key));
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userDomain, String userEmail, UserBoundary updatedUserBoundary) {
		UserEntity userEntityToBeUpdated = getEntityUserFromDatabase(new UserEntityId(userDomain, userEmail));

		if (userEntityToBeUpdated == null) {
			throw new RuntimeException(
					"Could not find User in domain " + userDomain + " with email " + userEmail + " to update");
		}

		UserEntity userEntityUpdates = this.userConverter.toEntity(updatedUserBoundary);

		// Update Name:
		userEntityToBeUpdated.setUserName(userEntityUpdates.getUserName() == null ? userEntityToBeUpdated.getUserName()
				: userEntityUpdates.getUserName());
		// Update Role:
		userEntityToBeUpdated.setRole(
				userEntityUpdates.getRole() == null ? userEntityToBeUpdated.getRole() : userEntityUpdates.getRole());
		// Update Avatar:
		userEntityToBeUpdated.setAvatar(userEntityUpdates.getAvatar() == null ? userEntityToBeUpdated.getAvatar()
				: userEntityUpdates.getAvatar());

		return this.userConverter.toBoundary(this.userDao.save(userEntityToBeUpdated));
	}

	// all GET queries
	private UserEntity getEntityUserFromDatabase(UserEntityId id) {
		return this.userDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userDomain, String userEmail) {
		UserEntity entity = getEntityUserFromDatabase(new UserEntityId(userDomain, userEmail));
		return this.userConverter.toBoundary(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		UserEntity userInSystem = getEntityUserFromDatabase(new UserEntityId(adminDomain, adminEmail));
		if (userInSystem == null)
			throw new RuntimeException(
					"Could not find User in domain " + adminDomain + " with email " + adminEmail + " to get all users");
		else if (userInSystem.getRole() != UserRole.ADMIN)
			throw new RuntimeException("User in domain " + adminDomain + " with email " + adminEmail
					+ " has no permission to get all users");

		return StreamSupport.stream(this.userDao.findAll().spliterator(), false).map(this.userConverter::toBoundary)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<UserBoundary> getAllUsers(String userDomain, String userEmail, int size, int page) {
		UserEntity userInSystem = getEntityUserFromDatabase(new UserEntityId(userDomain, userEmail));
		if (userInSystem == null)
			throw new RuntimeException(
					"Could not find User in domain " + userDomain + " with email " + userDomain + " to get all users");
		else if (userInSystem.getRole() != UserRole.ADMIN)
			throw new RuntimeException("User in domain " + userDomain + " with email " + userDomain
					+ " has no permission to get all users");

		return this.userDao.findAll(PageRequest.of(page, size, Direction.ASC, "id")).getContent().stream()
				.map(this.userConverter::toBoundary).collect(Collectors.toList());
	}

	// all DELETE queries
	@Override
	@Transactional
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		UserEntityId isUserIdExists = new UserEntityId(adminDomain, adminEmail);
		UserEntity userInSystem = getEntityUserFromDatabase(isUserIdExists);
		if (userInSystem == null)
			throw new RuntimeException(String.format("%s doesn't exist in the system.", isUserIdExists.toString()));
		else if (userInSystem.getRole() != UserRole.ADMIN)
			throw new RuntimeException(String.format("%s has has no permission to delete all users. He is an %s.",
					isUserIdExists.toString(), userInSystem.getRole()));

		this.userDao.deleteAll();
	}

}
