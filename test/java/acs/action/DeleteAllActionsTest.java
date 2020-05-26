package acs.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
class DeleteAllActionsTest {

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
	public void testDETELEAllActionsWithDatabaseWithTenElementsReturnEmptyArray() {
		// GIVEN A NON EMPTY DATABASE
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		ActionBoundary[] beforDelete = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);
		assertThat(beforDelete).isEmpty();

		// WHEN I DELETE all elements by /acs/admin/elements/{adminDomain}/{adminEmail}
		ElementBoundary map = ElementTestUtil.randElementBoundary();
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
		for (int i = 0; i < 10; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		ActionBoundary[] rv = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary, 10, 0);

		assertThat(rv).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);

		ActionTestUtil.deleteActions(this.restTemplate, this.url, adminBoundary);

		// Then the after delete array is empty
		ActionBoundary[] afterDelete = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);
		assertThat(afterDelete).isEmpty();
	}

	@Test
	public void testDETELEAllElementsGivenNonEmptyDatabaseWithTenElementsReturnEmptyArray() {
		// GIVEN A NON EMPTY DATABASE
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		ActionBoundary[] beforDelete = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);
		assertThat(beforDelete).isEmpty();

		// WHEN I DELETE all elements by /acs/admin/elements/{adminDomain}/{adminEmail}
		ElementBoundary map = ElementTestUtil.randElementBoundary();
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
		for (int i = 0; i < 10; i++) {
			actionBoundary.setActionAttributes(ActionTestUtil.randFeedingAreaAttributes(managerBoundary, map));
			storedElements.add(ActionTestUtil.postAction(restTemplate, url, actionBoundary));
		}

		beforDelete = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary, 10, 0);

		assertThat(beforDelete).usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(storedElements);

		ActionTestUtil.deleteActions(this.restTemplate, this.url, adminBoundary);

		// Then the after delete array is empty
		ActionBoundary[] afterDelete = ActionTestUtil.getAllActions(restTemplate, url, adminBoundary);
		assertThat(afterDelete).isEmpty();
	}
}
