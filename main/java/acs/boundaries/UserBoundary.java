package acs.boundaries;

import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserId;
import acs.boundaries.details.UserRole;

public class UserBoundary {	
	private UserId userId;
	private UserRole role;
	private String username;
	private String avatar;
	
	public UserBoundary() {
	}
	
	public UserBoundary(UserId userId, UserRole role, String username, String avatar) {
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}
	public UserBoundary(NewUserDetails details) {
		this.userId = new UserId(details.getEmail());
		this.role = details.getRole();
		this.username = details.getUsername();
		this.avatar = details.getAvatar();
	}


	public UserRole getRole() {
		return this.role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	
	
	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return this.avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@Override
	public String toString() {
		return "UserBoundary [id=" + userId + ", role=" + role + ", userName=" + username + ", avatar=" + avatar + "]";
	}
}
