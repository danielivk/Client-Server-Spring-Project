package util;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.springframework.web.client.RestTemplate;

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.Location;
import acs.boundaries.details.UserId;

public class ElementTestUtil {

	// make a random ElementBoundary
	public static ElementBoundary randElementBoundary() {
		Random rand = new Random();
		ElementId elementId = null;
		String randType = randString(5);
		String randName = randString(5);
		Boolean randActive = rand.nextBoolean();
		Date newDate = new Date();
		CreatedBy randCreatedBy = new CreatedBy(new UserId(randString(5), randString(5)));
		Location location = new Location(rand.nextInt(10), rand.nextInt(10));
		Map<String, Object> randAttributes = new TreeMap<>();
		IntStream.rangeClosed(0, rand.nextInt(10)).forEach(i -> randAttributes.put(randString(5), i));

		return new ElementBoundary(elementId, randType, randName, randActive, newDate, randCreatedBy, location,
				randAttributes);
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

	// POST a random ElementBoundary
	public static ElementBoundary elementPost(RestTemplate restTemplate, String url, UserBoundary userBoundary) {
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", randElementBoundary(),
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	// POST a specific ElementBoundary
	public static ElementBoundary elementPost(RestTemplate restTemplate, String url,
			ElementBoundary randElementBoundary, UserBoundary userBoundary) {
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", randElementBoundary,
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	// POST an element with a specific Type
	public static ElementBoundary elementPostWithSpecificType(RestTemplate restTemplate, String url, String type,
			UserBoundary userBoundary) {
		ElementBoundary element = randElementBoundary();
		element.setType(type);
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", element,
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	// POST an element with a specific Name
	public static ElementBoundary elementPostWithSpecificName(RestTemplate restTemplate, String url, String name,
			UserBoundary userBoundary) {
		ElementBoundary element = randElementBoundary();
		element.setName(name);
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", element,
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	// POST an element in a specific distance
	public static ElementBoundary elementPostWithInSpecificDistance(RestTemplate restTemplate, String url, Double lat,
			Double lng, Double distance, UserBoundary userBoundary) {
		Random rand = new Random();
		ElementBoundary element = randElementBoundary();
		element.getLocation().setLat(rand.nextInt(distance.intValue()) + lat - distance);
		element.getLocation().setLng(rand.nextInt(distance.intValue()) + lng - distance);
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", element,
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	public static ElementBoundary elementPostWithInSpecificDistanceAndType(RestTemplate restTemplate, String url,
			Double lat, Double lng, Double distance, String type, UserBoundary userBoundary) {
		Random rand = new Random();
		ElementBoundary element = randElementBoundary();
		element.getLocation().setLat(rand.nextInt(distance.intValue()) + lat - distance);
		element.getLocation().setLng(rand.nextInt(distance.intValue()) + lng - distance);
		element.setType(type);
		return restTemplate.postForObject(url + "/elements/{managerDomain}/{managerEmail}", element,
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	public static void deleteElements(RestTemplate restTemplate, String url, String appDomain,
			UserBoundary userBoundary) {
		restTemplate.delete(url + "/admin/elements/{adminDomain}/{adminEmail}", userBoundary.getUserId().getDomain(),
				userBoundary.getUserId().getEmail());
	}

	public static ElementBoundary getElement(RestTemplate restTemplate, String url, UserBoundary userBoundary,
			ElementBoundary elementBoundary) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}",
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				elementBoundary.getElementId().getDomain(), elementBoundary.getElementId().getId());
	}

	public static ElementBoundary getElementById(RestTemplate restTemplate, String url, UserBoundary userBoundary,
			ElementId elementId) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}",
				ElementBoundary.class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				elementId.getDomain(), elementId.getId());
	}

	public static ElementBoundary[] getAllElements(RestTemplate restTemplate, String url, UserBoundary userBoundary) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}", ElementBoundary[].class,
				userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	public static ElementBoundary[] getAllElements(RestTemplate restTemplate, String url, UserBoundary userBoundary,
			int size, int page) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}?size=" + size + "&page=" + page,
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail());
	}

	public static ElementBoundary[] getAllElementsByName(RestTemplate restTemplate, String url, String name,
			UserBoundary userBoundary) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/search/byName/{name}",
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				name);
	}

	public static ElementBoundary[] getAllElementsByName(RestTemplate restTemplate, String url, String name,
			UserBoundary userBoundary, int size, int page) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/search/byName/{name}?size=" + size + "&page=" + page,
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				name);
	}

	public static ElementBoundary[] getAllElementsByType(RestTemplate restTemplate, String url, String type,
			UserBoundary userBoundary) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/search/byType/{type}",
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				type);
	}

	public static ElementBoundary[] getAllElementsByType(RestTemplate restTemplate, String url, String type,
			UserBoundary userBoundary, int size, int page) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/search/byType/{type}?size=" + size + "&page=" + page,
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(),
				type);
	}

	public static ElementBoundary[] getAllElementsNearBy(RestTemplate restTemplate, String url, double lat, double lng,
			double distance, UserBoundary userBoundary) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}",
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(), lat,
				lng, distance);
	}

	public static ElementBoundary[] getAllElementsNearBy(RestTemplate restTemplate, String url, double lat, double lng,
			double distance, UserBoundary userBoundary, int size, int page) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}?size=" + size + "&page="
						+ page,
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(), lat,
				lng, distance);

	}

	public static ElementBoundary[] getAllElementsByTypeNearby(RestTemplate restTemplate, String url, Double lat,
			Double lng, Double distance, String type, UserBoundary userBoundary) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/search/typeNearby/{lat}/{lng}/{distance}/{type}",
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(), lat,
				lng, distance, type);
	}

	public static ElementBoundary[] getAllElementsByTypeNearby(RestTemplate restTemplate, String url, Double lat,
			Double lng, Double distance, String type, UserBoundary userBoundary, int size, int page) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/search/typeNearby/{lat}/{lng}/{distance}/{type}?size=" + size
						+ "&page=" + page,
				ElementBoundary[].class, userBoundary.getUserId().getDomain(), userBoundary.getUserId().getEmail(), lat,
				lng, distance, type);
	}

	public static void updateElement(RestTemplate restTemplate, String url, ElementBoundary update,
			ElementBoundary oldBoundary, UserBoundary managerBoundary) {
		restTemplate.put(url + "/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}", update,
				managerBoundary.getUserId().getDomain(), managerBoundary.getUserId().getEmail(),
				oldBoundary.getElementId().getDomain(), oldBoundary.getElementId().getId());
	}

	public static void bindElement(RestTemplate restTemplate, String url, ElementBoundary father,
			ElementBoundary child) {
		restTemplate.put(url + "/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children",
				child.getElementId(), father.getCreatedBy().getUserId().getDomain(),
				father.getCreatedBy().getUserId().getEmail(), father.getElementId().getDomain(),
				father.getElementId().getId());
	}

	public static ElementBoundary[] getParentOf(RestTemplate restTemplate, String url, ElementBoundary elementChild) {
		return restTemplate.getForObject(url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents",
				ElementBoundary[].class, elementChild.getCreatedBy().getUserId().getDomain(),
				elementChild.getCreatedBy().getUserId().getEmail(), elementChild.getElementId().getDomain(),
				elementChild.getElementId().getId());
	}

	public static ElementBoundary[] getChildrenOf(RestTemplate restTemplate, String url,
			ElementBoundary elementFather) {
		return restTemplate.getForObject(
				url + "/elements/{userDomain}/{userEmail}/{elementDomain}/{elementId}/children",
				ElementBoundary[].class, elementFather.getCreatedBy().getUserId().getDomain(),
				elementFather.getCreatedBy().getUserId().getEmail(), elementFather.getElementId().getDomain(),
				elementFather.getElementId().getId());
	}

	// make a random ElementBoundary
	public static ElementBoundary randElementBoundaryWithSpecificActive(Boolean isActive) {
		Random rand = new Random();
		ElementId elementId = null;
		String randType = randString(5);
		String randName = randString(5);
		Date newDate = new Date();
		CreatedBy randCreatedBy = new CreatedBy(new UserId(randString(5), randString(5)));
		Location location = new Location(rand.nextInt(10), rand.nextInt(10));
		Map<String, Object> randAttributes = new TreeMap<>();
		IntStream.rangeClosed(0, rand.nextInt(10)).forEach(i -> randAttributes.put(randString(5), i));

		return new ElementBoundary(elementId, randType, randName, isActive, newDate, randCreatedBy, location,
				randAttributes);
	}
}
