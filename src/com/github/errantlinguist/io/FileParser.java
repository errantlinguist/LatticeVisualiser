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

import com.github.errantlinguist.parsers.Parser;

/**
 * A {@link Parser} which parses a single line in a file of a given type.
 * 
 * @author Todd Shore
 * @version 2011-07-06
 * @since 2011-07-06
 * 
 * @param <L>
 *            The object type representing the line data.
 * @param <F>
 *            The object type representing all of the file data.
 */
public abstract class FileParser<L, F> extends Parser<String, L, F> {

	// /**
	// * Returns the standard string representation in the style of
	// * {@link Object#toString()}.
	// *
	// * @param obj
	// * The {@link Object} to get the string representation of.
	// * @return The string representation in the style of
	// * {@link Object#toString()}.
	// */
	// private static final <T> String getObjectString(final T obj) {
	// final StringBuilder builder = new StringBuilder();
	//
	// return getObjectString(obj, builder);
	// }

	/**
	 * A constant value used for estimating the length of the string
	 * representation of the object returned by {@link #toString()}.
	 */
	protected static final int ESTIMATED_STRING_LENGTH = 64;

	/**
	 * Returns the standard string representation in the style of
	 * {@link Object#toString()}.
	 * 
	 * @param obj
	 *            The {@link Object} to get the string representation of.
	 * @param estimatedLength
	 *            A value used for estimating the length of the string
	 *            representation of the object returned by {@link #toString()}.
	 * @return The string representation in the style of
	 *         {@link Object#toString()}.
	 */
	private static final <T> String getObjectString(final T obj,
			final int estimatedLength) {
		final StringBuilder builder = new StringBuilder(estimatedLength);
		return getObjectString(obj, builder);
	}

	/**
	 * Returns the standard string representation in the style of
	 * {@link Object#toString()}.
	 * 
	 * @param obj
	 *            The {@link Object} to get the string representation of.
	 * @param builder
	 *            The {@link StringBuilder} used for creating the
	 *            representation.
	 * @return The string representation in the style of
	 *         {@link Object#toString()}.
	 */
	private static final <T> String getObjectString(final T obj,
			final StringBuilder builder) {
		final String className = obj.getClass().getName();
		builder.append(className);
		builder.append('@');
		final String hashHexString = Integer.toHexString(obj.hashCode());
		builder.append(hashHexString);

		return builder.toString();
	}

	private FileLineReader<L, F> reader;

	/**
	 * 
	 * @return A new {@link FileLineReader} using the {@link FileParser}.
	 */
	public FileLineReader<L, F> createFileReader() {
		return new FileLineReader<L, F>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getObjectString(this, ESTIMATED_STRING_LENGTH);
	}

	/**
	 * Makes a new {@link FileParseException} with the current line number, if
	 * any.
	 * 
	 * @return A new <code>FileParseException</code> object.
	 */
	protected final FileParseException createFileParseException() {

		final FileParseException newException;
		if (reader != null) {
			final int currentLineNumber = reader.getLineNumber();
			newException = new FileParseException(currentLineNumber);
		} else {
			newException = new FileParseException(-1);
		}

		return newException;
	}

	/**
	 * Makes a new {@link FileParseException} with the current line number, if
	 * any.
	 * 
	 * @param cause
	 *            The cause.
	 * @return A new <code>FileParseException</code> object.
	 */
	protected final FileParseException createFileParseException(
			final Exception cause) {

		final FileParseException newException;
		if (reader != null) {
			final int currentLineNumber = reader.getLineNumber();
			newException = new FileParseException(cause, currentLineNumber);
		} else {
			newException = new FileParseException(cause, -1);
		}

		return newException;
	}

	/**
	 * Makes a new {@link FileParseException} with the current line number, if
	 * any.
	 * 
	 * @param message
	 *            The message.
	 * @return A new <code>FileParseException</code> object.
	 */
	protected final FileParseException createFileParseException(
			final String message) {

		final FileParseException newException;
		if (reader != null) {
			final int currentLineNumber = reader.getLineNumber();
			newException = new FileParseException(message, currentLineNumber);
		} else {
			newException = new FileParseException(message, -1);
		}

		return newException;
	}

	/**
	 * Makes a new {@link FileParseException} with the current line number, if
	 * any.
	 * 
	 * @param message
	 *            The message.
	 * @param cause
	 *            The cause.
	 * @return A new <code>FileParseException</code> object.
	 */
	protected final FileParseException createFileParseException(
			final String message, final Exception cause) {

		final FileParseException newException;
		if (reader != null) {
			final int currentLineNumber = reader.getLineNumber();
			newException = new FileParseException(message, cause,
					currentLineNumber);
		} else {
			newException = new FileParseException(message, cause, -1);
		}

		return newException;
	}

	/**
	 * 
	 * @param reader
	 *            the reader
	 */
	protected void setReader(final FileLineReader<L, F> reader) {
		this.reader = reader;
	}

}
