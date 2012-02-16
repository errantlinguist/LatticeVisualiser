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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.github.errantlinguist.io.FileLineReader;
import com.github.errantlinguist.io.ImmutableSetFileParser;
import com.github.errantlinguist.parsers.ParseException;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;

import edu.berkeley.cs.SysExits;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

/**
 * A simple tool for visualising ASR word/phone lattices from the <a
 * href="http://www.distant-speech-recognition.org/">Millennium</a> ASR system
 * using the <a href="http://jung.sourceforge.net/">JUNG Framework</a>.
 * 
 * @author Todd Shore
 * @version 2011-11-12
 * @since 2011-11-12
 * 
 */
public class LatticeVisualiser implements Settings, SysExits {

	/**
	 * 
	 * @return A new {@link FileLineReader} for reading lattice files.
	 */
	private static FileLineReader<Edge, DirectedSparseGraph<Integer, Edge>> createFileReader() {
		final Transformer<String, String> stringLowerCaseTransformer = StringLowerCaseTransformer
				.getInstance();
		final LatticeFileParser fileParser = new LatticeFileParser(
				stringLowerCaseTransformer);
		final FileLineReader<Edge, DirectedSparseGraph<Integer, Edge>> fileReader = fileParser
				.createFileReader();
		return fileReader;
	}

	/**
	 * Returns the maximum count of outgoing edges for a single vertex.
	 * 
	 * @param graph
	 *            The {@link Graph} to get the maximum outgoing edge count for.
	 * @param <V>
	 *            The {@code Graph} vertex type.
	 * @param <E>
	 *            The {@code Graph} edge type.
	 * @return The maximum count of outgoing edges for a single vertex.
	 */
	private static <V, E> int getMaxOutEdges(final Graph<V, E> graph) {
		final Collection<V> vertices = graph.getVertices();
		int max = 0;
		for (final V vertex : vertices) {
			final int outEdgeCount = graph.getOutEdges(vertex).size();
			if (outEdgeCount > max) {
				max = outEdgeCount;
			}
		}

		return max;
	}

	/**
	 * Parses command-line arguments for input file, window width and height,
	 * minimum state (i.e.&nbsp;vertex) size and the state/vertex size
	 * multiplier.&nbsp;The input file is then read and visualised.
	 * 
	 * @param args
	 *            The command-line arguments.
	 */
	public static void main(final String[] args) {

		final ArgParser argParser = ArgParser.getInstance();
		argParser.parseArgs(args);

		System.out.print("Reading lattice from path: ");
		final File latticeInfile = argParser.getLatticeInfile();
		System.out.println(latticeInfile.getPath());
		DirectedSparseGraph<Integer, Edge> latticeGraph = null;
		try {
			latticeGraph = readLattice(latticeInfile);
		} catch (final IOException e) {
			System.err.println("I/O exception while reading lattice file.");
			e.printStackTrace();
			System.exit(EX_IOERR);
		} catch (final ParseException e) {
			System.err.println("Parse exception while reading lattice file.");
			e.printStackTrace();
			System.exit(EX_DATAERR);
		}

		final File nonwordInfile = argParser.getNonwordsInfile();
		ImmutableSet<String> nonwords = null;
		try {
			nonwords = readNonwordLabelSet(nonwordInfile);
		} catch (final IOException e) {
			System.err
					.println("I/O exception while reading non-word label file.");
			e.printStackTrace();
			System.exit(EX_IOERR);
		} catch (final ParseException e) {
			System.err
					.println("Parse exception while reading non-word label file.");
			e.printStackTrace();
			System.exit(EX_DATAERR);
		}
		// Set the set of symbols which represent non-word labels.&nbsp;NOTE:
		// This has to be set in order for most of the visualisation to work
		// properly.
		StateType.setNonwords(nonwords);

		final Dimension windowDimension = argParser.getWindowDimension();

		final double stateSizeMultiplier = argParser.getStateSizeMultiplier();
		final int minStateSize = argParser.getMinStateSize();

		final LatticeVisualiser visualizer = new LatticeVisualiser(
				latticeGraph, nonwords, windowDimension, stateSizeMultiplier,
				minStateSize);
		visualizer.visualise();
		visualizer.show();

		printInfo(latticeGraph);
	}

	/**
	 * Prints information about a given {@link Graph} to the standard output.
	 * 
	 * @param graph
	 *            The {@code Graph} to print information about.
	 * @param <V>
	 *            The {@code Graph} vertex type.
	 * @param <E>
	 *            The {@code Graph} edge type.
	 */
	private static <V, E> void printInfo(final Graph<V, E> graph) {
		System.out.print("Vertex count: ");
		final int vertexCount = graph.getVertexCount();
		System.out.println(vertexCount);
		System.out.print("Edge count: ");
		final int edgeCount = graph.getEdgeCount();
		System.out.println(edgeCount);
		System.out.print("Maximum number of outgoing edges: ");
		final int maxOutEdges = getMaxOutEdges(graph);
		System.out.println(maxOutEdges);
	}

