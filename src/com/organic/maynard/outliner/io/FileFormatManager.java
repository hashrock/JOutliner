/**
 * Portions copyright (C) 2001, 2002 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
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

package com.organic.maynard.outliner.io;

import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2002/12/16 01:51:45 $
 */

public class FileFormatManager {
	
	// public class constants
	public static final String FORMAT_TYPE_OPEN = "open";
	public static final String FORMAT_TYPE_SAVE = "save";
	public static final String FORMAT_TYPE_IMPORT = "import";
	public static final String FORMAT_TYPE_EXPORT = "export";
	public static final String FORMAT_TYPE_OPEN_DEFAULT = "open_default";
	public static final String FORMAT_TYPE_SAVE_DEFAULT = "save_default";
	public static final String FORMAT_TYPE_IMPORT_DEFAULT = "import_default";
	public static final String FORMAT_TYPE_EXPORT_DEFAULT = "export_default";
	
	// private instance variables
	private ArrayList openers = new ArrayList();
	private ArrayList openerNames = new ArrayList();
	
	private ArrayList savers = new ArrayList();
	private ArrayList saverNames = new ArrayList();
	
	private ArrayList exporters = new ArrayList();
	private ArrayList exporterNames = new ArrayList();
	
	private ArrayList importers = new ArrayList();
	private ArrayList importerNames = new ArrayList();
	
	private OpenFileFormat defaultOpenFileFormat = null;
	private SaveFileFormat defaultSaveFileFormat = null;
	private OpenFileFormat defaultImportFileFormat = null;
	private SaveFileFormat defaultExportFileFormat = null;
	
	
	// The Constructor
	public FileFormatManager() {}
	
