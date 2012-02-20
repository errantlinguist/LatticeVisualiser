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

import java.util.HashMap;
import java.util.Map;

import com.github.errantlinguist.parsers.ParseException;
import com.google.common.base.Function;

/**
 * A {@link FileParser} implementation which parses a file comprising two
 * columns:&nbsp;A column of keys mapping to a second column of values.
 * 
 * @author Todd Shore
 * @version 2011-08-13
 * @since 2011-08-13
 * 
 * @param <O>
 *            The type an input string key maps to.
 * 
 */
public class MapFileParser<O> extends FileParser<O, HashMap<String, O>> {

	/**
	 * A constant value used for estimating the length of the string
	 * representation of the object returned by {@link #toString()}.
	 */
	private static final int ESTIMATED_STRING_LENGTH = 64;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The {@link HashMap} to be filled by the file contents.
	 */
	private HashMap<String, O> parse;

	/**
	 * The {@link Function} for converting the values listed in the file into
	 * the desired {@link Map} values.
	 */
	private final Function<String, O> transformer;

	public MapFileParser(final Function<String, O> transformer) {
		this.transformer = transformer;

		parse = new HashMap<String, O>();

		this.hashCode = calculateHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#completeParse()
	 */
	@Override
	public HashMap<String, O> completeParse() throws ParseException {
		final HashMap<String, O> completedParse = parse;
		return completedParse;
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
		if (!(obj instanceof MapFileParser)) {
			return false;
		}
		final MapFileParser<?> other = (MapFileParser<?>) obj;
		if (transformer == null) {
			if (other.transformer != null) {
				return false;
			}
		} else if (!transformer.equals(other.transformer)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The {@link Function} for converting the values listed in the file
	 *         into the desired {@link Map} values.
	 */
	public Function<String, O> getTransformer() {
		return transformer;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(final String input) throws ParseException {
		try {
			transformer.apply(input);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#parse(java.lang.Object)
	 */
	@Override
	public O parse(final String input) throws ParseException {
		return transformer.apply(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#reset()
	 */
	@Override
	public void reset() {
		parse = new HashMap<String, O>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(ESTIMATED_STRING_LENGTH);
		builder.append(this.getClass().getSimpleName());
		builder.append("[transformer=");
		builder.append(transformer);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 
	 * @return The hash code.
	 */
	private int calculateHashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ (transformer == null ? 0 : transformer.hashCode());
		return result;
	}

}
