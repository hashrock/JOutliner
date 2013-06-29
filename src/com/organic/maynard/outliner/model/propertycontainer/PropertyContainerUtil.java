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
import java.lang.reflect.*;
import com.organic.maynard.xml.XMLTools;
import com.organic.maynard.xml.XMLProcessor;
import org.xml.sax.*;

/**
 * Holds static methods useful for working with PropertyContainer objects.
 */
public class PropertyContainerUtil {
	
	// XML Serialization
	private static final String ELEMENT_PROPERTY_CONTAINERS = "property_containers";
	private static final String ELEMENT_PROPERTY_CONTAINER = "property_container";
	private static final String ELEMENT_PROPERTY = "property";
	private static final String ATTRIBUTE_CLASS = "class";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_VALUE = "value";
	
	public static List parseXML(String filepath) {
		PropertyContainerXMLParser parser = new PropertyContainerXMLParser();
		try {
			parser.process(filepath);
			return parser.getPropertyContainerList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeXML(
		StringBuffer buf, 
		List containers, 
		int depth, 
		String line_ending
	) {
		if (containers != null) {
			HashMap attributes = new HashMap();
			attributes.put(ATTRIBUTE_CLASS, containers.getClass().getName());
			XMLTools.writeElementStart(buf, depth, false, line_ending, ELEMENT_PROPERTY_CONTAINERS, attributes);
			for (int i = 0; i < containers.size(); i++) {
				PropertyContainer container = (PropertyContainer) containers.get(i);
				PropertyContainerUtil.writeXML(buf, container, depth + 1, line_ending);
			}
			XMLTools.writeElementEnd(buf, depth, line_ending, ELEMENT_PROPERTY_CONTAINERS);
		}
	}
	
	public static void writeXML(
		StringBuffer buf, 
		PropertyContainer container, 
		int depth, 
		String line_ending
	) {
		if (container != null) {
			HashMap attributes = new HashMap();
			attributes.put(ATTRIBUTE_CLASS, container.getClass().getName());
			XMLTools.writeElementStart(buf, depth, false, line_ending, ELEMENT_PROPERTY_CONTAINER, attributes);
			Iterator it = container.getKeys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object property = container.getProperty(key);
				attributes = new HashMap();
				attributes.put(ATTRIBUTE_NAME, key);
				if (property != null) {
					attributes.put(ATTRIBUTE_CLASS, property.getClass().getName());
					attributes.put(ATTRIBUTE_VALUE, property.toString());
				}
				XMLTools.writeElementStart(buf, depth + 1, true, line_ending, ELEMENT_PROPERTY, attributes);
			}
			XMLTools.writeElementEnd(buf, depth, line_ending, ELEMENT_PROPERTY_CONTAINER);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	// String Typed
	public static void setPropertyAsString(PropertyContainer container, String key, String value) {
		if (container != null) {
			container.setProperty(key, value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static void setPropertyDefaultAsString(PropertyContainer container, String key, String default_value) {
		if (container != null) {
			container.setPropertyDefault(key, default_value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static String getPropertyAsString(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getProperty(key);
			return convertObjectToString(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static String getPropertyAsString(PropertyContainer container, String key, Object backup_value) {
		if (container != null) {
			Object value = container.getProperty(key, backup_value);
			return convertObjectToString(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static String getPropertyDefaultAsString(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getPropertyDefault(key);
			return convertObjectToString(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean propertyEqualsAsString(PropertyContainer container, String key, String test_value) {
		if (container != null) {
			return container.propertyEquals(key, test_value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static String convertObjectToString(Object value) {
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}
	
	// boolean typed
	public static void setPropertyAsBoolean(PropertyContainer container, String key, boolean value) {
		if (container != null) {
			container.setProperty(key, new Boolean(value));
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static void setPropertyDefaultAsBoolean(PropertyContainer container, String key, boolean default_value) {
		if (container != null) {
			container.setPropertyDefault(key, new Boolean(default_value));
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean getPropertyAsBoolean(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getProperty(key);
			return convertObjectToBoolean(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean getPropertyAsBoolean(PropertyContainer container, String key, boolean backup_value) {
		if (container != null) {
			Object value = container.getProperty(key, new Boolean(backup_value));
			return convertObjectToBoolean(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean getPropertyDefaultAsBoolean(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getPropertyDefault(key);
			return convertObjectToBoolean(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean propertyEqualsAsBoolean(PropertyContainer container, String key, boolean test_value) {
		if (container != null) {
			try {
				boolean value = getPropertyAsBoolean(container, key);
				return value == test_value;
			} catch (NullPointerException npe) {
				return false;
			}
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if the provided value is null.
	 */
	public static boolean convertObjectToBoolean(Object value) throws IllegalArgumentException {
		if (value != null) {
			if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue();
			} else {
				return Boolean.valueOf(value.toString()).booleanValue();
			}
		} else {
			throw new IllegalArgumentException("Property value was null so it couldn't be converted to a boolean.");
		}
	}
	
	// int typed
	public static void setPropertyAsInt(PropertyContainer container, String key, int value) {
		if (container != null) {
			container.setProperty(key, new Integer(value));
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static void setPropertyDefaultAsInt(PropertyContainer container, String key, int default_value) {
		if (container != null) {
			container.setPropertyDefault(key, new Integer(default_value));
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static int getPropertyAsInt(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getProperty(key);
			return convertObjectToInt(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static int getPropertyAsInt(PropertyContainer container, String key, int backup_value) {
		if (container != null) {
			Object value = container.getProperty(key, new Integer(backup_value));
			return convertObjectToInt(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static int getPropertyDefaultAsInt(PropertyContainer container, String key) {
		if (container != null) {
			Object value = container.getPropertyDefault(key);
			return convertObjectToInt(value);
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static boolean propertyEqualsAsInt(PropertyContainer container, String key, int test_value) {
		if (container != null) {
			try {
				int value = getPropertyAsInt(container, key);
				return value == test_value;
			} catch (NullPointerException npe) {
				return false;
			}
		} else {
			throw new IllegalArgumentException("Provided PropertyContainer was null.");
		}
	}
	
	public static int convertObjectToInt(Object value) {
		if (value != null) {
			if (value instanceof Integer) {
				return ((Integer) value).intValue();
			} else {
				return Integer.valueOf(value.toString()).intValue();
			}
		} else {
			throw new NullPointerException("Property value was null so it couldn't be converted to an int.");
		}
	}
	
	
	// InnerClasses
	private static class PropertyContainerXMLParser extends XMLProcessor {
		// Instance Fields
		private List containers;
		private PropertyContainer container;
		
		
		// Constructors
		public PropertyContainerXMLParser() {
			super();
			init();
		}
		
		private void init() {
			containers = null;
			container = null;
		}
		
		
		// Accessors
		public List getPropertyContainerList() {
			return this.containers;
		}
		
		
		// Sax DocumentHandler Implementation
		public void startDocument () {}
		
		public void endDocument () {}
		
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
			if (ELEMENT_PROPERTY.equals(qName)) {
				try {
					String class_name = atts.getValue(ATTRIBUTE_CLASS);
					String property_name = atts.getValue(ATTRIBUTE_NAME);
					String property_value = atts.getValue(ATTRIBUTE_VALUE);
					
					Class[] parameterTypes = {(new String("")).getClass()};
					Constructor constructor = Class.forName(class_name).getConstructor(parameterTypes);
					Object[] args = {property_value};
					Object property = constructor.newInstance(args);
					
					if (container != null) {
						container.setProperty(property_name, property);
					} else {
						System.out.println("Warning: no PropertyContainer to add property to during PropertyContainerXMLParser parsing.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (ELEMENT_PROPERTY_CONTAINER.equals(qName)) {
				try {
					String class_name = atts.getValue(ATTRIBUTE_CLASS);
					container = (PropertyContainer) Class.forName(class_name).newInstance();
					
					// Instantiate a List if we don't have one by now.
					if (containers == null) {
						containers = new ArrayList();
					}
					containers.add(container);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (ELEMENT_PROPERTY_CONTAINERS.equals(qName)) {
				try {
					String class_name = atts.getValue(ATTRIBUTE_CLASS);
					containers = (List) Class.forName(class_name).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			super.startElement(namespaceURI, localName, qName, atts);
		}
		
		public void endElement(String namespaceURI, String localName, String qName) {
			super.endElement(namespaceURI, localName, qName);
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			super.characters(ch, start, length);
		}
	}
}
