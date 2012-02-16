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

package com.github.errantlinguist.latticevisualiser;

import org.apache.commons.collections15.Transformer;

/**
 * Converts a given string to lowercase.
 * 
 * @author Todd Shore
 * @version 2011-11-13
 * @since 2011-11-13
 * 
 */
public class StringLowerCaseTransformer implements Transformer<String, String> {

	/**
	 * {@link SingletonHolder} is loaded on the first execution of
	 * {@link StringLowerCaseTransformer#getInstance()} or the first access to
	 * {@link #INSTANCE}, not before.
	 * 
	 * @author Todd Shore
	 * @version 2011-07-05
	 * @since 2011-07-05
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
	 */
	private static final class SingletonHolder {
		private static final StringLowerCaseTransformer INSTANCE = new StringLowerCaseTransformer();
	}

	/**
	 * 
	 * @return A single static {@link StringLowerCaseTransformer} instance.
	 */
	public static StringLowerCaseTransformer getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private StringLowerCaseTransformer() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public String transform(final String arg0) {
		return arg0.toLowerCase();
	}

}
