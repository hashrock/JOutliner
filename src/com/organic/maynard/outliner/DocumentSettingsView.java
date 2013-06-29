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

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.22 $, $Date: 2004/01/30 00:12:42 $
 */
 
public class DocumentSettingsView extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 450;
	private static final int INITIAL_HEIGHT = 300;
	private static final int MINIMUM_WIDTH = 450;
	private static final int MINIMUM_HEIGHT = 200;
	
	
	protected static String OK = null;
	protected static String CANCEL = null;
	protected static String RESTORE_TO_GLOBAL = null;
	
	private static String LINE_TERMINATOR = null;
	private static String ENCODING_WHEN_SAVING = null;
	private static String FORMAT_WHEN_SAVING = null;
	private static String OWNER_NAME = null;
	private static String OWNER_EMAIL = null;
	private static String APPLY_FONT_STYLE_FOR_COMMENTS = null;
	private static String APPLY_FONT_STYLE_FOR_EDITABILITY = null;
	private static String APPLY_FONT_STYLE_FOR_MOVEABILITY = null;
	private static String CREATION_DATE = null;
	private static String MODIFICATION_DATE = null;
	private static String USE_CREATE_MOD_DATES = null;
	private static String CREATE_MOD_DATES_FORMAT = null;
	
	private static String IS_USING_APPLICATION_PREFS = null;
	private static String IS_USING_DOCUMENT_PREFS = null;
	
	// GUI Elements
	protected JButton buttonOK = null;
	protected JButton buttonCancel = null;
	protected JButton buttonRestoreToGlobal = null;
	
	protected JComboBox lineEndComboBox = null;
	protected JComboBox saveEncodingComboBox = null;
	protected JComboBox saveFormatComboBox = null;
	protected JTextField ownerNameField = null;
	protected JTextField ownerEmailField = null;
	protected JCheckBox applyFontStyleForCommentsCheckBox = null;
	protected JCheckBox applyFontStyleForEditabilityCheckBox = null;
	protected JCheckBox applyFontStyleForMoveabilityCheckBox = null;
	protected JCheckBox useCreateModDatesCheckBox = null;
	protected JTextField createModDatesFormatField = null;
	
	protected JLabel creationDateLabel = null;
	protected JLabel modificationDateLabel = null;
	protected JLabel isInheritingPrefsLabel = null;
	
	protected ComboBoxListener lineEndComboBoxListener = null;
	protected ComboBoxListener saveEncodingComboBoxListener = null;
	protected ComboBoxListener saveFormatComboBoxListener = null;
	protected TextFieldListener ownerNameTextFieldListener = null;
	protected TextFieldListener ownerEmailTextFieldListener = null;
	protected CheckboxListener applyFontStyleForCommentsCheckBoxListener = null;
	protected CheckboxListener applyFontStyleForEditabilityCheckBoxListener = null;
	protected CheckboxListener applyFontStyleForMoveabilityCheckBoxListener = null;
	protected CheckboxListener useCreateModDatesCheckBoxListener = null;
	protected TextFieldListener createModDatesFormatTextFieldListener = null;
	
	// The Constructors
	public DocumentSettingsView() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}
	
	private void initialize() {
		OK =                               GUITreeLoader.reg.getText("ok");
		CANCEL =                           GUITreeLoader.reg.getText("cancel");
		RESTORE_TO_GLOBAL =                GUITreeLoader.reg.getText("restore_to_application_preferences");
		
		LINE_TERMINATOR =                  GUITreeLoader.reg.getText("line_terminator");
		ENCODING_WHEN_SAVING =             GUITreeLoader.reg.getText("encoding_when_saving");
		FORMAT_WHEN_SAVING =               GUITreeLoader.reg.getText("format_when_saving");
		OWNER_NAME =                       GUITreeLoader.reg.getText("owner_name");
		OWNER_EMAIL =                      GUITreeLoader.reg.getText("owner_email");
		APPLY_FONT_STYLE_FOR_COMMENTS =    GUITreeLoader.reg.getText("apply_font_style_for_comments");
		APPLY_FONT_STYLE_FOR_EDITABILITY = GUITreeLoader.reg.getText("apply_font_style_for_editability");
		APPLY_FONT_STYLE_FOR_MOVEABILITY = GUITreeLoader.reg.getText("apply_font_style_for_moveability");
		CREATION_DATE =                    GUITreeLoader.reg.getText("creation_date");
		MODIFICATION_DATE =                GUITreeLoader.reg.getText("modification_date");
		USE_CREATE_MOD_DATES =             GUITreeLoader.reg.getText("use_create_mod_dates"); // ???
		CREATE_MOD_DATES_FORMAT =          GUITreeLoader.reg.getText("create_mod_dates_format"); // ???
		
		IS_USING_APPLICATION_PREFS =       GUITreeLoader.reg.getText("is_using_application_prefs");
		IS_USING_DOCUMENT_PREFS =          GUITreeLoader.reg.getText("is_using_document_prefs");
		
		buttonOK =              new JButton(OK);
		buttonCancel =          new JButton(CANCEL);
		buttonRestoreToGlobal = new JButton(RESTORE_TO_GLOBAL);
		
		lineEndComboBox =                      new JComboBox(PlatformCompatibility.PLATFORM_IDENTIFIERS);
		saveEncodingComboBox =                 new JComboBox();
		saveFormatComboBox =                   new JComboBox();
		ownerNameField =                       new JTextField(10);
		ownerEmailField =                      new JTextField(10);
		applyFontStyleForCommentsCheckBox =    new JCheckBox();
		applyFontStyleForEditabilityCheckBox = new JCheckBox();
		applyFontStyleForMoveabilityCheckBox = new JCheckBox();
		useCreateModDatesCheckBox =            new JCheckBox();
		createModDatesFormatField =            new JTextField(10);
		
		creationDateLabel =      new JLabel(" ");
		modificationDateLabel =  new JLabel(" ");
		isInheritingPrefsLabel = new JLabel(" ");
		
		lineEndComboBoxListener =                      new ComboBoxListener(lineEndComboBox, null);
		saveEncodingComboBoxListener =                 new ComboBoxListener(saveEncodingComboBox, null);
		saveFormatComboBoxListener =                   new ComboBoxListener(saveFormatComboBox, null);
		ownerNameTextFieldListener =                   new TextFieldListener(ownerNameField, null);
		ownerEmailTextFieldListener =                  new TextFieldListener(ownerEmailField, null);
		applyFontStyleForCommentsCheckBoxListener =    new CheckboxListener(applyFontStyleForCommentsCheckBox, null);
		applyFontStyleForEditabilityCheckBoxListener = new CheckboxListener(applyFontStyleForEditabilityCheckBox, null);
		applyFontStyleForMoveabilityCheckBoxListener = new CheckboxListener(applyFontStyleForMoveabilityCheckBox, null);
		useCreateModDatesCheckBoxListener =            new CheckboxListener(useCreateModDatesCheckBox, null);
		createModDatesFormatTextFieldListener =        new TextFieldListener(createModDatesFormatField, null);
		
		// Setup ComboBoxes
		for (int i = 0, limit = Preferences.ENCODINGS.size(); i < limit; i++) {
			saveEncodingComboBox.addItem((String) Preferences.ENCODINGS.get(i));
		}
		
		for (int i = 0, limit = Preferences.FILE_FORMATS_SAVE.size(); i < limit; i++) {
			saveFormatComboBox.addItem((String) Preferences.FILE_FORMATS_SAVE.get(i));
		}
		
		// Add Listeners
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		buttonRestoreToGlobal.addActionListener(this);
		buttonRestoreToGlobal.setToolTipText(GUITreeLoader.reg.getText("tooltip_restore_to_global"));
		
		lineEndComboBox.addItemListener(lineEndComboBoxListener);
		saveEncodingComboBox.addItemListener(saveEncodingComboBoxListener);
		saveFormatComboBox.addItemListener(saveFormatComboBoxListener);
		ownerNameField.addFocusListener(ownerNameTextFieldListener);
		ownerEmailField.addFocusListener(ownerEmailTextFieldListener);
		applyFontStyleForCommentsCheckBox.addActionListener(applyFontStyleForCommentsCheckBoxListener);
		applyFontStyleForEditabilityCheckBox.addActionListener(applyFontStyleForEditabilityCheckBoxListener);
		applyFontStyleForMoveabilityCheckBox.addActionListener(applyFontStyleForMoveabilityCheckBoxListener);
		useCreateModDatesCheckBox.addActionListener(useCreateModDatesCheckBoxListener);
		createModDatesFormatField.addFocusListener(createModDatesFormatTextFieldListener);
		
		
		// Define the top panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		
		AbstractPreferencesPanel.addSingleItemCentered(isInheritingPrefsLabel,               topPanel);
		AbstractPreferencesPanel.addPreferenceItem(CREATION_DATE,     creationDateLabel,     topPanel);
		AbstractPreferencesPanel.addPreferenceItem(MODIFICATION_DATE, modificationDateLabel, topPanel);
		
		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		
		// Define the Center Panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		
		AbstractPreferencesPanel.addPreferenceItem(LINE_TERMINATOR,                  lineEndComboBox,                      centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(ENCODING_WHEN_SAVING,             saveEncodingComboBox,                 centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(FORMAT_WHEN_SAVING,               saveFormatComboBox,                   centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(OWNER_NAME,                       ownerNameField,                       centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(OWNER_EMAIL,                      ownerEmailField,                      centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(APPLY_FONT_STYLE_FOR_COMMENTS,    applyFontStyleForCommentsCheckBox,    centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(APPLY_FONT_STYLE_FOR_EDITABILITY, applyFontStyleForEditabilityCheckBox, centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(APPLY_FONT_STYLE_FOR_MOVEABILITY, applyFontStyleForMoveabilityCheckBox, centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(USE_CREATE_MOD_DATES,             useCreateModDatesCheckBox,            centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(CREATE_MOD_DATES_FORMAT,          createModDatesFormatField,            centerPanel);
		
		JScrollPane jsp = new JScrollPane(centerPanel);
		
		getContentPane().add(jsp, BorderLayout.CENTER);
		
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		
		bottomPanel.add(buttonRestoreToGlobal);
		bottomPanel.add(buttonOK);
		bottomPanel.add(buttonCancel);
		
		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	
	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	// Configuration
	private DocumentSettings docSettings = null;
	
	public void configure(DocumentSettings docSettings) {
		
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		this.docSettings = docSettings;
		
		// Set the Preference Objects on the Listeners so that changes are always sent to the document level Preferences.
		lineEndComboBoxListener.setPreference(docSettings.lineEnd);
		saveEncodingComboBoxListener.setPreference(docSettings.saveEncoding);
		saveFormatComboBoxListener.setPreference(docSettings.saveFormat);
		ownerNameTextFieldListener.setPreference(docSettings.ownerName);
		ownerEmailTextFieldListener.setPreference(docSettings.ownerEmail);
		applyFontStyleForCommentsCheckBoxListener.setPreference(docSettings.applyFontStyleForComments);
		applyFontStyleForEditabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForEditability);
		applyFontStyleForMoveabilityCheckBoxListener.setPreference(docSettings.applyFontStyleForMoveability);
		useCreateModDatesCheckBoxListener.setPreference(docSettings.useCreateModDates);
		createModDatesFormatTextFieldListener.setPreference(docSettings.createModDatesFormat);
		
		if (docSettings.useDocumentSettings()) {
			isInheritingPrefsLabel.setText(IS_USING_DOCUMENT_PREFS);
			buttonRestoreToGlobal.setEnabled(true);
		} else {
			isInheritingPrefsLabel.setText(IS_USING_APPLICATION_PREFS);
			buttonRestoreToGlobal.setEnabled(false);
			docSettings.syncToGlobal();
		}
		syncToDocumentSettings();
	}
	
	public void configureAndShow(DocumentSettings docSettings) {
		configure(docSettings);
		super.show();
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		} else if (e.getActionCommand().equals(RESTORE_TO_GLOBAL)) {
			restoreToGlobal();
		}
	}
	
	private void ok() {
		docSettings.setUseDocumentSettings(true);
		applyChanges();
		hide();
	}
	
	private void cancel() {
		hide();
	}
	
	private void restoreToGlobal() {
		// We should no longer use document settings because the user explicitly said Restore to Global to them.
		docSettings.syncToGlobal();
		updateGUI();
		docSettings.restoreTemporaryToCurrent();
		docSettings.setUseDocumentSettings(false);
		hide();
	}
	
	private void applyChanges() {
		updateGUI();
		
		// Record all the changes.
		docSettings.getLineEnd().applyTemporaryToCurrent();
		docSettings.getSaveEncoding().applyTemporaryToCurrent();
		docSettings.getSaveFormat().applyTemporaryToCurrent();
		docSettings.getOwnerName().applyTemporaryToCurrent();
		docSettings.getOwnerEmail().applyTemporaryToCurrent();
		docSettings.getApplyFontStyleForComments().applyTemporaryToCurrent();
		docSettings.getApplyFontStyleForEditability().applyTemporaryToCurrent();
		docSettings.getApplyFontStyleForMoveability().applyTemporaryToCurrent();
		docSettings.getUseCreateModDates().applyTemporaryToCurrent();
		docSettings.getCreateModDatesFormat().applyTemporaryToCurrent();
		docSettings.updateSimpleDateFormat(docSettings.getCreateModDatesFormat().cur);
	}
	
	private void updateGUI() {
		// If anything has changed then mark the doc as modified.
		if (!docSettings.getLineEnd().cur.equals(docSettings.getLineEnd().tmp)
			|| !docSettings.getSaveEncoding().cur.equals(docSettings.getSaveEncoding().tmp)
			|| !docSettings.getSaveFormat().cur.equals(docSettings.getSaveFormat().tmp)
			|| !docSettings.getOwnerName().cur.equals(docSettings.getOwnerName().tmp)
			|| !docSettings.getOwnerEmail().cur.equals(docSettings.getOwnerEmail().tmp)
			|| !docSettings.getApplyFontStyleForComments().cur == docSettings.getApplyFontStyleForComments().tmp
			|| !docSettings.getApplyFontStyleForEditability().cur == docSettings.getApplyFontStyleForEditability().tmp
			|| !docSettings.getApplyFontStyleForMoveability().cur == docSettings.getApplyFontStyleForMoveability().tmp
			|| !docSettings.getUseCreateModDates().cur == docSettings.getUseCreateModDates().tmp
			|| !docSettings.getCreateModDatesFormat().cur.equals(docSettings.getCreateModDatesFormat().tmp)
		) {
			docSettings.getDocument().setModified(true);
		}
		
		// If anything has changed that would effect the GUI then redraw.
		boolean doRedraw = false;
		if (!docSettings.getApplyFontStyleForComments().cur == docSettings.getApplyFontStyleForComments().tmp
			|| !docSettings.getApplyFontStyleForEditability().cur == docSettings.getApplyFontStyleForEditability().tmp
			|| !docSettings.getApplyFontStyleForMoveability().cur == docSettings.getApplyFontStyleForMoveability().tmp
		) {
			doRedraw = true;
		}
		
		if (doRedraw) {
			docSettings.getDocument().panel.layout.redraw();
		}
	}
	
	// Syncing Methods
	private void syncToDocumentSettings() {
		docSettings.getLineEnd().restoreTemporaryToCurrent();
		lineEndComboBox.setSelectedItem(docSettings.getLineEnd().tmp);
		
		docSettings.getSaveEncoding().restoreTemporaryToCurrent();
		saveEncodingComboBox.setSelectedItem(docSettings.getSaveEncoding().tmp);
		
		docSettings.getSaveFormat().restoreTemporaryToCurrent();
		saveFormatComboBox.setSelectedItem(docSettings.getSaveFormat().tmp);
		
		docSettings.getOwnerName().restoreTemporaryToCurrent();
		ownerNameField.setText(docSettings.getOwnerName().tmp);
		
		docSettings.getOwnerEmail().restoreTemporaryToCurrent();
		ownerEmailField.setText(docSettings.getOwnerEmail().tmp);
		
		docSettings.getApplyFontStyleForComments().restoreTemporaryToCurrent();
		applyFontStyleForCommentsCheckBox.setSelected(docSettings.getApplyFontStyleForComments().tmp);
		
		docSettings.getApplyFontStyleForEditability().restoreTemporaryToCurrent();
		applyFontStyleForEditabilityCheckBox.setSelected(docSettings.getApplyFontStyleForEditability().tmp);
		
		docSettings.getApplyFontStyleForMoveability().restoreTemporaryToCurrent();
		applyFontStyleForMoveabilityCheckBox.setSelected(docSettings.getApplyFontStyleForMoveability().tmp);
		
		docSettings.getUseCreateModDates().restoreTemporaryToCurrent();
		useCreateModDatesCheckBox.setSelected(docSettings.getUseCreateModDates().tmp);
		
		docSettings.getCreateModDatesFormat().restoreTemporaryToCurrent();
		createModDatesFormatField.setText(docSettings.getCreateModDatesFormat().tmp);
		
		creationDateLabel.setText(docSettings.getDateCreated());
		modificationDateLabel.setText(docSettings.getDateModified());
	}
}
