package jm.stockx.controller.rest;

import com.github.scribejava.apis.vk.VKOAuth2AccessToken;
import com.github.scribejava.core.model.OAuth2AccessToken;
import jm.stockx.auth.GoogleAuthorization;
import jm.stockx.entity.User;
import jm.stockx.UserService;
import jm.stockx.auth.VkAuthorisation;
import jm.stockx.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/authorization")
public class AuthRestController {

    private GoogleAuthorization googleAuthorization;
    private VkAuthorisation vkAuthorization;
    private UserService userService;

    @Autowired
    public AuthRestController(GoogleAuthorization googleAuthorization,
                              VkAuthorisation vkAuthorisation,
                              UserService userService) {
        this.googleAuthorization = googleAuthorization;
        this.vkAuthorization = vkAuthorisation;
        this.userService = userService;
    }

    @GetMapping("/vkAuth")
    public Response<Object> toVk() throws URISyntaxException {
        URI vk = new URI(vkAuthorization.getAuthorizationUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(vk);
        Response.BodyBuilder bodyBuilder = Response.ok();
        return bodyBuilder.headers(httpHeaders).build();
    }

    @GetMapping("/returnCodeVK")
    public Response<Object> getCodeThird(@RequestParam String code) throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        OAuth2AccessToken token = vkAuthorization.toGetTokenVK(code);
        String email = ((VKOAuth2AccessToken) token).getEmail();
        User currentUser = vkAuthorization.toCreateUser(token, email);
        if (userService.getUserByUserName(email) == null) {
            userService.createUser(currentUser);
        }
        userService.login(currentUser.getUsername(), currentUser.getPassword(), currentUser.getAuthorities());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/index"));
        Response.BodyBuilder bodyBuilder = Response.ok();
        return bodyBuilder.headers(httpHeaders).build();
    }

    @GetMapping("/googleAuth")
    public Response<?> toGoogle() throws URISyntaxException {
        URI google = new URI(googleAuthorization.getAuthorizationUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(google);
        Response.BodyBuilder bodyBuilder = Response.ok();
        return bodyBuilder.headers(httpHeaders).build();
    }

    @GetMapping("/returnCodeGoogle")
    public Response<?> getCodeGoogle(@RequestParam String code) throws InterruptedException, ExecutionException, IOException, URISyntaxException {
        OAuth2AccessToken token = googleAuthorization.getGoogleOAuth2AccessToken(code);
        String email = ((VKOAuth2AccessToken) token).getEmail();
        User currentUser = googleAuthorization.getGoogleUser(token, email);
        userService.login(currentUser.getUsername(), currentUser.getPassword(), currentUser.getAuthorities());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/index"));
        Response.BodyBuilder bodyBuilder = Response.ok();
        return bodyBuilder.headers(httpHeaders).build();
    }
}