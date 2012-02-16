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

import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;

/**
 * An {@link Enum} of the different types of states, e.g.&nbsp;intermediate,
 * final states.
 * 
 * @author Todd Shore
 * @version 2011-11-13
 * @since 2011-11-13
 * 
 */
public enum StateType implements Settings {

	/**
	 * The final state in the lattice, i.e.&nbsp;that which outputs e.g.&nbsp;
	 * <code>"&lt;/s&gt;"<code>.
	 */
	FINAL,

	/**
	 * A (potential) goal state in a word label search, i.e.&nbsp;those which
	 * have edges outputting a word symbol.
	 */
	GOAL,

	/**
	 * The initial state in the lattice, i.e.&nbsp; that which outputs
	 * e.g.&nbsp;<code>"&lt;s&gt;"<code>.
	 */
	INITIAL,

	/**
	 * A (potential) intermediate state in a word label search, i.e.&nbsp;those
	 * which have edges outputting a non-word symbol.
	 */
	INTERMEDIATE;

	/**
	 * The {@link Collection} of symbols which represent non-word
	 * labels.&nbsp;NOTE: This cannot be <code>null</code> in order for most of
	 * the dynamic visualisation to work properly.
	 */
	private static Collection<String> nonwords = null;

	/**
	 * @return The {@link Collection} of symbols which represent non-word
	 *         labels.&nbsp;NOTE: This cannot be <code>null</code> in order for
	 *         most of the visualisation to work properly.
	 */
	public static Collection<String> getNonwords() {
		return nonwords;
	}

	/**
	 * Gets the corresponding {@link StateType} for a given state in a given
	 * {@link Graph}.
	 * 
	 * @param state
	 *            The ID of the state to get the corresponding {@code StateType}
	 *            of.
	 * @param graph
	 *            The {@link Graph} used for getting state information.
	 * @return The corresponding {@link StateType} for the given state in the
	 *         given {@code Graph}.
	 */
	public static StateType getStateType(final Integer state,
			final Graph<Integer, Edge> graph) {
		// Check if the state has no incoming edges; i.e. is a start state
		final Collection<Edge> inEdges = graph.getInEdges(state);
		if (inEdges == null || inEdges.isEmpty()) {
			return INITIAL;

		} else {
			// Check if the end goal label is the last edge
			boolean hasIncomingGoalLabel = false;

			for (final Edge inEdge : inEdges) {
				if (FINAL_LABEL.equals(inEdge.getOutputSymbol())) {
					hasIncomingGoalLabel = true;
					break;

				}

			}

			if (hasIncomingGoalLabel) {
				// System.out.println("final");
				return FINAL;

			}

		}

		if (nonwords != null) {
			// Check if the state has only non-word edges
			final Collection<Edge> outEdges = graph.getOutEdges(state);
			if (outEdges != null) {
				boolean hasWordLabels = false;
				for (final Edge edge : outEdges) {
					if (!nonwords.contains(edge.getOutputSymbol())) {
						hasWordLabels = true;
					}
				}

				if (hasWordLabels) {
					return GOAL;
				} else {
					return INTERMEDIATE;
				}

			} else {
				throw new RuntimeException(
						"State has neither incoming nor outgoing edges: "
								+ state.toString());
			}
		} else {
			return GOAL;
		}

	}

	/**
	 * @param nonwords
	 *            A {@link Collection} of symbols which represent non-word
	 *            labels.&nbsp;NOTE: This has to be set (to a non-
	 *            <code>null</code> value) in order for most of the
	 *            visualisation to work properly.
	 */
	public static void setNonwords(final Collection<String> nonwords) {
		StateType.nonwords = nonwords;
	}

}
