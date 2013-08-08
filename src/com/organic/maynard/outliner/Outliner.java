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

package com.organic.maynard.outliner;

import com.organic.maynard.io.FileTools;
import com.organic.maynard.outliner.dom.DocumentRepository;
import com.organic.maynard.outliner.guitree.GUITreeComponent;
import com.organic.maynard.outliner.guitree.GUITreeComponentRegistry;
import com.organic.maynard.outliner.guitree.GUITreeLoader;
import com.organic.maynard.outliner.io.FileFormatManager;
import com.organic.maynard.outliner.io.FileProtocol;
import com.organic.maynard.outliner.io.FileProtocolManager;
import com.organic.maynard.outliner.io.LoadFileFormatClassCommand;
import com.organic.maynard.outliner.io.LoadFileProtocolClassCommand;
import com.organic.maynard.outliner.menus.OutlinerDesktopMenuBar;
import com.organic.maynard.outliner.menus.file.FileMenu;
import com.organic.maynard.outliner.menus.file.QuitMenuItem;
import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.PropertyContainerUtil;
import com.organic.maynard.outliner.util.find.FindReplaceFrame;
import com.organic.maynard.outliner.util.find.FindReplaceResultsDialog;
import com.organic.maynard.outliner.util.preferences.IntRangeValidator;
import com.organic.maynard.outliner.util.preferences.PreferenceInt;
import com.organic.maynard.outliner.util.preferences.Preferences;
import com.organic.maynard.outliner.util.preferences.SetPrefCommand;
import com.organic.maynard.util.Command;
import com.organic.maynard.util.CommandParser;
import com.organic.maynard.util.UnknownCommandException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import org.xml.sax.Attributes;

/**
 * @author $Author: maynardd $
 * @version $Revision: 1.89 $, $Date: 2004/03/22 04:48:03 $
 */

public class Outliner extends JFrame implements ClipboardOwner, GUITreeComponent, JoeXMLConstants {

	// Constants
	public static final boolean DEBUG = false;

	// Language Handling
	public static String LANGUAGE = "en"; // Defaults to English.

	// Splash Screen
	//public static SplashScreen splash = new SplashScreen(Thread.currentThread().getContextClassLoader().getResource("graphics/splash_screen.gif"));

	// Directory setup
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String USER_OUTLINER_DIR = "outliner";

	// [deric] 31sep2001, We want to be able to specify the graphics dir via a
	// Property in the packaging for MacOS X. If it isn't defined it defaults to
	// the usual "graphics".
	public static String PREFS_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.prefsdir", "prefs"))
			.append(FILE_SEPARATOR).toString();
	public static String USER_PREFS_DIR = PREFS_DIR;
	public static final String APP_DIR_PATH = new StringBuffer().append(System.getProperty("user.dir")).append(FILE_SEPARATOR).toString();
	public static String DOX_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.doxdir", "dox")).append(FILE_SEPARATOR)
			.toString();
	public static String EXTRAS_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.extrasdir", "extras"))
			.append(FILE_SEPARATOR).toString();
	public static String LIB_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.libdir", "lib")).append(FILE_SEPARATOR)
			.toString();
	public static String LOGS_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.logsdir", "logs"))
			.append(FILE_SEPARATOR).toString();
	public static String MODULES_DIR = new StringBuffer().append(System.getProperty("com.organic.maynard.outliner.Outliner.modulesdir", "modules"))
			.append(FILE_SEPARATOR).toString();

	// Find out if we've got a home directory to work with for user preferences,
	// if
	// not then we use the prefs dir as usual.
	static {
		String userhome = System.getProperty("user.home");
		if ((userhome != null) && !userhome.equals("")) {
			USER_PREFS_DIR = new StringBuffer().append(userhome).append(FILE_SEPARATOR).append(USER_OUTLINER_DIR).append(FILE_SEPARATOR).toString();
		}
	}

	// These prefs should be under the users prefs dir, or if no user prefs dir
	// exists then
	// they should be under the apps prefs dir.
	public static String MACROS_DIR = new StringBuffer().append(USER_PREFS_DIR).append("macros").append(FILE_SEPARATOR).toString();
	public static String MACROS_FILE = new StringBuffer().append(USER_PREFS_DIR).append("macros.txt").toString();
	public static String SCRIPTS_DIR = new StringBuffer().append(USER_PREFS_DIR).append("scripts").append(FILE_SEPARATOR).toString();
	public static String SCRIPTS_FILE = new StringBuffer().append(USER_PREFS_DIR).append("scripts.txt").toString();
	public static String FIND_REPLACE_FILE = new StringBuffer().append(USER_PREFS_DIR).append("find_replace.xml").toString();
	public static String CONFIG_FILE = new StringBuffer().append(USER_PREFS_DIR).append("config.txt").toString();
	public static String RECENT_FILES_FILE = new StringBuffer().append(USER_PREFS_DIR).append("recent_files.xml").toString();
	public static String OPEN_FILES_FILE = new StringBuffer().append(USER_PREFS_DIR).append("open_files.xml").toString();
	public static String ADDED_WORDS_FILE = new StringBuffer().append(USER_PREFS_DIR).append("added_words.dict").toString();

