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

package com.miltos.exceptions;


public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 2L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String msg) {
		super(msg);
	}
	
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public UserNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}