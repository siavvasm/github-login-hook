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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

//import com.liferay.portal.model.User;
//import com.liferay.portal.security.auth.BaseAutoLogin;
//import com.liferay.portal.service.UserLocalServiceUtil;
//import com.liferay.portal.util.PortalUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class GithubAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		long companyId = PortalUtil.getCompanyId(request);
	
		User user = getUser(request, companyId);
	
		if (user == null) {
			return null;
		}
	
		String[] credentials = new String[]{String.valueOf(user.getUserId()), user.getPassword(), Boolean.TRUE.toString()};
	
		return credentials;
	}
	
	protected User getUser(HttpServletRequest request, long companyId) throws PortalException, SystemException {
	
		HttpSession session = request.getSession();
	
		String emailAddress = GetterUtil.getString(session.getAttribute("GITHUB_USER_EMAIL_ADDRESS"));
	
		if (Validator.isNull(emailAddress)) {
			return null;
		}
	
		session.removeAttribute("GITHUB_USER_EMAIL_ADDRESS");
	
		User user = UserLocalServiceUtil.getUserByEmailAddress(companyId, emailAddress);
	
		return user;
	}
}