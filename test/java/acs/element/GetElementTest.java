package acs.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import acs.boundaries.ElementBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.details.ElementId;
import acs.boundaries.details.UserId;
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GetElementTest {
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
	public void testGetSingleElementFromServerWithEmptyDatabaseReturnStatusDifferenceFrom2xx() throws Exception {
		// GIVEN the server is up
		// do nothing
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.randElementBoundary();
		// WHEN I GET
		// /acs/elements/2020b.eylon.mizrahi/notGood@mail/2020b.eylon.mizrahi/notGoodId

		// THEN the server returns status != 2xx
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

	@Test
	public void testGetSingleElementWithDatabaseContatingThatElementRetreivesThatElement() throws Exception {
		// GIVEN the database contains an Element with id x
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		// WHEN I GET /messages/x
		ElementBoundary specificElementId = ElementTestUtil.getElement(restTemplate, url, managerBoundary, element);

		// THEN the server returns a message boundary with id: x
		assertThat(element.getElementId()).usingRecursiveComparison().isEqualTo(specificElementId.getElementId());
	}

	@Test
	public void testGetSingleElementWithDatabaseContatingThatElementButWithDifferenceDomainReturnStatusDifferenceFrom2xx()
			throws Exception {
		// GIVEN the database contains an Element with id x
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		String domain = "notGoodDomain";
		element.setElementId(new ElementId(domain, element.getElementId().getId()));
		// WHEN I GET /messages/x

		// THEN the server returns a message boundary with id: x
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

	@Test
	public void testGetSingleElementWithDatabaseContatingThatElementButWithDifferenceElementIdReturnStatusDifferenceFrom2xx()
			throws Exception {
		// GIVEN the database contains an Element with id x
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		String id = "notGoodId";
		element.setElementId(new ElementId(element.getElementId().getDomain(), id));

		// WHEN I GET /messages/x

		// THEN the server returns a message boundary with id: x
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

	@Test
	public void testGetSingleElementWithDatabaseContatingThatElementAndNineOther() throws Exception {
		// GIVEN the database contains an Element with id x and Nine others
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		IntStream.range(0, 9).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I GET /messages/x

		// THEN the server returns a message boundary with id: x
		assertThat(element).usingRecursiveComparison()
				.isEqualTo(ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

	@Test
	public void testGetSingleElementByPassingNullElementIdWithDatabaseContatingElementsReturnStatusDifferenceFrom2xx()
			throws Exception {
		// GIVEN the database contains an Element with id x and Nine others
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		String id = "notGoodId";
		element.setElementId(new ElementId(element.getElementId().getDomain(), id));

		IntStream.range(0, 10).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I GET /messages/x

		// THEN the server returns a message boundary with id: x
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

	@Test
	public void testGetSingleElementByPassingNullDomainWithDatabaseContatingElementsReturnStatusDifferenceFrom2xx()
			throws Exception {
		// GIVEN the database contains an Element with id x and Nine others
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		String domain = "notGoodDomain";
		element.setElementId(new ElementId(domain, element.getElementId().getId()));

		IntStream.range(0, 10).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I GET /messages/x

		// THEN the server returns a message boundary with id: x
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));

	}

	@Test
	public void testGetSingleElementByPassingNullEmailWithDatabaseContatingElementsReturnStatusDifferenceFrom2xx()
			throws Exception {
		// GIVEN the database contains an Element with id x and Nine others
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());
		ElementBoundary element = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		IntStream.range(0, 10).forEach(i -> ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));

		// WHEN I GET /messages/x
		// THEN the server returns a message boundary with id: x
		managerBoundary.setUserId(new UserId(managerBoundary.getUserId().getDomain(), null));
		assertThrows(Exception.class, () -> ElementTestUtil.getElement(restTemplate, url, managerBoundary, element));
	}

}
