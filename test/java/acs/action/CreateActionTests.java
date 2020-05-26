package acs.action;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.Element;
import acs.boundaries.details.InvokedBy;
import acs.boundaries.details.Location;
import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserRole;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CreateActionTests {

	private int port;
	private String url;
	private RestTemplate restTemplate;
	private String adminEmail;
	private String managerEmail;
	private String playerEmail;
	private String mapName;

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
		this.adminEmail = "admin@email.feeder";
		this.managerEmail = "manager@email.feeder";
		this.playerEmail = "player@email.feeder";
		this.mapName = "map";
	}

	@BeforeEach
	public void setup() {
		deleteAllDatabases();
		postManagerAndMap();
	}

	@AfterEach
	public void teardown() {
		deleteAllDatabases();
	}

	@Test
	void addFeedingAreaToMapActionByPlayerAndAssertChildBindingTest() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingAreaChildOfMap = getChildrenOf(map)[0];
		assertTrue(feedingAreaChildOfMap.getElementAttributes().equals(feedingAreaAttributes()));
	}

	@Test
	void addFeedingAreaToMapActionByNonPlayerUserDoesNothing() {
		// Given Non-Empty DB with Manager and Admin
		UserBoundary managerBoundary = UserTestUtil.managerPost(this.restTemplate, this.url);
		UserBoundary adminBoundary = UserTestUtil.adminPost(this.restTemplate, this.url);

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		// When Manager and Admin try to invoke action
		// Then permission denied - RuntimeException
		assertThrows(Exception.class, () -> addFeedingAreaToMap(map, managerBoundary));
		assertThrows(Exception.class, () -> addFeedingAreaToMap(map, adminBoundary));
	}

	@Test
	void addFeedingAreaToMapActionAndFoodBowlToFeedingAreaByNonPlayerUserDoesNothing() {
		// Given Non-Empty DB
		UserBoundary playerBoundary = createPlayer();
		UserBoundary managerBoundary = UserTestUtil.managerPost(this.restTemplate, this.url);
		UserBoundary adminBoundary = UserTestUtil.adminPost(this.restTemplate, this.url);

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingAreaChildOfMap = getChildrenOf(map)[0];

		// When Manager and Admin try to invoke action
		// Then permission denied - RuntimeException
		assertThrows(Exception.class, () -> addFoodBowlToFeedingArea(feedingAreaChildOfMap, managerBoundary));
		assertThrows(Exception.class, () -> addFoodBowlToFeedingArea(feedingAreaChildOfMap, adminBoundary));
	}

	@Test
	void addFeedingAreaToMapActionByPlayerAndAddFoodBowlToFeedingAreaAndAssertBind() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingAreaChildOfMap = getChildrenOf(map)[0];
		addFoodBowlToFeedingArea(feedingAreaChildOfMap, playerBoundary);
		ElementBoundary foodBowlChildOfFeedingArea = getChildrenOf(feedingAreaChildOfMap)[0];
		assertTrue(foodBowlChildOfFeedingArea.getElementAttributes().equals(foodBowlAttributes()));
	}

	@Test
	void addTwoFeedingAreasToMapAndAddBowlsToThemByPlayerAndCheckForChildrenBinding() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		addFeedingAreaToMap(map, playerBoundary);
		addFeedingAreaToMap(map, playerBoundary);

		// child of the map will be feeding_area
		ElementBoundary[] feedingAreas = getChildrenOf(map);
		ElementBoundary feedingAreaChildOfMap1 = feedingAreas[0];
		ElementBoundary feedingAreaChildOfMap2 = feedingAreas[1];

		assertTrue(feedingAreaChildOfMap1.getElementAttributes().equals(feedingAreaAttributes()));
		assertTrue(feedingAreaChildOfMap2.getElementAttributes().equals(feedingAreaAttributes()));

		addWaterBowlToFeedingArea(feedingAreaChildOfMap1, playerBoundary);
		addFoodBowlToFeedingArea(feedingAreaChildOfMap2, playerBoundary);

		ElementBoundary[] childrenOfFeedingArea1 = getChildrenOf(feedingAreaChildOfMap1);
		ElementBoundary[] childrenOfFeedingArea2 = getChildrenOf(feedingAreaChildOfMap2);
		assertFalse(feedingAreaChildOfMap1.equals(feedingAreaChildOfMap2));
		assertTrue(childrenOfFeedingArea1[0].getElementAttributes().equals(waterBowlAttributes()));
		assertTrue(childrenOfFeedingArea2[0].getElementAttributes().equals(foodBowlAttributes()));
	}

	@Test
	void addTwoFeedingAreasToMapAndAddBowlsToThemByNonPlayerUserDoesNothing() {
		// Given Non-Empty DB
		UserBoundary playerBoundary = createPlayer();
		UserBoundary managerBoundary = UserTestUtil.managerPost(this.restTemplate, this.url);
		UserBoundary adminBoundary = UserTestUtil.adminPost(this.restTemplate, this.url);

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];

		addFeedingAreaToMap(map, playerBoundary);
		addFeedingAreaToMap(map, playerBoundary);

		// child of the map will be feeding_area
		ElementBoundary feedingAreaChildOfMap1 = getChildrenOf(map)[0];
		ElementBoundary feedingAreaChildOfMap2 = getChildrenOf(map)[1];

		assertTrue(feedingAreaChildOfMap1.getElementAttributes().equals(feedingAreaAttributes()));
		assertTrue(feedingAreaChildOfMap2.getElementAttributes().equals(feedingAreaAttributes()));

		// When Manager and Admin try to invoke action
		// Then permission denied - RuntimeException
		assertThrows(Exception.class, () -> addWaterBowlToFeedingArea(feedingAreaChildOfMap1, managerBoundary));
		assertThrows(Exception.class, () -> addFoodBowlToFeedingArea(feedingAreaChildOfMap2, adminBoundary));
	}

	private ElementBoundary[] getChildrenOf(ElementBoundary element) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children",
				ElementBoundary[].class, this.appDomain, this.managerEmail, element.getElementId().getDomain(),
				element.getElementId().getId());
	}

	private void addWaterBowlToFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary addWaterBowlToFeedingAreaAction = new ActionBoundary(null, "add-water_bowl",
				new Element(feedingArea.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				waterBowlActionAttributes());
		postAnAction(addWaterBowlToFeedingAreaAction);

	}

	private void addFoodBowlToFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary addFoodBowlToFeedingAreaAction = new ActionBoundary(null, "add-food_bowl",
				new Element(feedingArea.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				foodBowlActionAttributes());
		postAnAction(addFoodBowlToFeedingAreaAction);

	}

	private void addFeedingAreaToMap(ElementBoundary map, UserBoundary playerBoundary) {
		ActionBoundary addFeedingAreaToMapAction = new ActionBoundary(null, "add-feeding_area",
				new Element(map.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				feedingAreaActionAttributes());

		postAnAction(addFeedingAreaToMapAction);
	}

	private Map<String, Object> feedingAreaActionAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "feeding_area");
		actionAttributes.put("elementLat", 3);
		actionAttributes.put("elementLng", 4);

		actionAttributes.put("fullFoodBowl", 5);
		actionAttributes.put("fullWaterBowl", 6);
		return actionAttributes;
	}

	private Map<String, Object> feedingAreaAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("fullFoodBowl", 5);
		actionAttributes.put("fullWaterBowl", 6);
		return actionAttributes;
	}

	private Map<String, Object> foodBowlActionAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "food_bowl");
		actionAttributes.put("elementLat", 3);
		actionAttributes.put("elementLng", 4);

		actionAttributes.put("state", true);
		actionAttributes.put("brand", "bonzo");
		actionAttributes.put("animal", "dog");
		actionAttributes.put("weight", 300);
		actionAttributes.put("lastFillDate", null);
		return actionAttributes;
	}

	private Map<String, Object> foodBowlAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();

		actionAttributes.put("state", true);
		actionAttributes.put("brand", "bonzo");
		actionAttributes.put("animal", "dog");
		actionAttributes.put("weight", 300);
		actionAttributes.put("lastFillDate", null);
		return actionAttributes;
	}

	private Map<String, Object> waterBowlActionAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "water_bowl");
		actionAttributes.put("elementLat", 3);
		actionAttributes.put("elementLng", 4);

		actionAttributes.put("state", true);
		actionAttributes.put("waterQuality", "Good");
		return actionAttributes;
	}

	private Map<String, Object> waterBowlAttributes() {
		Map<String, Object> actionAttributes = new TreeMap<>();

		actionAttributes.put("state", true);
		actionAttributes.put("waterQuality", "Good");

		return actionAttributes;
	}

	String randTimestamp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		long min = 1000000000000L;
		long max = 1587039873000L;
		Random rand = new Random();
		long timestamp = rand.nextLong() % (max - min) + max;
		return dateFormat.format(timestamp).toString();
	}

	Date strToDate(String dateLikeString) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			return dateFormat.parse(dateLikeString);
		} catch (ParseException e) {
		}
		return null;
	}

