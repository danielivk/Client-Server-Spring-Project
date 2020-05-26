package util;

import java.util.Random;

import org.springframework.web.client.RestTemplate;

import acs.boundaries.UserBoundary;
import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserRole;

public class UserTestUtil {
	public static NewUserDetails randNewUserBoundary() {
		Random rand = new Random();
		String randEmail = randString(5) + "@" + randString(5) + ".com";
		UserRole randRole = UserRole.values()[rand.nextInt(3)];
		String randName = randString(5);
		String randAvatar = randString(5);

		return new NewUserDetails(randEmail, randRole, randName, randAvatar);
	}

	public static String randString(int size) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	// POST a random UserBoundary
	public static UserBoundary userPost(RestTemplate restTemplate, String url) {
		NewUserDetails newUserDetails = randNewUserBoundary();
		return restTemplate.postForObject(url + "/users", newUserDetails, UserBoundary.class);
	}
	
	// POST a random Player
	public static UserBoundary playerPost(RestTemplate restTemplate, String url) {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.PLAYER);
		return restTemplate.postForObject(url + "/users", newUserDetails, UserBoundary.class);
	}
	
	// POST a random Manager
	public static UserBoundary managerPost(RestTemplate restTemplate, String url) {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.MANAGER);
		return restTemplate.postForObject(url + "/users", newUserDetails, UserBoundary.class);
	}
	
	// POST a random Admin
	public static UserBoundary adminPost(RestTemplate restTemplate, String url) {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.ADMIN);
		return restTemplate.postForObject(url + "/users", newUserDetails, UserBoundary.class);
	}

	// POST a specific ElementBoundary
	public static UserBoundary userPost(RestTemplate restTemplate, String url, NewUserDetails randNewUserDetails) {
		return restTemplate.postForObject(url + "/users", randNewUserDetails, UserBoundary.class);
	}

	public static UserBoundary[] getAllUsers(RestTemplate restTemplate, String adminDomain, String adminEmail,
			String url) {
		return restTemplate.getForObject(url + "/admin/users/{adminDomain}/{adminEmail}", UserBoundary[].class,
				adminDomain, adminEmail);
	}

	public static UserBoundary[] getAllUsers(RestTemplate restTemplate, String adminDomain, String adminEmail,
			String url, int size, int page) {
		return restTemplate.getForObject(url + "/admin/users/{adminDomain}/{adminEmail}?size=" + size + "&page=" + page,
				UserBoundary[].class, adminDomain, adminEmail);
	}

	public static NewUserDetails createAdmin() {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.ADMIN);
		return newUserDetails;
	}

	public static NewUserDetails createManager() {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.MANAGER);
		return newUserDetails;
	}

	public static NewUserDetails createPlayer() {
		NewUserDetails newUserDetails = randNewUserBoundary();
		newUserDetails.setRole(UserRole.PLAYER);
		return newUserDetails;
	}

	public static void deleteEveryUser(RestTemplate restTemplate, String url) {
		UserBoundary tempAdmin = userPost(restTemplate, url, createAdmin());
		restTemplate.delete(url + "/admin/users/{adminDomain}/{adminEmail}", tempAdmin.getUserId().getDomain(),
				tempAdmin.getUserId().getEmail());
	}
}