	/**
	 * 
	 * @param infile
	 *            The input {@link File} to read which represents the word/phone
	 *            lattice to visualise.
	 * @return A {@link DirectedSparseGraph} representing the word/phone lattice
	 *         in the given file.
	 * @throws IOException
	 *             If the given input path for a non-word label file does not
	 *             refer to a valid file or another I/O error occurs.
	 * @throws ParseException
	 *             If there is an error parsing the contents of a given input
	 *             non-word label file path.
	 */
	private static DirectedSparseGraph<Integer, Edge> readLattice(
			final File infile) throws IOException, ParseException {
		final FileLineReader<Edge, DirectedSparseGraph<Integer, Edge>> fileReader = createFileReader();

		DirectedSparseGraph<Integer, Edge> newGraph = null;
		newGraph = fileReader.readFile(infile);

		return newGraph;
	}

	/**
	 * Creates an {@link ImmutableSet} of non-word labels from a given infile.
	 * 
	 * @param infile
	 *            The {@link File} to read.
	 * @return An {@link ImmutableSet} of symbols representing non-word labels.
	 * @throws IOException
	 *             If the input {@link File} object does not refer to a valid
	 *             file or another I/O error occurs.
	 * @throws com.github.errantlinguist.parsers.ParseException
	 *             If there is an error parsing the contents of the file.
	 */
	private static ImmutableSet<String> readNonwordLabelSet(final File infile)
			throws IOException,
			com.github.errantlinguist.parsers.ParseException {
		final Function<String, String> identityFunction = Functions.identity();
		final ImmutableSetFileParser<String> fileParser = new ImmutableSetFileParser<String>(
				identityFunction);
		final FileLineReader<String, ImmutableSet<String>> fileReader = fileParser
				.createFileReader();

		final ImmutableSet<String> nonwords = fileReader.readFile(infile);
		return nonwords;
	}

	/**
	 * The main window frame.
	 */
	private JFrame frame;

	/**
	 * The {@link Graph} to be visualised.
	 */
	private Graph<Integer, Edge> graph;

	/**
	 * The visualisation {@link Layout} to use.
	 */
	private Layout<Integer, Edge> layout;

	/**
	 * The {@link ImmutableSet} of symbols which represent non-word labels.
	 */
	private final ImmutableSet<String> nonwords;

	/**
	 * The {@link StatePaintTransformer} to be used in visualisation.
	 */
	private final StatePaintTransformer statePaintTransformer;

	/**
	 * The {@link StateShapeTransformer} to be used in visualisation.
	 */
	private final StateShapeTransformer stateShapeTransformer;

	/**
	 * The {@link StateSizeTransformer} to be used in visualisation.
	 */
	private final StateSizeTransformer stateSizeTransformer;

	/**
	 * The {@link VisualizationImageServer} used for visualisation.
	 */
	private VisualizationImageServer<Integer, Edge> visualizationServer;

	/**
	 * The {@link Dimension} to be used for creating the main window.
	 */
	private Dimension windowDimension;

	/**
	 * 
	 * @param graph
	 *            The {@link Graph} to be visualised.
	 * @param nonwords
	 *            An {@link ImmutableSet} of symbols which represent non-word
	 *            labels.
	 * @param windowDimension
	 *            The {@link Dimension} to be used for creating the main window.
	 * @param stateSizeMultiplier
	 *            The state/vertex size multiplier to use to scale their size
	 *            based on the number of incoming/outgoing edges they have.
	 * @param minStateSize
	 *            The minimum size of a state/vertex, to avoid them being too
	 *            small to see on-screen.
	 */
	public LatticeVisualiser(final Graph<Integer, Edge> graph,
			final ImmutableSet<String> nonwords,
			final Dimension windowDimension, final double stateSizeMultiplier,
			final int minStateSize) {
		this.graph = graph;
		this.nonwords = nonwords;
		this.windowDimension = windowDimension;

		stateSizeTransformer = new StateSizeTransformer(graph,
				stateSizeMultiplier, minStateSize);
		stateShapeTransformer = new StateShapeTransformer(graph,
				stateSizeTransformer);
		statePaintTransformer = new StatePaintTransformer(graph);

	}

