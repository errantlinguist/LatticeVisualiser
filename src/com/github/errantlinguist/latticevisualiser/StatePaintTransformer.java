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

import java.awt.Color;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;

import com.google.common.collect.ImmutableMap;

import edu.uci.ics.jung.graph.Graph;

/**
 * A {@link Transformer} for deriving the {@link Paint} to use for drawing the
 * vertex from the characteristics of the state it represents.
 * 
 * @author Todd Shore
 * @version 2011-11-12
 * @since 2011-11-12
 * 
 */
public class StatePaintTransformer implements Settings,
		Transformer<Integer, Paint> {

	/**
	 * 
	 * @return A map of {@link StateType StateTypes} to {@link Color Colors} for
	 *         deriving the colour used to draw the vertex representing a state.
	 */
	static final ImmutableMap<StateType, Color> makeColorMap() {
		final ImmutableMap.Builder<StateType, Color> builder = ImmutableMap
				.builder();

		builder.put(StateType.INITIAL, Color.CYAN);
		builder.put(StateType.FINAL, Color.MAGENTA);
		builder.put(StateType.GOAL, Color.RED);
		builder.put(StateType.INTERMEDIATE, Color.GREEN);

		return builder.build();

	}

	/**
	 * The {@link Graph} to use for getting the state information.
	 */
	private final Graph<Integer, Edge> graph;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * 
	 * @param graph
	 *            The {@link Graph} to use for getting the state information.
	 */
	public StatePaintTransformer(final Graph<Integer, Edge> graph) {
		this.graph = graph;

		hashCode = calculateHashCode();
	}

	/**
	 * 
	 * @return The hash code.
	 */
	private int calculateHashCode() {
		final int prime = 31;
		int result = 1;
		// Get the identity hash code of the graph because it may change during
		// runtime (it is mutable)
		result = prime * result
				+ (graph == null ? 0 : System.identityHashCode(graph));
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StatePaintTransformer)) {
			return false;
		}
		final StatePaintTransformer other = (StatePaintTransformer) obj;
		if (graph == null) {
			if (other.graph != null) {
				return false;
			}
		} else if (!graph.equals(other.graph)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The {@link Graph} to use for getting the state information.
	 */
	public Graph<Integer, Edge> getGraph() {
		return graph;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		final String className = this.getClass().getSimpleName();
		builder.append(className);
		builder.append("[graph=");
		builder.append(graph);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Paint transform(final Integer state) {

		final StateType stateType = StateType.getStateType(state, graph);

		return COLOURS.get(stateType);

	}

}
