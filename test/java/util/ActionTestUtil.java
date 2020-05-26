package util;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.ActionId;
import acs.boundaries.details.Element;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.InvokedBy;
import acs.boundaries.details.UserId;

public class ActionTestUtil {

	// make a random ActionBoundary
	public static ActionBoundary randActionBoundary() {
		Random rand = new Random();
		ActionId actionId = null;
		String randType = randString(5);
		Element element = new Element(new ElementId(randString(5), randString(5)));
		Date newDate = new Date();
		InvokedBy randInvokedBy = new InvokedBy(new UserId(randString(5), randString(5)));
		Map<String, Object> randAttributes = new TreeMap<>();
		IntStream.rangeClosed(0, rand.nextInt(10)).forEach(i -> randAttributes.put(randString(5), i));

		return new ActionBoundary(actionId, randType, element, newDate, randInvokedBy, randAttributes);
	}

	public static Map<String, Object> randFeedingAreaAttributes(UserBoundary UserBoundary,
			ElementBoundary elementBoundary) {
		Random rand = new Random();
		Map<String, Object> actionAttributes = new TreeMap<>();

		actionAttributes.put("managerDomain", UserBoundary.getUserId().getDomain());
		actionAttributes.put("managerEmail", UserBoundary.getUserId().getEmail());
		actionAttributes.put("elementName", "feeding_area");
		actionAttributes.put("elementLat", elementBoundary.getLocation().getLat());
		actionAttributes.put("elementLng", elementBoundary.getLocation().getLat());

		actionAttributes.put("fullFoodBowl", rand.nextInt(5));
		actionAttributes.put("fullWaterBowl", rand.nextInt(5));
		return actionAttributes;
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

	// POST an action
	public static ActionBoundary postAction(RestTemplate restTemplate, String url, ActionBoundary ActionBoundary) {
		return restTemplate.postForObject(url + "/actions", ActionBoundary, ActionBoundary.class);
	}

	public static ActionBoundary[] getAllActions(RestTemplate restTemplate, String url, UserBoundary UserBoundary) {
		return restTemplate.getForObject(url + "/admin/actions/{adminDomain}/{adminEmail}", ActionBoundary[].class,
				UserBoundary.getUserId().getDomain(), UserBoundary.getUserId().getEmail());
	}

	public static ActionBoundary[] getAllActions(RestTemplate restTemplate, String url, UserBoundary UserBoundary,
			int size, int page) {
		return restTemplate.getForObject(
				url + "/admin/actions/{adminDomain}/{adminEmail}?size=" + size + "&page=" + page,
				ActionBoundary[].class, UserBoundary.getUserId().getDomain(), UserBoundary.getUserId().getEmail());
	}

	public static void deleteActions(RestTemplate restTemplate, String url, UserBoundary adminBoundary) {
		restTemplate.delete(url + "/admin/actions/{adminDomain}/{adminEmail}", adminBoundary.getUserId().getDomain(),
				adminBoundary.getUserId().getEmail());
	}
}
