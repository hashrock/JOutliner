/**
 * Copyright (C) 2004 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.outliner.model.propertycontainer;

import java.io.Serializable;

/**
 * Holds a list of PropertyFilters intended to be applied in order on a
 * property being set in a PropertyContainer..
 */
public interface PropertyFilterChain extends Cloneable, Serializable {
	
	/**
	 * Counts the number of PropertyFilters in this PropertyFilterChain.
	 */
	public int countFilters();
	
	/**
	 * Adds a PropertyFilter to the end of this PropertyFilterChain.
	 */
	public void addFilter(PropertyFilter filter);
	
	/**
	 * Inserts a PropertyFilter into this PropertyFilterChain at the provided index.
	 */
	public void insertFilter(int index, PropertyFilter filter);
	
	/**
	 * Gets the PropertyFilter located at the provided index.
	 */
	public PropertyFilter getFilter(int index);
	
	/**
	 * Gets the first PropertyFilter found with a name equal to the provided name
	 * starting at the PropertyFilter with index 0.
	 */
	public PropertyFilter getFilterByName(String name);
	
	/**
	 * Removes the PropertyFilter at the provided index.
	 */
	public PropertyFilter removeFilter(int index);
	
	/**
	 * Removes the first PropertyFilter found with a name equal to the provided name
	 * starting at the PropertyFilter with index 0.
	 */
	public PropertyFilter removeFilterByName(String name);
	
	/**
	 * Removes all PropertyFilters from this PropertyFilterChain.
	 */
	public void removeAllFilters();
	
	/**
	 * Applies the filters in this PropertyFilterChain to the provided value using
	 * the provided PropertyContainer as context. Typically this is done when a
	 * property is being set on the provided PropertyContainer.
	 */
	public Object applyFilters(PropertyContainer container, Object value);
}