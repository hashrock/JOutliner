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
 
package com.organic.maynard.outliner.scripting.script;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class ThreadsTableModel extends AbstractTableModel {
	
	// Pseudo Constants
	private static String TEXT_KILL = GUITreeLoader.reg.getText("kill");
	private static String TEXT_ID = GUITreeLoader.reg.getText("id");
	private static String TEXT_SCRIPT_NAME = GUITreeLoader.reg.getText("script_name");
	private static String TEXT_STARTED_BY = GUITreeLoader.reg.getText("started_by");
	private static String TEXT_START_TIME = GUITreeLoader.reg.getText("start_time");
	private static String TEXT_ERROR = GUITreeLoader.reg.getText("error");
	
	
	// Instance Fields
	private ArrayList threads = new ArrayList(); // Threads
	private ArrayList threadIDs = new ArrayList(); // Threads
	private ArrayList startedBy = new ArrayList(); // Threads
	private ArrayList startedAt = new ArrayList(); // Threads
	private int threadIDCount = 0;
	
	
	// Constructors
	public ThreadsTableModel() {}
	
	
	// Methods
	public int getSize() {
		return threads.size();
	}
	
	public int indexOf(String name) {
		for (int i = 0; i < threads.size(); i++) {
			Thread thread = get(i);
			if (thread.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOfThreadID(int threadID) {
		for (int i = 0; i < threadIDs.size(); i++) {
			Integer currentThreadID = getThreadID(i);
			if (currentThreadID.intValue() == threadID) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Getters
	public Thread get(int i) {
		return (Thread) threads.get(i);
	}
	
	public Integer getThreadID(int i) {
		return (Integer) threadIDs.get(i);
	}
	
	public Thread get(String name) {
		int i = indexOf(name);
		if (i != -1) {
			return get(i);
		} else {
			return null;
		}
	}
	
	// Add/Insert
	public int add(Thread thread, int startedBy, String startedAt) {
		int i = getSize();
		threads.add(i, thread);
		
		int threadID = ++threadIDCount;
		threadIDs.add(i, new Integer(threadID));
		
		String startedByString = null;
		switch(startedBy) {
			case ScriptsManagerModel.STARTUP_SCRIPT:
				startedByString = ScriptsManagerModel.STARTUP_SCRIPT_TEXT;
				break;
			case ScriptsManagerModel.SHUTDOWN_SCRIPT:
				startedByString = ScriptsManagerModel.SHUTDOWN_SCRIPT_TEXT;
				break;
			case ScriptsManagerModel.USER_SCRIPT:
				startedByString = ScriptsManagerModel.USER_SCRIPT_TEXT;
				break;
			default:
				startedByString = ScriptsManagerModel.UNKNOWN_SCRIPT_TEXT;
		}
		
		this.startedBy.add(i, startedByString);
		
		this.startedAt.add(i, startedAt);
		
		// Update the table
		fireTableRowsInserted(i, i);
		
		return threadID;
	}
	
	// Remove
	public void remove(int i) {
		threads.remove(i);
		threadIDs.remove(i);
		startedBy.remove(i);
		startedAt.remove(i);
		
		fireTableRowsDeleted(i, i);
	}
	
	public int remove(String name) {
		int i = indexOf(name);
		if (i != -1) {
			remove(i);
		}
		
		return i;
	}
	
	public int removeThread(int threadID) {
		int i = indexOfThreadID(threadID);
		if (i != -1) {
			remove(i);
		}
		
		return i;
	}
	
	public String getName(int i) {
		return get(i).getName();
	}
	
	public String getStartedBy(int i) {
		return (String) startedBy.get(i);
	}
	
	public String getStartedAt(int i) {
		return (String) startedAt.get(i);
	}
	
	
	// TableModel Interface
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return TEXT_ID;
		} else if (col == 2) {
			return TEXT_SCRIPT_NAME;
		} else if (col == 3) {
			return TEXT_STARTED_BY;
		} else if (col == 4) {
			return TEXT_START_TIME;
		} else {
			return TEXT_ERROR;
		}
	}
	
	
	public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
	}
  
	public int getColumnCount() {
		return 5;
	}
	
	public int getRowCount() {
		return getSize();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return TEXT_KILL;
		} else if (col == 1) {
			return getThreadID(row).toString();
		} else if (col == 2) {
			return getName(row);
		} else if (col == 3) {
			return getStartedBy(row);
		} else if (col == 4) {
			return getStartedAt(row);
		} else {
			return TEXT_ERROR;
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setValueAt(Object value, int row, int col) {
		// Nothing is editable.
	}
}
