package acs.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.Element;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.InvokedBy;
import acs.boundaries.details.Location;
import acs.boundaries.details.NewUserDetails;
import acs.boundaries.details.UserRole;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UpdateActionTests {

	static String[] animals = new String[] { "dog", "cat", "mouse", "parrot", "bat" };
	static String[] foodBrands = new String[] { "bonzo", "drools", "tripett", "petkind" };
	static String[] waterQualities = new String[] { "amazing", "nice", "bad", "worst" };
	static int maxDays = 100;

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
	void update_food_bowl_by_invoke_action() {
		// Given a DB with a single Feeding Area and a single Water Bowl:
		UserBoundary playerBoundary = createPlayer();

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
		addFeedingAreaToMap(map, playerBoundary);

		ElementBoundary feedingArea = getChildrenOf(map)[0];
		addFoodBowlToFeedingArea(feedingArea, playerBoundary);

		// When I PUT a new Water Bowl:
		ElementBoundary foodBowl = getChildrenOf(feedingArea)[0];
		Map<String, Object> updatedAttributes = updateFoodBowl(foodBowl.getElementId(), playerBoundary);

		// Then the DB would contain the updated Water Bowl:
		ElementBoundary updatedFoodBowl = getChildrenOf(feedingArea)[0];
		assertTrue(updatedFoodBowl.getElementId().equals(foodBowl.getElementId()));
		assertThat(updatedFoodBowl.getElementAttributes().equals(updatedAttributes));
	}

	@Test
	void update_water_bowl_by_invoke_action() {
		// Given a DB with a single Feeding Area and a single Water Bowl:
		UserBoundary playerBoundary = createPlayer();

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
		addFeedingAreaToMap(map, playerBoundary);

		ElementBoundary feedingArea = getChildrenOf(map)[0];
		addWaterBowlToFeedingArea(feedingArea, playerBoundary);

		// When I PUT a new Water Bowl:
		ElementBoundary waterBowl = getChildrenOf(feedingArea)[0];
		Map<String, Object> updatedAttributes = updateWaterBowl(waterBowl.getElementId(), playerBoundary);

		// Then the DB would contain the updated Water Bowl:
		ElementBoundary updatedWaterBowl = getChildrenOf(feedingArea)[0];
		assertTrue(updatedWaterBowl.getElementId().equals(waterBowl.getElementId()));
		assertThat(updatedWaterBowl.getElementAttributes().equals(updatedAttributes));
	}

	@Test
	void update_feeding_area_by_invoke_action() {
		// Given a DB with a single Feeding Area:
		UserBoundary playerBoundary = createPlayer();

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
		addFeedingAreaToMap(map, playerBoundary);

		// When I PUT a new Feeding Area:
		ElementBoundary feedingArea = getChildrenOf(map)[0];
		Map<String, Object> updatedAttrs = updateFeedingAreaByUpdatingBowls(feedingArea.getElementId(), playerBoundary);

		// Then the DB would contain the updated Feeding Area:
		ElementBoundary updatedFeedingArea = getChildrenOf(map)[0];
		
		assertTrue(updatedFeedingArea.getElementId().equals(updatedFeedingArea.getElementId()));
		assertTrue(updatedFeedingArea.getElementAttributes().equals(updatedAttrs));
	}

	@Test
	void update_fod_bowl_and_water_bowl_by_invoke_action() {
		// Given a DB with a single Feeding Area and a Food Bowl and a Water Bowl:
		UserBoundary playerBoundary = createPlayer();

		ElementBoundary map = this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/search/byName/{name}", ElementBoundary[].class,
				this.appDomain, this.managerEmail, this.mapName)[0];
		addFeedingAreaToMap(map, playerBoundary);

		ElementBoundary feedingArea = getChildrenOf(map)[0];
		addFoodBowlToFeedingArea(feedingArea, playerBoundary);
		addWaterBowlToFeedingArea(feedingArea, playerBoundary);

		// When I PUT a new Food Bowl and a new Water Bowl:
		ElementBoundary[] bowls = getChildrenOf(feedingArea);
		List<Map<String, Object>> updatedAttributes = new ArrayList<>();
		IntStream.range(0, bowls.length).forEach(i -> updatedAttributes.add(updateBowl(bowls[i], playerBoundary)));

		// Then the DB would contain the updated Food Bowl and Water Bowl:
		ElementBoundary[] updatedBowls = getChildrenOf(feedingArea);

		assertThat(updatedBowls).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(Arrays.asList(bowls));
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

	private Map<String, Object> updateFoodBowl(ElementId foodBowlId, UserBoundary playerBoundary) {
		Map<String, Object> foodBowlAttrs = foodBowlActionAttributes();
		ActionBoundary updateFoodBowlAction = new ActionBoundary(null, "refill-food_bowl", new Element(foodBowlId),
				null, new InvokedBy(playerBoundary.getUserId()), foodBowlAttrs);
		postAnAction(updateFoodBowlAction);
		foodBowlAttrs.remove("managerDomain");
		foodBowlAttrs.remove("managerEmail");
		foodBowlAttrs.remove("elementName");
		foodBowlAttrs.remove("elementLat");
		foodBowlAttrs.remove("elementLng");
		return foodBowlAttrs;
	}

	private Map<String, Object> updateWaterBowl(ElementId waterBowlId, UserBoundary playerBoundary) {
		Map<String, Object> waterBowlAttrs = waterBowlActionAttributes();
		ActionBoundary updateWaterBowlAction = new ActionBoundary(null, "refill-water_bowl", new Element(waterBowlId),
				null, new InvokedBy(playerBoundary.getUserId()), waterBowlAttrs);
		postAnAction(updateWaterBowlAction);
		waterBowlAttrs.remove("managerDomain");
		waterBowlAttrs.remove("managerEmail");
		waterBowlAttrs.remove("elementName");
		waterBowlAttrs.remove("elementLat");
		waterBowlAttrs.remove("elementLng");
		return waterBowlAttrs;
	}

	private Map<String, Object> updateBowl(ElementBoundary bowl, UserBoundary playerBoundary) {
		if (bowl.getType().equals("food_bowl")) {
			return updateFoodBowl(bowl.getElementId(), playerBoundary);
		}
		return updateWaterBowl(bowl.getElementId(), playerBoundary);
	}

	private Map<String, Object> updateFeedingAreaByUpdatingBowls(ElementId feedingAreaId, UserBoundary playerBoundary) {
		ElementBoundary feedingArea = getElement(feedingAreaId, playerBoundary);
		ElementBoundary[] bowls = getChildrenOf(feedingArea);
		Arrays.stream(bowls).forEach(bowl -> updateBowl(bowl, playerBoundary));
		return feedingArea.getElementAttributes();
	}

	public ElementBoundary getElement(ElementId elementId, UserBoundary userBoundary) {
		return this.restTemplate.getForObject(
				this.url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}", ElementBoundary.class,
				userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(), elementId.getDomain(),
				elementId.getId());
	}

	private Map<String, Object> feedingAreaActionAttributes() {
		Random r = new Random();
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "feeding_area_" + randString(5));
		actionAttributes.put("elementLat", r.nextDouble());
		actionAttributes.put("elementLng", r.nextDouble());

		actionAttributes.put("fullFoodBowl", 0);
		actionAttributes.put("fullWaterBowl", 0);
		return actionAttributes;
	}

	private Map<String, Object> foodBowlActionAttributes() {
		Random r = new Random();
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "food_bowl_" + randString(5));
		actionAttributes.put("elementLat", r.nextDouble());
		actionAttributes.put("elementLng", r.nextDouble());

		actionAttributes.put("state", r.nextBoolean());
		actionAttributes.put("brand", foodBrands[r.nextInt(foodBrands.length)]);
		actionAttributes.put("animal", animals[r.nextInt(animals.length)]);
		actionAttributes.put("weight", r.nextInt(500));
		actionAttributes.put("lastFillDate", LocalDate.now().minusDays(new Random().nextInt(maxDays)));
		return actionAttributes;
	}

	private Map<String, Object> waterBowlActionAttributes() {
		Random r = new Random();
		Map<String, Object> actionAttributes = new TreeMap<>();
		actionAttributes.put("managerDomain", this.appDomain);
		actionAttributes.put("managerEmail", this.managerEmail);
		actionAttributes.put("elementName", "water_bowl_" + randString(5));
		actionAttributes.put("elementLat", r.nextDouble());
		actionAttributes.put("elementLng", r.nextDouble());

		actionAttributes.put("state", r.nextBoolean());
		actionAttributes.put("waterQuality", waterQualities[r.nextInt(waterQualities.length)]);
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
			e.printStackTrace();
		}
		return null;
	}

	public static String randString(int size) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
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
