package acs.element;

import static org.assertj.core.api.Assertions.assertThat;

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

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetAllByTypeTest {

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
	public void testGetElementsWithSpecificTypeFromServerWith20ElementsWithoutThisSpecificTypeInDatabaseReturnEmpty()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String type = "type";

		// GIVEN database which contains 20 elements
		IntStream.range(0, 20).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary);

		// THEN the return value is an empty array
		assertThat(rv).isEmpty();

	}

	@Test
	public void testGetElementsWithSpecificTypeFromServerWith6ElementsInDatabaseReturnOneElmenetWithSpecificType()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String type = "type";

		// GIVEN database which contains 6 elements
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		ElementBoundary element = ElementTestUtil.elementPostWithSpecificType(restTemplate, url, type, managerBoundary);

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary);

		// THEN the server returns array with the Specific Type
		assertThat(rv[0]).usingRecursiveComparison().isEqualTo(element);
	}

	@Test
	public void testGetAllElementsWithSpecificTypeFromServerWith20ElementsInDatabaseReturnArraysOf5ElementsWithSpecificType()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String type = "type";

		// GIVEN database which contains 20 elements
		IntStream.range(0, 15).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificType(this.restTemplate, this.url, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary);

		// THEN the server returns array of 5 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsFromServerWith30ElementsWithSpecificTypeInDatabaseWillNotReturnArraysOf30ElementWithSpecificTypeTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String type = "type";

		// GIVEN database which contains 30 elements
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificType(this.restTemplate, this.url, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary);

		// THEN the server returns array of 20 elements
		assertThat(rv).hasSize(20);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsWithSpecificTypeFromServerWith30ElementsWithSpecificTypeInDatabaseWillReturnOnlyThe10ElementFromPage0()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		String type = "type";
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificType(this.restTemplate, this.url, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary, 10, 0);

		// THEN the server returns array of 10 elements
		assertThat(rv).hasSize(10);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsWithSpecificTypeFromServerWith42ElementsWithSpecificTypeInDatabaseWillReturnAllElementWithSpecificTypePageByPageInSizeOf10()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 42 elements
		String type = "type";
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificType(this.restTemplate, this.url, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<ElementBoundary> rv = new ArrayList<ElementBoundary>();
		for (int i = 0; i < 5; i++) {
			rv.addAll(Arrays
					.asList(ElementTestUtil.getAllElementsByType(restTemplate, url, type, managerBoundary, 10, i)));
		}

		// THEN the server returns array of 42 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}
}
