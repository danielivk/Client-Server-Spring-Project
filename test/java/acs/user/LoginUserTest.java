package acs.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.UserBoundary;
import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginUserTest {
	private int port;
	private RestTemplate restTemplate;
	private String url;

	@Value("${spring.application.name:default}")
	private String appDomain;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + port + "/acs";
		this.restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup() {
		deleteEveryUser();
	}
	
	@AfterEach
	public void tearDown() {
		deleteEveryUser();
	}
	
	@Test
	public void testGETUserWithEmptyDataBaseReturnsStatusDifferenceFrom2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I try to GET a user
		// THEN throw a RuntimeException
		
		assertThrows(Exception.class, () -> getUser(this.appDomain, "nonExisting@gmail.com"));
	}
	
	@Test
	public void testGETNonExistingUserWithNonEmptyDataBaseReturnsStatusDifferenceFrom2xx() {
		// GIVEN non empty DATABASE
		// WHEN I try to GET a non existing user
		// THEN throw a RuntimeException
		
		// POSTing a new user to the database 
		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");
		postUser(details);
		
		// Trying to get a user boundary which doesn't exist	
		assertThrows(Exception.class, () -> getUser(this.appDomain, "nonExisting@gmail.com"));
	}
	
	@Test
	public void testGETExistingUserWithNonEmptyDataBaseReturnsTheExistingUserStatus2xx() {
		// GIVEN non empty DATABASE
		// WHEN I try to GET an existing user
		// THEN return the user
		
		// POSTing a new user to the database 
		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");
		UserBoundary boundary = postUser(details);
		
		// Trying to get a user boundary which exists in the database
		UserBoundary existing = getUser(this.appDomain, "userEmail@gmail.com");		
		assertThat(boundary).usingRecursiveComparison().isEqualTo(existing);
	}
		
	private UserBoundary postUser(NewUserDetails details) {
		return this.restTemplate.postForObject(this.url + "/users", details, UserBoundary.class);
	}
	
	private UserBoundary getUser(String domain, String email) {
		return this.restTemplate.getForObject(this.url + "/users/login/{domain}/{email}",
				UserBoundary.class, domain, email);
	}
	
	private NewUserDetails createAdmin(String email) {
		return new NewUserDetails(email, UserRole.ADMIN, "adminName", "avatar");
	}
	
	private void deleteEveryUser() {
		UserBoundary tempAdmin = postUser(createAdmin("TemporaryAdminEmail@gmail.com"));
		this.restTemplate.delete(this.url + "/admin/users/{adminDomain}/{adminEmail}", this.appDomain,
				tempAdmin.getUserId().getEmail());
	}
}
