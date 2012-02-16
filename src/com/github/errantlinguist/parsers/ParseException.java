/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */
package com.github.errantlinguist.parsers;

/**
 * An {@link Exception} thrown by a {@link Parser} instance.
 * 
 * @author Todd Shore
 * @version 2011-08-09
 * @since 2011-08-09
 * 
 */
public class ParseException extends Exception {

	/**
	 * The generated serial version UID.
	 */
	private static final long serialVersionUID = -8010058817004395715L;

	public ParseException() {
	}

	/**
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 */
	public ParseException(final String message) {
		super(message);
	}

	/**
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method).&nbsp;(A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public ParseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method).&nbsp;(A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public ParseException(final Throwable cause) {
		super(cause);
	}

}