	/** create a new file format object of a particular flavor */
	public void createFileFormat(String formatType, String formatName, String className, Vector extensions) {
		try {
			Class theClass = Class.forName(className);
			
			if (formatType.equals(FORMAT_TYPE_OPEN)) {
				OpenFileFormat openFileFormat = (OpenFileFormat) theClass.newInstance();
				setExtensions(openFileFormat, extensions);
				boolean success = addOpenFormat(formatName, openFileFormat);
				addImportFormat(formatName, openFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_SAVE)) {
				SaveFileFormat saveFileFormat = (SaveFileFormat) theClass.newInstance();
				setExtensions(saveFileFormat, extensions) ;
				boolean success = addSaveFormat(formatName, saveFileFormat);
				addExportFormat(formatName, saveFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_IMPORT)) {
				ImportFileFormat importFileFormat = (ImportFileFormat) theClass.newInstance();
				setExtensions(importFileFormat, extensions) ;
				boolean success = addImportFormat(formatName, importFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_EXPORT)) {
				ExportFileFormat exportFileFormat = (ExportFileFormat) theClass.newInstance();
				setExtensions(exportFileFormat, extensions) ;
				boolean success = addExportFormat(formatName, exportFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_OPEN_DEFAULT)) {
				OpenFileFormat defOpenFileFormat = (OpenFileFormat) theClass.newInstance();
				setExtensions(defOpenFileFormat, extensions);
				setDefaultOpenFileFormat(defOpenFileFormat);
				boolean success = addOpenFormat(formatName, defOpenFileFormat);
				addImportFormat(formatName, defOpenFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_SAVE_DEFAULT)) {
				SaveFileFormat defSaveFileFormat = (SaveFileFormat) theClass.newInstance();
				setExtensions(defSaveFileFormat, extensions);
				setDefaultSaveFileFormat(defSaveFileFormat);
				boolean success = addSaveFormat(formatName, defSaveFileFormat);
				addExportFormat(formatName, defSaveFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_IMPORT_DEFAULT)) {
				OpenFileFormat defImportFileFormat = (OpenFileFormat) theClass.newInstance();
				setExtensions(defImportFileFormat, extensions);
				setDefaultImportFileFormat(defImportFileFormat);
				boolean success = addImportFormat(formatName, defImportFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			} else if (formatType.equals(FORMAT_TYPE_EXPORT_DEFAULT)) {
				SaveFileFormat exportFileFormat = (ExportFileFormat) theClass.newInstance();
				setExtensions(exportFileFormat, extensions);
				setDefaultExportFileFormat(exportFileFormat);
				boolean success = addExportFormat(formatName, exportFileFormat);
				addExportFormat(formatName, exportFileFormat);
				
				if (!success) {
					System.out.println("  Duplicate File Format Name: " + formatName);
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** set a format's set of filename extensions */
	private void setExtensions(FileFormat format, Vector extensions) {
		if (extensions == null) {
			return;
		}
		
		for (int i = 0, limit = extensions.size(); i < limit; i++) {
			// the first extension in the set is the default
			if (i == 0) {
				format.addExtension(((String) extensions.get(i)).toLowerCase(), true);
			} else {
				format.addExtension(((String) extensions.get(i)).toLowerCase(), false);
			}
		}
	}
	
	
	//---------------------- Open Accessors --------------------------
	public String getOpenFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = openers.size(); i < limit; i++) {
			OpenFileFormat format = (OpenFileFormat) openers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
				return (String) openerNames.get(i);
			}
		}
		
		int index = openers.indexOf(getDefaultOpenFileFormat());
		return (String) openerNames.get(index);
	}
	
	public OpenFileFormat getDefaultOpenFileFormat() {
		return defaultOpenFileFormat;
	}
	
	public void setDefaultOpenFileFormat(OpenFileFormat defaultOpenFileFormat) {
		this.defaultOpenFileFormat = defaultOpenFileFormat;
	}
	
	public boolean addOpenFormat(String formatName, OpenFileFormat format) {
		if (isNameUnique(formatName, openerNames)) {
			format.setName(formatName);
			openerNames.add(formatName);
			openers.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_OPEN.add(formatName);
			
			return true;
		}
		return false;
	}
	
	public OpenFileFormat getOpenFormat(String formatName) {
		int index = indexOfName(formatName, openerNames);
		if (index >= 0) {
			return (OpenFileFormat) openers.get(index);
		}
		return null;
	}
	
	public boolean removeOpenFormat(String formatName) {
		int index = indexOfName(formatName, openerNames);
		if (index >= 0) {
			openerNames.remove(index);
			openers.remove(index);
			
			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_OPEN.remove(index);
			
			return true;
		}
		return false;
	}
	
	
	//------------------------- Save Accessors ---------------------------
	public String getSaveFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = savers.size(); i < limit; i++) {
			SaveFileFormat format = (SaveFileFormat) savers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
				return (String) saverNames.get(i);
			}
		}
		
