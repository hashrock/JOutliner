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
import java.util.Iterator;

/**
 * A container for property values. Manages both a current value and an optional
 * default value for each named property contained within the PropertyContainer.
 * 
 * Allows for PropertyFilterChains to be assigned to each named property. The
 * PropertyFilterChain will be executed whenever the property value is set.
 */
public interface PropertyContainer extends Cloneable, Serializable {
	
	// Property Removal
	/**
	 * Removes all properties and their default values and PropertyFilterChains.
	 */
	public void removeAllProperties();
	
	/**
	 * Removes the property and it's default value and PropertyFilterChain indicated 
	 * by the provided key.
	 */
	public void removeProperty(String key);
	
	// Property Resetting
	/**
	 * Resets all properties to thier default values.
	 */
	public void resetAllProperties();
	
	/**
	 * Resets the property indicated by the provided key to it's default value.
	 */
	public void resetProperty(String key);
	
	// Property Setting
	/**
	 * Sets the current value of the property indicated by the provided key to 
	 * the provided value.
	 */
	public void setProperty(String key, Object value);
	
	/**
	 * Sets the default value of the property indicated by the provided key to 
	 * the provided value.
	 */
	public void setPropertyDefault(String key, Object default_value);
	
	// PropertyFilterChain Setting
	/**
	 * Sets a PropertyFilterChain for the indicated key.
	 */
	public void setPropertyFilterChain(String key, PropertyFilterChain filter_chain);
	
	/**
	 * Gets the PropertyFilterChain associated with the provided key if it exists.
	 */
	public PropertyFilterChain getPropertyFilterChain(String key);
	
	/**
	 * Removes the PropertyFilterChain associated with the provided key if it exists.
	 */
	public PropertyFilterChain removePropertyFilterChain(String key, int index);
	
	/**
	 * A convience method that adds a PropertyFilter to the PropertyFilterChain
	 * associated with the provided key and instantiates a new PropertyFilterChain
	 * if none exists yet.
	 */
	public void addPropertyFilter(String key, PropertyFilter filter);
	
	// Property Getters
	/**
	 * Gets the property indicated by the provided key. If no current value exists
	 * then the default value is returned. If no default value exists then null is
	 * returned.
	 */
	public Object getProperty(String key);
	
	/**
	 * Gets the property indicated by the provided key. If no current value exists
	 * then the provided backup value is returned.
	 */
	public Object getProperty(String key, Object backup_value);
	
	/**
	 * Gets the default value of the property indicated by the provided key.
	 */
	public Object getPropertyDefault(String key);
	
	// Tests
	/**
	 * Tests if the property indicated by the provided key is equal to the
	 * provided test value. Note: a property that does not exists will always return
	 * false.
	 */
	public boolean propertyEquals(String key, Object test_value);
	
	/**
	 * Tests if a current value exists for the property indicated by the provided
	 * key. Note: a property having a null value still indicates that it exists.
	 */
	public boolean propertyExists(String key);
	
	/**
	 * Tests if a default value exists for the property indicated by the provided
	 * key. Note: a property having a null value still indicates that it exists.
	 */
	public boolean propertyDefaultExists(String key);
	
	/**
	 * Tests if the current value of the property indicated by the provided key
	 * is equal to the default value of the property. If either the current value
	 * or the default value do not exist then this returns false.
	 */
	public boolean propertyIsDefault(String key);
	
	// Miscellaneous
	/**
	 * Gets an Iterator for all the existing property keys.
	 */
	public Iterator getKeys();
}