/**
 * Copyright (C) 2003, 2004 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.util;

import com.organic.maynard.util.TruthConstants;

/**
 * Contains static methods for converting String values to booleans and the 
 * cannonical String representations for truth and false as defined in
 * revenge.constants.TruthConstants.
 */
public class TruthTest implements TruthConstants {
	
	/**
	 * Converts a value into a cannonical truth value. If the value is 
	 * indeterminate, VALUE_FALSE_CANNONICAL is returned.
	 *
	 * @param value The value to convert.
	 *
	 * @return the String VALUE_TRUE_CANNONICAL or VALUE_FALSE_CANNONICAL.
	 */
	public static String getCannonicalTruthValue(String value) {
		return getCannonicalTruthValue(value, false);
	}
	
	/**
	 * Converts a value into a cannonical truth value. If the value cannot be 
	 * determined, the provided "indeterminate" value is used to return a truth 
	 * value.
	 *
	 * @param value         The value to convert.
	 * @param indeterminate The value to use if we can't figure out the truth 
	 *                      state of the provided value.
	 *
	 * @return the String VALUE_TRUE_CANNONICAL or VALUE_FALSE_CANNONICAL.
	 */
	public static String getCannonicalTruthValue(String value, boolean indeterminate) {
		if (
			VALUE_TRUE_CANNONICAL.equalsIgnoreCase(value) ||
			VALUE_TRUE_SHORT.equalsIgnoreCase(value) ||
			VALUE_YES.equalsIgnoreCase(value) ||
			VALUE_YES_SHORT.equalsIgnoreCase(value) ||
			VALUE_TRUE_NUMERICAL.equalsIgnoreCase(value)
		) {
			return VALUE_TRUE_CANNONICAL;
		} else if (
			VALUE_FALSE_CANNONICAL.equalsIgnoreCase(value) ||
			VALUE_FALSE_SHORT.equalsIgnoreCase(value) ||
			VALUE_NO.equalsIgnoreCase(value) ||
			VALUE_NO_SHORT.equalsIgnoreCase(value) ||
			VALUE_FALSE_NUMERICAL.equalsIgnoreCase(value)
		) {
			return VALUE_FALSE_CANNONICAL;
		} else {
			if (indeterminate) {
				return VALUE_TRUE_CANNONICAL;
			} else {
				return VALUE_FALSE_CANNONICAL;
			}
		}
	}
	
	/**
	 * Converts a value into a boolean. If the value is indeterminate, false is 
	 * returned.
	 *
	 * @param value The value to convert.
	 */
	public static boolean getBooleanTruthValue(String value) {
		return getBooleanTruthValue(value, false);
	}
	
	/**
	 * Converts a value into a boolean. If the value cannot be determined, the 
	 * provided "indeterminate" value is used to return a truth value.
	 *
	 * @param value         The value to convert.
	 * @param indeterminate The value to use if we can't figure out the truth 
	 *                      state of the provided value.
	 */
	public static boolean getBooleanTruthValue(String value, boolean indeterminate) {
		String cannoncial_truth_value = getCannonicalTruthValue(value, indeterminate);
		if (VALUE_TRUE_CANNONICAL.equalsIgnoreCase(cannoncial_truth_value)) {
			return true;
		} else {
			return false;
		}
	}
}