package acs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import acs.boundaries.ActionBoundary;
import acs.boundaries.UserBoundary;
import acs.logic.DBActionService;
import acs.logic.DBElementService;
import acs.logic.DBUserService;

@RestController
@RequestMapping("/acs/admin")
public class AdminController {
	private DBActionService actionService;
	private DBElementService elementService;
	private DBUserService userService;

	@Autowired
	public AdminController(DBActionService actionService, DBElementService elementService, DBUserService userService) {
		this.actionService = actionService;
		this.elementService = elementService;
		this.userService = userService;
	}

	@RequestMapping(path = "/users/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllUsers(@PathVariable String adminDomain, @PathVariable String adminEmail) {
		this.userService.deleteAllUsers(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/elements/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllElements(@PathVariable String adminDomain, @PathVariable String adminEmail) {
		this.elementService.deleteAllElements(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/actions/{adminDomain}/{adminEmail}", method = RequestMethod.DELETE)
	public void deleteAllActions(@PathVariable String adminDomain, @PathVariable String adminEmail) {
		this.actionService.deleteAllActions(adminDomain, adminEmail);
	}

	@RequestMapping(path = "/users/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers(@PathVariable String adminDomain, @PathVariable String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.userService.getAllUsers(adminDomain, adminEmail, size, page).toArray(new UserBoundary[0]);
	}

	@RequestMapping(path = "/actions/{adminDomain}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] exportAllActions(@PathVariable String adminDomain, @PathVariable String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "20") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.actionService.getAllActions(adminDomain, adminEmail, size, page).toArray(new ActionBoundary[0]);
	}

}