	/**
	 * Creates a new {@link VisualizationImageServer} using a given
	 * {@link Layout} and the {@link LatticeVisualiser} settings.
	 * 
	 * @param layout
	 *            The <code>Layout</code> to use.
	 * @return A new {@code VisualizationImageServer}.
	 */
	private VisualizationImageServer<Integer, Edge> createVisualizationServer(
			final Layout<Integer, Edge> layout) {
		// The BasicVisualizationServer<Integer,Edge> is parameterised by
		// the vertex and edge types
		final VisualizationImageServer<Integer, Edge> visualizationServer = new VisualizationImageServer<Integer, Edge>(
				layout, windowDimension);
		visualizationServer.setPreferredSize(windowDimension);

		final RenderContext<Integer, Edge> renderContext = visualizationServer
				.getRenderContext();
		renderContext.setVertexFillPaintTransformer(statePaintTransformer);
		renderContext.setEdgeStrokeTransformer(new EdgeStrokeTransformer(
				nonwords));
		renderContext.setEdgeLabelTransformer(EDGE_LABEL_TRANSFORMER);
		renderContext.setEdgeFontTransformer(new EdgeFontTransformer(nonwords));

		renderContext.setVertexShapeTransformer(stateShapeTransformer);
		renderContext.setVertexLabelTransformer(STATE_LABEL_TRANSFORMER);
		final StateFontTransformer stateFontTransformer = new StateFontTransformer(
				graph, nonwords);
		renderContext.setVertexFontTransformer(stateFontTransformer);

		visualizationServer.getRenderer().getVertexLabelRenderer()
				.setPosition(STATE_LABEL_POSITION);
		visualizationServer.setBackground(BACKGROUND_COLOUR);
		return visualizationServer;
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
		if (!(obj instanceof LatticeVisualiser)) {
			return false;
		}
		final LatticeVisualiser other = (LatticeVisualiser) obj;
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
		if (visualizationServer == null) {
			if (other.visualizationServer != null) {
				return false;
			}
		} else if (!visualizationServer.equals(other.visualizationServer)) {
			return false;
		}
		if (windowDimension == null) {
			if (other.windowDimension != null) {
				return false;
			}
		} else if (!windowDimension.equals(other.windowDimension)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The main window frame.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @return The {@link Graph} to be visualised.
	 */
	public Graph<Integer, Edge> getGraph() {
		return graph;
	}

	/**
	 * @return The visualisation {@link Layout} to use.
	 */
	public Layout<Integer, Edge> getLayout() {
		return layout;
	}

	/**
	 * @return The {@link ImmutableSet} of symbols which represent non-word
	 *         labels.
	 */
	public ImmutableSet<String> getNonwords() {
		return nonwords;
	}

	/**
	 * @return The {@link StatePaintTransformer} to be used in visualisation.
	 */
	public StatePaintTransformer getStatePaintTransformer() {
		return statePaintTransformer;
	}

	/**
	 * @return The {@link StateShapeTransformer} to be used in visualisation.
	 */
	public StateShapeTransformer getStateShapeTransformer() {
		return stateShapeTransformer;
	}

	/**
	 * @return The {@link StateSizeTransformer} to be used in visualisation.
	 */
	public StateSizeTransformer getStateSizeTransformer() {
		return stateSizeTransformer;
	}

	/**
	 * @return The {@link VisualizationImageServer} used for visualisation.
	 */
	public VisualizationImageServer<Integer, Edge> getVisualizationServer() {
		return visualizationServer;
	}

	/**
	 * @return The {@link Dimension} to be used for creating the main window.
	 */
	public Dimension getWindowDimension() {
		return windowDimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (graph == null ? 0 : graph.hashCode());
		result = prime * result + (nonwords == null ? 0 : nonwords.hashCode());
		result = prime
				* result
				+ (visualizationServer == null ? 0 : visualizationServer
						.hashCode());
		result = prime * result
				+ (windowDimension == null ? 0 : windowDimension.hashCode());
		return result;
	}

	/**
	 * @param frame
	 *            The main window frame.
	 */
	public void setFrame(final JFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param graph
	 *            The {@link Graph} to be visualised.
	 */
	public void setGraph(final Graph<Integer, Edge> graph) {
		this.graph = graph;
	}

	/**
	 * @param layout
	 *            The visualisation {@link Layout} to use.
	 */
	public void setLayout(final Layout<Integer, Edge> layout) {
		this.layout = layout;
	}

	/**
	 * @param visualizationServer
	 *            The {@link VisualizationImageServer} used for visualisation.
	 */
	public void setVisualizationServer(
			final VisualizationImageServer<Integer, Edge> visualizationServer) {
		this.visualizationServer = visualizationServer;
	}

	/**
	 * @param windowDimension
	 *            The {@link Dimension} to be used for creating the main window.
	 */
	public void setWindowDimension(final Dimension windowDimension) {
		this.windowDimension = windowDimension;
	}

	/**
	 * Displays {@link #frame} on-screen in a window.
	 */
	private void show() {
		frame = new JFrame("Lattice Visualiser");
		frame.getContentPane().setBackground(BACKGROUND_COLOUR);
		frame.setBackground(BACKGROUND_COLOUR);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(visualizationServer);
		frame.pack();
		frame.setVisible(true);
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
		builder.append("nonwords=");
		builder.append(nonwords);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Constructs a new {@link ISOMLayout} for visualisation with
	 * {@link #visualizationServer}, calling
	 * {@link #createVisualizationServer(Layout)} to create it.
	 */
	private void visualise() {
		// We create our graph in here
		// The Layout<Integer, Edge> is parameterised by the vertex and edge
		// types
		layout = new ISOMLayout<Integer, Edge>(graph);
		// sets the initial size of the layout space
		layout.setSize(windowDimension);

		visualizationServer = createVisualizationServer(layout);

	}

}
