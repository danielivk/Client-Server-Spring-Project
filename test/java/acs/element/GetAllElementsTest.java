package acs.element;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
public class GetAllElementsTest {

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

	// test if server is up
	@Test
	public void testContext() {

	}

	@Test
	public void testGetAllElementsFromServerWithEmptyDatabaseReturnStatus2xxAndAnEmptyArray() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary);

		// THEN the return value is an empty array
		assertThat(rv).isEmpty();

	}

	@Test
	public void testGetAllElementsFromServerWithFiveElementsInDatabaseReturnArraysOf5Messages() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 5 elements
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary);

		// THEN the server returns array of 5 elements
		assertThat(rv).hasSize(5);
	}

	@Test
	public void testGetAllElementsFromServerWith20ElementsInDatabaseReturnArraysOf20SameMessages() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			storedElements.add(ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary);

		// THEN the server returns array of 20 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsFromServerWith30ElementsInDatabaseWillNotReturnArraysOf30SameMessagesTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 30 elements
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary);

		// THEN the server returns array of 20 elements
		assertThat(rv).hasSize(20);
	}

	@Test
	public void testGetAllElementsFromServerWith30ElementsInDatabaseWillReturnOnlyThe10ElementFromPage0()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary, 10, 0);

		// THEN the server returns array of 10 elements
		assertThat(rv).hasSize(10);
	}

	@Test
	public void testGetAllElementsFromServerWith42ElementsInDatabaseWillReturnAllElementPageByPageInSizeOf10()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 42 elements
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			storedElements.add(ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<ElementBoundary> rv = new ArrayList<ElementBoundary>();
		for (int i = 0; i < 5; i++) {
			rv.addAll(Arrays.asList(ElementTestUtil.getAllElements(restTemplate, url, managerBoundary, 10, i)));
		}

		// THEN the server returns array of 42 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsByPlayerWithFiveActiveElementsInDatabaseAnd3NotActiveReturnArraysOf5Messages()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());

		// GIVEN database which contains 5 active elements and 3 not active elements
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url,
				ElementTestUtil.randElementBoundaryWithSpecificActive(true), managerBoundary));
		IntStream.range(0, 3).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url,
				ElementTestUtil.randElementBoundaryWithSpecificActive(false), managerBoundary));

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, playerBoundary);

		// THEN the server returns array of 5 elements
		assertThat(rv).hasSize(5);
	}

	@Test
	public void testGetAllElementsByPlayerWithNoActiveElementsReturnsEmptyArray() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());

		// GIVEN database which contains 5 not active elements
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url,
				ElementTestUtil.randElementBoundaryWithSpecificActive(false), managerBoundary));

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElements(restTemplate, url, playerBoundary);

		// THEN the server returns empty array
		assertThat(rv).hasSize(0);
	}

	@Test
	public void testGetAllElementsFromServerWithFiveElementsInDatabaseButOnlySomeOfThemAreActive() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());

		// GIVEN database which contains 5 elements
		List<ElementBoundary> elementsInDB = new ArrayList<>();
		IntStream.range(0, 5).forEach(
				i -> elementsInDB.add(ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary)));

		List<ElementBoundary> onlyActiveElementsInDB = elementsInDB.stream().filter(elem -> elem.getActive() == true)
				.collect(Collectors.toList());

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rvManager = ElementTestUtil.getAllElements(restTemplate, url, managerBoundary);
		ElementBoundary[] rvPlayer = ElementTestUtil.getAllElements(restTemplate, url, playerBoundary);

		// THEN the server returns array of 5 elements for the manager and array of only
		// active elements for the player.
		assertThat(rvManager).hasSize(5);

		assertThat(elementsInDB).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(rvManager);
		assertThat(onlyActiveElementsInDB).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(rvPlayer);
	}

}
