/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner.io;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.organic.maynard.util.string.Replace;
import javax.swing.filechooser.*;
import com.organic.maynard.util.string.StanStringTools;
import com.organic.maynard.util.string.StringTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.5 $, $Date: 2004/01/30 00:12:42 $
 */

public class OutlinerFileChooser extends JFileChooser implements ItemListener {

	private JPanel openAccessory = new JPanel();
	private JPanel importAccessory = new JPanel();
	private JPanel saveAccessory = new JPanel();
	private JPanel exportAccessory = new JPanel();

	private JComboBox saveLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox saveEncodingComboBox = new JComboBox();
	private JComboBox saveFormatComboBox = new JComboBox();

	private JComboBox openEncodingComboBox = new JComboBox();
	private JComboBox openFormatComboBox = new JComboBox();

	private JComboBox importEncodingComboBox = new JComboBox();
	private JComboBox importFormatComboBox = new JComboBox();

	private JComboBox exportLineEndComboBox = new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
	private JComboBox exportEncodingComboBox = new JComboBox();
	private JComboBox exportFormatComboBox = new JComboBox();

	/**
	 * Indicates that lazy instantiation has occurred.
	 */
	private boolean isInitialized = false; 
	
	/**
	 * dialogType is here so we know the type of dialog we are, since Swing forces 
	 * us to be CUSTOM_DIALOG. Without this the approveSelection() method won't work right.
	 *
	 * This field is updated in each of the configure methods so that it reflects the current
	 * dialog type being used.
	 */
	private int dialogType = JFileChooser.CUSTOM_DIALOG;

	/**
	 * Holds a reference to the JTextField for entering filenames. Since the API for JFileChooser
	 * does not expose this component. Any use of this component should always check for a null
	 * value first since there is no guarantee it was found.
	 * 
	 * This field is currently populated during lazy instantiation.
	 */
	protected JTextField textEntryField = null;


	// The Constructor
	public OutlinerFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	private void lazyInstantiate() {
		if (isInitialized) {
			return;
		}
		
		// This is a hack to get the JTextField used to enter text since they don't expose it in the API.
		// This should be done before adding accessories since we might find a JTextField in an accessory
		// by accident. Also, the search should be slightly faster if we don't have to crawl the accessories
		// too.
		ArrayList components = new ArrayList();
		int cursor = 0;
		components.add(this);
		
		while (cursor < components.size()) {
			Object o = components.get(cursor);
			
			if (o instanceof JTextField) {
    			this.textEntryField = (JTextField) o;
    			break;			
			} else if (o instanceof Container) {
				Container c = (Container) o;
				for (int i = 0, limit = c.getComponentCount(); i < limit; i++) {
		    		components.add(c.getComponent(i));
		    	}			
			} else {
				// do nothing
			}
			
			cursor++;
		}


		// TBD [srk] have different encoding prefs for each of these OPs
		for (int i = 0, limit = Preferences.ENCODINGS.size(); i < limit; i++) {
			String encoding = Preferences.ENCODINGS.get(i);
			saveEncodingComboBox.addItem(encoding);
			exportEncodingComboBox.addItem(encoding);
			openEncodingComboBox.addItem(encoding);
			importEncodingComboBox.addItem(encoding);
		}

		for (int i = 0, limit = Preferences.FILE_FORMATS_OPEN.size(); i < limit; i++) {
			openFormatComboBox.addItem(Preferences.FILE_FORMATS_OPEN.get(i));
		}

		for (int i = 0, limit = Preferences.FILE_FORMATS_IMPORT.size(); i < limit; i++) {
			importFormatComboBox.addItem(Preferences.FILE_FORMATS_IMPORT.get(i));
		}

		for (int i = 0, limit = Preferences.FILE_FORMATS_SAVE.size(); i < limit; i++) {
			saveFormatComboBox.addItem(Preferences.FILE_FORMATS_SAVE.get(i));
		}

		for (int i = 0, limit = Preferences.FILE_FORMATS_EXPORT.size(); i < limit; i++) {
			exportFormatComboBox.addItem(Preferences.FILE_FORMATS_EXPORT.get(i));
		}
		
		String lineTerminatorText = GUITreeLoader.reg.getText("line_terminator");
		String fileEncodingText = GUITreeLoader.reg.getText("file_encoding");
		String fileFormatText = GUITreeLoader.reg.getText("file_format");


		// Lay out save panel
		saveAccessory.setLayout(new GridBagLayout());

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(lineTerminatorText), saveAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(saveLineEndComboBox, saveAccessory);

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileEncodingText), saveAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(saveEncodingComboBox, saveAccessory);

