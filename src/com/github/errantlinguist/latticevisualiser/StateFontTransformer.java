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

import java.awt.Font;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import com.google.common.collect.ImmutableSet;

import edu.uci.ics.jung.graph.Graph;

/**
 * A {@link Transformer} for deriving the {@link Font} to use for drawing the
 * vertex from the characteristics of the state it represents.
 * 
 * @author Todd Shore
 * @version 2011-11-13
 * @since 2011-11-13
 * 
 */
public class StateFontTransformer implements Settings,
		Transformer<Integer, Font> {

	/**
	 * The {@link Graph} to use for getting the state information.
	 */
	private final Graph<Integer, Edge> graph;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The {@link ImmutableSet} of symbols which represent non-word labels.
	 */
	private final ImmutableSet<String> nonwords;

	/**
	 * 
	 * @param graph
	 *            The {@link Graph} to use for getting the state information.
	 * @param nonwords
	 *            An {@link ImmutableSet} of symbols which represent non-word
	 *            labels.
	 */
	public StateFontTransformer(final Graph<Integer, Edge> graph,
			final ImmutableSet<String> nonwords) {
		this.graph = graph;
		this.nonwords = nonwords;

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
		result = prime * result + (nonwords == null ? 0 : nonwords.hashCode());
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
		if (!(obj instanceof StateFontTransformer)) {
			return false;
		}
		final StateFontTransformer other = (StateFontTransformer) obj;
		if (graph == null) {
			if (other.graph != null) {
				return false;
			}
		} else if (!graph.equals(other.graph)) {
			return false;
		}
		if (nonwords == null) {
			if (other.nonwords != null) {
				return false;
			}
		} else if (!nonwords.equals(other.nonwords)) {
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

	/**
	 * @return The {@link ImmutableSet} of symbols which represent non-word
	 *         labels.
	 */
	public ImmutableSet<String> getNonwords() {
		return nonwords;
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
	public Font transform(final Integer state) {
		// Check if the state has only non-word edges
		boolean hasWordLabels = false;
		final Collection<Edge> edges = graph.getOutEdges(state);
		for (final Edge edge : edges) {
			if (nonwords != null && !nonwords.contains(edge.getOutputSymbol())) {
				hasWordLabels = true;
			}
		}

		if (hasWordLabels) {
			return GOAL_VERTEX_FONT;
		} else {
			return INTERMEDIATE_VERTEX_FONT;
		}
	}

}
