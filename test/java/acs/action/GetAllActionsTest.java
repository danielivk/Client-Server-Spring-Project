package acs.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.Element;
import acs.boundaries.details.InvokedBy;
import util.ActionTestUtil;
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GetAllActionsTest {

	private int port;
	private String url;
	private RestTemplate restTemplate;

	@Value("${spring.application.name:default}")
	private String appDomain;

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.url = "http://localhost:" + this.port + "/acs";
		this.restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup() {
		DeleteTestUtil.deleteAllDatabases(restTemplate, url);
	}

	@AfterEach
	public void teardown() {
		DeleteTestUtil.deleteAllDatabases(restTemplate, url);
	}

	@Test
	void addFeedingAreaToMapActionAndAssertChildBindingTest() {
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action
		ActionTestUtil.postAction(restTemplate, url, actionBoundary);

		// get all actions
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);

		assertThat(rv).hasSize(1);
	}

	@Test
	public void testGetAllActionsFromServerWithEmptyDatabaseReturnStatus2xxAndAnEmptyArray() throws Exception {
		// GIVEN the server is up
		// do nothing

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);

		// THEN the return value is an empty array
		assertThat(rv).isEmpty();

	}

	@Test
	public void testGetAllActionsFromServerWithFiveElementsInDatabaseReturnArraysOf5Actions() throws Exception {
		// GIVEN the server is up
		// do nothing
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action

		// GIVEN database which contains 5 actions
		List<ActionBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);

		// THEN the server returns array of 5 actions
		assertThat(rv).hasSize(5);
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllActionsFromServerWith20ActionInDatabaseReturnArraysOf20SameActions() throws Exception {
		// GIVEN the server is up
		// do nothing
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action

		// GIVEN database which contains 5 actions
		List<ActionBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);

		// THEN the server returns array of 20 actions
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

	@Test
	public void testGetAllActionsFromServerWith30ActionsInDatabaseWillNotReturnArraysOf30SameActionsTheDefualtSizeOfPageIs20()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action

		// GIVEN database which contains 5 actions
		List<ActionBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);

		// THEN the server returns array of 20 actions
		assertThat(rv).hasSize(20);
	}

	@Test
	public void testGetAllActionsFromServerWith30ActionInDatabaseWillReturnOnlyThe10ActionFromPage0() throws Exception {
		// GIVEN the server is up
		// do nothing
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action

		// GIVEN database which contains 5 actions
		List<ActionBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary, 10, 0);

		// THEN the server returns array of 10 actions
		assertThat(rv).hasSize(10);
	}

	@Test
	public void testGetAllActionsFromServerWith42ActionsInDatabaseWillReturnAllActionsPageByPageInSizeOf10()
			throws Exception {
		// GIVEN the server is up
		// do nothing
		// create an map element to put the feeding area on him
		ElementBoundary map = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		map.setName("map");
		map.setType("map");
		map.setActive(true);
		map = ElementTestUtil.elementPost(restTemplate, url, map, managerBoundary);

		// create actionBoundary to create feeding area
		ActionBoundary actionBoundary = ActionTestUtil.randActionBoundary();
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		actionBoundary.setType("add-feeding_area");
		actionBoundary.setInvokedBy(new InvokedBy(playerBoundary.getUserId()));
		actionBoundary.setElement(new Element(map.getElementId()));

		// send invoke action

		// GIVEN database which contains 5 actions
		List<ActionBoundary> storedElements = new ArrayList<>();
		for (int i = 0; i < 42; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		// WHEN I GET /{userDomain}/{userEmail}
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		// WHEN I GET /{userDomain}/{userEmail}
		ArrayList<ActionBoundary> rv = new ArrayList<ActionBoundary>();
		for (int i = 0; i < 5; i++) {
			rv.addAll(Arrays.asList(ActionTestUtil.getAllActions(restTemplate, url, adminBoundary, 10, i)));
		}

		// THEN the server returns array of 42 actions
		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);
	}

//	private Map<String, Object> foodBowlActionAttributes() {
//		Map<String, Object> actionAttributes = new TreeMap<>();
//		actionAttributes.put("managerDomain", this.appDomain);
//		actionAttributes.put("managerEmail", this.managerEmail);
//		actionAttributes.put("elementName", "food_bowl");
//		actionAttributes.put("elementLat", 3);
//		actionAttributes.put("elementLng", 4);
//
//		actionAttributes.put("state", true);
//		actionAttributes.put("brand", "bonzo");
//		actionAttributes.put("animal", "dog");
//		actionAttributes.put("weight", 300);
//		actionAttributes.put("lastFillDate", null);
//		return actionAttributes;
//	}
//
//	private Map<String, Object> foodBowlAttributes() {
//		Map<String, Object> actionAttributes = new TreeMap<>();
//
//		actionAttributes.put("state", true);
//		actionAttributes.put("brand", "bonzo");
//		actionAttributes.put("animal", "dog");
//		actionAttributes.put("weight", 300);
//		actionAttributes.put("lastFillDate", null);
//		return actionAttributes;
//	}
//
//	private Map<String, Object> waterBowlActionAttributes() {
//		Map<String, Object> actionAttributes = new TreeMap<>();
//		actionAttributes.put("managerDomain", this.appDomain);
//		actionAttributes.put("managerEmail", this.managerEmail);
//		actionAttributes.put("elementName", "water_bowl");
//		actionAttributes.put("elementLat", 3);
//		actionAttributes.put("elementLng", 4);
//
//		actionAttributes.put("state", true);
//		actionAttributes.put("waterQuality", "Good");
//		return actionAttributes;
//	}
//
//	private Map<String, Object> waterBowlAttributes() {
//		Map<String, Object> actionAttributes = new TreeMap<>();
//
//		actionAttributes.put("state", true);
//		actionAttributes.put("waterQuality", "Good");
//
//		return actionAttributes;
//	}
}
