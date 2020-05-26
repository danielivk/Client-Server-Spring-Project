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
public class GelAllElementsByTypeNearby {

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
	public void testGetElementsWithSpecificTypeNearbyFromServerWith20ElementsWithoutElementInSpecificDistanceAndTypeInDatabaseReturnEmpty()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		Double lat = 12.0, lng = 12.0, distance = 1.0;
		String type = "type";
		IntStream.range(0, 20).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance, type,
				managerBoundary);
		// THEN the return value is an empty array
		assertThat(rv).isEmpty();

	}

	@Test
	public void testGetElementsWithSpecificTypeNearbyFromServerWith6ElementsWith1ElementWithSpecificTypeNearbyeInDatabaseReturnArraysOf1ElementsWithSpecificTypeNearby()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 6 elements
		Double lat = 20.0, lng = 20.0, distance = 5.0;
		String type = "type";
		IntStream.range(0, 5).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		ElementBoundary element = ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url,
				lat, lng, distance, type, managerBoundary);

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance, type,
				managerBoundary);

		// THEN the server returns array with the Specific Type
		assertThat(rv[0]).usingRecursiveComparison().isEqualTo(element);
	}

	@Test
	public void testGetElementsWithSpecificTypeNearbyFromServerWith20ElementsWith5ElementWithSpecificTypeNearbyInDatabaseReturnArraysOf5WithSpecificTypeNearby()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 20 elements
		Double lat = 20.0, lng = 20.0, distance = 5.0;
		String type = "type";

		IntStream.range(0, 15).forEach(i -> ElementTestUtil.elementPost(restTemplate, url, managerBoundary));
		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			storedElements.add(ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url,
					lat, lng, distance, type, managerBoundary));
		}
		ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url, lat, lng, distance,
				"another type", managerBoundary);

		// WHEN I GET /{userDomain}/{userEmail}/search/byType/{type}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance, type,
				managerBoundary);

		// THEN the server returns array of 5 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllElementsWithSpecificTypeNearbyFromServerWith30ElementsWithSpecificTypeNearbyInDatabaseWillNotReturnArraysOf30ElementsWithSpecificTypeNearbyTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// GIVEN database which contains 30 elements
		Double lat = 20.0, lng = 20.0, distance = 5.0;
		String type = "type";

		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url,
					lat, lng, distance, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance, type,
				managerBoundary);

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
		Double lat = 20.0, lng = 20.0, distance = 5.0;
		String type = "type";

		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			storedElements.add(ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url,
					lat, lng, distance, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ElementBoundary[] rv = ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance, type,
				managerBoundary, 10, 0);

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
		Double lat = 20.0, lng = 20.0, distance = 5.0;
		String type = "type";

		List<ElementBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			storedElements.add(ElementTestUtil.elementPostWithInSpecificDistanceAndType(this.restTemplate, this.url,
					lat, lng, distance, type, managerBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<ElementBoundary> rv = new ArrayList<ElementBoundary>();
		for (int i = 0; i < 5; i++) {
			rv.addAll(Arrays.asList(ElementTestUtil.getAllElementsByTypeNearby(restTemplate, url, lat, lng, distance,
					type, managerBoundary, 10, i)));
		}

		// THEN the server returns array of 42 elements
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

}
