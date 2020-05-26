package acs.boundaries.details;

public class NewUserDetails {
	private String email;
	private UserRole role;
	private String username;
	private String avatar;

	public NewUserDetails() {
	}

	public NewUserDetails(String email, UserRole role, String name, String avatar) {
		this.email = email;
		this.role = role;
		this.username = name;
		this.avatar = avatar;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRole getRole() {
		return this.role;
	}

	public void setRole(UserRole role) {
		this.role = role;
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
}
