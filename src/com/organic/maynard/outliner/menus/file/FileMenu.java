/**
 * Portions copyright (C) 2000-2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2001, 2002 Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner.menus.file;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.data.IntList;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.awt.*;
import javax.swing.*;
import org.xml.sax.*;
import com.organic.maynard.util.string.Replace;
import com.organic.maynard.util.string.StanStringTools ;

/**
 * This class implements the meat of several File Menu commands: New, Open, Import, Save, Revert, Close.
 *
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/05/26 20:59:07 $
 */

public class FileMenu extends AbstractOutlinerMenu implements GUITreeComponent, JoeReturnCodes {
	
	// Constants
	private static final int MODE_SAVE = 0;
	private static final int MODE_EXPORT = 1;
	private static final int MODE_OPEN = 2;
	private static final int MODE_IMPORT = 3;
	private static final int MODE_REVERT = 4;
	
	private static final String TRUNC_STRING = GUITreeLoader.reg.getText("trunc_string");
	
	// document title name forms
	private static final int FULL_PATHNAME = 0;
	private static final int TRUNC_PATHNAME = 1;
	private static final int JUST_FILENAME = 2;
	
	
	// Constructors
	public FileMenu() {
		super();
		
		Outliner.menuBar.fileMenu = this;
	}
	
	
	/**
	 * Exports a file.
	 *
	 * @param filename the filename to write the exported document to.
	 * @param document the document to export.
	 * @param protocol the Protocol to use when exporting the document.
	 */
	public static void exportFile(String filename, OutlinerDocument document, FileProtocol protocol) {
		saveFile(filename, document, protocol, true, MODE_EXPORT);
	}
	
	/**
	 * Saves a file using the protocol currently set on the Document's DocumentInfo object.
	 *
	 * @param filename the filename to write the saved document to.
	 * @param document the document to save.
	 * @param saveAs indicates if we are in the process of doing a "Save As" operation rather than a straight "Save".
	 */
	public static void saveFile(String filename, OutlinerDocument document, boolean saveAs) {
		FileProtocol protocol = Outliner.fileProtocolManager.getProtocol(PropertyContainerUtil.getPropertyAsString(document.getDocumentInfo(), DocumentInfo.KEY_PROTOCOL_NAME));
		saveFile(filename, document, protocol, saveAs, MODE_SAVE);
	}
	
	/**
	 * Saves a file.
	 *
	 * @param filename the filename to write the saved document to.
	 * @param document the document to save.
	 * @param protocol the Protocol to use when saving the document.
	 * @param saveAs indicates if we are in the process of doing a "Save As" operation rather than a straight "Save".
	 */
	public static void saveFile(String filename, OutlinerDocument document, FileProtocol protocol, boolean saveAs) {
		saveFile(filename, document, protocol, saveAs, MODE_SAVE);
	}
	
	/**
	 * The main save routine which the above export and save methods resolve to.
	 *
	 * @param filename the filename to write the saved document to.
	 * @param document the document to save.
	 * @param protocol the Protocol to use when saving the document.
	 * @param saveAs indicates if we are in the process of doing a "Save As" operation rather than a straight "Save".
	 * @param mode indicates if we are in the process of doing a "Save" or an "Export".
	 */
	private static void saveFile(String filename, OutlinerDocument document, FileProtocol protocol, boolean saveAs, int mode) {
		
		String msg = null;
		DocumentInfo docInfo = document.getDocumentInfo();
		
		String fileFormatName = null;
		SaveFileFormat saveOrExportFileFormat = null;
		boolean commentExists = false;
		boolean editableExists = false;
		boolean moveableExists = false;
		boolean attributesExist = false;
		boolean documentAttributesExist = false;
		boolean wereImported = false ;
		String savedDocsPrevName = null;
		String title;
		
		// set up the protocol
		PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PROTOCOL_NAME, protocol.getName());
		
