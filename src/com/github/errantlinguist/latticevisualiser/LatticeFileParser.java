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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections15.Transformer;

import com.github.errantlinguist.io.FileParseException;
import com.github.errantlinguist.io.RegexFileParser;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * A {@link RegexFileParser} which creates a {@link DirectedSparseGraph}
 * representing a word/phone lattice FST file from the <a
 * href="http://www.distant-speech-recognition.org/">Millennium</a> ASR system.
 * 
 * @author Todd Shore
 * @version 2011-11-12
 * @since 2011-11-12
 * 
 */
public class LatticeFileParser extends
		RegexFileParser<Edge, DirectedSparseGraph<Integer, Edge>> {

	/**
	 * A {@link Pattern} matching the end line in an FST file.
	 */
	private static final Pattern FINAL_LINE_PATTERN = Pattern
			.compile("^\\s+([0-9]+)\\s*$");

	/**
	 * A {@link Pattern} matching a single line in an FST file, representing a
	 * single edge.
	 */
	private static final Pattern LINE_PATTERN = Pattern
			.compile("^\\s+([0-9]+)\\s+([0-9]+)\\s+(\\S+)\\s+(\\S+)(?:\\s+(-?[0-9]*\\.?[0-9]+))?\\s*$");

	/**
	 * The {@link DirectedSparseGraph} to be returned.
	 */
	private DirectedSparseGraph<Integer, Edge> graph;

	/**
	 * The {@link Transformer} used for converting edge output symbols into
	 * user-friendly edge output labels.
	 */
	private Transformer<String, String> labelTransformer;

	public LatticeFileParser() {
		this(null);
	}

	/**
	 * 
	 * @param labelTransformer
	 *            The {@link Transformer} used for converting edge output
	 *            symbols into user-friendly edge output labels.
	 */
	public LatticeFileParser(final Transformer<String, String> labelTransformer) {
		super(LINE_PATTERN);
		this.labelTransformer = labelTransformer;

		graph = new DirectedSparseGraph<Integer, Edge>();
	}

	@Override
	public final DirectedSparseGraph<Integer, Edge> completeParse() {
		final DirectedSparseGraph<Integer, Edge> completedParse = graph;
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
		if (!(obj instanceof LatticeFileParser)) {
			return false;
		}
		final LatticeFileParser other = (LatticeFileParser) obj;
		if (graph == null) {
			if (other.graph != null) {
				return false;
			}
		} else if (!graph.equals(other.graph)) {
			return false;
		}
		if (labelTransformer == null) {
			if (other.labelTransformer != null) {
				return false;
			}
		} else if (!labelTransformer.equals(other.labelTransformer)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the graph
	 */
	public DirectedSparseGraph<Integer, Edge> getGraph() {
		return graph;
	}

	/**
	 * @return The {@link Transformer} used for converting edge output symbols
	 *         into user-friendly edge output labels.
	 */
	public Transformer<String, String> getLabelTransformer() {
		return labelTransformer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see scoreLattice.FileParser#parse(java.lang.String)
	 */
	@Override
	public void handleMatch() throws FileParseException {
		// System.out.println(line)

		final int startStateID = Integer.parseInt(lastMatcher.group(1));
		final int endStateID = Integer.parseInt(lastMatcher.group(2));

		String outputSymbol;

		if (labelTransformer != null) {

			outputSymbol = labelTransformer.transform(lastMatcher.group(4));

		} else {

			outputSymbol = lastMatcher.group(4);

		}

		// Parse weight if included in file
		final double weight;
		if (lastMatcher.group(5) != null) {
			weight = Double.parseDouble(lastMatcher.group(5));
		} else {
			weight = 0.0;
		}

		final Edge newEdge = new Edge(startStateID, endStateID, outputSymbol,
				weight);

		graph.addVertex(startStateID);
		graph.addVertex(endStateID);

		graph.addEdge(newEdge, startStateID, endStateID);

		currentValue = newEdge;

	}

	@Override
	public void handleMismatch() throws FileParseException {
		final Matcher finalLineMatcher = FINAL_LINE_PATTERN
				.matcher(currentLine);
		if (finalLineMatcher.matches()) {
			currentValue = null;
		} else {
			final FileParseException newException = createFileParseException("File format error: "
					+ currentLine);
			throw newException;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (graph == null ? 0 : graph.hashCode());
		result = prime * result
				+ (labelTransformer == null ? 0 : labelTransformer.hashCode());
		return result;
	}

	@Override
	public void resetParse() {
		graph = new DirectedSparseGraph<Integer, Edge>();
	}

	/**
	 * @param graph
	 *            The {@link DirectedSparseGraph} to be added to.
	 */
	public final void setGraph(final DirectedSparseGraph<Integer, Edge> graph) {
		this.graph = graph;
	}

	/**
	 * @param labelTransformer
	 *            The {@link Transformer} used for converting edge output
	 *            symbols into user-friendly edge output labels.
	 */
	public void setLabelTransformer(
			final Transformer<String, String> labelTransformer) {
		this.labelTransformer = labelTransformer;
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
		builder.append(", labelTransformer=");
		builder.append(labelTransformer);
		builder.append("]");
		return builder.toString();
	}

}
