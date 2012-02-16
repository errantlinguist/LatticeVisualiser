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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.errantlinguist.parsers.ParseException;

/**
 * A {@link FileParser} implementation which parses using regular expressions.
 * 
 * @author Todd Shore
 * @version 2011-08-13
 * @since 2011-08-13
 * 
 * @param <L>
 *            The object type representing the line data.
 * @param <F>
 *            The object type representing all of the file data.
 * 
 */
public abstract class RegexFileParser<L, F> extends FileParser<L, F> {

	/**
	 * A {@link Pattern} matching an empty line.
	 */
	private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("\\s*");

	/**
	 * The line currently being parsed.
	 */
	protected String currentLine;

	/**
	 * The parsed value corresponding to {@link #currentLine}.
	 */
	protected L currentValue;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The string last matched, corresponding to {@link #lastMatcher}.
	 */
	private String lastMatched;

	/**
	 * The last {@link Matcher} object used to parse a line, corresponding to
	 * {@link #lastMatched}.
	 */
	protected Matcher lastMatcher;

	/**
	 * The {@link Pattern} to be used for parsing each line.
	 */
	private final Pattern linePattern;

	/**
	 * 
	 * @param linePattern
	 *            The {@link Pattern} to be used for parsing each line.
	 */
	public RegexFileParser(final Pattern linePattern) {
		this.linePattern = linePattern;

		this.hashCode = calculateHashCode();
	}

	/**
	 * 
	 * @return The hash code.
	 */
	private int calculateHashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ (linePattern == null ? 0 : linePattern.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RegexFileParser)) {
			return false;
		}
		final RegexFileParser<?, ?> other = (RegexFileParser<?, ?>) obj;
		if (linePattern == null) {
			if (other.linePattern != null) {
				return false;
			}
		} else if (!linePattern.equals(other.linePattern)) {
			return false;
		}
		return true;
	}

	/**
	 * A function used to handle a regular expression match.
	 * 
	 * @throws ParseException
	 *             If there was an error while handling a regular expression
	 *             parsing match.
	 */
	protected abstract void handleMatch() throws ParseException;

	/**
	 * A function used to handle a regular expression mismatch.
	 * 
	 * @throws ParseException
	 *             If there was an error while handling a regular expression
	 *             parsing match.
	 */
	protected void handleMismatch() throws ParseException {
		if (!EMPTY_LINE_PATTERN.matcher(currentLine).matches()) {
			final FileParseException newException = createFileParseException("File format error: "
					+ currentLine);
			throw newException;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public final boolean isValid(final String line) {
		if (!line.equals(lastMatched)) {
			match(line);
		}

		return lastMatcher.matches();
	}

	/**
	 * Matches a line against {@link #linePattern}.
	 * 
	 * @param line
	 *            The line to match.
	 */
	private final void match(final String line) {
		lastMatcher = linePattern.matcher(line);
		lastMatched = line;

	}

	@Override
	public L parse(final String line) throws ParseException {
		currentLine = line;

		if (isValid(line)) {

			handleMatch();

		} else {
			handleMismatch();
		}

		return currentValue;

	}

	@Override
	public void reset() {
		resetParse();
		lastMatched = null;
		lastMatcher = null;
		currentLine = null;
		currentValue = null;

	}

	/**
	 * An abstract method for specifying the specific actions to be taken to
	 * reset the {@link RegexFileParser} when calling {@link #reset()}.
	 */
	protected abstract void resetParse();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		final String className = this.getClass().getSimpleName();
		builder.append(className);
		builder.append("[linePattern=");
		builder.append(linePattern);
		builder.append("]");
		return builder.toString();
	}

}