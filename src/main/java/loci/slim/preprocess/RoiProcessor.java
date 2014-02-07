/*
 * #%L
 * SLIM Plugin for combined spectral-lifetime image analysis.
 * %%
 * Copyright (C) 2010 - 2014 Board of Regents of the University of
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

package loci.slim.preprocess;

import ij.gui.Roi;

/**
 *
 * @author Aivar Grislis
 */
public class RoiProcessor implements IProcessor {
	private Roi[] _rois;
    private IProcessor _processor;
	
	public RoiProcessor(Roi[] rois) {
		_rois = rois;
	}
	
    @Override
    public void chain(IProcessor processor) {
		_processor = processor;
	}
    
    @Override
    public double[] getPixel(int[] location) {
		double[] returnValue = _processor.getPixel(location);
		for (Roi roi : _rois) {
			if (roi.contains(location[0], location[1])) {
			   return returnValue;
			}
		}
		return null;
	}
}
