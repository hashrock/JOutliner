/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import org.xml.sax.*;

public class IntRangeValidator extends AbstractValidator implements Validator, GUITreeComponent {

	// Constants
	public static final String A_MIN = "min";
	public static final String A_MAX = "max";
	public static final String A_DEFAULT = "default";
	
	
	// Instance Fields
	private int lowerBound = 0;
	private int upperBound = 0;
	private int defaultValue = 0;
	private boolean returnNearestValue = true;


	// Constructors
	public IntRangeValidator() {}
	
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue) {
		this(lowerBound,upperBound,defaultValue,true);
	}
	
	public IntRangeValidator(int lowerBound, int upperBound, int defaultValue, boolean returnNearestValue) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.defaultValue = defaultValue;
		this.returnNearestValue = returnNearestValue;
	}


	// GUITreeComponent Interface
	public void startSetup(Attributes atts) {
		String min = atts.getValue(A_MIN);
		String max = atts.getValue(A_MAX);
		String def = atts.getValue(A_DEFAULT);
		
		try {
			this.lowerBound = Integer.parseInt(min);
			this.upperBound = Integer.parseInt(max);
			this.defaultValue = Integer.parseInt(def);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		super.startSetup(atts);
	}


	// Validator Interface
	public Object getValidValue(Object val) {
		if (val instanceof String) {
			return getValidValue((String) val);
		} else if (val instanceof Integer) {
			return getValidValue((Integer) val);
		} else {
			return null;
		}
	}
	
	private Integer getValidValue(String value) {
		Integer retVal = new Integer(defaultValue);
		try {
			retVal = getValidValue(new Integer(value));
		} catch (NumberFormatException nfe) {
			if (!returnNearestValue) {return null;}
			retVal = new Integer(defaultValue);
		}
		return retVal;
	}

	private Integer getValidValue(Integer value) {
		if (value.intValue() < lowerBound) {
			if (!returnNearestValue) {return null;}
			value = new Integer(lowerBound);
		} else if (value.intValue() > upperBound) {
			if (!returnNearestValue) {return null;}
			value = new Integer(upperBound);
		}
		return value;
	}
	
	
	// Additional Accessors
	public boolean isReturnNearestValue() {
		return returnNearestValue;
	}
	
	public void setReturnNearestValue(boolean returnNearestValue) {
		this.returnNearestValue = returnNearestValue;
	}
	
	public int getMin() {
		return this.lowerBound;
	}
	
	public void setMin(int lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	public int getMax() {
		return this.upperBound;
	}
	
	public void setMax(int upperBound) {
		this.upperBound = upperBound;
	}
}