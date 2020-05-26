package util;

import org.springframework.web.client.RestTemplate;

import acs.boundaries.UserBoundary;

public class DeleteTestUtil {

	public static void deleteAllDatabases(RestTemplate restTemplate, String url) {
		UserBoundary tempAdmin = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());
		// Delete all actions:
		restTemplate.delete(url + "/admin/actions/{adminDomain}/{adminEmail}", tempAdmin.getUserId().getDomain(),
				tempAdmin.getUserId().getEmail());
		// Delete all elements:
		restTemplate.delete(url + "/admin/elements/{adminDomain}/{adminEmail}", tempAdmin.getUserId().getDomain(),
				tempAdmin.getUserId().getEmail());
		// Delete all users:
		restTemplate.delete(url + "/admin/users/{adminDomain}/{adminEmail}", tempAdmin.getUserId().getDomain(),
				tempAdmin.getUserId().getEmail());
	}
}
