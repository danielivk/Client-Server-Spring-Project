package acs.action;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RemoveActionTests {

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
	void testRemoveFeedingAreaFromNonEmptyDBRemovesSameFeedingAreaAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingAreaChildOfMap = getChildrenOf(map)[0];
		// Existent feedingAreaChildOfMap test
		assertTrue(feedingAreaChildOfMap.getElementAttributes().equals(feedingAreaAttributes()));
		
		// Removes existing feeding area 
		removeFeedingAreaFromMap(map, playerBoundary);
		
		assertFalse(getElementById(feedingAreaChildOfMap).getActive());
	}
	
	@Test
	void testRemoveFeedingAreaFromNonEmptyDBRemovesSameFeedingAreaFromExistingMapChildrenAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingAreaChildOfMap = getChildrenOf(map)[0];
		// Existent feedingAreaChildOfMap test
		assertTrue(feedingAreaChildOfMap.getElementAttributes().equals(feedingAreaAttributes()));
		
		// Removes existing feeding area 
		removeFeedingAreaFromMap(map, playerBoundary);
		
		assertFalse(getChildrenOf(map)[0].getActive());
	}
	
	@Test
	void testRemoveWaterbowlFromNonEmptyDBRemovesSameWaterbowlAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingArea = getChildrenOf(map)[0];
		// Existent feedingArea test
		assertTrue(feedingArea.getElementAttributes().equals(feedingAreaAttributes()));

		addWaterBowlToFeedingArea(feedingArea, playerBoundary);
		ElementBoundary waterBowlChildOfFeedingArea = getChildrenOf(feedingArea)[0];
		// Existent waterBowlChildOfFeedingArea test
		assertTrue(waterBowlChildOfFeedingArea.getElementAttributes().equals(waterBowlAttributes()));
		
		
		// Removes existing WaterBowl
		removeWaterBowlFromFeedingArea(feedingArea, playerBoundary);
		
		assertFalse(getElementById(waterBowlChildOfFeedingArea).getActive());
	}
	
	@Test
	void testRemoveWaterbowlFromNonEmptyDBRemovesSameWaterbowlFromExistingFeedingareaChildrenAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingArea = getChildrenOf(map)[0];
		// Existent feedingArea test
		assertTrue(feedingArea.getElementAttributes().equals(feedingAreaAttributes()));

		addWaterBowlToFeedingArea(feedingArea, playerBoundary);
		ElementBoundary waterBowlChildOfFeedingArea = getChildrenOf(feedingArea)[0];
		// Existent waterBowlChildOfFeedingArea test
		assertTrue(waterBowlChildOfFeedingArea.getElementAttributes().equals(waterBowlAttributes()));
		
		
		// Removes existing WaterBowl
		removeWaterBowlFromFeedingArea(feedingArea, playerBoundary);
		
		assertFalse(getChildrenOf(feedingArea)[0].getActive());
	}
	
	@Test
	void testRemoveFoodbowlFromNonEmptyDBRemovesSameFoodbowlAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingArea = getChildrenOf(map)[0];
		addFoodBowlToFeedingArea(feedingArea, playerBoundary);
		ElementBoundary foodBowlChildOfFeedingArea = getChildrenOf(feedingArea)[0];
		assertTrue(foodBowlChildOfFeedingArea.getElementAttributes().equals(foodBowlAttributes()));
		
		// Removes existing WaterBowl
		removeFoodBowlFromFeedingArea(feedingArea, playerBoundary);
		
		assertFalse(getElementById(foodBowlChildOfFeedingArea).getActive());
	}
	
	@Test
	void testRemoveFoodbowlFromNonEmptyDBRemovesSameFoodbowlFromExistingFeedingareaChildrenAndReturnsStatus2xx() {
		UserBoundary playerBoundary = createPlayer();
		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
	
		// Given Non-empty database
		addFeedingAreaToMap(map, playerBoundary);
		ElementBoundary feedingArea = getChildrenOf(map)[0];
		addFoodBowlToFeedingArea(feedingArea, playerBoundary);
		ElementBoundary foodBowlChildOfFeedingArea = getChildrenOf(feedingArea)[0];
		assertTrue(foodBowlChildOfFeedingArea.getElementAttributes().equals(foodBowlAttributes()));
		
		// Removes existing WaterBowl
		removeFoodBowlFromFeedingArea(feedingArea, playerBoundary);
		
		
		assertFalse(getChildrenOf(feedingArea)[0].getActive());
	}
	
	private ElementBoundary[] getChildrenOf(ElementBoundary element) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children",
				ElementBoundary[].class, this.appDomain,
				this.managerEmail, element.getElementId().getDomain(),
				element.getElementId().getId());
	}
	
	private ElementBoundary getElementById(ElementBoundary element) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}",
				ElementBoundary.class, this.appDomain,
				this.managerEmail, element.getElementId().getDomain(),
				element.getElementId().getId());
	}
	
	private void addWaterBowlToFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary addWaterBowlToFeedingAreaAction = new ActionBoundary(null, "add-water_bowl",
				new Element(feedingArea.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				waterBowlActionAttributes());
		postAnAction(addWaterBowlToFeedingAreaAction);

	}
	
	private void removeWaterBowlFromFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary removeWaterBowlFromMapAction = new ActionBoundary(null, "remove-water_bowl",
				new Element(feedingArea.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				waterBowlActionAttributes());
	
		postAnAction(removeWaterBowlFromMapAction);
	}
	
	private void addFoodBowlToFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary addFoodBowlToFeedingAreaAction = new ActionBoundary(null, "add-food_bowl",
				new Element(feedingArea.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				foodBowlActionAttributes());
		postAnAction(addFoodBowlToFeedingAreaAction);

	}
	
	private void removeFoodBowlFromFeedingArea(ElementBoundary feedingArea, UserBoundary playerBoundary) {
		ActionBoundary addFoodBowlToFeedingAreaAction = new ActionBoundary(null, "remove-food_bowl",
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
	
	private void removeFeedingAreaFromMap(ElementBoundary map, UserBoundary playerBoundary) {
		ActionBoundary removeFeedingAreaFromMapAction = new ActionBoundary(null, "remove-feeding_area",
				new Element(map.getElementId()), null, new InvokedBy(playerBoundary.getUserId()),
				feedingAreaActionAttributes());
	
		postAnAction(removeFeedingAreaFromMapAction);
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

	private void createAdmin() {
		this.restTemplate.postForObject(this.url + "/users",
				new NewUserDetails(this.adminEmail, UserRole.ADMIN, "adminName", "adminAvatar"), UserBoundary.class);
	}
	
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

	private UserBoundary createPlayer() {
		return this.restTemplate.postForObject(this.url + "/users",
				new NewUserDetails(this.playerEmail, UserRole.PLAYER, "player_name", "player_avatar"), UserBoundary.class);
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
