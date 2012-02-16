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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.berkeley.cs.SysExits;

/**
 * A POSIX-compliant command-line argument parser using the <a
 * href="http://commons.apache.org/cli/">Apache Commons CLI</a> library.
 * 
 * @author Todd Shore
 * @version 2011-11-12
 * @since 2011-11-12
 * 
 */
public class ArgParser implements Settings, SysExits {

	/**
	 * {@link SingletonHolder} is loaded on the first execution of
	 * {@link ArgParser#getInstance()} or the first access to {@link #INSTANCE},
	 * not before.
	 * 
	 * @author Todd Shore
	 * @version 2011-07-05
	 * @since 2011-07-05
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
	 */
	private static final class SingletonHolder {
		private static final ArgParser INSTANCE = new ArgParser();
	}

	/**
	 * The {@link HelpFormatter} used for displaying command-line help.
	 */
	private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

	private static final String HELP_KEY = "?",
			HELP_KEY_LONG = "help",
			HELP_DESCR = "Displays usage information.",

			NONWORD_INFILE_KEY_LONG = "nonwords",
			NONWORD_INFILE_KEY = "nw",
			NONWORD_INFILE_DESCR = "A text file of labels which are to be considered non-words for search purposes.",

			WIDTH_KEY_LONG = "width",
			WIDTH_KEY = "w",
			WIDTH_DESCR = "The horizontal size of the displayed window.",

			HEIGHT_KEY_LONG = "height",
			HEIGHT_KEY = "h",
			HEIGHT_DESCR = "The vertical size of the displayed window.",

			MIN_STATE_SIZE_KEY_LONG = "minsize",
			MIN_STATE_SIZE_KEY = "min",
			MIN_STATE_SIZE_DESCR = "The minimum size of a state/vertex, to avoid them being too small to see on-screen.",

			STATE_SIZE_MULTIPLIER_KEY_LONG = "sizemultiplier",
			STATE_SIZE_MULTIPLIER_KEY = "mult",
			STATE_SIZE_MULTIPLIER_DESCR = "The state/vertex size multiplier to use to scale their size based on the number of incoming/outgoing edges they have.",

			LATTICE_INFILE_KEY_LONG = "infile", LATTICE_INFILE_KEY = "inf",
			LATTICE_INFILE_DESCR = "The word/phone lattice file to visualise.",

			INFILE_ARG_NAME = "infile", SIZE_ARG_NAME = "size";

	/**
	 * The {@link CommandLineParser} used for parsing commands &mdash; In this
	 * case, it is a {@link PosixParser}.
	 */
	private static final CommandLineParser OPTION_PARSER = new PosixParser();

	/**
	 * The {@link Options} to parse.
	 */
	private static final Options OPTIONS = createOptions();