	// These dirs/files should always be under the apps prefs dir.
	public static String ENCODINGS_FILE = new StringBuffer().append(PREFS_DIR).append("encodings.txt").toString();
	public static String FILE_FORMATS_FILE = new StringBuffer().append(PREFS_DIR).append("file_formats.txt").toString();

	// This static block should be considered "installer" functionality.
	// At some point this could all be moved to an installer app.
	// Make the directories in case they don't exist.
	static {
		boolean isCreated = false;

		File prefsFile = new File(PREFS_DIR);
		isCreated = prefsFile.mkdirs();
		if (isCreated) {
			System.out.println("Created Preferences Directory: " + prefsFile.getPath());
		}

		File userPrefsFile = new File(USER_PREFS_DIR);
		isCreated = userPrefsFile.mkdirs();
		if (isCreated) {
			System.out.println("Created User Preferences Directory: " + userPrefsFile.getPath());
		}

		// Create macros directory it it doesn't exist.
		File macrosFile = new File(MACROS_DIR);
		isCreated = macrosFile.mkdirs();
		if (isCreated) {
			System.out.println("Created Macros Directory: " + macrosFile.getPath());
		}

		// Second, copy macros that don't exist.
		StringBuffer appendBuffer = new StringBuffer();

		// Copy over find_replace.xml from installation directory if it doesn't
		// exist in the user's home directory.
		File userFindReplaceFile = new File(FIND_REPLACE_FILE);
		if (!userFindReplaceFile.exists()) {
			System.out.println("Copying over find_replace config file: " + userFindReplaceFile.getPath());
			try {
				FileTools.copy(new File(PREFS_DIR + "find_replace.xml"), userFindReplaceFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Copy over added_words.dict from installation directory if it doesn't
		// exist in the user's home directory.
		File userAddedWordsFile = new File(ADDED_WORDS_FILE);
		if (!userAddedWordsFile.exists()) {
			System.out.println("Copying over added_words.dict file: " + userAddedWordsFile.getPath());
			try {
				FileTools.copy(new File(PREFS_DIR + "added_words.dict"), userAddedWordsFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// XML Parser
	// public static final Parser XML_PARSER = new com.jclark.xml.sax.Driver();
	public static final GUITreeLoader GUI_TREE_LOADER = new GUITreeLoader();

	// Command Parser
	public static final String COMMAND_PARSER_SEPARATOR = "|";
	public static final String COMMAND_SET = "set";
	public static final String COMMAND_MACRO_CLASS = "macro_class";
	public static final String COMMAND_MACRO = "macro";
	public static final String COMMAND_SCRIPT_CLASS = "script_class";
	public static final String COMMAND_SCRIPT = "script";
	public static final String COMMAND_FILE_FORMAT = "file_format";
	public static final String COMMAND_FILE_PROTOCOL = "file_protocol";

	public static final Command SET_PREF_COMMAND = new SetPrefCommand(COMMAND_SET);
	public static final Command LOAD_FILE_FORMAT_CLASS_COMMAND = new LoadFileFormatClassCommand(COMMAND_FILE_FORMAT);
	public static final Command LOAD_FILE_PROTOCOL_CLASS_COMMAND = new LoadFileProtocolClassCommand(COMMAND_FILE_PROTOCOL);

	public static final CommandParser PARSER = new CommandParser(COMMAND_PARSER_SEPARATOR);

	static {
		PARSER.addCommand(SET_PREF_COMMAND);
		PARSER.addCommand(LOAD_FILE_FORMAT_CLASS_COMMAND);
		PARSER.addCommand(LOAD_FILE_PROTOCOL_CLASS_COMMAND);
	}

	// GUI Objects
	public static FindReplaceFrame findReplace = null;
	public static FindReplaceResultsDialog findReplaceResultsDialog = null;
	public static FileFormatManager fileFormatManager = null;
	public static FileProtocolManager fileProtocolManager = null;
	public static Preferences prefs = null;
	public static HTMLViewerDialog html_viewer = null;

	// DOM Objects
	public static DocumentRepository documents = new DocumentRepository();

	// GUI Settings
	public static Outliner outliner = null;
	public static OutlinerDesktop desktop = new OutlinerDesktop();
	public static JScrollPane jsp = null;
	public static OutlinerDesktopMenuBar menuBar = null;
	public static DocumentStatistics statistics = null;
	public static DocumentAttributesView documentAttributes = null;

	// Main
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		init(args);
		

	}

	private static void init(String[] args) {
		// Property assignment to enable the top-level menubar in Mac OS X (does
		// nothing on other platforms)
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// This allows scrollbars to be resized while they are being dragged.
		// UIManager.put("ScrollBarUI",
		// PlatformCompatibility.getScrollBarUIClassName());

		// Set the preferred language to use.
		String ISO639LangCode = Locale.getDefault().getLanguage();
		if (ISO639LangCode != null && !ISO639LangCode.equals("")) {
			// Verify that gui_tree.xml file exists for this language.
			File file = new File(new StringBuffer().append(PREFS_DIR).append("gui_tree.").append(ISO639LangCode).append(".xml").toString());
			if (file.exists()) {
				LANGUAGE = ISO639LangCode;
			} else {
				System.out.println("WARNING: Default language code, " + ISO639LangCode + " does not have a gui_tree.xml file. Using default value instead.");
			}
		}

		// Load the Main GUITree
		String guiTreeFileName = new StringBuffer().append(PREFS_DIR).append("gui_tree.").append(LANGUAGE).append(".xml").toString();
		if (!GUI_TREE_LOADER.load(guiTreeFileName)) {
			System.out.println("GUI Loading Error: exiting.");
			return;
		}

		// Load the GUITree for any modules found
		File modulesDir = new File(MODULES_DIR);
		File[] modulesDirs = modulesDir.listFiles();

		if (modulesDirs != null) {
			for (int i = 0; i < modulesDirs.length; i++) {
				File moduleDir = modulesDirs[i];

				String moduleGuiTreeFileName = new StringBuffer().append(moduleDir.getPath()).append(FILE_SEPARATOR).append("gui_tree.").append(LANGUAGE)
						.append(".xml").toString();
				if (!GUI_TREE_LOADER.load(moduleGuiTreeFileName)) {
					System.out.println("GUI Loading Error. Skipping Module: " + moduleGuiTreeFileName);
					break;
				}
			}
		}


		// Open documents from open documents list.
		if (Preferences.getPreferenceBoolean(Preferences.OPEN_DOCS_ON_STARTUP).cur) {
			java.util.List openDocumentsList = PropertyContainerUtil.parseXML(Outliner.OPEN_FILES_FILE);
			for (int i = 0; i < openDocumentsList.size(); i++) {
				DocumentInfo docInfo = (DocumentInfo) openDocumentsList.get(i);
				String protocol_name = (String) docInfo.getProperty(DocumentInfo.KEY_PROTOCOL_NAME);
				FileProtocol fileProtocol = fileProtocolManager.getProtocol(protocol_name);
				FileMenu.openFile(docInfo, fileProtocol);
			}
		}

		// See if the command line included a file to be opened.
		if (args.length > 0) { // srk - put this test in -- 2002.09.03
			StringBuffer sb_filepath = new StringBuffer(args[0]);
			for (int i = 1; i < args.length; i++) {
				sb_filepath.append(" ").append(args[i]);
			}
			final String filepath = sb_filepath.toString();
			
			SwingUtilities.invokeLater(new Runnable() {
                                @Override
				public void run() {
					openFile(filepath);
				}
			});			
		}

		// For Debug Purposes
		if (Preferences.getPreferenceBoolean(Preferences.PRINT_ENVIRONMENT).cur) {
			Properties properties = System.getProperties();
			Enumeration names = properties.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				System.out.println(name + " : " + properties.getProperty(name));
			}
		}
	}

	private static void openFile(String filepath) {
		if ((filepath != null) && (!filepath.equals("")) && (!filepath.equals("%1"))) {
			try {
				// ensure that we have a full pathname [srk]
				filepath = new File(filepath).getCanonicalPath();

				// grab the file's extension
				String extension = filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length());

				// use the extension to figure out the file's format
				String fileFormat = Outliner.fileFormatManager.getOpenFileFormatNameForExtension(extension);

				// crank up a fresh docInfo struct
				DocumentInfo docInfo = new DocumentInfo();
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_PATH, filepath);
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE,
						Preferences.getPreferenceString(Preferences.OPEN_ENCODING).cur);
				PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_FILE_FORMAT, fileFormat);

				// try to open up the file
				FileMenu.openFile(docInfo, fileProtocolManager.getDefault());
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
		} else {
			// Create a Document. This must come after visiblity otherwise
			// the window won't be activated.
			if (Preferences.getPreferenceBoolean(Preferences.NEW_DOC_ON_STARTUP).cur) {
				OutlinerDocument doc = new OutlinerDocument("");
				Outliner.menuBar.windowMenu.changeToWindow(doc);
			}
		}
	}

	// GUITreeComponent interface
	private String id = null;

        @Override
	public String getGUITreeComponentID() {
		return this.id;
	}

        @Override
	public void setGUITreeComponentID(String id) {
		this.id = id;
	}

        @Override
	public void startSetup(Attributes atts) {
		outliner = this;

		setTitle(atts.getValue(A_TITLE));

		// Load Preferences
		loadPrefsFile(PARSER, ENCODINGS_FILE);

		// Setup the FileFormatManager and FileProtocolManager
		fileFormatManager = new FileFormatManager();
		fileProtocolManager = new FileProtocolManager();

		loadPrefsFile(PARSER, FILE_FORMATS_FILE);
	}

        @Override
	public void endSetup(Attributes atts) {
		// Set the Window Location.
		// Get Main Window Dimension out of the prefs.
		PreferenceInt pWidth = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_W);
		PreferenceInt pHeight = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_H);
		PreferenceInt pInitialPositionX = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_X);
		PreferenceInt pInitialPositionY = Preferences.getPreferenceInt(Preferences.MAIN_WINDOW_Y);