		int index = savers.indexOf(getDefaultSaveFileFormat());
		return (String) saverNames.get(index);
	}
	
	public SaveFileFormat getDefaultSaveFileFormat() {
		return defaultSaveFileFormat;
	}
	
	public void setDefaultSaveFileFormat(SaveFileFormat defaultSaveFileFormat) {
		this.defaultSaveFileFormat = defaultSaveFileFormat;
	}
	
	public boolean addSaveFormat(String formatName, SaveFileFormat format) {
		if (isNameUnique(formatName, saverNames)) {
			format.setName(formatName);
			saverNames.add(formatName);
			savers.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_SAVE.add(formatName);
			
			return true;
		}
		return false;
	}
	
	public SaveFileFormat getSaveFormat(String formatName) {
		int index = indexOfName(formatName, saverNames);
		if (index >= 0) {
			return (SaveFileFormat) savers.get(index);
		}
		return null;
	}
	
	public boolean removeSaveFormat(String formatName) {
		int index = indexOfName(formatName, saverNames);
		if (index >= 0) {
			saverNames.remove(index);
			savers.remove(index);
			
			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_SAVE.remove(index);
			
			return true;
		}
		return false;
	}
	
	
	//====================   import methods   ====================
	public String getImportFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = importers.size(); i < limit; i++) {
			OpenFileFormat format = (OpenFileFormat) importers.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
				return (String) importerNames.get(i);
			}
		}
		
		int index = importers.indexOf(getDefaultImportFileFormat());
		
		if (index == -1) {
			index = importers.indexOf(getDefaultOpenFileFormat());
		}
		return (String) importerNames.get(index);
	}
	
	public OpenFileFormat getDefaultImportFileFormat() {
		return defaultImportFileFormat;
	}
	
	
	/** add an import format to the set of such  [srk] 12/31/01 11:01PM */
	private boolean addImportFormat(String formatName, OpenFileFormat format) {
		if (isNameUnique(formatName, importerNames)) {
			format.setName(formatName);
			importerNames.add(formatName);
			importers.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_IMPORT.add(formatName);
			
			return true;
		}
		return false;
	}
	
	/** get the import format */
	public OpenFileFormat getImportFormat(String formatName) {
		int index = indexOfName(formatName, importerNames);
		if (index >= 0) {
			return (OpenFileFormat) importers.get(index);
		}
		return null;
	}
	
	/** set up a default import format  [srk] 12/31/01 11:09PM */
	private void setDefaultImportFileFormat(OpenFileFormat aFileFormat) {
		defaultImportFileFormat = aFileFormat;
	}
	
	public boolean removeImportFormat(String formatName) {
		int index = indexOfName(formatName, importerNames);
		if (index >= 0) {
			importerNames.remove(index);
			importers.remove(index);
			
			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_IMPORT.remove(index);
			
			return true;
		}
		return false;
	}
	
	
	//====================   export methods   ====================
	public String getExportFileFormatNameForExtension(String extension) {
		String lowerCaseExtension = extension.toLowerCase();
		for (int i = 0, limit = exporters.size(); i < limit; i++) {
			SaveFileFormat format = (SaveFileFormat) exporters.get(i);
			if (format.extensionExists(lowerCaseExtension)) {
				return (String) exporterNames.get(i);
			}
		}
		
		int index = exporters.indexOf(getDefaultExportFileFormat());
		
		if (index == -1) {
			index = exporters.indexOf(getDefaultSaveFileFormat());
		}
		return (String) exporterNames.get(index);
	}
	
	public SaveFileFormat getDefaultExportFileFormat() {
		return defaultExportFileFormat;
	}
	
	public boolean addExportFormat(String formatName, SaveFileFormat format) {
		if (isNameUnique(formatName, exporterNames)) {
			format.setName(formatName);
			exporterNames.add(formatName);
			exporters.add(format);
			
			// Also add it to the list of formats stored in the preferences
			Preferences.FILE_FORMATS_EXPORT.add(formatName);
			
			return true;
		}
		return false;
	}
	
	public SaveFileFormat getExportFormat(String formatName) {
		int index = indexOfName(formatName, exporterNames);
		if (index >= 0) {
			return (SaveFileFormat) exporters.get(index);
		}
		return null;
	}
	
	public boolean removeExportFormat(String formatName) {
		int index = indexOfName(formatName, exporterNames);
		if (index >= 0) {
			exporterNames.remove(index);
			exporters.remove(index);
			
			// Also remove it from the list of formats stored in the preferences
			Preferences.FILE_FORMATS_EXPORT.remove(index);
			
			return true;
		}
		return false;
	}
	
	/** set up a default export format  [srk] 12/31/01 11:12 PM */
	private void setDefaultExportFileFormat(SaveFileFormat aFileFormat) {
		defaultExportFileFormat = aFileFormat;
	}
	
	
	//====================   utility methods   ====================
	/** determine whether a name is not yet a member of a vector */
	private static boolean isNameUnique(String name, ArrayList list) {
		for (int i = 0, limit = list.size(); i < limit; i++) {
			if (name.equals(list.get(i).toString())) {
				return false;
			}
		}
		return true;
	}
	
	/** determine a name's position within a vector */
	private static int indexOfName(String name, ArrayList list) {
		for (int i = 0, limit = list.size(); i < limit; i++) {
			if (name.equals(list.get(i).toString())) {
				return i;
			}
		}
		return -1;
	}
}
