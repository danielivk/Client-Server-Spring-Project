package acs.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetAllUsersTest {
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
		UserTestUtil.deleteEveryUser(restTemplate, url);

	}

	@AfterEach
	public void tearDown() {
		UserTestUtil.deleteEveryUser(restTemplate, url);
	}

	@Test
	public void testGETAllUsersGivenNonEmptyDatabaseWithExistingAdminReturnsSameUsersInsideAnArrayFromDatabaseWithStatus2xx() {
		// GIVEN A NON EMPTY DATABASE with an existing admin
		// WHEN I GET allUsers by /acs/admin/users/{adminDomain}/{adminEmail}
		// THEN that exact array of users will be returned

		// Creates a non empty DATABASE
		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName0", "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "userName1", "avatar1");
		NewUserDetails adminDetails = UserTestUtil.createAdmin();

		List<UserBoundary> storedUsers = new ArrayList<>();
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, adminDetails);
		storedUsers.add(adminBoundary);
		storedUsers.add(UserTestUtil.userPost(restTemplate, url, details1));
		storedUsers.add(UserTestUtil.userPost(restTemplate, url, details2));

		UserBoundary[] allUsers = UserTestUtil.getAllUsers(restTemplate, adminBoundary.getUserId().getDomain(),
				adminBoundary.getUserId().getEmail(), url);

		assertThat(allUsers.length).isEqualTo(3);
		assertThat(allUsers).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedUsers);
	}

	@Test
	public void testGETAllUsersGivenAnEmptyDatabaseReturnsStatusDifferentFrom2xx() {
		// GIVEN AN EMPTY DATABASE
		// WHEN I GET allUsers according to /acs/admin/users/{adminDomain}/{adminEmail}
		// THEN throw a RuntimeException
		assertThrows(Exception.class,
				() -> UserTestUtil.getAllUsers(restTemplate, this.appDomain, "AdminEmail@gmail.com", url));
	}

	@Test
	public void testGETAllUsersGivenNonEmptyDatabaseWithNonAdminUserReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE with a non admin user type.
		// WHEN I GET allUsers by /acs/admin/users/{adminDomain}/{adminEmail} with non
		// admin arguments
		// THEN throw a RuntimeException

		NewUserDetails playerUser = new NewUserDetails("normalPlayerUserEmail@gmail.com", UserRole.PLAYER, "name0",
				"avatar0");
		NewUserDetails managerUser = new NewUserDetails("normalManagerUserEmail@gmail.com", UserRole.MANAGER, "name1",
				"avatar1");

		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName0", "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "userName1", "avatar1");
		NewUserDetails details3 = new NewUserDetails("userEmail2@gmail.com", UserRole.PLAYER, "userName2", "avatar2");

		UserBoundary playerUserBoundary = UserTestUtil.userPost(restTemplate, url, playerUser);
		UserBoundary managerUserBoundary = UserTestUtil.userPost(restTemplate, url, managerUser);
		UserTestUtil.userPost(restTemplate, url, details1);
		UserTestUtil.userPost(restTemplate, url, details2);
		UserTestUtil.userPost(restTemplate, url, details3);

		assertThrows(Exception.class, () -> UserTestUtil.getAllUsers(restTemplate,
				playerUserBoundary.getUserId().getDomain(), playerUserBoundary.getUserId().getEmail(), url));
		assertThrows(Exception.class, () -> UserTestUtil.getAllUsers(restTemplate,
				managerUserBoundary.getUserId().getDomain(), managerUserBoundary.getUserId().getEmail(), url));
	}

	@Test
	public void testGetAllUsersFromServerWithOnlyTheAdminInTheDatabaseReturnStatus2xxAndAndSize1Array()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary[] rv = UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url);

		// THEN the return value is an empty array
		assertThat(rv).hasSize(1);
		assertThat(rv[0]).isEqualToComparingFieldByField(adminUserBoundary);

	}

	@Test
	public void testGetAllUserFromServerWithSixUsersInDatabaseReturnArraysOf6Users() throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 5 users
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		IntStream.range(0, 5).forEach(i -> UserTestUtil.userPost(restTemplate, url));

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary[] rv = UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url);

		// THEN the server returns array of 5 users
		assertThat(rv).hasSize(6);
	}

	@Test
	public void testGetAllUserFromServerWith20UsersInDatabaseReturnArraysOf20Users() throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 20 Users
		List<UserBoundary> storedUsers = new ArrayList<>();
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		storedUsers.add(adminUserBoundary);
		for (int i = 0; i < 19; i++) {
			storedUsers.add(UserTestUtil.userPost(restTemplate, url));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary[] rv = UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url);

		// THEN the server returns array of 20 users
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(storedUsers);
	}

	@Test
	public void testGetAllUserFromServerWith30UsersInDatabaseNotReturnArraysOf30SameUsersTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 30 elements
		List<UserBoundary> storedUsers = new ArrayList<>();
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		storedUsers.add(adminUserBoundary);
		for (int i = 0; i < 29; i++) {
			storedUsers.add(UserTestUtil.userPost(restTemplate, url));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary[] rv = UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url);

		// THEN the server returns array of 20 elements
		assertThat(rv).hasSize(20);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedUsers);

	}

	@Test
	public void testGetAllUserFromServerWith30UsersInDatabaseWillReturnOnlyThe10UsersFromPage0() throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 20 elements
		List<UserBoundary> storedUsers = new ArrayList<>();
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		storedUsers.add(adminUserBoundary);
		for (int i = 0; i < 29; i++) {
			storedUsers.add(UserTestUtil.userPost(restTemplate, url));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary[] rv = UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url, 10, 0);

		// THEN the server returns array of 10 elements
		assertThat(rv).hasSize(10);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedUsers);

	}

	@Test
	public void testGetAllUserFromServerWith42UsersInDatabaseWillReturnAllUsersPageByPageInSizeOf10() throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 42 elements
		List<UserBoundary> storedUsers = new ArrayList<>();
		UserBoundary adminUserBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		storedUsers.add(adminUserBoundary);
		for (int i = 0; i < 41; i++) {
			storedUsers.add(UserTestUtil.userPost(restTemplate, url));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<UserBoundary> rv = new ArrayList<UserBoundary>(Arrays.asList(UserTestUtil.getAllUsers(restTemplate,
				adminUserBoundary.getUserId().getDomain(), adminUserBoundary.getUserId().getEmail(), url, 10, 0)));

		rv.addAll(Arrays.asList(UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url, 10, 1)));
		rv.addAll(Arrays.asList(UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url, 10, 2)));
		rv.addAll(Arrays.asList(UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url, 10, 3)));
		rv.addAll(Arrays.asList(UserTestUtil.getAllUsers(restTemplate, adminUserBoundary.getUserId().getDomain(),
				adminUserBoundary.getUserId().getEmail(), url, 10, 4)));

		// THEN the server returns array of 42 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(storedUsers);
	}

}
