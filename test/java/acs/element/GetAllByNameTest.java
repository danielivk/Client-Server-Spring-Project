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
public class GetAllByNameTest {

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
	public void testGetElementsWithSpecificNameFromServerWith20ElementsWithoutThisSpecificNameInDatabaseReturnEmpty()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		String name = "name";
		IntStream.range(0, 20).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		// WHEN I GET /{userDomain}/{userEmail}/search/byName/{name}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary);

		// THEN the return value is an empty array
		assertThat(rv).isEmpty();
	}

	@Test
	public void testGetElementsWithSpecificNameFromServerWith6ElementsInDatabaseReturnOneElmenetWithSpecificName()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String name = "name";

		// GIVEN database which contains 6 elements
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		ElementBoundary element = ElementTestUtil.elementPostWithSpecificName(restTemplate, url, name, managerBoundary);

		// WHEN I GET /{userDomain}/{userEmail}/search/byName/{name}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary);

		// THEN the server returns array with the Specific Name
		assertThat(rv[0]).usingRecursiveComparison().isEqualTo(element);
	}

	@Test
	public void testGetAllElementsWithSpecificNameFromServerWith20ElementsInDatabaseReturnArraysOf5ElementsWithSpecificName()
			throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 20 elements
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String name = "name";
		IntStream.range(0, 15).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificName(this.restTemplate, this.url, name, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}/search/byName/{Name}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary);

		// THEN the server returns array of 5 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsFromServerWith30ElementsWithSpecificNameInDatabaseWillNotReturnArraysOf30ElementWithSpecificNameTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 30 elements
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String name = "name";
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificName(this.restTemplate, this.url, name, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary);

		// THEN the server returns array of 20 elements
		assertThat(rv).hasSize(20);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsWithSpecificNameFromServerWith30ElementsWithSpecificNameInDatabaseWillReturnOnlyThe10ElementFromPage0()
			throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 20 elements
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String name = "name";
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificName(this.restTemplate, this.url, name, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary, 10, 0);

		// THEN the server returns array of 10 elements
		assertThat(rv).hasSize(10);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator().containsAnyElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsWithSpecificNameFromServerWith42ElementsWithSpecificNameInDatabaseWillReturnAllElementWithSpecificNamePageByPageInSizeOf10()
			throws Exception {
		// GIVEN the server is up
		// do nothing

		// GIVEN database which contains 42 elements
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		String name = "name";

		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(
					ElementTestUtil.elementPostWithSpecificName(this.restTemplate, this.url, name, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<ElementBoundary> rv = new ArrayList<ElementBoundary>();
		for (int i = 0; i < 5; i++) {
			rv.addAll(Arrays
					.asList(ElementTestUtil.getAllElementsByName(restTemplate, url, name, managerBoundary, 10, i)));
		}

		// THEN the server returns array of 42 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}
}
