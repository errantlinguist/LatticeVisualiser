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
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * A {@link FileParser} which creates an {@link ImmutableSet} from a file, with
 * each item in the set corresponding to a single line in the file.
 * 
 * @author Todd Shore
 * @version 2011-11-11
 * @since 2011-11-11
 * 
 * @param <O>
 *            The output object type.
 * 
 */
public class ImmutableSetFileParser<O> extends FileParser<O, ImmutableSet<O>> {

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The {@link ImmutableSet.Builder} used for creating the
	 * {@link ImmutableSet} representing the file.
	 */
	private ImmutableSet.Builder<O> parseBuilder;

	/**
	 * The {@link Function} used for transforming each file line into the
	 * desired output type.
	 */
	private final Function<String, O> transformer;

	/**
	 * 
	 * @param transformer
	 *            The {@link Function} used for transforming each file line into
	 *            the desired output type.
	 */
	public ImmutableSetFileParser(final Function<String, O> transformer) {
		parseBuilder = ImmutableSet.builder();

		this.transformer = transformer;

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
				+ (transformer == null ? 0 : transformer.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#completeParse()
	 */
	@Override
	public ImmutableSet<O> completeParse() throws ParseException {
		final ImmutableSet<O> completedParse = parseBuilder.build();
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
		if (!(obj instanceof ImmutableSetFileParser)) {
			return false;
		}
		final ImmutableSetFileParser<?> other = (ImmutableSetFileParser<?>) obj;
		if (transformer == null) {
			if (other.transformer != null) {
				return false;
			}
		} else if (!transformer.equals(other.transformer)) {
			return false;
		}
		return true;
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
		final O output = transformer.apply(input);
		parseBuilder.add(output);

		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.errantlinguist.parsers.Parser#reset()
	 */
	@Override
	public void reset() {
		parseBuilder = ImmutableSet.builder();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(ESTIMATED_STRING_LENGTH);
		final String className = this.getClass().getSimpleName();
		builder.append(className);
		builder.append("[transformer=");
		builder.append(transformer);
		builder.append("]");
		return builder.toString();
	}

}
