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

import java.util.*;

public class PropertyFilterChainImpl implements PropertyFilterChain {
	
	// Instance Fields
	private ArrayList filters;
	
	
	// Constructor
	/**
	 * Constructs a new PropertyFilterChainImpl object.
	 */
	public PropertyFilterChainImpl() {
		super();
		removeAllFilters();
	}
	
	// Cloneable Interface
	public Object clone() {
		PropertyFilterChainImpl cloned = new PropertyFilterChainImpl();
		cloned.filters = (ArrayList) this.filters.clone();
		return cloned;
	}
	
	
	// PropertyFilterChain Interface
	public int countFilters() {
		return filters.size();
	}
	
	public void addFilter(PropertyFilter filter) {
		if (filter != null) {
			filters.add(filter);
		} else {
			throw new IllegalArgumentException("Provided PropertyFilter was null.");
		}
	}
	
	public void insertFilter(int index, PropertyFilter filter) throws IndexOutOfBoundsException {
		if (filter != null) {
			filters.add(index, filter);
		} else {
			throw new IllegalArgumentException("Provided PropertyFilter was null.");
		}
	}
	
	public PropertyFilter getFilter(int index) throws IndexOutOfBoundsException {
		return (PropertyFilter) filters.get(index);
	}
	
	public PropertyFilter getFilterByName(String name) {
		for (int i = 0; i < filters.size(); i++) {
			PropertyFilter filter = (PropertyFilter) filters.get(i);
			String filter_name = filter.getName();
			if (filter_name == null && name == null) {
				return filter;
			} else if (filter_name.equals(name)) {
				return filter;
			}
		}
		return null;
	}
	
	public PropertyFilter removeFilter(int index) throws IndexOutOfBoundsException {
		return (PropertyFilter) filters.remove(index);
	}
	
	public PropertyFilter removeFilterByName(String name) {
		for (int i = 0; i < filters.size(); i++) {
			PropertyFilter filter = (PropertyFilter) filters.get(i);
			String filter_name = filter.getName();
			if (filter_name == null && name == null) {
				filters.remove(i);
				return filter;
			} else if (filter_name.equals(name)) {
				filters.remove(i);
				return filter;
			}
		}
		return null;
	}
	
	public void removeAllFilters() {
		filters = new ArrayList();
	}
	
	public Object applyFilters(PropertyContainer container, Object value) {
		for (int i = 0; i < filters.size(); i++) {
			PropertyFilter filter = (PropertyFilter) filters.get(i);
			value = filter.filter(container, value);
		}
		return value;
	}
}