		// Get the proper file format object for the specified mode
		// Initialize DocumentInfo with current document state, prefs and document settings.
		// Filter out bad modes
		switch (mode) {
			case MODE_SAVE:
				if (saveAs) {
					fileFormatName = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT);
				} else {
					fileFormatName = document.settings.getSaveFormat().cur;
				}
				saveOrExportFileFormat = Outliner.fileFormatManager.getSaveFormat(fileFormatName);
				savedDocsPrevName = document.getFileName();
				document.setFileName(filename);
				docInfo.updateDocumentInfoForDocument(document, saveAs); // Might not be neccessary anymore.
				break;
				
			case MODE_EXPORT:
				fileFormatName = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT);
				saveOrExportFileFormat = Outliner.fileFormatManager.getExportFormat(fileFormatName);
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PATH, filename);
				break;
				
			default:
				// Ack, this shouldn't happen.
				// illegal/unknown mode specification
				System.out.println("FileMenu:SaveFile: bad mode parameter");
				return;
		}
		
		// if we couldn't get a saveOrExportFileFormat
		if (saveOrExportFileFormat == null) {
			msg = GUITreeLoader.reg.getText("error_could_not_save_no_file_format");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, fileFormatName);
			JOptionPane.showMessageDialog(document, msg);
			return;
		}
		
		// Check to see what special features the outline has
		// That's because some save/export formats don't support all special features
		
		// do we have document attributes ?
		if (document.tree.getAttributeCount() > 0) {
			documentAttributesExist = true;
		}
		
		// walk the document tree, looking for various node features:
		// comments, not-editable, locked-in-place, attributes
		// [srk] hmmm, I sniff it'd be a useful optimization to maintain this info in DocInfo
		// as someone works on an outline .... rather than having to
		// scan the whole tree .... I think this scan is a big factor in
		// the slowness of saving very large outlines  ... can test that by
		// temporarily commenting it out and doing some vlo saves
		Node node = document.tree.getRootNode();
		while (true) {
			node = node.nextNode();
			
			if (node.isRoot()) {
				break;
			}
			
			if (!commentExists && node.isComment()) {
				commentExists = true;
			}
			
			if (!editableExists && !node.isEditable()) {
				editableExists = true;
			}
			
			if (!moveableExists && !node.isMoveable()) {
				moveableExists = true;
			}
			
			if (!attributesExist && node.getAttributeCount() > 0) {
				attributesExist = true;
			}
		}
		
		// if we found comment nodes, but the save/export format doesn't support that concept  ...
		if (commentExists && !saveOrExportFileFormat.supportsComments()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_comments");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}
		
		// if found non-editable nodes, but the save/export format doesn't support that concept ...
		if (editableExists && !saveOrExportFileFormat.supportsEditability()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_editability");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}
		
		// if we found immoveable nodes, but the save/export format doesn't support that concept ...
		if (moveableExists && !saveOrExportFileFormat.supportsMoveability()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_moveability");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}
		
		// if we found nodes with attributes, but the save/export format doesn't support that concept ...
		if (attributesExist && !saveOrExportFileFormat.supportsAttributes()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_attributes");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}
		
		// if we've got document attributes, but the save/export format doesn't support that concept ....
		if (documentAttributesExist && !saveOrExportFileFormat.supportsDocumentAttributes()) {
			msg = GUITreeLoader.reg.getText("error_file_format_does_not_support_document_attributes");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, fileFormatName);
			if (USER_ABORTED == promptUser(msg)) {
				return;
			}
		}
		
		// if parts of the doc are hoisted, temporarily dehoist
		// so that a hoisted doc will be completely saved.
		if (document.hoistStack.isHoisted()) {
			document.hoistStack.temporaryDehoistAll();
		}
		
		// ask the save/export format to send us an array of bytes to save/export.
		// This also gives the format a chance to display a dialog to the user.
		byte[] bytes = saveOrExportFileFormat.save(document.tree, docInfo);
		
		int saveOrExportResult = USER_ABORTED;
		
		// If the bytes aren't null then save the file.
		if (bytes != null) {
			// point the doc info at that array of save/export bytes
			docInfo.setOutputBytes(bytes);
			
			// if we're an imported file, the savee/exportee won't be
			if (wereImported = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED)){
				PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED, false);
			}
			
			// Ask the protocol to save/export the file. This is where the data actually gets written.
			if (protocol.saveFile(docInfo)) {
				saveOrExportResult = SUCCESS;
			} else {
				saveOrExportResult = FAILURE;
			}
			
			// Get rid of the bytes now that were done so they can be GC'd.
			docInfo.setOutputBytes(null);
		}
		
		// if we had to unhoist stuff then rehoist it.
		if (document.hoistStack.isHoisted()) {
			document.hoistStack.temporaryHoistAll();
		}
		
		switch (saveOrExportResult) {
			case FAILURE:
				msg = GUITreeLoader.reg.getText("error_could_not_save_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
				JOptionPane.showMessageDialog(document, msg);
				
			case USER_ABORTED:
				if (wereImported) {
					PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED, true);
				}
				break;
				
			case SUCCESS:
				switch (mode) {
					case MODE_EXPORT:
						// if we were imported, we stay that way
						if (wereImported) {
							PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED, true);
						}
						break;
					
					case MODE_SAVE:
						// we're going to use the document settings
						document.settings.setUseDocumentSettings(true);
						
						// Stop collecting text edits into the current undoable.
						UndoableEdit.freezeUndoEdit(document.tree.getEditingNode());
						
						// Update the Recent File List
						RecentFilesList.addFileNameToList(docInfo);
						
						document.setModified(false);
						
						// case out on the form to build the title
						switch (OutlinerDocument.getTitleNameForm()) {
							case FULL_PATHNAME:
							
							default: 
								title = filename;
								break;
							
							case TRUNC_PATHNAME: 
								title = StanStringTools.getTruncatedPathName(filename, TRUNC_STRING) ;
								break;
							
							case JUST_FILENAME: 
								title = StanStringTools.getFileNameFromPathName(filename) ;
								break;
						}
						
						document.setTitle(title) ;
						
						Outliner.menuBar.windowMenu.updateWindow(document);
						break;
				}
				break;
		}
	}
	
	
	/**
	 * Imports a file. Similar to opening a file. The key difference
	 * is that we assume we can't save it.
	 *
	 * @param docInfo contains all the info about the file we're importing.
	 * @param protocol the Protocol to use when importing the document.
	 */
	protected static void importFile(DocumentInfo docInfo, FileProtocol protocol) {
		openFile(docInfo, protocol, MODE_IMPORT) ;
	}
	
	/**
	 * Opens a file. Similar to importing a file. The key difference
	 * is that we assume we can save it.
	 *
	 * @param docInfo contains all the info about the file we're opening.
	 * @param protocol the Protocol to use when opening the document.
	 */
	public static void openFile(DocumentInfo docInfo, FileProtocol protocol) {
		openFile(docInfo, protocol, MODE_OPEN) ;
	}
	
	/**
	 * This is the main method used to open or import a file. The above
	 * import and open methods all resolve to this method.
	 *
	 * @param docInfo contains all the info about the file we're opening.
	 * @param protocol the Protocol to use when opening the document.
	 * @param mode indicates if the current process is an "Open" or an "Import".
	 */
	protected static void openFile(DocumentInfo docInfo, FileProtocol protocol, int mode) {
		// if mode is invalid, abort
		if (mode != MODE_OPEN && mode != MODE_IMPORT) {
				System.out.println("FileMenu:OpenFile: invalid mode parameter");
				return;
		}
		
		// Validate uniqueness of the filename. At some point this should probably 
		// incorporate the protocol as well. More like an URL: 
		//   "file:///C:/path/to/my/file.txt" for Local File System Protocol
		//   "phpwebfile:///path/to/my/file.txt" for PHP:WebFile Protocol
		//   "null:///Untitled 1" for unsaved documents.
		String filename = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH);
		if (!Outliner.documents.isFileNameUnique(filename)) {
			String msg = GUITreeLoader.reg.getText("message_file_already_open");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, filename);
			JOptionPane.showMessageDialog(Outliner.outliner, msg);
			return;
		}
		
		// try to open the file and pour its data into the tree
		JoeTree tree = Outliner.newTree(null); // TBD: let's make a real factory class for trees if we even neeed one.
		int openOrImportResult = openOrImportFileAndGetTree(tree, docInfo, protocol, mode);
		if ((openOrImportResult != SUCCESS) && (openOrImportResult != SUCCESS_MODIFIED)) { // Might be good to have codes we can do % on.
			return;
		}
		
		OutlinerDocument newDoc = new OutlinerDocument(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH), docInfo);
		
		// Use document settings immediately, otherwise we would update the application prefs which is bad.
		newDoc.settings.setUseDocumentSettings(true);
		
		// hook the outline tree to the doc, and the doc to the outline tree
		newDoc.setTree(tree);
		
		// [srk] bug:we can get to this point with no line ending set
		//  fix: set it to current pref if lineEnding is not yet set ...
		if (PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING).length() == 0) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING, Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur);
		}
		
		// Update DocumentSettings
		syncDocumentToDocInfo(newDoc, mode);
		
		// make any final modal adjustments
		if (mode == MODE_IMPORT) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED, true);
		}
		
		// make sure we're in the Recent Files list
		RecentFilesList.addFileNameToList(docInfo);
		
		setupAndDraw(docInfo, newDoc, openOrImportResult);
	}
	
	// revert a file to it's previous state
	// TBD get this to work with IMPORTs
	protected static void revertFile(OutlinerDocument document) {
		// get the document info and file protocol
		DocumentInfo docInfo = document.getDocumentInfo();
		FileProtocol protocol = Outliner.fileProtocolManager.getProtocol(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PROTOCOL_NAME));
		
		// set mode based on whether we were OPENed or IMPORTed
		int mode = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_IMPORTED) ? MODE_IMPORT:MODE_OPEN;
		
		// try to open the file and pour its data into that tree
		JoeTree tree = Outliner.newTree(null);
		int openOrImportResult = openOrImportFileAndGetTree(tree, docInfo, protocol, mode);
		if ((openOrImportResult != SUCCESS) && (openOrImportResult != SUCCESS_MODIFIED)) {
			return;
		} else {
			document.setTree(tree);
		}
		
		document.undoQueue.clear();
		document.hoistStack.clear();
		
		// Reset the document attributes
		Outliner.documentAttributes.configure(tree);
		
		// Reset the document preferences
		document.getSettings().setUseDocumentSettings(true);
		syncDocumentToDocInfo(document, MODE_REVERT);
		((DocumentSettingsView) GUITreeLoader.reg.get(GUITreeComponentRegistry.JDIALOG_DOCUMENT_SETTINGS_VIEW)).configure(document.getSettings());
		
		setupAndDraw(docInfo, document, openOrImportResult);
	}
	
	private static void syncDocumentToDocInfo(OutlinerDocument doc, int mode) {
		DocumentInfo docInfo = doc.getDocumentInfo();
		
		// Update DocumentSettings
		doc.settings.getLineEnd().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING);
		doc.settings.getLineEnd().restoreCurrentToDefault();
		doc.settings.getLineEnd().restoreTemporaryToDefault();
		
		doc.settings.getSaveEncoding().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE);
		doc.settings.getSaveEncoding().restoreCurrentToDefault();
		doc.settings.getSaveEncoding().restoreTemporaryToDefault();
		
		if (mode == MODE_IMPORT) {
			// the doc's default save format is the app's default save format
			doc.settings.getSaveFormat().def = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		} else {
			// the doc's default save format is its existing format
			doc.settings.getSaveFormat().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT);
		}
		doc.settings.getSaveFormat().restoreCurrentToDefault();
		doc.settings.getSaveFormat().restoreTemporaryToDefault();
		
		doc.settings.getApplyFontStyleForComments().def = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_COMMENTS);
		doc.settings.getApplyFontStyleForComments().restoreCurrentToDefault();
		doc.settings.getApplyFontStyleForComments().restoreTemporaryToDefault();
		
		doc.settings.getApplyFontStyleForEditability().def = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_EDITABILITY);
		doc.settings.getApplyFontStyleForEditability().restoreCurrentToDefault();
		doc.settings.getApplyFontStyleForEditability().restoreTemporaryToDefault();
		
		doc.settings.getApplyFontStyleForMoveability().def = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY);
		doc.settings.getApplyFontStyleForMoveability().restoreCurrentToDefault();
		doc.settings.getApplyFontStyleForMoveability().restoreTemporaryToDefault();
		
		doc.settings.getUseCreateModDates().def = PropertyContainerUtil.getPropertyAsBoolean(docInfo, DocumentInfo.KEY_USE_CREATE_MOD_DATES);
		doc.settings.getUseCreateModDates().restoreCurrentToDefault();
		doc.settings.getUseCreateModDates().restoreTemporaryToDefault();
		
		doc.settings.getCreateModDatesFormat().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_CREATE_MOD_DATES_FORMAT);
		doc.settings.getCreateModDatesFormat().restoreCurrentToDefault();
		doc.settings.getCreateModDatesFormat().restoreTemporaryToDefault();
		
		doc.settings.getOwnerName().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME);
		doc.settings.getOwnerName().restoreCurrentToDefault();
		doc.settings.getOwnerName().restoreTemporaryToDefault();
		
		doc.settings.getOwnerEmail().def = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL);
		doc.settings.getOwnerEmail().restoreCurrentToDefault();
		doc.settings.getOwnerEmail().restoreTemporaryToDefault();
		
		doc.settings.setDateCreated(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED));
		doc.settings.setDateModified(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED));
		
		// Update size and location based off of DocumentInfo
		if (mode != MODE_REVERT) {
			int width = docInfo.getWidth();
			int height = docInfo.getHeight();
			int left = PropertyContainerUtil.getPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_LEFT);
			int top = PropertyContainerUtil.getPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_TOP);
			doc.setSize(width, height);
			doc.setLocation(left, top);
		}
	}
	
	
	// open/import a file and store its outline into a tree
	private static int openOrImportFileAndGetTree(JoeTree tree, DocumentInfo docInfo, FileProtocol protocol, int mode) {
		String msg = null;
		int openOrImportResult = FAILURE;
		OpenFileFormat openOrImportFileFormat = null ;
		
		// try to open the file
		if (!protocol.openFile(docInfo)) {
			return FAILURE;
		}
		
		// Get the proper file format object for the specified mode
		switch (mode) {
			
			case MODE_OPEN:
				openOrImportFileFormat = Outliner.fileFormatManager.getOpenFormat(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT));
				break;
				
			case MODE_IMPORT:
				openOrImportFileFormat = Outliner.fileFormatManager.getImportFormat(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT));
				break;
				
			default:
				// Ack, this shouldn't happen.
				// illegal/unknown mode specification
				System.out.println("FileMenu:OpenFile: bad mode parameter");
				return FAILURE;
		}
		
		// if we couldn't get a file format object reference ...
		if (openOrImportFileFormat == null) {
			msg = GUITreeLoader.reg.getText("error_could_not_open_no_file_format");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_2, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT));
			JOptionPane.showMessageDialog(Outliner.outliner, msg);
		} else {
			// we got one try to open the file
			openOrImportResult = openOrImportFileFormat.open(tree, docInfo, docInfo.getInputStream());
			
			// if we couldn't ....
			if (openOrImportResult == FAILURE) {
				msg = GUITreeLoader.reg.getText("error_could_not_open_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH));
				
				JOptionPane.showMessageDialog(Outliner.outliner, msg);
				RecentFilesList.removeFileNameFromList(docInfo);
				
			} else if (openOrImportResult != FAILURE_USER_ABORTED) {
				// Deal with a childless RootNode or an Empty or Null Tree
				if ((tree.getRootNode() == null) || (tree.getRootNode().numOfChildren() <= 0)) {
					tree.reset();
				}
			}
		}
		
		// no matter what happened, reset the input stream in the docInfo
		docInfo.setInputStream(null);
		
		return openOrImportResult;
	}
	
	private static void setupAndDraw(DocumentInfo docInfo, OutlinerDocument doc, int openOrImportResult) {
		JoeTree tree = doc.tree;
		String filename = PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH);
		
		tree.clearSelection();
		tree.getVisibleNodes().clear();
		
		// Insert nodes into the VisibleNodes Cache
		Node rootNode = tree.getRootNode();
		for (int i = 0, limit = tree.getRootNode().numOfChildren(); i < limit; i++) {
			tree.addNode(rootNode.getChild(i));
		}
		
		// Update the menuBar
		doc.setFileName(filename);
		doc.setModified(false);
		
		// case out on the form to build the title
		String title;
		switch (doc.getTitleNameForm()) {
			case FULL_PATHNAME:
			
			default: 
				title = filename;
				break;
				
			case TRUNC_PATHNAME: 
				title = StanStringTools.getTruncatedPathName(filename, TRUNC_STRING);
				break;
				
			case JUST_FILENAME: 
				title = StanStringTools.getFileNameFromPathName(filename) ;
				break;
		}
		
		doc.setTitle(title);
		
		Outliner.menuBar.windowMenu.updateWindow(doc);
		
		// Expand Nodes
		IntList expandedNodes = docInfo.getExpandedNodes();
		for (int i = 0, limit = expandedNodes.size(); i < limit; i++) {
			int nodeNum = expandedNodes.get(i);
			try {
				Node node = doc.tree.getVisibleNodes().get(nodeNum);
				node.setExpanded(true);
			} catch (Exception e) {
				break;
			}
		}
		
		// Record the current location
		Node firstVisibleNode;
		int index = -1;
		try {
			index = PropertyContainerUtil.getPropertyAsInt(docInfo, DocumentInfo.KEY_VERTICAL_SCROLL_STATE) - 1;
			firstVisibleNode = tree.getVisibleNodes().get(index);
		} catch (IndexOutOfBoundsException e) {
			index = 0;
			firstVisibleNode = tree.getVisibleNodes().get(0);
		}
		
		tree.setEditingNode(firstVisibleNode);
		tree.setCursorPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		
		// Redraw
		OutlineLayoutManager layout = doc.panel.layout;
		layout.setNodeToDrawFrom(firstVisibleNode,index);
		layout.redraw();
		
		// Set document as modified if something happened on open
		if (openOrImportResult == SUCCESS_MODIFIED) {
			doc.setModified(true);
		}
		
		// Maximize document if necessary
		//if (Outliner.desktop.isMaximized()) {
		//	Outliner.desktop.getDesktopManager().maximizeFrame(doc);
		//}
		Outliner.menuBar.windowMenu.changeToWindow(doc);
	}
	
	
	// Utility Methods
	private static int promptUser(String msg) {
		Object[] options = {GUITreeLoader.reg.getText("yes"), GUITreeLoader.reg.getText("no")};
		int result = JOptionPane.showOptionDialog(
			Outliner.outliner,
			msg,
			GUITreeLoader.reg.getText("confirm_save"),
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (result == JOptionPane.NO_OPTION) {
			return USER_ABORTED;
		} else {
			return SUCCESS;
		}
	}
}
