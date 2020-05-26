package acs.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Map;

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
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreateElementTest {
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
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementDomainVariableChangeProperly()
			throws Exception {
		// GIVEN an empty database
		String domain = appDomain;
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		String elementDomain = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary).getElementId()
				.getDomain();

		// THEN the database is updated with a new element with and element domain
		// variable
		// "2020b.eylon.mizrahi"
		assertThat(elementDomain).isEqualTo(domain);
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementIdVariableChangeProperly() throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		String elementId = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getElementId().getId();

		// THEN the database is updated with a new element with an element id variable
		// different then "notGoodId"
		assertThat(elementId).isNotEqualTo(null);
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementTypeVariableChangeProperly() throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		String elementType = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getType();

		// THEN the database is updated with a new element with a type variable
		// equals to "testType"
		assertThat(elementType).isEqualTo(element.getType());
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementNameVariableChangeProperly() throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		String elementName = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getName();

		// THEN the database is updated with a new element with a name variable
		// equals to "testName"
		assertThat(elementName).isEqualTo(element.getName());
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementActiveVariableChangeProperly()
			throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		Boolean elementActiveState = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getActive();

		// THEN the database is updated with a new element with an active variable
		// equals to true
		assertThat(elementActiveState).isEqualTo(element.getActive());
	}

	// TODO: WHAT WHAT
	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementDateWhenDateInTheBodyDosentCorrectChangeProperly()
			throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		Date elementTimestamp = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getCreatedTimestamp();

		// THEN the database is updated with a new element with a date variable
		// different then new Date() that i send to him
		assertThat(elementTimestamp).isNotEqualTo(element.getCreatedTimestamp());
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementCreatedByWhenCreatedByInTheBodyDosentCorrectChangeProperly()
			throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		CreatedBy elementCreatedBy = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getCreatedBy();

		// THEN the database is updated with a new element with a createdBy variable
		// from the url and not from the body
		assertThat(elementCreatedBy.getUserId()).usingRecursiveComparison()
				.isNotEqualTo(element.getCreatedBy().getUserId());
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementLocationChangeProperly() throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function
		Location elementLocation = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getLocation();

		// THEN the database is updated with a new element with a location variables
		// lat = 13.3 and lng = 23.5
		assertThat(elementLocation).usingRecursiveComparison().isEqualTo(element.getLocation());
	}

	@Test
	public void testPostCreateTwoElementInEmptyDatebaseWithSameElementIdInTheBodyAndExpectTwoElementWithDifferenttId()
			throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the newElement function two times
		ElementId element1 = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getElementId();
		ElementId element2 = ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary)
				.getElementId();

		// THEN the database is updated with two new elements with different id from
		// each other
		assertThat(element1).usingRecursiveComparison().isNotEqualTo(element2);
	}

	@Test
	public void testPostCreateOneElementInEmptyDatebaseAndCheckTheElementAttributesChangeProperly() throws Exception {
		// GIVEN an empty database
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} and send the body
		// from the randElementBoundary function
		Map<String, Object> elementAttributes = ElementTestUtil
				.elementPost(this.restTemplate, this.url, element, managerBoundary).getElementAttributes();

		// THEN the database is updated with a new element with a random Attributes
		// variables
		System.out.println(elementAttributes);
		// check the values
		assertThat(elementAttributes).isEqualTo(element.getElementAttributes());
	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_null_type_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the type to null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setType(null);

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));

		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("invalid type");
		}

	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_null_name_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the name to null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setName(null);

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));

		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("invalid name");
		}
	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_null_element_attributes_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the element attributes to null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setElementAttributes(null);

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));

		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("invalid attributes");
		}
	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_null_active_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the active to null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setActive(null);

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));

		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("invalid active status");
		}
	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_null_location_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the location to null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setLocation(null);

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));
		System.out.println(element.toString());
		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("invalid location");
		}
	}

	@Test
	public void test_POST_create_one_element_in_empty_database_with_not_null_element_id_return_status_difference_from_2xx()
			throws Exception {
		// GIVEN an empty database

		// WHEN i change the element id to be not a null
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		element.setElementId(new ElementId(appDomain, "something"));

		// THEN the database will not updated with a new element
		assertThrows(Exception.class,
				() -> ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary));

		// Check the exception is the right one
		try {
			ElementTestUtil.elementPost(this.restTemplate, this.url, element, managerBoundary);
		} catch (Exception error) {
			assertThat(error.getMessage()).contains("ElementId must be defined as null");
		}
	}

	@Test
	public void test_POST_create_element_with_manager() throws Exception {
		// GIVEN an empty database
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} with a non-manager
		// user
		ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		// THEN the server would reject the POST requests and would contain no element
		assertThat(element).isNotEqualTo(null);
	}

	@Test
	public void test_POST_create_element_with_non_manager_user_return_null() throws Exception {
		// GIVEN an empty database
		UserBoundary playerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createPlayer());
		UserBoundary adminBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createAdmin());

		// WHEN I POST /acs/elements/{managerDomain}/{managerEmail} with a non-manager
		// user
		ElementBoundary elementByPlayer = ElementTestUtil.elementPost(this.restTemplate, this.url, playerBoundary);
		ElementBoundary elementByAdmin = ElementTestUtil.elementPost(this.restTemplate, this.url, adminBoundary);

		// THEN the server would reject the POST requests and would contain no element
		assertThat(elementByPlayer).isEqualTo(null);
		assertThat(elementByAdmin).isEqualTo(null);
	}
}