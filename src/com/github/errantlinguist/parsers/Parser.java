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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class for objects which takes an object as input and outputs
 * another object as a simple parse, and also can create a complex, "complete"
 * parse of multiple such objects.&nbsp;For example, given that a simple parse
 * is a node in a tree, the complex parse is the complete tree structure.
 * 
 * @author Todd Shore
 * @version 2011-08-06
 * @since 2011-08-06
 * 
 * @param <I>
 *            The input object type.
 * @param <OS>
 *            The simple parse output type.
 * @param <OC>
 *            The complex parse output type.
 * 
 */
public abstract class Parser<I, OS, OC> {

	/**
	 * Get the underlying class for a type, or null if the type is a variable
	 * type.
	 * 
	 * @since 2003-06-27
	 * @see <a
	 *      href="http://www.artima.com/weblogs/viewpost.jsp?thread=208860">http://www.artima.com/weblogs/viewpost.jsp?thread=208860</a>
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	public static final Class<?> getClass(final Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			final Type componentType = ((GenericArrayType) type)
					.getGenericComponentType();
			final Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Get the actual type arguments a child class has used to extend a generic
	 * base class.
	 * 
	 * @since 2003-06-27
	 * @see <a
	 *      href="http://www.artima.com/weblogs/viewpost.jsp?thread=208860">http://www.artima.com/weblogs/viewpost.jsp?thread=208860</a>
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	public static final <T> List<Class<?>> getTypeArguments(
			final Class<T> baseClass, final Class<? extends T> childClass) {
		final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class<?>) {
				// there is no useful information for us in raw types, so just
				// keep going.
				type = ((Class<?>) type).getGenericSuperclass();
			} else {
				final ParameterizedType parameterizedType = (ParameterizedType) type;
				final Class<?> rawType = (Class<?>) parameterizedType
						.getRawType();

				final Type[] actualTypeArguments = parameterizedType
						.getActualTypeArguments();
				final TypeVariable<?>[] typeParameters = rawType
						.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes
							.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass,
		// determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class<?>) {
			actualTypeArguments = ((Class<?>) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type)
					.getActualTypeArguments();
		}
		final List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		// resolve types by chasing down type variables.
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * The class of objects representing complex parses, for use in e.g.&nbsp;
	 * casting.
	 */
	private final Class<?> complexParseClass;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The class of objects representing simple parses, for use in e.g.&nbsp;
	 * casting.
	 */
	private final Class<?> simpleParseClass;

	protected Parser() {
		final List<Class<?>> typeArgs = getTypeArguments(Parser.class,
				getClass());
		this.simpleParseClass = typeArgs.get(1);
		this.complexParseClass = typeArgs.get(2);

		this.hashCode = calculateHashCode();
	}

