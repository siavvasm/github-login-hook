/**
 * Copyright (c) 2016 Liferay, Inc. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.miltos.github;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;

import com.miltos.parsers.GithubSecretsParser;
import com.miltos.exceptions.AccessTokenRetrievalException;
import com.miltos.exceptions.UserNotFoundException;
import com.miltos.model.UserData;
import com.miltos.service.UserService;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Scanner;

import org.json.JSONException;


/**
 * @author Miltiadis Siavvas
 */

public class GithubOAuth extends BaseStrutsAction {

	private static String client_id = "";
	private static String client_secret = "";

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		client_id = GithubSecretsParser.getGithubCilentId();
		client_secret = GithubSecretsParser.getGithubCilentSecret();
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

		String cmd = ParamUtil.getString(request, Constants.CMD);
		
		if (cmd.equals("login")) {
						
			String url = "https://github.com/login/oauth/authorize?client_id=" + client_id;
			
			response.sendRedirect(url);
			
		} else if (cmd.equals("token")) {
			
			HttpSession session = request.getSession();

			String code = ParamUtil.getString(request, "code");

			if (Validator.isNotNull(code)) {

				String accessToken = getToken(code);
			
				UserData userData = getUserData(accessToken);
				
				User user = addOrUpdateUser(session, themeDisplay.getCompanyId(), userData);
						
				if ((user != null) && (user.getStatus() == 6)) {
					redirectUpdateAccount(request, response, user);
					return null;
				}

				allowAccess(request, response, themeDisplay);
				
			}
		}
		return null;
	}
	
	protected void allowAccess(HttpServletRequest request, HttpServletResponse response, ThemeDisplay themeDisplay) throws WindowStateException, IOException{
		
		PortletURL portletURL = PortletURLFactoryUtil.create(request, PortletKeys.FAST_LOGIN, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		portletURL.setWindowState(LiferayWindowState.POP_UP);

		portletURL.setParameter("struts_action", "/login/login_redirect");

		response.sendRedirect(portletURL.toString());
		
	}

	protected AuthorizationCodeFlow getAuthFlow() throws IOException {
		
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(), httpTransport, jsonFactory, 
				   new GenericUrl("https://github.com/login/oauth/access_token"), 
				   new ClientParametersAuthentication(client_id, client_secret), 
				   client_id, "https://github.com/login/oauth/authorize").build();

		return flow;
	}
	
	protected String getToken(String authorizationCode) throws AccessTokenRetrievalException {

		try {
			
			AuthorizationCodeFlow authFlow = getAuthFlow();

			TokenResponse tokenResponse = authFlow.newTokenRequest(authorizationCode).setScopes(Collections.singletonList("user:email")).setRequestInitializer(new HttpRequestInitializer(){
				
				public void initialize(HttpRequest request) throws IOException {
					request.getHeaders().setAccept("application/json");
				}
				
			}).execute();
			
			return tokenResponse.getAccessToken();
			
		}
		catch (IOException e) {
			throw new AccessTokenRetrievalException();
		}
	}

	protected UserData getUserData(String accessToken) throws UserNotFoundException, IOException, JSONException {
		
		UserData userData = null;
		
		userData = fetchGithubUserData(accessToken);

		if ((userData != null) && (userData.getId() != null)) {
			return userData;
		}
		else {
			throw new UserNotFoundException();
		}
	}

	private UserData fetchGithubUserData(String accessToken) throws IOException, JSONException {
		
		String url = "https://api.github.com/user?access_token=" + accessToken;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
			
	    JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonObject();
	   
	    String userId = jsonResponse.get("id").getAsString();
	    String fullName = jsonResponse.getAsJsonObject().get("name").getAsString();
	   
	    String firstName = "";
        StringBuilder lastNameBuilder = new StringBuilder();
        Scanner tokenizer = new Scanner(fullName);
        boolean first = true;
        while (tokenizer.hasNext()) {
            if (first) {
                firstName = tokenizer.next();
                first = false;
                continue;
            }
            lastNameBuilder.append(String.valueOf(tokenizer.next()) + " ");
        }
        tokenizer.close();
       
        String lastName = lastNameBuilder.toString().trim();
        if (Validator.isNull((String)lastName)) {
            lastName = "Github";
        }
	   
	    String email = jsonResponse.get("email").getAsString();
	    
	    UserData userData = new UserData(); 
	    userData.setId(userId);
	    userData.setEmail(email);
	    userData.setFirstName(firstName);
	    userData.setLastName(lastName);
		   
	    return userData;
	}

	protected User addOrUpdateUser(HttpSession session, long companyId, UserData userData) throws Exception {
		
		if (userData == null) {
			return null;
		}
		
		User user = null;

		String emailAddress = userData.getEmail();
		
		if ((user == null) && Validator.isNotNull(emailAddress)) {
			
			user = UserLocalServiceUtil.fetchUserByEmailAddress(companyId, emailAddress);

			if ((user != null) && (user.getStatus() != 6)) {
				
				session.setAttribute("GITHUB_USER_EMAIL_ADDRESS", emailAddress);
				
			}
		}


		if (user != null) {
			

			if (user.getStatus() == 6) {
				session.setAttribute("GITHUB_INCOMPLETE_USER_ID", userData.getId());
				
				user.setEmailAddress(userData.getEmail());
				user.setFirstName(userData.getFirstName());
				user.setLastName(userData.getLastName());

				return user;
			}
		
			user = UserService.updateUser(user, userData);
		}
		else {
			
			user = UserService.addUser(session, companyId, userData);
		}
		
		return user;
	}
	
	protected void redirectUpdateAccount(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		PortletURL portletURL = PortletURLFactoryUtil.create(request, PortletKeys.LOGIN, themeDisplay.getPlid(),PortletRequest.RENDER_PHASE);

		portletURL.setParameter("saveLastPath", Boolean.FALSE.toString());
		portletURL.setParameter("struts_action", "/login/update_account");

		PortletURL redirectURL = PortletURLFactoryUtil.create(request, PortletKeys.FAST_LOGIN, themeDisplay.getPlid(),PortletRequest.RENDER_PHASE);

		redirectURL.setParameter("struts_action", "/login/login_redirect");
		redirectURL.setParameter("emailAddress", user.getEmailAddress());
		redirectURL.setParameter("anonymousUser", Boolean.FALSE.toString());
		redirectURL.setPortletMode(PortletMode.VIEW);
		redirectURL.setWindowState(LiferayWindowState.POP_UP);

		portletURL.setParameter("redirect", redirectURL.toString());
		portletURL.setParameter("userId", String.valueOf(user.getUserId()));
		portletURL.setParameter("emailAddress", user.getEmailAddress());
		portletURL.setParameter("firstName", user.getFirstName());
		portletURL.setParameter("lastName", user.getLastName());
		portletURL.setPortletMode(PortletMode.VIEW);
		portletURL.setWindowState(LiferayWindowState.POP_UP);

		response.sendRedirect(portletURL.toString());
	}
}
