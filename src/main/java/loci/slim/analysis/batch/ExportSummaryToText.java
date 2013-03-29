//
// ExportSummaryToText.java
//

/*
SLIMPlugin for combined spectral-lifetime image analysis.

Copyright (c) 2013, UW-Madison LOCI
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package loci.slim.analysis.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import loci.curvefitter.ICurveFitter;
import loci.slim.analysis.HistogramStatistics;
import loci.slim.analysis.batch.ui.BatchHistogramsFrame;
import loci.slim.analysis.batch.ui.BatchHistogramListener;
import loci.slim.fitted.AFittedValue;
import loci.slim.fitted.ChiSqFittedValue;
import loci.slim.fitted.FittedValue;
import loci.slim.fitted.FractionalContributionFittedValue;
import loci.slim.fitted.FractionalIntensityFittedValue;
import loci.slim.fitted.TFittedValue;
import loci.slim.fitted.TMeanFittedValue;
import loci.slim.fitted.ZFittedValue;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.real.DoubleType;

/**
 * Exports a summary histogram in batch mode.
 * 
 * @author Aivar Grislis
 */
public class ExportSummaryToText {
	private ICurveFitter.FitFunction function;
	private BatchHistogramListener listener;
	private FittedValue[] parameters;
	private BatchHistogram[] histograms;
	private String[] titles;
	private int[] indices;
	private BatchHistogramsFrame frame;
	// combine histograms in horizontal columns
	private boolean combined = true;

	/**
	 * Initializes for given fitting function.
	 * 
	 * @param parameters
	 * @param function 
	 * @param listener
	 */
	public void init(ICurveFitter.FitFunction function, FittedValue[] parameters, BatchHistogramListener listener) {
		this.function = function;
		this.listener = listener;
		this.parameters = parameters;
		
		histograms = buildBatchHistograms(parameters);
	}

	/**
	 * Processes each image in batch job.
	 * 
	 * @param fileName
	 * @param image 
	 */
	public void process(String fileName, Image<DoubleType> image) {
		int[] dimensions = image.getDimensions();
		int fittedParameters = dimensions[3];
		LocalizableByDimCursor<DoubleType> cursor = image.createLocalizableByDimCursor();

		// build array of BatchHistogram for this image
		BatchHistogram[] imageHistograms = buildBatchHistograms(parameters);

		// traverse all pixels
		int[] position = new int[dimensions.length];
		for (int y = 0; y < dimensions[1]; ++y) {
			for (int x = 0; x < dimensions[0]; ++x) {
				// set position
				position[0] = x;
				position[1] = y;
				// non-xy dimensions remain at zero

				// get all fitted values
				double[] values = new double[fittedParameters];
				for (int i = 0; i < fittedParameters; ++i) {
					position[3] = i;
					cursor.setPosition(position);
					values[i] = cursor.getType().getRealDouble();
				}
				
				// update histograms for this image
				for (BatchHistogram histogram : imageHistograms) {
					histogram.process(values);
				}

				// update all batch histograms
				for (BatchHistogram histogram : histograms) {
					histogram.process(values);
				}
			}
		}
		
		
		// build list of histogram statistics for the current image
		List<HistogramStatistics> imageList = new ArrayList<HistogramStatistics>();
		for (BatchHistogram histogram : imageHistograms) {
			HistogramStatistics imageStatistics = histogram.getStatistics();
			imageList.add(imageStatistics);
		}
		
		// build list of summarized histogram statistics
		List<HistogramStatistics> summaryList = new ArrayList<HistogramStatistics>();
		for (BatchHistogram histogram : histograms) {
		    HistogramStatistics summaryStatistics = histogram.getStatistics();
		    summaryList.add(summaryStatistics);
		}
		
		// lazy instantiation of frame
		if (null == frame) {
		    frame = new BatchHistogramsFrame(listener);
		}
		// show new image statistics and update summary
		frame.update(
			fileName,
			imageList.toArray(new HistogramStatistics[imageList.size()]), 
			summaryList.toArray(new HistogramStatistics[summaryList.size()]));
	}

	/**
	 * Exports the summary to a file.
	 * 
	 * @param fileName
	 * @param separator
	 */
    public void export(String fileName, char separator) {
		BufferedWriter bufferedWriter = null;
		try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
        }
		catch (IOException e) {
            IJ.log("exception opening file " + fileName);
            IJ.handleException(e);
        }
		
		if (null != bufferedWriter) {
			try {
				// title this export
				bufferedWriter.write("Export Summary Histogram");
				bufferedWriter.newLine();
				bufferedWriter.newLine();

				if (combined) {
					HistogramStatistics[] statistics = new HistogramStatistics[histograms.length];
					for (int i = 0; i < statistics.length; ++i) {
						statistics[i] = histograms[i].getStatistics();
					}
					HistogramStatistics.export(statistics, bufferedWriter, separator);
				}
				else {
					for (BatchHistogram histogram : histograms) {
						HistogramStatistics statistics = histogram.getStatistics();
						statistics.export(bufferedWriter, separator);
					}
				}

				bufferedWriter.newLine();
				bufferedWriter.close();
			}
			catch (IOException exception) {
				IJ.log("exception writing to file " + fileName);
				IJ.handleException(exception);
			}
		}
	}

	/**
	 * Given an array of FittedValue creates a corresponding array of BatchHistogram.
	 * 
	 * @param parameters
	 * @return 
	 */
	private BatchHistogram[] buildBatchHistograms(FittedValue[] parameters) {
		// go through list of fitted values and build corresponding batch histograms
		List<BatchHistogram> histogramsList = new ArrayList<BatchHistogram>();
		for (FittedValue parameter : parameters) {
			BatchHistogram histogram = null;
			
			if (parameter instanceof ChiSqFittedValue) {
				histogram = new ChiSqBatchHistogram();
			}
			else if (parameter instanceof ZFittedValue) {
				histogram = new ZBatchHistogram();
			}
			else if (parameter instanceof AFittedValue) {
				histogram = new ABatchHistogram();
			}
			else if (parameter instanceof FractionalContributionFittedValue) {
				histogram = new FractionalContribBatchHistogram();
			}
			else if (parameter instanceof FractionalIntensityFittedValue) {
				histogram = new FractionalIntensityBatchHistogram();
			}
			else if (parameter instanceof TFittedValue) {
				histogram = new TauBatchHistogram();
			}
			else if (parameter instanceof TMeanFittedValue) {
				histogram = new TauBatchHistogram();
			}
			//TODO 'h' parameter for stretched
			
			if (null != histogram) {
				histogram.init(parameter);
				histogramsList.add(histogram);
			}
		}
		
		return histogramsList.toArray(new BatchHistogram[histogramsList.size()]);
	}
}
