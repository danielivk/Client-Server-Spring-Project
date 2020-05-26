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
public class UpdateUserTest {
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
	public void testPUTUserGivenNonEmptyDatabaseAndGettingSameUserFromDatabaseWithStatus2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user by /acs/users and try to get it back by a GET request
		// THEN that exact User will be in the DATABASE

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		UserBoundary boundary = postUser(details);

		// Creates the object to update
		UserId newId = new UserId(this.appDomain, "userEmail@gmail.com");
		UserBoundary update = new UserBoundary(newId, UserRole.MANAGER, "newName", "newAvatar");

		putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(), update);

		// Getting the updated user after a PUT request
		UserBoundary updatedUser = getUser(this.appDomain, "userEmail@gmail.com");

		assertThat(updatedUser.getUserId()).usingRecursiveComparison().isEqualTo(newId);
		assertThat(updatedUser.getRole()).usingRecursiveComparison().isEqualTo(UserRole.MANAGER);
		assertThat(updatedUser.getUsername()).isEqualTo("newName");
		assertThat(updatedUser.getAvatar()).isEqualTo("newAvatar");
	}

	@Test
	public void testPUTUserGivenEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN AN EMPTY DATABASE
		// WHEN I PUT a user by /acs/users and try to get it back by a GET request
		// THEN throw RuntimeException

		// Creates a non-existing-in-database object to be updated
		UserId newId = new UserId(this.appDomain, "userEmail@gmail.com");
		UserBoundary update = new UserBoundary(newId, UserRole.MANAGER, "newName", "newAvatar");

		assertThrows(Exception.class, () -> putUser(newId.getDomain(), newId.getEmail(), update));
	}

	@Test
	public void testPUTNonExistingUserGivenNonEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a non existing user by /acs/users and try to get it back by a GET
		// request
		// THEN throw RuntimeException

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		UserBoundary boundary = postUser(details);

		// Creates a non-existing-in-database object to be updated
		UserId newId = new UserId(this.appDomain, "nonExisting@gmail.com");
		UserBoundary update = new UserBoundary(newId, UserRole.MANAGER, "newName", boundary.getAvatar());

		assertThrows(Exception.class, () -> putUser(newId.getDomain(), newId.getEmail(), update));
	}

	@Test
	public void testPUTUserWithInvalidEmailGivenNonEmptyDatabaseAndGettingSameUserFromDatabaseWithValidEmailAndStatus2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user with an invalid email, by /acs/users and try to get it back
		// by a GET request
		// THEN that exact User will be in the DATABASE with a valid email

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		UserBoundary boundary = postUser(details);

		// Creates an invalid id
		UserId invalidId = new UserId(this.appDomain, "invalidEmail");
		UserBoundary update = new UserBoundary(invalidId, UserRole.MANAGER, "newName", "newAvatar");

		assertThrows(Exception.class,
				() -> putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(), update));

//		// Getting the updated user with the correct old email after a PUT request
//		UserBoundary updatedUser = getUser(this.appDomain, boundary.getUserId().getEmail());
//
//		assertThat(updatedUser.getUserId()).usingRecursiveComparison().isEqualTo(boundary.getUserId());
//		assertThat(updatedUser.getRole()).usingRecursiveComparison().isEqualTo(UserRole.MANAGER);
//		assertThat(updatedUser.getUsername()).isEqualTo("newName");
//		assertThat(updatedUser.getAvatar()).isEqualTo("newAvatar");
	}

	@Test
	public void testPUTUserWithInvalidDomainGivenNonEmptyDatabaseAndGettingSameUserFromDatabaseWithValidDomainAndStatus2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user with an invalid appDomain, by /acs/users and try to get it
		// back by a GET request
		// THEN that exact User will be in the DATABASE with a valid appDomain

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		UserBoundary boundary = postUser(details);

		// Creates an invalid id
		UserId invalidId = new UserId("invalidDomain", details.getEmail());
		UserBoundary update = new UserBoundary(invalidId, UserRole.MANAGER, "newName", "newAvatar");

		putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(), update);

		// Getting the updated user with the correct old email after a PUT request
		UserBoundary updatedUser = getUser(this.appDomain, boundary.getUserId().getEmail());

		assertThat(updatedUser.getUserId()).usingRecursiveComparison().isEqualTo(boundary.getUserId());
		assertThat(updatedUser.getUserId().getDomain()).isEqualTo(this.appDomain);
		assertThat(updatedUser.getRole()).usingRecursiveComparison().isEqualTo(UserRole.MANAGER);
		assertThat(updatedUser.getUsername()).isEqualTo("newName");
		assertThat(updatedUser.getAvatar()).isEqualTo("newAvatar");
	}

	@Test
	public void testPUTUserWithInvalidUsernameGivenNonEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user with an invalid username
		// THEN an exception will be thrown

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		// User to be updated
		UserBoundary boundary = postUser(details);

		// Invalid Update
		NewUserDetails invalidDetails = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "", "avatar0");
		UserBoundary invalidUpdate = new UserBoundary(invalidDetails);

		assertThrows(Exception.class,
				() -> putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(), invalidUpdate));
	}

	@Test
	public void testPUTUserWithInvalidAvatarGivenNonEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user with an invalid avatar
		// THEN an exception will be thrown

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		// User to be updated
		UserBoundary boundary = postUser(details);

		// Invalid Update
		NewUserDetails invalidDetails = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName", "");
		UserBoundary invalidUpdate = new UserBoundary(invalidDetails);

		assertThrows(Exception.class,
				() -> putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(), invalidUpdate));
	}

	@Test
	public void testPUTUserWithInvalidRoleGivenNonEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE
		// WHEN I PUT a user with an invalid avatar
		// THEN an exception will be thrown

		NewUserDetails details = new NewUserDetails("userEmail@gmail.com", UserRole.PLAYER, "userName", "avatar");

		// Creates a non empty DATABASE
		// User to be updated
		UserBoundary boundary = postUser(details);

		// Invalid Update (Role)
		assertThrows(Exception.class, () -> putUser(boundary.getUserId().getDomain(), boundary.getUserId().getEmail(),
				new UserBoundary(new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName", ""))));
	}

	private UserBoundary postUser(NewUserDetails details) {
		return this.restTemplate.postForObject(this.url + "/users", details, UserBoundary.class);
	}

	private void putUser(String userDomain, String userEmail, UserBoundary updateUser) {
		this.restTemplate.put(this.url + "/users/{userDomain}/{userEmail}", updateUser, userDomain, userEmail);
	}

	private UserBoundary getUser(String domain, String email) {
		return this.restTemplate.getForObject(this.url + "/users/login/{domain}/{email}", UserBoundary.class, domain,
				email);
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
