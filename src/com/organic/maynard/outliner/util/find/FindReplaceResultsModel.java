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

package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.*;

import com.organic.maynard.outliner.dom.*;

import javax.swing.table.*;
import java.util.*;
import java.io.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2002/12/22 03:39:29 $
 */

public class FindReplaceResultsModel extends AbstractTableModel {
	
	// Constants
	
	
	// Fields
	private ArrayList results = new ArrayList();
	
	private FindReplaceResultsDialog view = null;
	
	
	// Constructors
	public FindReplaceResultsModel() {}
	
	public void setView(FindReplaceResultsDialog view) {
		this.view = view;
	}
	
	
	// Accessors
	public int size() {
		return results.size();
	}
	
	public void addResult(FindReplaceResult result) {
		results.add(result);
	}
	
	public void insertResult(int index, FindReplaceResult result) {
		results.add(index, result);
	}
	
	public void removeResult(int index) {
		results.remove(index);
	}
	
	public void removeAllResultsForDocument(Document doc) {
		for (int i = results.size() - 1; i >= 0; i--) {
			if (doc == getResult(i).getDocument()) {
				removeResult(i);
			}
		}
		
		fireTableDataChanged();
	}
	
	public FindReplaceResult getResult(int index) {
		return (FindReplaceResult) results.get(index);
	}
	
	public void clear() {
		results.clear();
	}
	
	
	// TableModel Interface
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
		view.updateTotalMatches();
	}
	
	public int getRowCount() {
		return size();
	}
	
	public int getColumnCount() {
		return 5;
	}
	
	public Object getValueAt(int row, int col) {
		FindReplaceResult result = getResult(row);
		
		if (col == 0) {
			int type = result.getType();
			if (type == FindReplaceResult.TYPE_DOC) {
				return result.getDocument().getTitle();
			} else if (type == FindReplaceResult.TYPE_FILE) {
				return result.getFile().getPath();
			} else {
				return "UNKNOWN";
			}
		} else if (col == 1) {
			return new Integer(result.getLine());
		} else if (col == 2) {
			return new Integer(result.getStart());
		} else if (col == 3) {
			return result.getMatch();
		} else if (col == 4) {
			return result.getReplacement();
		} else {
			return null;
		}
	}
	
	public String getColumnName(int col) {
		if (col == 0) {
			return "Document";
		} else if (col == 1) {
			return "Line";
		} else if (col == 2) {
			return "Col.";
		} else if (col == 3) {
			return "Match";
		} else if (col == 4) {
			return "Replacement";
		} else {
			return "";
		}
	}
	
    public boolean isCellEditable(int row, int col) {
		return false;
	}
}
