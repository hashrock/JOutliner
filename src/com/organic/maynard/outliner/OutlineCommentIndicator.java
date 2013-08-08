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

import com.organic.maynard.imaging.ImageFilters;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.15 $, $Date: 2002/12/19 00:28:23 $
 */
 
public class OutlineCommentIndicator extends AbstractOutlineIndicator {
	
	// Class Fields
	public static final ImageIcon ICON_IS_NOT_PROPERTY = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/is_not_commented.gif"));
	public static ImageIcon ICON_IS_PROPERTY = null;
	public static ImageIcon ICON_IS_PROPERTY_INHERITED = null;
	public static ImageIcon ICON_IS_NOT_PROPERTY_INHERITED = null;
	
	public static int TRUE_WIDTH = ICON_IS_NOT_PROPERTY.getIconWidth();
	
	public static int WIDTH_DEFAULT = ICON_IS_NOT_PROPERTY.getIconWidth() + AbstractOutlineIndicator.SPACING;
	public static int BUTTON_WIDTH = WIDTH_DEFAULT;
	public static int BUTTON_HEIGHT = ICON_IS_NOT_PROPERTY.getIconHeight();
	
	
	// The Constructor
	public OutlineCommentIndicator(OutlinerCellRendererImpl renderer) {
		super(renderer, GUITreeLoader.reg.getText("tooltip_toggle_comment"));
	}
	
	// Misc Methods
        @Override
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
		// Create a buffered image from the commented image.
		Image notCommentedImage = ICON_IS_NOT_PROPERTY.getImage();
		BufferedImage image = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(notCommentedImage,0,0,Outliner.outliner);
		
		// Create Buffered Image for the derived images.
		BufferedImage commentedImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, image.getType());
		
		// Define a transforamtion to rotate the closed image to create the open image.
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getRotateInstance((java.lang.Math.PI), TRUE_WIDTH/2, BUTTON_HEIGHT/2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		at.filter(image, commentedImage);
		
		Color c = Preferences.getPreferenceColor(Preferences.TEXTAREA_COMMENT_COLOR).cur;
		int hexColor = ((c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue());
		
		RGBImageFilter redFilter = ImageFilters.getLightenFilter(hexColor);
		FilteredImageSource commentedSource = new FilteredImageSource(commentedImage.getSource(), redFilter);
		Image commentedImage2 = Outliner.outliner.createImage(commentedSource);
		
		ICON_IS_PROPERTY = new ImageIcon(commentedImage2);
		
		// Lighten color to inherited versions
		RGBImageFilter lightenFilter = ImageFilters.getLightenFilter(0x00cccccc);
		FilteredImageSource commentedInheritedSource = new FilteredImageSource(commentedImage2.getSource(), lightenFilter);
		FilteredImageSource notCommentedInheritedSource = new FilteredImageSource(image.getSource(), lightenFilter);
		Image commentedInheritedImage = Outliner.outliner.createImage(commentedInheritedSource);
		Image notCommentedInheritedImage = Outliner.outliner.createImage(notCommentedInheritedSource);
		
		ICON_IS_PROPERTY_INHERITED = new ImageIcon(commentedInheritedImage);
		ICON_IS_NOT_PROPERTY_INHERITED = new ImageIcon(notCommentedInheritedImage);
	}
}