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
public class DeleteAllUsersTest {
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
	public void testDELETEAllUsersGivenNonEmptyDatabaseWithExistingAdminThenGETtingAllUsersReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE with an existing admin
		// WHEN I DELETE allUsers by /acs/admin/users/{adminDomain}/{adminEmail}
		// THEN after i GET all users - throw a RuntimExceptin due to empty database after deleting.
			
		// Creates a non empty DATABASE	
		UserBoundary[] allUsers;
		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName0", "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "userName1", "avatar1");
		NewUserDetails adminDetails = createAdmin("AdminEmail@gmail.com");
		
		UserBoundary adminBoundary = postUser(adminDetails);
		postUser(details1);			
		postUser(details2);
		
		allUsers = getAllUsers(adminBoundary.getUserId().getDomain(), adminBoundary.getUserId().getEmail());
		assertThat(allUsers.length).isEqualTo(3);				
		
		deleteEveryUser(adminBoundary);
		
		assertThrows(Exception.class, () -> getAllUsers(adminBoundary.getUserId().getDomain(), adminBoundary.getUserId().getEmail()));
	}
	
	@Test
	public void testDETELEAllUsersGivenNonEmptyDatabaseWithPLAYERUserReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE 
		// WHEN I DELETE allUsers by /acs/admin/users/{adminDomain}/{adminEmail} with a PLAYER user Type
		// THEN throw a RuntimExceptin
			
		// Creates a non empty DATABASE	
		NewUserDetails details1 = new NewUserDetails("userEmail0@gmail.com", UserRole.PLAYER, "userName0", "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "userName1", "avatar1");
		
		// Creates a player in the database to use him as an admin
		NewUserDetails nonAdminUser = new NewUserDetails("userEmail30@gmail.com", UserRole.PLAYER, "userName1", "avatar1");

		postUser(details1);			
		postUser(details2);
		UserBoundary nonAdminUserBoundary = postUser(nonAdminUser);
		
		assertThrows(Exception.class, () -> deleteEveryUser(nonAdminUserBoundary));
	}
	
	@Test
	public void testDETELEAllUsersGivenNonEmptyDatabaseWithMANAGERUserReturnsStatusDifferentFrom2xx() {
		// GIVEN A NON EMPTY DATABASE 
		// WHEN I DELETE allUsers by /acs/admin/users/{adminDomain}/{adminEmail} with a MANAGER user Type
		// THEN throw a RuntimExceptin
			
		// Creates a non empty DATABASE	
		NewUserDetails details1 = new NewUserDetails("userEmail1@gmail.com", UserRole.PLAYER, "userName0", "avatar0");
		NewUserDetails details2 = new NewUserDetails("userEmail2@gmail.com", UserRole.PLAYER, "userName1", "avatar1");
		
		// Creates a manager in the database to use him as an admin
		NewUserDetails nonAdminUser = new NewUserDetails("userEmail3@gmail.com", UserRole.MANAGER, "userName1", "avatar1");

		postUser(details1);			
		postUser(details2);
		UserBoundary nonAdminUserBoundary = postUser(nonAdminUser);
		
		assertThrows(Exception.class, () -> deleteEveryUser(nonAdminUserBoundary));
	}
	
		
	private UserBoundary postUser(NewUserDetails details) {
		return this.restTemplate.postForObject(this.url + "/users", details, UserBoundary.class);
	}
		
	private UserBoundary[] getAllUsers(String adminDomain, String adminEmail) {
		return this.restTemplate.getForObject(this.url + "/admin/users/{adminDomain}/{adminEmail}",
				UserBoundary[].class, adminDomain, adminEmail);
	}
	
	private NewUserDetails createAdmin(String email) {
		return new NewUserDetails(email, UserRole.ADMIN, "adminName", "avatar");
	}
	
	private void deleteEveryUser(UserBoundary admin) {
		this.restTemplate.delete(this.url + "/admin/users/{adminDomain}/{adminEmail}", this.appDomain,
				admin.getUserId().getEmail());
	}
	
	private void deleteEveryUser() {
		UserBoundary tempAdmin = postUser(createAdmin("TemporaryAdminEmail@gmail.com"));
		this.restTemplate.delete(this.url + "/admin/users/{adminDomain}/{adminEmail}", this.appDomain,
				tempAdmin.getUserId().getEmail());
	}
}
