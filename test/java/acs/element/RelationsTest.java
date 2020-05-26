package acs.element;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;
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
import util.DeleteTestUtil;
import util.ElementTestUtil;
import util.UserTestUtil;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RelationsTest {
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
	public void bind_two_elements_test() throws Exception {
		// GIVEN an empty database
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN I change bind 2 elements together
		ElementBoundary elementFather = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary elementChild = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		ElementTestUtil.bindElement(this.restTemplate, this.url, elementFather, elementChild);

		// THEN the database will show the binding results
		assertThat(elementChild).usingRecursiveComparison()
				.isEqualTo(ElementTestUtil.getChildrenOf(this.restTemplate, this.url, elementFather)[0]);
	}

	@Test
	public void bind_two_elements_to_single_father_test() throws Exception {
		// GIVEN an empty database
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN i bind 2 elements to the same element
		ElementBoundary elementFather = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary[] children = new ElementBoundary[10];
		IntStream.range(0, 10)
				.forEach(i -> children[i] = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary));
		IntStream.range(0, 10)
				.forEach(i -> ElementTestUtil.bindElement(this.restTemplate, this.url, elementFather, children[i]));

		// THEN the database will show the binding results
		assertThat(children).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(
				Arrays.stream(ElementTestUtil.getChildrenOf(this.restTemplate, this.url, elementFather))
						.collect(Collectors.toList()));
	}

	@Test
	public void bind_child_element_to_its_father_and_the_father_to_its_grandfather_test() throws Exception {
		// GIVEN an empty database
		UserBoundary managerBoundary = UserTestUtil.userPost(restTemplate, url, UserTestUtil.createManager());

		// WHEN i bind a child element to a father element, and then bind the father
		// element to a grandfather element
		ElementBoundary elementGrandFather = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary elementFather = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);
		ElementBoundary elementChild = ElementTestUtil.elementPost(this.restTemplate, this.url, managerBoundary);

		ElementTestUtil.bindElement(this.restTemplate, this.url, elementFather, elementChild);
		ElementTestUtil.bindElement(this.restTemplate, this.url, elementGrandFather, elementFather);

		// THEN the database will show both of the binding results
		assertThat(elementFather).usingRecursiveComparison()
				.isEqualTo(ElementTestUtil.getParentOf(this.restTemplate, this.url, elementChild)[0]);
		assertThat(elementGrandFather).usingRecursiveComparison()
				.isEqualTo(ElementTestUtil.getParentOf(this.restTemplate, this.url, elementFather)[0]);
	}

}
