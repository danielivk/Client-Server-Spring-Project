package acs.element;

import static org.assertj.core.api.Assertions.assertThat;

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

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DeleteElementTest {
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
		DeleteTestUtil.deleteAllDatabases(this.restTemplate, this.url);
	}

	@AfterEach
	public void teardown() {
		DeleteTestUtil.deleteAllDatabases(this.restTemplate, this.url);
	}

	@Test
	public void testDETELEAllElementsGivenNonEmptyDatabaseWithTenElementsReturnEmptyArray() {
		// GIVEN A NON EMPTY DATABASE with an existing admin
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		IntStream.range(0, 10).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		ElementBoundary[] elementArr = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);
		assertThat(elementArr).hasSize(10);

		// WHEN I DELETE all elements by /acs/admin/elements/{adminDomain}/{adminEmail}
		ElementTestUtil.deleteElements(this.restTemplate, this.url, this.appDomain, adminBoundary);

		// THEN after i GET all users the array will be EMPTY.
		elementArr = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);
		assertThat(elementArr).hasSize(0);
		assertThat(elementArr).isEmpty();

	}

	@Test
	public void testDETELEAllUsersGivenNonEmptyDatabaseWithTenElementsReturnArrayInDifferenceLengthBeforeTheDelete() {
		// GIVEN A NON EMPTY DATABASE
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		IntStream.range(0, 9).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I DELETE all elements by /acs/admin/elements/{adminDomain}/{adminEmail}
		ElementBoundary[] beforDelete = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);

		ElementTestUtil.deleteElements(this.restTemplate, this.url, this.appDomain, adminBoundary);

		// Then the getAllElements before the delete have difference length then the
		// getAllElements after the delete

		ElementBoundary[] afterDelete = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);
		assertThat(afterDelete).isEmpty();

		assertThat(beforDelete.length).isNotEqualTo(afterDelete.length);
	}

	@Test
	public void testDETELEAllUsersGivenEmptyDatabaseWithTenElementsReturnEmptyArray() {
		// GIVEN A NON EMPTY DATABASE
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		ElementBoundary[] beforDelete = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);
		assertThat(beforDelete).isEmpty();

		// WHEN I DELETE all elements by /acs/admin/elements/{adminDomain}/{adminEmail}
		IntStream.range(0, 10).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		ElementTestUtil.deleteElements(this.restTemplate, this.url, this.appDomain, adminBoundary);

		// Then the after delete array is empty

		ElementBoundary[] afterDelete = ElementTestUtil.getAllElements(this.restTemplate, this.url, managerBoundary);

		assertThat(afterDelete).isEmpty();
	}

}
