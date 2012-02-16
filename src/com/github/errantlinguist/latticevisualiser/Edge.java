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

/**
 * A simple WFST edge representation for use in visualisation.
 * 
 * @author Todd Shore
 * @version 2011-07-25
 * @since 2011-07-05
 * 
 * 
 */
public class Edge implements Comparable<Edge> {

	/**
	 * The edge transition end state ID.
	 */
	private final Integer endStateID;

	/**
	 * The pre-cached hash code.
	 */
	private final int hashCode;

	/**
	 * The symbol output by the state transition (transduction).
	 */
	private final String outputSymbol;

	/**
	 * The edge transition start state ID.
	 */
	private final Integer startStateID;

	/**
	 * The edge transition weight.
	 */
	private final double weight;

	/**
	 * Constructs a new {@link Edge} object representing a transition from one
	 * {@link Integer} to another.
	 * 
	 * @param startStateID
	 *            The transition start {@link Integer} represented by the edge.
	 * @param endStateID
	 *            The transition start <code>NFSMState</code> represented by the
	 *            edge.
	 * 
	 * @param outputSymbol
	 *            The symbol output by the state transition (transduction).
	 * @param weight
	 *            The weight of the transition (in most cases, a negative log
	 *            probability).
	 */
	protected Edge(final Integer startStateID, final Integer endStateID,
			final String outputSymbol, final double weight) {

		this.startStateID = startStateID;
		this.endStateID = endStateID;
		
		this.outputSymbol = outputSymbol;
		this.weight = weight;

		hashCode = calculateHashCode();

	}

	/**
	 * 
	 * @return The hash code.
	 */
	private int calculateHashCode() {
		int result = 1;
		final int prime = 31;
		result = prime * result
				+ (endStateID == null ? 0 : endStateID.hashCode());
		result = prime * result
				+ (outputSymbol == null ? 0 : outputSymbol.hashCode());
		// long temp;
		// temp = Double.doubleToLongBits(weight);
		// result = HASH_PRIME * result + (int) (temp ^ temp >>> 32);
		result = prime * result
				+ (startStateID == null ? 0 : startStateID.hashCode());
		return result;
	}

	@Override
	public int compareTo(final Edge o) {
		return Double.compare(weight, o.weight);
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
		if (!(obj instanceof Edge)) {
			return false;
		}
		final Edge other = (Edge) obj;
		if (endStateID == null) {
			if (other.endStateID != null) {
				return false;
			}
		} else if (!endStateID.equals(other.endStateID)) {
			return false;
		}
		if (outputSymbol == null) {
			if (other.outputSymbol != null) {
				return false;
			}
		} else if (!outputSymbol.equals(other.outputSymbol)) {
			return false;
		}
		// if (Double.doubleToLongBits(weight) != Double
		// .doubleToLongBits(other.weight)) {
		// return false;
		// }
		if (startStateID == null) {
			if (other.startStateID != null) {
				return false;
			}
		} else if (!startStateID.equals(other.startStateID)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The edge transition end state ID.
	 */
	public Integer getEndStateID() {
		return endStateID;
	}

	/**
	 * @return The symbol output by the state transition (transduction).
	 */
	public String getOutputSymbol() {
		return outputSymbol;
	}

	/**
	 * @return The edge transition start state ID.
	 */
	public Integer getStartStateID() {
		return startStateID;
	}

	/**
	 * @return The edge transition weight.
	 */
	public double getWeight() {
		return weight;
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
		builder.append("[startStateID=");
		builder.append(startStateID);
		builder.append(", endStateID=");
		builder.append(endStateID);
		builder.append(", outputSymbol=");
		builder.append(outputSymbol);
		builder.append(", weight=");
		builder.append(weight);
		builder.append("]");
		return builder.toString();
	}

}