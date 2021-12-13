package com.fnr.confiar.controllers;

import java.util.Optional;

import com.fnr.confiar.Response;
import com.fnr.confiar.entities.User;
import com.fnr.confiar.exceptions.InvalidPasswordResponseException;
import com.fnr.confiar.exceptions.UserNotFoundResponseException;
import com.fnr.confiar.models.SessionInfoModel;
import com.fnr.confiar.services.LoginService;
import com.fnr.confiar.services.MessageService;
import com.fnr.confiar.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController extends BaseController {
  @Autowired
  LoginService loginService;

  @Autowired
  UserService userService;

  @Autowired
  private MessageService messageService;

  @GetMapping()
  public ResponseEntity<Response> getCurrentSessionInfo() {
    SessionInfoModel sessionInfo = SessionInfoModel.getInstance();
    return this.responseOk(sessionInfo);
  }

  @GetMapping(path = "/login")
  public ResponseEntity<Response> login(@RequestParam(User.Fields.userName) String userOrMail, @RequestParam(User.Fields.password) String password) {
    ResponseEntity<Response> response = null;
    SessionInfoModel sessionInfo = null;

    Optional<User> optionalUser = (!userOrMail.contains("@")) ? userService.findByUserName(userOrMail) : userService.findByMail(userOrMail);
    try {
      sessionInfo = loginService.doLogin(optionalUser, password);
      sessionInfo.generateNewToken();
      response = responseOk(sessionInfo);
    }
    catch (UserNotFoundResponseException | InvalidPasswordResponseException responseException) {
      response = buildResponseFromException(responseException, sessionInfo);
    }

    return response;
  }

  @GetMapping(path = "/logout")
  public ResponseEntity<Response> logout() {
    ResponseEntity<Response> response = null;

    SessionInfoModel sessionInfo = SessionInfoModel.getInstance();
    sessionInfo.setToken(null);
    sessionInfo.setUser(null);
    response = responseOk(sessionInfo);

    return response;
  }

  @PostMapping(path = "/validate", consumes=MediaType.TEXT_PLAIN_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> validate(@RequestBody String token) {
    SessionInfoModel sessionInfo = SessionInfoModel.getInstance();
    return (sessionInfo.getToken() != null && sessionInfo.getToken().equals(token)) ? responseOk(sessionInfo) : responseConflictError(messageService.getMessage("session.validation.invalid.token"), SessionInfoModel.class);
  }

}
