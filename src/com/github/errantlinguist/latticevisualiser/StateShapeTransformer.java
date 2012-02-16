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

import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

/**
 * A {@link Transformer} for deriving the {@link Shape} of a vertex drawn to
 * represent a lattice state.
 * 
 * @author Todd Shore
 * @version 2011-11-13
 * @since 2011-11-13
 * 
 */
public class StateShapeTransformer implements Settings,
		Transformer<Integer, Shape> {

	/**
	 * The {@link Graph} to use for getting the state information.
	 */
	private final Graph<Integer, Edge> graph;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The {@link VertexShapeFactory} used for creating {@link Shape} objects
	 * which are used for drawing states (i.e.&nbsp;vertices).
	 */
	private final VertexShapeFactory<Integer> shapeFactory;

	/**
	 * 
	 * @param graph
	 *            The {@link Graph} to use for getting the state information.
	 * @param sizeTransformer
	 *            The {@link Transformer} for deriving the size of a vertex
	 *            drawn to represent a lattice state.
	 */
	public StateShapeTransformer(final Graph<Integer, Edge> graph,
			final StateSizeTransformer sizeTransformer) {
		this.graph = graph;
		shapeFactory = new VertexShapeFactory<Integer>(sizeTransformer,
				STATE_ASPECT_TRANSFORMER);

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
		// result = prime * result
		// + (shapeFactory == null ? 0 : shapeFactory.hashCode());
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
		if (!(obj instanceof StateShapeTransformer)) {
			return false;
		}
		final StateShapeTransformer other = (StateShapeTransformer) obj;
		if (graph == null) {
			if (other.graph != null) {
				return false;
			}
		} else if (!graph.equals(other.graph)) {
			return false;
		}
		// if (shapeFactory == null) {
		// if (other.shapeFactory != null) {
		// return false;
		// }
		// } else if (!shapeFactory.equals(other.shapeFactory)) {
		// return false;
		// }
		return true;
	}

	/**
	 * @return The {@link Graph} to use for getting the state information.
	 */
	public Graph<Integer, Edge> getGraph() {
		return graph;
	}

	/**
	 * @return The {@link VertexShapeFactory} used for creating {@link Shape}
	 *         objects which are used for drawing states (i.e.&nbsp;vertices).
	 */
	public VertexShapeFactory<Integer> getShapeFactory() {
		return shapeFactory;
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
	public Shape transform(final Integer state) {

		final StateType stateType = StateType.getStateType(state, graph);
		if (StateType.INTERMEDIATE.equals(stateType)) {
			return shapeFactory.getRegularPolygon(state, POLYGON_SIDE_COUNT);
		} else if (StateType.INITIAL.equals(stateType)) {
			return shapeFactory.getRectangle(state);
		} else if (StateType.FINAL.equals(stateType)) {
			return shapeFactory.getRegularStar(state, STAR_POINT_COUNT);
		}

		return shapeFactory.getEllipse(state);
	}

}
