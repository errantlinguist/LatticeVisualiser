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
package com.github.errantlinguist.io;

import com.github.errantlinguist.parsers.ParseException;

/**
 * An {@link Exception} thrown if an error occurs during the parsing of a file.
 * 
 * @author Todd Shore
 * @version 2011-07-06
 * @since 2011-07-06
 * 
 */
public class FileParseException extends ParseException {

	/**
	 * The generated serial version UID.
	 */
	private static final long serialVersionUID = 5004694475472073126L;

	/**
	 * Appends a line number notice to the end of an error message.
	 * 
	 * @param message
	 *            The error message to append to.
	 * @param lineNumber
	 *            The line number the error occurred at.
	 * @return The given error message with a line number notice appended to the
	 *         end.
	 */
	private static String appendLineNumber(final String message,
			final int lineNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(message);
		builder.append(" (on line: ");
		builder.append(Integer.toString(lineNumber));
		builder.append(")");
		final String appendedMessage = builder.toString();
		return appendedMessage;
	}

	/**
	 * The line number the exception occurred at.
	 */
	private final int lineNumber;

	/**
	 * @param lineNumber
	 *            The line number the exception occurred at.
	 */
	protected FileParseException(final int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param lineNumber
	 *            The line number the exception occurred at.
	 */
	protected FileParseException(final String message, final int lineNumber) {
		super(appendLineNumber(message, lineNumber));
		this.lineNumber = lineNumber;
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
	 * @param lineNumber
	 *            The line number the exception occurred at.
	 */
	protected FileParseException(final String message, final Throwable cause,
			final int lineNumber) {
		super(appendLineNumber(message, lineNumber), cause);
		this.lineNumber = lineNumber;
	}

	/**
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method).&nbsp;(A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @param lineNumber
	 *            The line number the exception occurred at.
	 */
	protected FileParseException(final Throwable cause, final int lineNumber) {
		super(cause);
		this.lineNumber = lineNumber;
	}

	/**
	 * @return The line number the exception occurred at.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}
