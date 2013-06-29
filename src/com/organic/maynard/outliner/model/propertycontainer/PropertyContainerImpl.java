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

public class PropertyContainerImpl implements PropertyContainer {
	
	// Instance Fields
	private HashMap default_values;
	private HashMap current_values;
	private HashMap filter_chains;
	
	
	// Constructor
	/**
	 * Constructs a new PropertyContainerImpl object with no current or default
	 * values defined.
	 */
	public PropertyContainerImpl() {
		super();
		removeAllProperties();
	}
	
	
	// Cloneable Interface
	public Object clone() throws CloneNotSupportedException {
		//PropertyContainerImpl cloned = new PropertyContainerImpl();
		PropertyContainerImpl cloned = (PropertyContainerImpl) super.clone();
		cloned.current_values = (HashMap) this.current_values.clone();
		cloned.default_values = (HashMap) this.default_values.clone();
		cloned.filter_chains = (HashMap) this.filter_chains.clone();
		return cloned;
	}
	
	
	// PropertyContainer Interface
	public void removeAllProperties() {
		this.default_values = new HashMap();
		this.current_values = new HashMap();
		this.filter_chains = new HashMap();
	}
	
	public void removeProperty(String key) {
		this.default_values.remove(key);
		this.current_values.remove(key);
		this.filter_chains.remove(key);
	}
	
	public void resetAllProperties() {
		Iterator it = getKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (propertyDefaultExists(key)) {
				setProperty(key, getPropertyDefault(key));
			} else {
				removeProperty(key);
			}
		}
	}
	
	public void resetProperty(String key) {
		this.current_values.put(key, this.default_values.get(key));
	}
	
	public void setProperty(String key, Object value) {
		PropertyFilterChain filter_chain = (PropertyFilterChain) filter_chains.get(key);
		if (filter_chain != null) {
			// TBD: have the chain throw an exception when no value should be set.
			this.current_values.put(key, filter_chain.applyFilters(this, value));
		} else {
			this.current_values.put(key, value);
		}
	}
	
	public void setPropertyDefault(String key, Object default_value) {
		this.default_values.put(key, default_value);
	}
	
	public void setPropertyFilterChain(String key, PropertyFilterChain filter_chain) {
		this.filter_chains.put(key, filter_chain);
	}
	
	public PropertyFilterChain getPropertyFilterChain(String key) {
		return (PropertyFilterChain) filter_chains.get(key);
	}
	
	public PropertyFilterChain removePropertyFilterChain(String key, int index) {
		return (PropertyFilterChain) filter_chains.remove(key);
	}
	
	public void addPropertyFilter(String key, PropertyFilter filter) {
		PropertyFilterChain filter_chain = getPropertyFilterChain(key);
		if (filter_chain == null) {
			filter_chain = new PropertyFilterChainImpl();
			setPropertyFilterChain(key, filter_chain);
		}
		filter_chain.addFilter(filter);
	}
	
	public Object getProperty(String key) {
		if (propertyExists(key)) {
			return current_values.get(key);
		} else if (propertyDefaultExists(key)) {
			return default_values.get(key);
		} else {
			return null;
		}
	}
	
	public Object getProperty(String key, Object backup_value) {
		if (propertyExists(key)) {
			return current_values.get(key);
		} else {
			return backup_value;
		}
	}
	
	public Object getPropertyDefault(String key) {
		return default_values.get(key);
	}
	
	public boolean propertyEquals(String key, Object test_value) {
		if (propertyExists(key)) {
			Object value = getProperty(key);
			if (value != null) {
				return value.equals(test_value);
			} else {
				if (test_value == null) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	public boolean propertyExists(String key) {
		return current_values.containsKey(key);
	}
	
	public boolean propertyDefaultExists(String key) {
		return default_values.containsKey(key);
	}
	
	public boolean propertyIsDefault(String key) {
		if (propertyDefaultExists(key) && propertyExists(key)) {
			return propertyEquals(key, getPropertyDefault(key));
		} else {
			return false;
		}
	}
	
	public Iterator getKeys() {
		return current_values.keySet().iterator();
	}
}