//	private void createAdmin() {
//		this.restTemplate.postForObject(this.url + "/users",
//				new NewUserDetails(this.adminEmail, UserRole.ADMIN, "adminName", "adminAvatar"), UserBoundary.class);
//	}

	private void postManagerAndMap() {
		UserBoundary managerBoundary = createManager();
		ElementBoundary mapBoundary = new ElementBoundary(null, "maptype", "map", true, new Date(),
				new CreatedBy(managerBoundary.getUserId()), new Location(3, 4), new TreeMap<String, Object>());
		elementPost(mapBoundary);
	}

	private UserBoundary createManager() {
		return this.restTemplate.postForObject(this.url + "/users",
				new NewUserDetails(this.managerEmail, UserRole.MANAGER, "manager_name", "manager_avatar"),
				UserBoundary.class);
	}

	private UserBoundary createAdmin() {
		return this.restTemplate.postForObject(this.url + "/users",
				new NewUserDetails(this.adminEmail, UserRole.ADMIN, "adminName", "adminAvatar"), UserBoundary.class);
	}

	private UserBoundary createPlayer() {
		return this.restTemplate.postForObject(this.url + "/users",
				new NewUserDetails(this.playerEmail, UserRole.PLAYER, "player_name", "player_avatar"),
				UserBoundary.class);
	}

	private ActionBoundary postAnAction(ActionBoundary action) {
		return this.restTemplate.postForObject(this.url + "/actions", action, ActionBoundary.class);
	}

	private ElementBoundary elementPost(ElementBoundary elementBoundary) {
		return restTemplate.postForObject(this.url + "/elements/{managerDomain}/{managerEmail}", elementBoundary,
				ElementBoundary.class, this.appDomain, this.managerEmail);
	}

	private void deleteAllDatabases() {
		createAdmin();
		// Delete all actions:
		this.restTemplate.delete(this.url + "/admin/actions/{adminDomain}/{adminEmail}", this.appDomain,
				this.adminEmail);
		// Delete all elements:
		this.restTemplate.delete(this.url + "/admin/elements/{adminDomain}/{adminEmail}", this.appDomain,
				this.adminEmail);
		// Delete all users:
		this.restTemplate.delete(this.url + "/admin/users/{adminDomain}/{adminEmail}", this.appDomain, this.adminEmail);
	}
}
