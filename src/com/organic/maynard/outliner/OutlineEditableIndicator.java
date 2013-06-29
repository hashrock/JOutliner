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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.image.*;
import java.awt.geom.*;

import com.organic.maynard.imaging.ImageFilters;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.10 $, $Date: 2002/12/19 00:28:23 $
 */
 
public class OutlineEditableIndicator extends AbstractOutlineIndicator {
	
	// Class Fields
	public static final ImageIcon ICON_IS_NOT_PROPERTY = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/is_not_editable.gif"));
	public static final ImageIcon ICON_IS_PROPERTY = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/is_editable.gif"));
	public static ImageIcon ICON_IS_PROPERTY_INHERITED = null;
	public static ImageIcon ICON_IS_NOT_PROPERTY_INHERITED = null;
	
	public static int TRUE_WIDTH = ICON_IS_NOT_PROPERTY.getIconWidth();
	
	public static int WIDTH_DEFAULT = ICON_IS_NOT_PROPERTY.getIconWidth();
	public static int BUTTON_WIDTH = WIDTH_DEFAULT;
	public static int BUTTON_HEIGHT = ICON_IS_NOT_PROPERTY.getIconHeight();
	
	
	// The Constructor
	public OutlineEditableIndicator(OutlinerCellRendererImpl renderer) {
		super(renderer, GUITreeLoader.reg.getText("tooltip_toggle_editability"));
	}
	
	// Misc Methods
	public void updateIcon() {
		if(isProperty()) {
			if (isPropertyInherited()) {
				setIcon(ICON_IS_PROPERTY_INHERITED);
			} else {
				setIcon(ICON_IS_PROPERTY);
			}
		} else {
			if (isPropertyInherited()) {
				setIcon(ICON_IS_NOT_PROPERTY_INHERITED);
			} else {
				setIcon(ICON_IS_NOT_PROPERTY);
			}
		}
	}
	
	// Static Methods
	public static void createIcons() {
		// Create a buffered image from the is not property image.
		Image isNotPropertyImage = ICON_IS_NOT_PROPERTY.getImage();
		BufferedImage isNotImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gIsNotImage = isNotImage.createGraphics();
		gIsNotImage.drawImage(isNotPropertyImage,0,0,Outliner.outliner);
		
		// Create a buffered image from the is property image.
		Image isPropertyImage = ICON_IS_PROPERTY.getImage();
		BufferedImage isImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gIsImage = isImage.createGraphics();
		gIsImage.drawImage(isPropertyImage,0,0,Outliner.outliner);
		
		// Lighten color to inherited versions
		RGBImageFilter lightenFilter = ImageFilters.getLightenFilter(0x00cccccc);
		FilteredImageSource isNotPropertyInheritedSource = new FilteredImageSource(isNotImage.getSource(), lightenFilter);
		FilteredImageSource isPropertyInheritedSource = new FilteredImageSource(isImage.getSource(), lightenFilter);
		Image isNotPropertyInheritedImage = Outliner.outliner.createImage(isNotPropertyInheritedSource);
		Image isPropertyInheritedImage = Outliner.outliner.createImage(isPropertyInheritedSource);
		
		ICON_IS_NOT_PROPERTY_INHERITED = new ImageIcon(isNotPropertyInheritedImage);
		ICON_IS_PROPERTY_INHERITED = new ImageIcon(isPropertyInheritedImage);
	}
}