		int minimumWidth = ((IntRangeValidator) pWidth.getValidator()).getMin();
		int minimumHeight = ((IntRangeValidator) pHeight.getValidator()).getMin();
		int minimumInitialPositionX = ((IntRangeValidator) pInitialPositionX.getValidator()).getMin();
		int minimumInitialPositionY = ((IntRangeValidator) pInitialPositionY.getValidator()).getMin();

		int initialWidth = pWidth.cur;
		int initialHeight = pHeight.cur;
		int initialPositionX = pInitialPositionX.cur;
		int initialPositionY = pInitialPositionY.cur;

		// Make sure initial position isn't off screen, or even really close to
		// the edge.
		int bottom_left_inset = 100;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if (initialPositionX < minimumInitialPositionX) {
			initialPositionX = minimumInitialPositionX;
		}

		if (initialPositionX > (screenSize.width - bottom_left_inset)) {
			initialPositionX = screenSize.width - bottom_left_inset;
		}

		if (initialPositionY < minimumInitialPositionY) {
			initialPositionY = minimumInitialPositionY;
		}

		if (initialPositionY > (screenSize.height - bottom_left_inset)) {
			initialPositionY = screenSize.height - bottom_left_inset;
		}

		setLocation(initialPositionX, initialPositionY);

		addComponentListener(new WindowSizeManager(initialWidth, initialHeight, minimumWidth, minimumHeight));
		addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e) {
				QuitMenuItem item = (QuitMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.QUIT_MENU_ITEM);
				item.quit();
			}
		});

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Setup Desktop ScrollPane and set the ContentPane.
		jsp = new JScrollPane(desktop, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.addComponentListener(new DesktopScrollPaneComponentListener());
		setContentPane(jsp);

		// Set Frame Icon
		ImageIcon icon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/frame_icon.gif"));
		setIconImage(icon.getImage());

		// Initialize open/save_as/export/export_selection menus.
		fileProtocolManager.synchronizeDefault();
		fileProtocolManager.synchronizeMenus();

		// Setup the FindReplaceResultsDialog
		findReplaceResultsDialog = new FindReplaceResultsDialog();

		// Apply the Preference Settings
		Preferences.applyCurrentToApplication();

		setVisible(true); // Seems OK to do this now rather than at the end of
							// this method.

		Outliner.html_viewer = new HTMLViewerDialog();

		// Get rid of Splash Screen
		//Outliner.splash.dispose();

		// Generate Icons
		OutlineButton.createIcons();
	}

	// ClipboardOwner Interface
	public static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        @Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	// Misc Methods
	public static void loadPrefsFile(CommandParser parser, String filename) {
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

			String line = buffer.readLine();
			while (line != null) {
				try {
					parser.parse(line, true, true);
				} catch (UnknownCommandException uce) {
					System.out.println("Unknown Command");
				}

				line = buffer.readLine();
			}

			buffer.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("File Not Found: " + filename + "\n" + fnfe);
		} catch (Exception e) {
			System.err.println("Could not create FileReader: " + filename + "\n" + e);
		}
	}

	// all calls for a new JoeTree come thru here
	// if changing the class that's implementing JoeTree, do so here
	public static JoeTree newTree(OutlinerDocument document) {
		// JoeTree currently implemented by TreeContext
		if (document == null) {
			return new TreeContext();
		} else {
			return new TreeContext(document);
		}
	}

	// all calls for a new JoeNodeList come thru here
	// if changing the class that's implementing JoeNodeList, do so here
	public static JoeNodeList newNodeList(int initialCapacity) {
		// JoeNodeList currently implemented by NodeList
		return new NodeList(initialCapacity);
	}
}