	/**
	 * Creates and adds a lattice infile option to a given {@link Options}
	 * object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addLatticeInfileOption(final Options options) {
		OptionBuilder.isRequired(true);
		OptionBuilder.withLongOpt(LATTICE_INFILE_KEY_LONG);
		OptionBuilder.withDescription(LATTICE_INFILE_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(INFILE_ARG_NAME);
		OptionBuilder.withType(File.class);
		final Option latticeInfile = OptionBuilder.create(LATTICE_INFILE_KEY);
		options.addOption(latticeInfile);
	}

	/**
	 * Creates and adds a minimum state size option to a given {@link Options}
	 * object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addMinStateSizeOption(final Options options) {
		OptionBuilder.withLongOpt(MIN_STATE_SIZE_KEY_LONG);
		OptionBuilder.withDescription(MIN_STATE_SIZE_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(SIZE_ARG_NAME);
		OptionBuilder.withType(int.class);

		final Option minStateSize = OptionBuilder.create(MIN_STATE_SIZE_KEY);
		options.addOption(minStateSize);
	}

	/**
	 * Creates and adds a non-word infile option to a given {@link Options}
	 * object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addNonwordInfileOption(final Options options) {
		OptionBuilder.withLongOpt(NONWORD_INFILE_KEY_LONG);
		OptionBuilder.withDescription(NONWORD_INFILE_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(INFILE_ARG_NAME);
		OptionBuilder.withType(File.class);
		final Option nonwordInfile = OptionBuilder.create(NONWORD_INFILE_KEY);
		options.addOption(nonwordInfile);
	}

	/**
	 * Creates and adds horizontal and vertical size options to a given
	 * {@link Options} object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addSizeOptions(final Options options) {
		addXSizeOption(options);
		addYSizeOption(options);
	}

	/**
	 * Creates and adds a state size multiplier option to a given
	 * {@link Options} object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addStateSizeMultiplierOption(final Options options) {
		OptionBuilder.withLongOpt(STATE_SIZE_MULTIPLIER_KEY_LONG);
		OptionBuilder.withDescription(STATE_SIZE_MULTIPLIER_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(SIZE_ARG_NAME);
		OptionBuilder.withType(double.class);

		final Option stateSizeMultiplier = OptionBuilder
				.create(STATE_SIZE_MULTIPLIER_KEY);
		options.addOption(stateSizeMultiplier);
	}

	/**
	 * Creates and adds a horizontal size option to a given {@link Options}
	 * object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addXSizeOption(final Options options) {
		OptionBuilder.withLongOpt(WIDTH_KEY_LONG);
		OptionBuilder.withDescription(WIDTH_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(SIZE_ARG_NAME);
		OptionBuilder.withType(Integer.class);

		final Option width = OptionBuilder.create(WIDTH_KEY);
		options.addOption(width);
	}

	/**
	 * Creates and adds a vertical size option to a given {@link Options}
	 * object.
	 * 
	 * @param options
	 *            The <code>Options</code> object to add to.
	 */
	private static void addYSizeOption(final Options options) {
		OptionBuilder.withLongOpt(HEIGHT_KEY_LONG);
		OptionBuilder.withDescription(HEIGHT_DESCR);
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(SIZE_ARG_NAME);
		OptionBuilder.withType(int.class);

		final Option height = OptionBuilder.create(HEIGHT_KEY);
		options.addOption(height);
	}

	/**
	 * Creates the {@link Options} object defining possible command-line
	 * options.
	 * 
	 * @return A new {@link Options} object defining possible command-line
	 *         options.
	 */
	private static Options createOptions() {
		final Options options = new Options();

		options.addOption(HELP_KEY, HELP_KEY_LONG, false, HELP_DESCR);

		addLatticeInfileOption(options);

		addNonwordInfileOption(options);

		addSizeOptions(options);

		addStateSizeMultiplierOption(options);

		addMinStateSizeOption(options);

		return options;
	}

	/**
	 * 
	 * @return A single static {@link ArgParser} instance.
	 */
	public static ArgParser getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Gets the name of the class of which the main method was called; This is
	 * not necessarily {@code main(String[])} (which is what creates this
	 * method useful). NOTE: Might be inaccurate if used in a multi-threaded
	 * application, because it gets the head of the stack of only the thread in
	 * which the method was called.
	 * 
	 * @return The name of the class of which the main method was called.
	 */
	private static final String getMainClassName() {
		final StackTraceElement[] stack = Thread.currentThread()
				.getStackTrace();
		final StackTraceElement main = stack[stack.length - 1];
		final String mainClassName = main.getClassName();
		return mainClassName;
	}

	/**
	 * Writes a usage message to the standard error output and exits the program
	 * with the error code {@link edu.berkeley.cs.SysExits#EX_USAGE}.
	 */
	protected static void notifyBadArgs() {
		final String mainClassName = getMainClassName();

		HELP_FORMATTER.printHelp(mainClassName, OPTIONS);
		System.exit(EX_USAGE);
	}

