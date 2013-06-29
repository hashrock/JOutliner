/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner.model;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.model.propertycontainer.*;

import com.organic.maynard.data.IntList;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.util.string.StringTools;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.awt.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2004/01/30 00:12:43 $
 */

public class DocumentInfo extends PropertyContainerImpl implements Serializable {
	
	// Constants
	private static final String EXPANDED_NODE_SEPERATOR = ",";
	
	// Keys used by the PropertyContainer
	public static final String KEY_FILE_FORMAT = "file_format";
	public static final String KEY_IMPORTED = "imported";
	public static final String KEY_ENCODING_TYPE = "encoding_type";
	public static final String KEY_LINE_ENDING = "line_ending";
	public static final String KEY_PADDING = "padding";
	public static final String KEY_PATH = "path";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DATE_CREATED = "date_created";
	public static final String KEY_DATE_MODIFIED = "date_modified";
	public static final String KEY_OWNER_NAME = "owner_name";
	public static final String KEY_OWNER_EMAIL = "owner_email";
	public static final String KEY_VERTICAL_SCROLL_STATE = "vertical_scroll_state";
	public static final String KEY_WINDOW_TOP = "window_top";
	public static final String KEY_WINDOW_LEFT = "window_left";
	public static final String KEY_WINDOW_BOTTOM = "window_bottom";
	public static final String KEY_WINDOW_RIGHT = "window_right";
	public static final String KEY_EXPANDED_NODES = "expanded_nodes";
	public static final String KEY_APPLY_FONT_STYLE_FOR_COMMENTS = "apply_font_style_for_comments";
	public static final String KEY_APPLY_FONT_STYLE_FOR_EDITABILITY = "apply_font_style_for_editability";
	public static final String KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY = "apply_font_style_for_moveability";
	public static final String KEY_USE_CREATE_MOD_DATES = "user_create_mod_dates";
	public static final String KEY_CREATE_MOD_DATES_FORMAT = "create_mod_dates_format";
	public static final String KEY_PROTOCOL_NAME = "protocol_name";
	public static final String KEY_HELP_FILE = "help_file";
	
	
	// Instance Fields
	private transient byte[] bytes;
	private transient InputStream stream = null;
	
	
	// The Constructors
	public DocumentInfo() {
		this(
			"",
			false,
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			1,
			OutlinerDocument.INITIAL_Y,
			OutlinerDocument.INITIAL_X,
			OutlinerDocument.INITIAL_Y + OutlinerDocument.INITIAL_HEIGHT,
			OutlinerDocument.INITIAL_X + OutlinerDocument.INITIAL_WIDTH,
			"",
			Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur,
			Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur,
			Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur,
			Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES).cur,
			Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT).cur,
			"",
			false
		);
	}
	
	public DocumentInfo(
		String file_format,
		boolean imported,
		String encoding_type,
		String line_ending,
		String padding,
		String path, 
		String title, 
		String date_created,
		String date_modified,
		String owner_name,
		String owner_email,
		int vertical_scroll_state,
		int window_top,
		int window_left,
		int window_bottom,
		int window_right,
		String expanded_nodes_string,
		boolean apply_font_style_for_comments,
		boolean apply_font_style_for_editability,
		boolean apply_font_style_for_moveability,
		boolean use_create_mod_dates,
		String create_mod_dates_format,
		String protocol_name,
		boolean help_file
		)
	{
		// Assign Filters
		addPropertyFilter(
			KEY_VERTICAL_SCROLL_STATE, 
			new PropertyFilterImpl(KEY_VERTICAL_SCROLL_STATE) {
				public Object filter(PropertyContainer container, Object value) {
					int vertical_scroll_state = PropertyContainerUtil.convertObjectToInt(value);
					if (vertical_scroll_state >= 1) {
						return new Integer(vertical_scroll_state);
					} else {
						return new Integer(1);
					}
				}
			}
		);
		
		addPropertyFilter(
			KEY_WINDOW_TOP, 
			new PropertyFilterImpl(KEY_WINDOW_TOP) {
				public Object filter(PropertyContainer container, Object value) {
					int window_top = PropertyContainerUtil.convertObjectToInt(value);
					if ((window_top >= 0) && (window_top <= 10000)) {
						return new Integer(window_top);
					} else {
						return new Integer(0);
					}
				}
			}
		);
		
		addPropertyFilter(
			KEY_WINDOW_LEFT, 
			new PropertyFilterImpl(KEY_WINDOW_LEFT) {
				public Object filter(PropertyContainer container, Object value) {
					int window_left = PropertyContainerUtil.convertObjectToInt(value);
					if ((window_left >= 0) && (window_left <= 10000)) {
						return new Integer(window_left);
					} else {
						return new Integer(0);
					}
				}
			}
		);
		
		addPropertyFilter(
			KEY_WINDOW_BOTTOM, 
			new PropertyFilterImpl(KEY_WINDOW_BOTTOM) {
				public Object filter(PropertyContainer container, Object value) {
					int window_bottom = PropertyContainerUtil.convertObjectToInt(value);
					int window_top = PropertyContainerUtil.getPropertyAsInt(container, KEY_WINDOW_TOP);
					if ((window_bottom - window_top >= OutlinerDocument.MIN_HEIGHT) && (window_bottom <= 10000 + OutlinerDocument.INITIAL_HEIGHT)) {
						return new Integer(window_bottom);
					} else {
						return new Integer(window_top + OutlinerDocument.INITIAL_HEIGHT);
					}
				}
			}
		);
		
		addPropertyFilter(
			KEY_WINDOW_RIGHT, 
			new PropertyFilterImpl(KEY_WINDOW_RIGHT) {
				public Object filter(PropertyContainer container, Object value) {
					int window_right = PropertyContainerUtil.convertObjectToInt(value);
					int window_left = PropertyContainerUtil.getPropertyAsInt(container, KEY_WINDOW_LEFT);
					if ((window_right - window_left >= OutlinerDocument.MIN_WIDTH) && (window_right <= 10000 + OutlinerDocument.INITIAL_WIDTH)) {
						return new Integer(window_right);
					} else {
						return new Integer(window_left + OutlinerDocument.INITIAL_WIDTH);
					}
				}
			}
		);
		
		// Set Values
		PropertyContainerUtil.setPropertyAsString(this, KEY_FILE_FORMAT, file_format);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_IMPORTED, imported);
		PropertyContainerUtil.setPropertyAsString(this, KEY_ENCODING_TYPE, encoding_type);
		PropertyContainerUtil.setPropertyAsString(this, KEY_LINE_ENDING, line_ending);
		PropertyContainerUtil.setPropertyAsString(this, KEY_PADDING, padding);
		PropertyContainerUtil.setPropertyAsString(this, KEY_PATH, path);
		PropertyContainerUtil.setPropertyAsString(this, KEY_TITLE, title);
		PropertyContainerUtil.setPropertyAsString(this, KEY_DATE_CREATED, date_created);
		PropertyContainerUtil.setPropertyAsString(this, KEY_DATE_MODIFIED, date_modified);
		PropertyContainerUtil.setPropertyAsString(this, KEY_OWNER_NAME, owner_name);
		PropertyContainerUtil.setPropertyAsString(this, KEY_OWNER_EMAIL, owner_email);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_VERTICAL_SCROLL_STATE, vertical_scroll_state);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_TOP, window_top);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_LEFT, window_left);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_BOTTOM, window_bottom);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_RIGHT, window_right);
		setProperty(KEY_EXPANDED_NODES, new IntList(expanded_nodes_string, EXPANDED_NODE_SEPERATOR));
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_COMMENTS, apply_font_style_for_comments);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_EDITABILITY, apply_font_style_for_editability);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY, apply_font_style_for_moveability);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_USE_CREATE_MOD_DATES, use_create_mod_dates);
		PropertyContainerUtil.setPropertyAsString(this, KEY_CREATE_MOD_DATES_FORMAT, create_mod_dates_format);
		PropertyContainerUtil.setPropertyAsString(this, KEY_PROTOCOL_NAME, protocol_name);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_HELP_FILE, help_file);
	}
	
	// This is used with Save As operations
	public Object clone() throws CloneNotSupportedException  {
		return super.clone();
	}
	
	
	// IO Data Accessors
	public byte[] getOutputBytes() {
		return this.bytes;
	}
	
	public void setOutputBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public InputStream getInputStream() {
		return this.stream;
	}
	
	public void setInputStream(InputStream stream) {
		this.stream = stream;
	}
	
	// Testing
	public void setProperty(String key, Object value) {
		//System.out.println(key + ":" + value.toString());
		super.setProperty(key, value);
	}
	
	
	// Additional Accessors
	public int getWidth() {
		return PropertyContainerUtil.getPropertyAsInt(this, KEY_WINDOW_RIGHT) - PropertyContainerUtil.getPropertyAsInt(this, KEY_WINDOW_LEFT);
	}
	
	public int getHeight() {
		return PropertyContainerUtil.getPropertyAsInt(this, KEY_WINDOW_BOTTOM) - PropertyContainerUtil.getPropertyAsInt(this, KEY_WINDOW_TOP);
	}
	
	
	// Expanded Nodes
	public IntList getExpandedNodes() {
		return (IntList) getProperty(KEY_EXPANDED_NODES);
	}
	
	private void addExpandedNodeNumber(
		IntList expanded_nodes, 
		int node_number, 
		int shift
	) {
		node_number += shift;
		
		int last_int_on_list = -1;
		try {
			last_int_on_list = expanded_nodes.get(expanded_nodes.size() - 1);
		} catch (IndexOutOfBoundsException iobe) {
			// Do Nothing
		}
		
		if (node_number > last_int_on_list) {
			expanded_nodes.add(node_number);
		} else {
			System.out.println(
				"Warning: could not add expanded node to list since it was provided in " +
				"the wrong order. Last int was: " + last_int_on_list + " Provided int was: " + 
				node_number + " which should have been larger."
			);
		}
	}
	
	public void setExpandedNodesStringShifted(String node_list_string, int shift) {
		IntList expanded_nodes = getExpandedNodes();
		expanded_nodes.clear();
		
		StringTokenizer tok = new StringTokenizer(node_list_string, EXPANDED_NODE_SEPERATOR);
		while (tok.hasMoreTokens()) {
			try {
				addExpandedNodeNumber(expanded_nodes, Integer.parseInt(tok.nextToken()), shift);
			} catch (NumberFormatException nfe) {
				System.out.println("Warning: invalid format for int when parsing expanded node list.");
			}
		}
	}
	
	public String getExpandedNodesStringShifted(int shift) {
		IntList expanded_nodes = getExpandedNodes();
		
		StringBuffer buf = new StringBuffer();
		for (int i = 0, limit = expanded_nodes.size(); i < limit; i++) {
			if (i != 0) {
				buf.append(EXPANDED_NODE_SEPERATOR);
			}
			buf.append(expanded_nodes.get(i) + shift);
		}
		
		return buf.toString();
	}
	
	
	// Utility Methods
	public void updateDocumentInfoForDocument(OutlinerDocument document, boolean saveAs) {
		// Date created is a special hidden document setting that should always be up 
		// to date if we are dealing with a file that has been opened or previously saved.
		String currentDateString = getCurrentDateTimeString();
		if (saveAs) {
			document.settings.setDateCreated(currentDateString);
		}
		document.settings.setDateModified(currentDateString);
		
		// Set Properties
		PropertyContainerUtil.setPropertyAsString(this, KEY_PATH, document.getFileName());
		PropertyContainerUtil.setPropertyAsString(this, KEY_ENCODING_TYPE, document.settings.getSaveEncoding().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_LINE_ENDING, document.settings.getLineEnd().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_FILE_FORMAT, document.settings.getSaveFormat().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_OWNER_NAME, document.settings.getOwnerName().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_OWNER_EMAIL, document.settings.getOwnerEmail().cur);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_COMMENTS, document.settings.getApplyFontStyleForComments().cur);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_EDITABILITY, document.settings.getApplyFontStyleForEditability().cur);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY, document.settings.getApplyFontStyleForMoveability().cur);
		PropertyContainerUtil.setPropertyAsBoolean(this, KEY_USE_CREATE_MOD_DATES, document.settings.getUseCreateModDates().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_CREATE_MOD_DATES_FORMAT, document.settings.getCreateModDatesFormat().cur);
		PropertyContainerUtil.setPropertyAsString(this, KEY_DATE_MODIFIED, document.settings.getDateModified());
		PropertyContainerUtil.setPropertyAsString(this, KEY_DATE_CREATED, document.settings.getDateCreated());
		
		recordWindowPositioning(document);
	}
	
	public void recordWindowPositioning(OutlinerDocument document) {
		Rectangle r = document.getNormalBounds();
		
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_TOP, r.y);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_LEFT, r.x);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_BOTTOM, r.y + r.height);
		PropertyContainerUtil.setPropertyAsInt(this, KEY_WINDOW_RIGHT, r.x + r.width);
		
		JoeNodeList nodes = document.tree.getVisibleNodes();
		
		PropertyContainerUtil.setPropertyAsInt(this, KEY_VERTICAL_SCROLL_STATE, nodes.indexOf(document.panel.layout.getNodeToDrawFrom()) + 1);
		
		IntList expanded_nodes = getExpandedNodes();
		expanded_nodes.clear();
		for (int i = 0, limit = nodes.size(); i < limit; i++) {
			Node node = nodes.get(i);
			if (node.isExpanded()) {
				addExpandedNodeNumber(expanded_nodes, i, 0);
			}
		}
	}
	
	
	// Static Methods
	public static String getCurrentDateTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");
		if (!Preferences.getPreferenceString(Preferences.TIME_ZONE_FOR_SAVING_DATES).cur.equals("")) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(Preferences.getPreferenceString(Preferences.TIME_ZONE_FOR_SAVING_DATES).cur));
		}
		return dateFormat.format(new Date());
	}
}