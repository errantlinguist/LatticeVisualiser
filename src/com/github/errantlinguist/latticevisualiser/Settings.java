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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import com.google.common.collect.ImmutableMap;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Constant settings to be used during visualisation of a word/phone lattice.
 * 
 * @author Todd Shore
 * @version 2011-11-12
 * @since 2011-11-12
 * 
 */
public interface Settings {

	/**
	 * The main window background colour.
	 */
	public static final Color BACKGROUND_COLOUR = Color.WHITE;

	/**
	 * A map of {@link StateType StateTypes} to {@link Color Colors} for
	 * deriving the colour used to draw the vertex representing a state.
	 */
	public static final ImmutableMap<StateType, Color> COLOURS = StatePaintTransformer
			.makeColorMap();

	/**
	 * The default minimum size of a state/vertex, to avoid them being too small
	 * to see on-screen.
	 */
	public static final int DEFAULT_MIN_STATE_SIZE = 1;

	/**
	 * The default state/vertex size multiplier to use to scale their size based
	 * on the number of incoming/outgoing edges they have.
	 */
	public static final double DEFAULT_STATE_SIZE_MULTIPLIER = 5.0;

	/**
	 * The default main window dimensions.
	 */
	public static final Dimension DEFAULT_WINDOW_DIMENSION = new Dimension(800,
			600);

	/**
	 * The {@link EdgeLabelTransformer} to be used in visualisation.
	 */
	public static final EdgeLabelTransformer EDGE_LABEL_TRANSFORMER = EdgeLabelTransformer
			.getInstance();

	/**
	 * The final label, e.g.&nbsp;<code>"&lt;/s&gt;"<code>.
	 */
	public static final String FINAL_LABEL = "</s>";

	/**
	 * The {@link Font} to use for labelling goal states, i.e.&nbsp; states
	 * which have edges outputting a word symbol.
	 */
	public static final Font GOAL_VERTEX_FONT = new Font(Font.SANS_SERIF,
			Font.BOLD, 14);

	/**
	 * The {@link Font} to use for labelling intermediate states, i.e.&nbsp;
	 * states which do not have edges outputting a word symbol.
	 */
	public static final Font INTERMEDIATE_VERTEX_FONT = new Font(
			Font.SANS_SERIF, Font.PLAIN, 12);

	/**
	 * The {@link Font} to use for visualising {@link Edge edges} which output a
	 * non-word label.
	 */
	public static final Font NON_WORD_LABEL_FONT = new Font(Font.SANS_SERIF,
			Font.PLAIN, 12);

	/**
	 * The number of sides a non-star {@link Polygon} is be drawn with.
	 */
	public static final int POLYGON_SIDE_COUNT = 6;

	/**
	 * The number of sides a star-shaped {@link Polygon} is be drawn with.
	 */
	public static final int STAR_POINT_COUNT = 5;

	/**
	 * The aspect ratio of the {@link Ellipse2D} vertex representing a state in
	 * visualisation.
	 */
	public static final float STATE_ASPECT = 1.0f;

	/**
	 * The {@link StateAspectTransformer} used for deriving the aspect ratio of
	 * a vertex drawn to represent a lattice state.
	 */
	public static final StateAspectTransformer STATE_ASPECT_TRANSFORMER = StateAspectTransformer
			.getInstance();

	/**
	 * The position of a state/vertex label in relation to the state/vertex
	 * itself.
	 */
	public static final Position STATE_LABEL_POSITION = Position.CNTR;

	/**
	 * The {@link ToStringLabeller} to be used in visualisation for labelling
	 * states/vertices.
	 */
	public static final ToStringLabeller<Integer> STATE_LABEL_TRANSFORMER = new ToStringLabeller<Integer>();

	/**
	 * The stroke type to use for drawing dashed {@link Edge edge} arcs.
	 */
	public static float[] STROKE_DASH = { 10.0f };

	/**
	 * The factor to multiply {@link Edge#weight} by to calculate the stroke
	 * weight to use when drawing the edge.
	 */
	public static final float WEIGHT_FACTOR = 2.0f;

	/**
	 * The {@link Font} to use for visualising {@link Edge edges} which output a
	 * word label.
	 */
	public static final Font WORD_LABEL_FONT = new Font(Font.SANS_SERIF,
			Font.BOLD, 14);
}
