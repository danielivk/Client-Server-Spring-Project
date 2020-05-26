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
import acs.boundaries.details.UserId;
import acs.boundaries.details.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreateUserTest {
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
	public void testPOSTUserGivenEmptyDatabaseAndGettingTheSameUserFromTheDatabaseWithStatus2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I POST /acs/users and try to get it back by postUser return value
		// THEN that User will be in the DATABASE
			
		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");
		UserBoundary boundary = postUser(details);
		
		assertThat(boundary.getUserId()).usingRecursiveComparison().isEqualTo(new UserId(this.appDomain, "userEmail@gmail.com"));
	}
	
	@Test 
	public void testPOSTUserWithInvalidEmailGivenEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I POST /acs/users with invalid email value
		// THEN an exception will be thrown
			
		NewUserDetails details = new NewUserDetails("invalidEmail", UserRole.PLAYER, "userName", "avatar");
		assertThrows(Exception.class, () -> postUser(details));
	}
	
	@Test 
	public void testPOSTUserWithInvalidUsernameGivenEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I POST /acs/users with invalid username value
		// THEN an exception will be thrown
			
		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, null, "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "", "avatar1");

		assertThrows(Exception.class, () -> postUser(details1));
		assertThrows(Exception.class, () -> postUser(details2));
	}
	
	@Test 
	public void testPOSTUserWithInvalidAvaterGivenEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I POST /acs/users with invalid avatar value
		// THEN an exception will be thrown
			
		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName0", null);
		NewUserDetails details2 = new NewUserDetails("userEmail@1gmail.com", UserRole.PLAYER, "userName1", "");

		assertThrows(Exception.class, () -> postUser(details1));
		assertThrows(Exception.class, () -> postUser(details2));
	}
	
	@Test 
	public void testPOSTUserWithInvalidRoleGivenEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN EMPTY DATABASE
		// WHEN I POST /acs/users with invalid Role value
		// THEN an exception will be thrown
			
		assertThrows(Exception.class, () -> 
			new NewUserDetails("userEmail@gmail.com", UserRole.valueOf("NONEXISTIING_ROLE"), "userName", "avatar"));
	}
		
	private UserBoundary postUser(NewUserDetails details) {
		return this.restTemplate.postForObject(this.url + "/users", details, UserBoundary.class);
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