		saveFormatComboBox.addItemListener(this);
		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileFormatText), saveAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(saveFormatComboBox, saveAccessory);


		// Lay out export panel
		exportAccessory.setLayout(new GridBagLayout());

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(lineTerminatorText), exportAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(exportLineEndComboBox, exportAccessory);

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileEncodingText), exportAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(exportEncodingComboBox, exportAccessory);

		exportFormatComboBox.addItemListener(this);
		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileFormatText), exportAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(exportFormatComboBox, exportAccessory);


		// Layout open panel
		openAccessory.setLayout(new GridBagLayout());

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileEncodingText), openAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(openEncodingComboBox, openAccessory);

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileFormatText), openAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(openFormatComboBox, openAccessory);


		// Layout import panel
		importAccessory.setLayout(new GridBagLayout());
		
		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileEncodingText), importAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(importEncodingComboBox, importAccessory);

		AbstractPreferencesPanel.addSingleItemCentered(new JLabel(fileFormatText), importAccessory);
		AbstractPreferencesPanel.addSingleItemCentered(importFormatComboBox, importAccessory);


		// Set the flag
		isInitialized = true;
	}


	//----------------------------- Configure Methods -----------------------------------
	public void configureForExport(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Export: " + protocolName);

		// adjust approve button
		setApproveButtonToolTipText("Export file as named");

		// Set the Accessory state
		setAccessory(exportAccessory);

		// Set the Accessory GUI state.
		exportLineEndComboBox.setSelectedItem(doc.settings.getLineEnd().cur);
		exportEncodingComboBox.setSelectedItem(doc.settings.getSaveEncoding().cur);
		exportFormatComboBox.setSelectedItem(doc.settings.getSaveFormat().cur);

		// Set the current directory location or selected file.
		String currentFileName = doc.getFileName();
		if (!currentFileName.equals("")) {
			setSelectedFile(new File(currentFileName));
		} else {
			setCurrentDirectory(new File(currentDirectory));
			setSelectedFile(null);
		}
		
		this.dialogType = JFileChooser.SAVE_DIALOG;
	}
	
	public void configureForSave(OutlinerDocument doc, String protocolName, String currentDirectory) {
		lazyInstantiate();
		
		// adjust title
		setDialogTitle("Save: " + protocolName);
		
		// adjust approve button
		setApproveButtonToolTipText("Save file as named");
		
		// Set the Accessory state
		setAccessory(saveAccessory);
		
		// Set the Accessory GUI state.
		saveLineEndComboBox.setSelectedItem(doc.settings.getLineEnd().cur);
		saveEncodingComboBox.setSelectedItem(doc.settings.getSaveEncoding().cur);
		saveFormatComboBox.setSelectedItem(doc.settings.getSaveFormat().cur);
		
		// Set the current directory location or selected file.
		// grab the file's name
		String currentFileName = doc.getFileName();
		
		// if it's an imported file ...
		if (PropertyContainerUtil.getPropertyAsBoolean(doc.getDocumentInfo(), DocumentInfo.KEY_IMPORTED)) {
			// trim any extension off the file name
			String trimmedFileName = StanStringTools.trimFileExtension(currentFileName);
			
			// obtain the current default save format's extension
			String extension = 	(Outliner.fileFormatManager.getSaveFormat(doc.settings.getSaveFormat().cur)).getDefaultExtension();
			
			// addemup
			setSelectedFile(new File(trimmedFileName + "." + extension));
		
		} else {
			if (!currentFileName.equals("")) {
				
				// set up using the filename
				setSelectedFile(new File(currentFileName));
				
			} else {
				// use the current directory
				setCurrentDirectory(new File(currentDirectory));
				
				// start with the window title
				String title = doc.getTitle();
				
				// obtain the current default save format's extension
				String extension = 	(Outliner.fileFormatManager.getSaveFormat(doc.settings.getSaveFormat().cur)).getDefaultExtension();
			
				// addemup
				setSelectedFile(new File(title + "." + extension));
			}
		}
		
		this.dialogType = JFileChooser.SAVE_DIALOG;
	}

	public void configureForOpen(String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Open: " + protocolName);

		// adjust approve button
		setApproveButtonToolTipText("Open selected file");
		
		// Set the Accessory state.
		setAccessory(openAccessory);

		// Set the Accessory GUI state.
		openEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
		openFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.OPEN_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
		
		this.dialogType = JFileChooser.OPEN_DIALOG;
	}


	public void configureForImport(String protocolName, String currentDirectory) {
		lazyInstantiate();

		// adjust title
		setDialogTitle("Import: " + protocolName);
		
		// adjust approve button
		setApproveButtonToolTipText("Import selected file");

		// Set the Accessory state.
		setAccessory(importAccessory);

		// Set the Accessory GUI state.
		importEncodingComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_ENCODING).cur);
		importFormatComboBox.setSelectedItem(Preferences.getPreferenceString(Preferences.IMPORT_FORMAT).cur);

		// Set the current directory location and selected file.
		setCurrentDirectory(new File(currentDirectory));
		setSelectedFile(null);
		
		this.dialogType = JFileChooser.OPEN_DIALOG;
	}


	// Accessors
	public String getOpenEncoding() {
		return (String) openEncodingComboBox.getSelectedItem();
	}
	
	public String getOpenFileFormat() {
		return (String) openFormatComboBox.getSelectedItem();
	}

	public String getImportEncoding() {
		return (String) importEncodingComboBox.getSelectedItem();
	}
	
	public String getImportFileFormat() {
		return (String) importFormatComboBox.getSelectedItem();
	}

	public String getSaveLineEnding() {
		return (String) saveLineEndComboBox.getSelectedItem();
	}
	
	public String getSaveEncoding() {
		return (String) saveEncodingComboBox.getSelectedItem();
	}
	
	public String getSaveFileFormat() {
		return (String) saveFormatComboBox.getSelectedItem();
	}

	public String getExportLineEnding() {
		return (String) exportLineEndComboBox.getSelectedItem();
	}
	
	public String getExportEncoding() {
		return (String) exportEncodingComboBox.getSelectedItem();
	}
	
	public String getExportFileFormat() {
		return (String) exportFormatComboBox.getSelectedItem();
	}


	// ItemListener Interface
	// The purpose of this listener is to update the file extension to the default 
	// extension of the newly selected format.
	public void itemStateChanged(ItemEvent e) {
		if (textEntryField == null) {
			// We we're unable to find a JTextField component that was a decendant of this
			// JFileChooser so we need to abort.
			return;
		}
		
		String filename = textEntryField.getText();
		
		if (filename == null || filename.equals("")) {
			return;
		}
		
		// Get the file format
		FileFormat format;
		if (e.getSource() == saveFormatComboBox) {
			format = Outliner.fileFormatManager.getSaveFormat(getSaveFileFormat());
		} else if (e.getSource() == exportFormatComboBox) {
			format = Outliner.fileFormatManager.getExportFormat(getExportFileFormat());
		} else {
			// Unknown source;
			System.out.println("Unknown source for itemStateChanged in OutlinerFileChooser.");
			return;
		}
		
		if (format == null) {
			System.out.println("Format is null for itemStateChanged in OutlinerFileChooser.");
			return;
		}
		
		// Get extension
		String extension = format.getDefaultExtension();
		
		// Abort if we don't have an extension.
		if (extension == null || extension.equals("")) {
			return;
		}

		// trim any extension off the file name
		String newFileName = StringTools.trimExtension(filename, Preferences.EXTENSION_SEPARATOR);
		
		// Set the filename in the Chooser
		textEntryField.setText(new StringBuffer().append(newFileName).append(Preferences.EXTENSION_SEPARATOR).append(extension).toString());
	}


	// Overriden Methods of JFileChooser
	public void approveSelection() {
		File file = getSelectedFile();
		
		if (this.dialogType == JFileChooser.OPEN_DIALOG) {
			// Alert if file does not exist.
			if (!file.exists()) {
				String msg = GUITreeLoader.reg.getText("error_file_not_found");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, file.getPath());

				JOptionPane.showMessageDialog(this, msg);
				return;
			}
		} else if (this.dialogType == JFileChooser.SAVE_DIALOG) {
			// Alert if file exists.
			if (file.exists()) {
				// Custom button text
				String yes = GUITreeLoader.reg.getText("yes");
				String no = GUITreeLoader.reg.getText("no");
				String confirm_replacement = GUITreeLoader.reg.getText("confirm_replacement");
				String msg = GUITreeLoader.reg.getText("confirmation_replace_file");
				msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, file.getPath());

				Object[] options = {yes, no};
				int result = JOptionPane.showOptionDialog(this,
					msg,
					confirm_replacement,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]
				);
				if (result == JOptionPane.YES_OPTION) {
					// Proceed normally.
				} else if (result == JOptionPane.NO_OPTION) {
					return;
				} else {
					return;
				}
			}
		}

		super.approveSelection();
	}
}
