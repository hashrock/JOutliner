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
import java.text.SimpleDateFormat;
import java.util.*;

public class DocumentSettings {
	private OutlinerDocument doc = null;
	
	private boolean useDocumentSettings = false;
	
	// Editable Settings
	protected PreferenceLineEnding lineEnd = new PreferenceLineEnding(Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur, Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur, "");
	protected PreferenceString saveEncoding = new PreferenceString(Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur, Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur, "");
	protected PreferenceString saveFormat = new PreferenceString(Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur, Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur, "");
	protected PreferenceString ownerName = new PreferenceString(Preferences.getPreferenceString(Preferences.OWNER_NAME).cur, Preferences.getPreferenceString(Preferences.OWNER_NAME).cur, "");
	protected PreferenceString ownerEmail = new PreferenceString(Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur, Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur, "");
	protected PreferenceBoolean applyFontStyleForComments = new PreferenceBoolean(Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur, Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur, "");
	protected PreferenceBoolean applyFontStyleForEditability = new PreferenceBoolean(Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur, Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur, "");
	protected PreferenceBoolean applyFontStyleForMoveability = new PreferenceBoolean(Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur, Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur, "");
	protected PreferenceBoolean useCreateModDates = new PreferenceBoolean(Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES).cur, Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES).cur, ""); 
	protected PreferenceString createModDatesFormat = new PreferenceString(Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT).cur, Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT).cur, "");
	
	// Hidden Settings: Has application level settings.
	protected PreferenceString fileProtocol = new PreferenceString(Preferences.getPreferenceString(Preferences.FILE_PROTOCOL).cur, Preferences.getPreferenceString(Preferences.FILE_PROTOCOL).cur, "");
	
	// Hidden Settings: Document Level only. No Application level settings for these.
	private String dateCreated = new String("");
	private String dateModified = new String("");
	
	// TBD: This could have an app level value.
	protected SimpleDateFormat dateFormat = null;
	
	
	// The Constructors
	public DocumentSettings(OutlinerDocument document) {
		this.doc = document;
		updateSimpleDateFormat(createModDatesFormat.cur);
	}
	
	public void destroy() {
		doc = null;
		lineEnd = null;
		saveEncoding = null;
		saveFormat = null;
		ownerName = null;
		ownerEmail = null;
		dateCreated = null;
		dateModified = null;
		fileProtocol = null;
		applyFontStyleForComments = null;
		applyFontStyleForEditability = null;
		applyFontStyleForMoveability = null;
		useCreateModDates = null;
		createModDatesFormat = null;
		dateFormat = null;
	}
	
	public void syncToGlobal() {
		lineEnd.cur = Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END).cur;
		saveEncoding.cur = Preferences.getPreferenceString(Preferences.SAVE_ENCODING).cur;
		saveFormat.cur = Preferences.getPreferenceString(Preferences.SAVE_FORMAT).cur;
		ownerName.cur = Preferences.getPreferenceString(Preferences.OWNER_NAME).cur;
		ownerEmail.cur = Preferences.getPreferenceString(Preferences.OWNER_EMAIL).cur;
		applyFontStyleForComments.cur = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS).cur;
		applyFontStyleForEditability.cur = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY).cur;
		applyFontStyleForMoveability.cur = Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY).cur;
		useCreateModDates.cur = Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES).cur;
		createModDatesFormat.cur = Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT).cur;
	}
	
	public void restoreTemporaryToCurrent() {
		lineEnd.restoreTemporaryToCurrent();
		saveEncoding.restoreTemporaryToCurrent();
		saveFormat.restoreTemporaryToCurrent();
		ownerName.restoreTemporaryToCurrent();
		ownerEmail.restoreTemporaryToCurrent();
		applyFontStyleForComments.restoreTemporaryToCurrent();
		applyFontStyleForEditability.restoreTemporaryToCurrent();
		applyFontStyleForMoveability.restoreTemporaryToCurrent();
		useCreateModDates.restoreTemporaryToCurrent();
		createModDatesFormat.restoreTemporaryToCurrent();
	}
	
	
	// Accessors
	public OutlinerDocument getDocument() {return this.doc;}
	
	public boolean useDocumentSettings() {return this.useDocumentSettings;}
	public void setUseDocumentSettings(boolean useDocumentSettings) {this.useDocumentSettings = useDocumentSettings;}
	
	public String getDateCreated() {return this.dateCreated;}
	public void setDateCreated(String dateCreated) {this.dateCreated = dateCreated;}
	
	public String getDateModified() {return this.dateModified;}
	public void setDateModified(String dateModified) {this.dateModified = dateModified;}
	
	public PreferenceBoolean getUseCreateModDates() {
		if (useDocumentSettings()) {
			return this.useCreateModDates;
		} else {
			return Preferences.getPreferenceBoolean(Preferences.USE_CREATE_MOD_DATES);
		}
	}
	
	public PreferenceString getCreateModDatesFormat() {
		if (useDocumentSettings()) {
			return this.createModDatesFormat;
		} else {
			return Preferences.getPreferenceString(Preferences.CREATE_MOD_DATES_FORMAT);
		}
	}
	
	public PreferenceLineEnding getLineEnd() {
		if (useDocumentSettings()) {
			return this.lineEnd;
		} else {
			return Preferences.getPreferenceLineEnding(Preferences.SAVE_LINE_END);
		}
	}
	
	public PreferenceString getSaveEncoding() {
		if (useDocumentSettings()) {
			return this.saveEncoding;
		} else {
			return Preferences.getPreferenceString(Preferences.SAVE_ENCODING);
		}
	}
	
	public PreferenceString getSaveFormat() {
		if (useDocumentSettings()) {
			return this.saveFormat;
		} else {
			return Preferences.getPreferenceString(Preferences.SAVE_FORMAT);
		}
	}
	
	public PreferenceString getOwnerName() {
		if (useDocumentSettings()) {
			return this.ownerName;
		} else {
			return Preferences.getPreferenceString(Preferences.OWNER_NAME);
		}
	}
	
	public PreferenceString getOwnerEmail() {
		if (useDocumentSettings()) {
			return this.ownerEmail;
		} else {
			return Preferences.getPreferenceString(Preferences.OWNER_EMAIL);
		}
	}
	
	public PreferenceBoolean getApplyFontStyleForComments() {
		if (useDocumentSettings()) {
			return this.applyFontStyleForComments;
		} else {
			return Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_COMMENTS);
		}
	}
	
	public PreferenceBoolean getApplyFontStyleForEditability() {
		if (useDocumentSettings()) {
			return this.applyFontStyleForEditability;
		} else {
			return Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_EDITABILITY);
		}
	}
	
	public PreferenceBoolean getApplyFontStyleForMoveability() {
		if (useDocumentSettings()) {
			return this.applyFontStyleForMoveability;
		} else {
			return Preferences.getPreferenceBoolean(Preferences.APPLY_FONT_STYLE_FOR_MOVEABILITY);
		}
	}
	
	public PreferenceString getFileProtocol() {
		if (useDocumentSettings()) {
			return this.fileProtocol;
		} else {
			return Preferences.getPreferenceString(Preferences.FILE_PROTOCOL);
		}
	}
	
	
	// Date Methods
	public void updateSimpleDateFormat(String format) {
		this.dateFormat = new SimpleDateFormat(format);
		if (!Preferences.getPreferenceString(Preferences.TIME_ZONE_FOR_SAVING_DATES).cur.equals("")) {
			this.dateFormat.setTimeZone(TimeZone.getTimeZone(Preferences.getPreferenceString(Preferences.TIME_ZONE_FOR_SAVING_DATES).cur));
		}
	}
	
	public void show() {
		((DocumentSettingsView) GUITreeLoader.reg.get(GUITreeComponentRegistry.JDIALOG_DOCUMENT_SETTINGS_VIEW)).configureAndShow(this);
	}
}
