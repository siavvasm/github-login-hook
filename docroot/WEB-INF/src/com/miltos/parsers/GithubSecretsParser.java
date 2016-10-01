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

package com.miltos.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class GithubSecretsParser {
	
	private static String CURRENT_DIR = new File(System.getProperty("user.dir")).getAbsolutePath().replace("bin", "");
	private static String GITHUB_SECRETS = new File(CURRENT_DIR + "/webapps/github-login-hook/github_secrets.json").getAbsolutePath();
	
	public static String getGithubCilentId() {
		
		String clientId = "";
		
		try{
				
		//Read the github_secrets.json file
		BufferedReader br = new BufferedReader(new FileReader(GITHUB_SECRETS));
		
		//Retrieve its content
		StringBuffer content = new StringBuffer();
		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			content.append(inputLine);
		}
		br.close();
		
		String secretsString = content.toString();
		
		//Parse the client_id attribute
		JSONObject obj = new JSONObject(secretsString);
		clientId = obj.getString("client_id");
		
		}catch(IOException e){
			System.out.println(e.getMessage());
		}catch(JSONException je){
			System.out.println(je.getMessage());
		}
		
		return clientId;	
	}
	
	public static String getGithubCilentSecret() {
		
		String clientSecret = "";
		
		try{
			
		BufferedReader br = new BufferedReader(new FileReader(GITHUB_SECRETS));
		
		StringBuffer content = new StringBuffer();
		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			content.append(inputLine);
		}
		br.close();
		
		String secretsString = content.toString();

		JSONObject obj = new JSONObject(secretsString);
		clientSecret = obj.getString("client_secret");
		
		}catch(IOException e){
			System.out.println(e.getMessage());
		}catch(JSONException je){
			System.out.println(je.getMessage());
		}
		
		return clientSecret;
	}

}
