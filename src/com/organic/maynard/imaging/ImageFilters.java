/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.imaging;

import java.awt.image.*;
import java.awt.geom.*;

/**
 * 
 * Defines static factory methods for getting various image filters.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2002/12/19 00:23:25 $
 */

public class ImageFilters {
	
	// Class Fields
	private static final InversionFilter inversion_filter = new InversionFilter();
	
	// Factory Methods
	/** Gets an image filter that inverts each color channel. Existing alpha transparency is preserved. */
	public static RGBImageFilter getInversionFilter() {
		return inversion_filter;
	}
	
	/** Lightens each color channel by the amount provided. Existing alpha transparency is preserved. */
	public static RGBImageFilter getLightenFilter(int amount) {
		return new LightenFilter(amount);
	}
	
	/** Darkens each color channel by the amount provided. Existing alpha transparency is preserved. */
	public static RGBImageFilter getDarkenFilter(int amount) {
		return new DarkenFilter(amount);
	}
	
	/** Fills a single color into the entire image. Existing alpha transparency is preserved. */
	public static RGBImageFilter getFillFilter(int amount) {
		return new FillFilter(amount);
	}
}

/** Inverts each color channel. Existing alpha transparency is preserved. */
class InversionFilter extends RGBImageFilter {
	
	// RGBImageFilter Methods
	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | (0xffffff - (rgb & 0x00ffffff))
		);
	}
}

/** Lightens each color channel by the amount provided. Existing alpha transparency is preserved. */
class LightenFilter extends RGBImageFilter {
	
	// Instance Fields
	private int amount = 0;
	
	// Constructor
	public LightenFilter(int amount) {
		this.amount = amount;
	}
	
	// RGBImageFilter Methods
	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | (amount | (rgb & 0x00ffffff))
		);
	}
}

/** Darkens each color channel by the amount provided. Existing alpha transparency is preserved. */
class DarkenFilter extends RGBImageFilter {
	
	// Instance Fields
	private int amount = 0;
	
	// Constructor
	public DarkenFilter(int amount) {
		this.amount = amount;
	}
	
	// RGBImageFilter Methods
	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | (amount & (rgb & 0x00ffffff))
		);
	}
}

/** Fills a single color into the entire image. Existing alpha transparency is preserved. */
class FillFilter extends RGBImageFilter {
	
	// Instance Fields
	private int amount = 0;
	
	// Constructor
	public FillFilter(int amount) {
		this.amount = amount;
	}
	
	// RGBImageFilter Methods
	public int filterRGB(int x, int y, int rgb) {
		return (rgb & 0xff000000) | amount;
	}
}