	/**
	 * 
	 * @return The hash code.
	 */
	private int calculateHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (complexParseClass == null ? 0 : complexParseClass.hashCode());
		result = prime * result
				+ (simpleParseClass == null ? 0 : simpleParseClass.hashCode());
		return result;
	}

	/**
	 * Completes a complex parse and returns it.
	 * 
	 * @return The complex parse.
	 * @throws ParseException
	 *             If the parse cannot be completed due to ill-formed input.
	 * @throws Exception
	 *             If there is an otherwise unhandled error.
	 */
	public abstract OC completeParse() throws ParseException;

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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Parser)) {
			return false;
		}
		final Parser<?, ?, ?> other = (Parser<?, ?, ?>) obj;
		if (complexParseClass == null) {
			if (other.complexParseClass != null) {
				return false;
			}
		} else if (!complexParseClass.equals(other.complexParseClass)) {
			return false;
		}
		if (simpleParseClass == null) {
			if (other.simpleParseClass != null) {
				return false;
			}
		} else if (!simpleParseClass.equals(other.simpleParseClass)) {
			return false;
		}
		return true;
	}

	/**
	 * Completes a complex parse and returns it, returning the {@link Parser} to
	 * its initial state afterwards.
	 * 
	 * @return The complex parse.
	 * @throws Exception
	 *             If there is an otherwise unhandled error.
	 * @throws ParseException
	 *             If the parse is incomplete due to ill-formed input.
	 */
	public OC getCompletedParse() throws ParseException {
		final OC parse = completeParse();
		reset();
		return parse;
	}

	/**
	 * @return the complexParseClass
	 */
	public final Class<?> getComplexParseClass() {
		return complexParseClass;
	}

	/**
	 * @return the simpleParseClass
	 */
	public final Class<?> getSimpleParseClass() {
		return simpleParseClass;
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

	/**
	 * 
	 * @param input
	 *            The object to check.
	 * @return <code>true</code> iff the object is accepted by the parser.
	 * @throws Exception
	 */
	public abstract boolean isValid(I input) throws ParseException;

	/**
	 * 
	 * @param inputSequence
	 *            The array of objects to check.
	 * @return <code>true</code> iff all objects in the array are accepted by
	 *         the parser.
	 * @throws Exception
	 */
	public final boolean isValid(final I[] inputSequence) throws ParseException {
		final List<I> sequenceList = Arrays.asList(inputSequence);
		return isValid(sequenceList);
	}

	/**
	 * 
	 * @param inputSequence
	 *            The {@link Collection} of objects to check.
	 * @return <code>true</code> iff all objects in the <code>List</code> are
	 *         accepted by the parser.
	 * @throws Exception
	 */
	public boolean isValid(final List<I> inputSequence) throws ParseException {
		boolean isValid = true;
		for (final I input : inputSequence) {
			if (!isValid(input)) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

	/**
	 * Parses a single input symbol.
	 * 
	 * @param input
	 *            The input symbol to parse.
	 * @return A simple output object representing the input symbol, e.g
	 *         <code>"twenty"</code>&rarr;<code>20</code>.
	 * @throws Exception
	 *             If there is an otherwise unhandled error.
	 * @throws ParseException
	 *             If the input cannot be parsed.
	 */
	public abstract OS parse(I input) throws ParseException;

	/**
	 * Parses a sequence of input symbols.
	 * 
	 * @param inputSequence
	 *            The sequence of input symbols to parse.
	 * @return An object representing the parse of the entire input symbol
	 *         sequence, e.g <code>"alpha twenty two"</code>&rarr;
	 *         <code>A22</code>.
	 * @throws Exception
	 *             If there is an otherwise unhandled error.
	 * @throws ParseException
	 *             If the input sequence cannot be parsed.
	 */
	public final OC parse(final I[] inputSequence) throws ParseException {
		final List<I> sequenceList = Arrays.asList(inputSequence);
		return parse(sequenceList);
	}

	/**
	 * Parses a sequence of input symbols.
	 * 
	 * @param inputSequence
	 *            The sequence of input symbols to parse.
	 * @return A complex parse representing the input symbol sequence, e.g
	 *         <code>"alpha twenty two"</code>&rarr;<code>A22</code>.
	 * @throws Exception
	 *             If there is an otherwise unhandled error.
	 * @throws ParseException
	 *             If the input sequence cannot be parsed.
	 */
	public final OC parse(final List<I> inputSequence) throws ParseException {

		// System.out.println("Parsing input sequence: " +
		// inputSequence.toString());

		if (inputSequence.size() == 0) {
			throw new IllegalArgumentException(
					"Parser input sequence is empty.");
		}

		for (final I input : inputSequence) {
			// System.out.println("Parsing: " + inputSequence);
			parse(input);
		}

		final OC parse = getCompletedParse();
		return parse;
	}

	/**
	 * Resets the parser to its initial state.
	 */
	public abstract void reset();

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
		builder.append("[complexParseClass=");
		builder.append(complexParseClass);
		builder.append(", simpleParseClass=");
		builder.append(simpleParseClass);
		builder.append("]");
		return builder.toString();
	}

}
