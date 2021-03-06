/*
 * #%L
 * SLIM Curve plugin for combined spectral-lifetime image analysis.
 * %%
 * Copyright (C) 2010 - 2015 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package loci.slim;

import ij.IJ;
import ij.plugin.PlugIn;

import java.util.Stack;

/**
 * TODO
 *
 * @author Aivar Grislis
 */
public class SLIM_PlugIn implements PlugIn {

	private static final String ONE = "1";
	private static final Stack<SLIMProcessor> stack = new Stack<SLIMProcessor>();
	private static volatile SLIMProcessor instance = null;

	@Override
	public void run(final String arg) {
		final SLIMProcessor slimProcessor = new SLIMProcessor();
		stack.push(slimProcessor);
		slimProcessor.process(arg);
		stack.pop();

	}

	/**
	 * Starts up batch processing.
	 *
	 * @return whether or not successful
	 */
	public static boolean startBatch() {
		boolean success = false;
		instance = null;
		if (stack.empty()) {
			IJ.showMessage("Error in Batch Processing",
				"SLIM Curve should be running before invoking batch processing macro.");
		}
		else {
			instance = stack.peek();
			success = instance.startBatch();
		}
		return success;
	}

	/**
	 * Processes an input file in batch processing.
	 *
	 * @param input file name
	 * @param output file name
	 */
	public static void batch(final String input, final String output,
		final String exportPixels, final String exportText)
	{
		if (null != instance) {
			instance.batch(input, output, ONE.equals(exportPixels), ONE
				.equals(exportText));
		}
	}

	/**
	 * Ends batch processing.
	 */
	public static void endBatch() {
		if (null != instance) {
			instance.endBatch();
		}
		instance = null;
	}

	// TODO ARG EXPERIMENTAL
	/**
	 * Starts up batch processing.
	 *
	 * @return whether or not successful
	 */
	public static boolean startBatchHisto() {
		boolean success = false;
		instance = null;
		if (stack.empty()) {
			IJ.showMessage("Error in Batch Processing",
				"SLIM Curve should be running before invoking batch processing macro.");
		}
		else {
			instance = stack.peek();
			success = instance.startBatchHisto();
		}
		return success;
	}

	/**
	 * Processes an input file in batch processing.
	 *
	 * @param input file name
	 * @param output file name
	 */
	public static void batchHisto(final String input, final String output) {
		if (null != instance) {
			instance.batchHisto(input, output);
		}
	}

	/**
	 * Ends batch processing.
	 */
	public static void endBatchHisto() {
		if (null != instance) {
			instance.endBatchHisto();
		}
		instance = null;
	}

}