	/**
	 * Parses command-line arguments for window width and height.
	 * 
	 * @param cl
	 *            The {@link CommandLine} representing the command-line
	 *            arguments.
	 * @return A {@link Dimension} object used for defining the dimensions of
	 *         the main window for visualisation.
	 */
	private static Dimension parseDimensionArgs(final CommandLine cl) {
		final Dimension windowDimension;
		if (!cl.hasOption(WIDTH_KEY)) {

			if (!cl.hasOption(HEIGHT_KEY)) {
				windowDimension = DEFAULT_WINDOW_DIMENSION;

			} else {
				final int height = Integer.parseInt(cl
						.getOptionValue(HEIGHT_KEY));
				windowDimension = new Dimension(DEFAULT_WINDOW_DIMENSION.width,
						height);

			}

		} else {
			final int width = Integer.parseInt(cl.getOptionValue(WIDTH_KEY));

			final int height;
			if (!cl.hasOption(HEIGHT_KEY)) {
				height = DEFAULT_WINDOW_DIMENSION.height;
				windowDimension = new Dimension(width,
						DEFAULT_WINDOW_DIMENSION.height);

			} else {
				height = Integer.parseInt(cl.getOptionValue(HEIGHT_KEY));

				windowDimension = new Dimension(width, height);
			}
		}
		return windowDimension;
	}
	
	/**
	 * The {@link File} representing non-word labels.
	 */
	private File nonwordsInfile;

	/**
	 * @return The {@link File} representing non-word labels.
	 */
	public File getNonwordsInfile() {
		return nonwordsInfile;
	}

	/**
	 * The input {@link File} to read which represents the word/phone lattice to
	 * visualise.
	 */
	private File latticeInfile;

	/**
	 * The minimum size of a state/vertex, to avoid them being too small to see
	 * on-screen.
	 */
	private int minStateSize;

	/**
	 * The state/vertex size multiplier to use to scale their size based on the
	 * number of incoming/outgoing edges they have.
	 */
	private double stateSizeMultiplier;

	/**
	 * The {@link Dimension} to be used for creating the main window.
	 */
	private Dimension windowDimension;

	private ArgParser() {

	}

	/**
	 * @return The input {@link File} to read which represents the word/phone
	 *         lattice to visualise.
	 */
	public File getLatticeInfile() {
		return latticeInfile;
	}

	/**
	 * @return The minimum size of a state/vertex, to avoid them being too small
	 *         to see on-screen.
	 */
	public int getMinStateSize() {
		return minStateSize;
	}

	/**
	 * @return The state/vertex size multiplier to use to scale their size based
	 *         on the number of incoming/outgoing edges they have.
	 */
	public double getStateSizeMultiplier() {
		return stateSizeMultiplier;
	}

	/**
	 * @return The {@link Dimension} to be used for creating the main window.
	 */
	public Dimension getWindowDimension() {
		return windowDimension;
	}

	/**
	 * 
	 * @param args
	 *            The command-line arguments.
	 * @throws IOException
	 *             If the given input path for a non-word label file does not
	 *             refer to a valid file or another I/O error occurs.
	 * @throws com.github.errantlinguist.parsers.ParseException
	 *             If there is an error parsing the contents of a given input
	 *             non-word label file path.
	 */
	public void parseArgs(final String[] args) {
		CommandLine cl = null;

		try {
			// parse the command line arguments
			cl = OPTION_PARSER.parse(OPTIONS, args);
		} catch (final ParseException e) {
			System.out.println(e.getLocalizedMessage());
			notifyBadArgs();
		}

		final String latticeInfilePath = cl.getOptionValue(LATTICE_INFILE_KEY);
		latticeInfile = new File(latticeInfilePath);
		if (!latticeInfile.exists()) {
			System.err.println("Invalid path: " + latticeInfilePath);
			notifyBadArgs();
		}

		windowDimension = parseDimensionArgs(cl);

		if (cl.hasOption(STATE_SIZE_MULTIPLIER_KEY)) {
			stateSizeMultiplier = Double.parseDouble(cl
					.getOptionValue(STATE_SIZE_MULTIPLIER_KEY));
		} else {
			stateSizeMultiplier = DEFAULT_STATE_SIZE_MULTIPLIER;
		}

		if (cl.hasOption(MIN_STATE_SIZE_KEY)) {
			minStateSize = Integer.parseInt(cl
					.getOptionValue(MIN_STATE_SIZE_KEY));
		} else {
			minStateSize = DEFAULT_MIN_STATE_SIZE;
		}

		if (cl.hasOption(NONWORD_INFILE_KEY)) {
			final String infilePath = cl.getOptionValue(NONWORD_INFILE_KEY);
			nonwordsInfile = new File(infilePath);
		}

	}

}
