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

package com.organic.maynard.outliner.io.protocols;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.menus.file.*;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import com.organic.maynard.util.string.Replace;

/**
 * Protocol for reading and writing files from the local file system.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.7 $, $Date: 2004/03/22 04:48:03 $
 */

public class LocalFileSystemFileProtocol extends AbstractFileProtocol {
	
	// Instance Fields
	private OutlinerFileChooser chooser = null;
	private boolean isInitialized = false;
	
	
	// Constructors
	public LocalFileSystemFileProtocol() {}
	
	private void lazyInstantiation() {
		if (isInitialized) {
			return;
		}
		
		chooser = new OutlinerFileChooser(null);
		
		isInitialized = true;
	}
	
	
	// select a file to save or export
	public boolean selectFileToSave(OutlinerDocument document, int type) {
		// we'll customize the approve button
		// [srk] this is done here, rather than in configureForOpen/Import, to workaround a bug
		String approveButtonText = null ;
		
		// make sure we're all set up
		lazyInstantiation();
		
		// Setup the File Chooser to save or export
		switch (type) {
			case FileProtocol.SAVE:
				chooser.configureForSave(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).cur);
				approveButtonText = GUITreeLoader.reg.getText("save");
				break;
				
			case FileProtocol.EXPORT:
				chooser.configureForExport(document, getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).cur);
				approveButtonText = GUITreeLoader.reg.getText("export");
				break;
				
			default:
				System.out.println("ERROR: invalid save/export type used. (" + type +")");
				return false;
		}
		
		// run the File Chooser
		int option = chooser.showDialog(Outliner.outliner, approveButtonText);
		
		// Update the most recent save dir preference
		// TBD [srk] set up a MOST_RECENT_EXPORT_DIR, then have this code act appropriately
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).cur = chooser.getCurrentDirectory().getPath();
		Preferences.getPreferenceString(Preferences.MOST_RECENT_SAVE_DIR).restoreTemporaryToCurrent();
		
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			
			String lineEnd;
			String encoding;
			String fileFormat;
			
			if (!Outliner.documents.isFileNameUnique(filename) && (!filename.equals(document.getFileName()))) {
				String msg = GUITreeLoader.reg.getText("message_cannot_save_file_already_open");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);
				
				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				// We might want to move this test into the approveSelection method of the file chooser.
				return false;
			}
			
			// Pull Preference Values from the file chooser
			switch (type) {
				case FileProtocol.SAVE:
					lineEnd = chooser.getSaveLineEnding();
					encoding = chooser.getSaveEncoding();
					fileFormat = chooser.getSaveFileFormat();
					break;
					
				case FileProtocol.EXPORT:
					lineEnd = chooser.getExportLineEnding();
					encoding = chooser.getExportEncoding();
					fileFormat = chooser.getExportFileFormat();
					break;
					
				default:
					System.out.println("ERROR: invalid save/export type used. (" + type +")");
					return false;
			}
			
			
			// Update the document settings
			if (document.settings.useDocumentSettings()) {
				document.settings.getLineEnd().def = lineEnd;
				document.settings.getLineEnd().cur = lineEnd;
				document.settings.getLineEnd().tmp = lineEnd;
				document.settings.getSaveEncoding().def = encoding;
				document.settings.getSaveEncoding().cur = encoding;
				document.settings.getSaveEncoding().tmp = encoding;
				document.settings.getSaveFormat().def = fileFormat;
				document.settings.getSaveFormat().cur = fileFormat;
				document.settings.getSaveFormat().tmp = fileFormat;
			}
			
			// Update Document Info
			DocumentInfo docInfo = document.getDocumentInfo();
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PATH, filename);
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING, lineEnd);
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE, encoding);
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT, fileFormat);
			
			return true;
		} else {
			return false;
		}
	}
	
	// select a file to open or import
	public boolean selectFileToOpen(DocumentInfo docInfo, int type) {
		// we'll customize the approve button
		// [srk] this is done here, rather than in configureForOpen/Import, to workaround a bug
		String approveButtonText = null;
		
		// make sure we're all set up
		lazyInstantiation();
		
		// Setup the File Chooser to open or import
		switch (type) {
			case FileProtocol.OPEN:
				chooser.configureForOpen(getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
				approveButtonText = "Open";
				break;
				
			case FileProtocol.IMPORT:
				chooser.configureForImport(getName(), Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur);
				approveButtonText = "Import";
				break;
				
			default:
				System.out.println("ERROR: invalid open/import type used. (" + type +")");
				return false;
		}
		
		// run the File Chooser
		int option = chooser.showDialog(Outliner.outliner, approveButtonText) ;
		
		// Update the most recent open dir preference
		// TBD [srk] set up a MOST_RECENT_IMPORT_DIR, then have this code act appropriately
		Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).cur = chooser.getCurrentDirectory().getPath();
		Preferences.getPreferenceString(Preferences.MOST_RECENT_OPEN_DIR).restoreTemporaryToCurrent();
		
		// Handle User Input
		if (option == JFileChooser.APPROVE_OPTION) {
			String filename = chooser.getSelectedFile().getPath();
			
			String encoding;
			String fileFormat;
			
			// pull proper preference values from the file chooser
			switch (type) {
			
				case FileProtocol.OPEN:
					encoding = chooser.getOpenEncoding();
					fileFormat = chooser.getOpenFileFormat();
					break;
					
				case FileProtocol.IMPORT:
					encoding = chooser.getImportEncoding();
					fileFormat = chooser.getImportFileFormat();
					break;
					
				default:
					System.out.println("ERROR: invalid open/import type used. (" + type +")");
					return false;
			}
			
			
			// store data into docInfo structure
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PATH, filename);
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE, encoding);
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT, fileFormat);
			
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean saveFile(DocumentInfo docInfo) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
			fileOutputStream.write(docInfo.getOutputBytes());
			fileOutputStream.flush();
			fileOutputStream.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean openFile(DocumentInfo docInfo) {
		String msg = null;
		
		String path = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH);
		try {
			docInfo.setInputStream(new FileInputStream(path));
			return true;
			
		} catch (FileNotFoundException fnfe) {
			msg = GUITreeLoader.reg.getText("error_file_not_found");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, path);
			
			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			RecentFilesList.removeFileNameFromList(docInfo);
			return false;
			
		} catch (Exception e) {
			msg = GUITreeLoader.reg.getText("error_could_not_open_file");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, path);
			
			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			RecentFilesList.removeFileNameFromList(docInfo);
			return false;
		}
	}
}
