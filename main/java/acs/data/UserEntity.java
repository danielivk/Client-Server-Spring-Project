package acs.data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import acs.data.details.UserEntityId;

@Entity
@Table(name = "USERS")
public class UserEntity {
	@EmbeddedId
	private UserEntityId id; // ID PK Embedded
	private UserRole role; // TYPE INTEGER
	private String username; // VARCHAR(255)
	private String avatar; // VARCHAR(255)

	public UserEntity() {
	}

	public UserEntity(UserEntityId id, UserRole role, String username, String avatar) {
		this.id = id;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public UserEntityId getId() {
		return this.id;
	}

	public void setId(UserEntityId id) {
		this.id = id;
	}

	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return this.role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUserName() {
		return this.username;
	}

	public void setUserName(String name) {
		this.username = name;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", role=" + role + ", username=" + username + ", avatar=" + avatar + "]";
	}

}
