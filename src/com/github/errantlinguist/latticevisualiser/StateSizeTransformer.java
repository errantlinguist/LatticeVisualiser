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

import edu.uci.ics.jung.graph.Graph;

/**
 * A {@link Transformer} for deriving the size of a vertex drawn to represent a
 * lattice state.
 * 
 * @author Todd Shore
 * @version 2011-11-13
 * @since 2011-11-13
 * 
 */
public class StateSizeTransformer implements Transformer<Integer, Integer> {

	/**
	 * The {@link Graph} to use for getting the state information.
	 */
	private final Graph<Integer, Edge> graph;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The minimum size of a state/vertex, to avoid them being too small to see
	 * on-screen.
	 */
	private final int minSize;

	/**
	 * The state/vertex size multiplier to use to scale their size based on the
	 * number of incoming/outgoing edges they have.
	 */
	private final double stateSizeMultiplier;

	/**
	 * 
	 * @param graph
	 *            The {@link Graph} to use for getting the state information.
	 * @param stateSizeMultiplier
	 *            The state/vertex size multiplier to use to scale their size
	 *            based on the number of incoming/outgoing edges they have.
	 * @param minSize
	 *            The minimum size of a state/vertex, to avoid them being too
	 *            small to see on-screen.
	 */
	public StateSizeTransformer(final Graph<Integer, Edge> graph,
			final double stateSizeMultiplier, final int minSize) {
		this.graph = graph;
		this.stateSizeMultiplier = stateSizeMultiplier;
		this.minSize = minSize;

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
		result = prime * result + minSize;
		long temp;
		temp = Double.doubleToLongBits(stateSizeMultiplier);
		result = prime * result + (int) (temp ^ temp >>> 32);
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
		if (!(obj instanceof StateSizeTransformer)) {
			return false;
		}
		final StateSizeTransformer other = (StateSizeTransformer) obj;
		if (graph == null) {
			if (other.graph != null) {
				return false;
			}
		} else if (!graph.equals(other.graph)) {
			return false;
		}
		if (minSize != other.minSize) {
			return false;
		}
		if (Double.doubleToLongBits(stateSizeMultiplier) != Double
				.doubleToLongBits(other.stateSizeMultiplier)) {
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
		builder.append("[minSize=");
		builder.append(minSize);
		builder.append(", stateSizeMultiplier=");
		builder.append(stateSizeMultiplier);
		builder.append(", graph=");
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
	public Integer transform(final Integer state) {
		// final int incomingEdgeCount = graph.getInEdges(state).size();
		final int outgoingEdgeCount = graph.getOutEdges(state).size();
		final int totalEdgeCount = /* incomingEdgeCount + */outgoingEdgeCount + 1;
		final double size = totalEdgeCount * stateSizeMultiplier;
		// final double weight = Math.log(totalEdgeCount) * WEIGHT_FACTOR;
		return (int) Math.max(Math.round(size), minSize);
		// return (int) Math.round(size);
	}

}
