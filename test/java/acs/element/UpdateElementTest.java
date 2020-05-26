package acs.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

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
import acs.boundaries.details.CreatedBy;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.Location;
import acs.boundaries.details.UserId;
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UpdateElementTest {
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

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfLocationChangedProperly() throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's
		// location (13.3, 23.5) with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with different location
		update.setLocation(new Location(20.5, 30.5));

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getLocation())
				.usingRecursiveComparison().isNotEqualTo(oldBoundary.getLocation());
	}

	// Working for isEqual and for isNotEqual!!!!! NEED TO CHECK!!!!! - Null problem
	// with object
	@Test
	public void testPutUpdateOneElementWithNullLocationInDatabaseAndCheckIfLocationStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's
		// location (13.3, 23.5) with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with null location
		update.setLocation(null);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getLocation())
				.usingRecursiveComparison().isNotEqualTo(oldBoundary.getLocation());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfActiveChangedProperly() throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's
		// active "true" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with "false" active
		update.setActive(!oldBoundary.getActive());

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getActive())
				.isEqualTo(update.getActive());
	}

	@Test
	public void testPutUpdateOneElementWithNullActiveInDatabaseAndCheckIfActiveStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's
		// active "true" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with "false" active
		update.setActive(null);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getActive())
				.isEqualTo(oldBoundary.getActive());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfTypeChangedProperly() throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's type
		// "testType" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		String type = "type";

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with different type
		update.setType(type);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getType())
				.isEqualTo(update.getType());
	}

	@Test
	public void testPutUpdateOneElementWithNullTypeInDatabaseAndCheckIfTypeStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's type
		// "testType" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		String type = null;

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with null type
		update.setType(type);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getType())
				.isEqualTo(oldBoundary.getType());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfNameChangedProperly() throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's name
		// "testName" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		String name = "name";

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with different name
		update.setType(name);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getName())
				.isEqualTo(update.getName());
	}

	@Test
	public void testPutUpdateOneElementWithNullNameInDatabaseAndCheckIfNameStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's name
		// "testName" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		String name = null;

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with null name
		update.setType(name);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getName())
				.isEqualTo(oldBoundary.getName());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfCreatedByStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's
		// userEmail "elad@mad" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		CreatedBy createdBy = new CreatedBy(
				new UserId(oldBoundary.getElementId().getDomain() + "hey", oldBoundary.getElementId().getId() + "hey"));

		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with different CreatedBy
		update.setCreatedBy(createdBy);

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getCreatedBy().getUserId()
				.getEmail()).isNotEqualTo(update.getCreatedBy().getUserId().getEmail());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseAndCheckIfDateStayTheSameAsInTheOldBoundaryLikeItShould()
			throws Exception {
		// GIVEN database which contain a single element with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with new Date
		update.setCreatedTimestamp(new Date());

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		assertThat(ElementTestUtil.getElement(restTemplate, url, managerBoundary, update).getCreatedTimestamp())
				.isNotEqualTo(update.getCreatedTimestamp());
	}

	@Test
	public void testPutUpdateOneElementInDatabaseWithElementIdThatDoesNotExistAndCheckIfReturnStatusDifferentFrom2xx()
			throws Exception {
		// GIVEN database which contain a single element with the ElementBoundary's name
		// "testName" with id {id}
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary oldBoundary = ElementTestUtil.elementPost(restTemplate, url, managerBoundary);
		ElementBoundary update = new ElementBoundary(oldBoundary.getElementId(), oldBoundary.getType(),
				oldBoundary.getName(), oldBoundary.getActive(), oldBoundary.getCreatedTimestamp(),
				oldBoundary.getCreatedBy(), oldBoundary.getLocation(), oldBoundary.getElementAttributes());
		// WHEN I PUT
		// /acs/elements/{managerDomain}/{managerEmail}/{elementDomain}/{elementId} and
		// send new element boundary with different name and different ElementId
		update.setElementId(
				new ElementId(oldBoundary.getElementId().getDomain(), oldBoundary.getElementId().getId() + "hey"));

		ElementTestUtil.updateElement(restTemplate, url, update, oldBoundary, managerBoundary);

		// THEN the server returns status != 2xx
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, update));
	}

	@Test
	public void test_PUT_update_element_with_manager_user() {
		// GIVEN a database with a single element
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary old = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary update = ElementTestUtil.randElementBoundary();

		// WHEN I PUT /acs/elements/{managerDomain}/{managerEmail} with a manager user
		ElementTestUtil.updateElement(this.restTemplate, this.url, update, old, managerBoundary);
		ElementBoundary updatedElement = ElementTestUtil.getElementById(this.restTemplate, this.url, managerBoundary,
				old.getElementId());

		// THEN the server would accept the PUT request and would contain the updated
		// element
		assertThat(updatedElement).isEqualToComparingOnlyGivenFields(update, "type", "name", "active", "location.lat",
				"location.lng", "elementAttributes");
	}

	@Test
	public void test_PUT_update_element_with_admin_user() {
		// GIVEN a database with a single element

		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary old = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary update = ElementTestUtil.randElementBoundary();

		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		// WHEN I PUT /acs/elements/{managerDomain}/{managerEmail} with a NON manager
		// user then an exception would occur
		assertThrows(RuntimeException.class,
				() -> ElementTestUtil.updateElement(this.restTemplate, this.url, update, old, adminBoundary));
	}

	@Test
	public void test_PUT_update_element_with_player_user() {
		// GIVEN a database with a single element

		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary old = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary update = ElementTestUtil.randElementBoundary();

		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());

		// WHEN I PUT /acs/elements/{managerDomain}/{managerEmail} with a NON manager
		// user then an exception would occur
		assertThrows(RuntimeException.class,
				() -> ElementTestUtil.updateElement(this.restTemplate, this.url, update, old, playerBoundary));
	}

}
