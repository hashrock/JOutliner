/**
 * PdbErrorHandler.java
 * 
 * Interface for classes that handle errors in parsing Palm pdb files
 *
 * 
 * Members
 *	interfaces
 *		instance
 *			public
 *				public abstract void warning (JoeException someException)
 *				public abstract void error (JoeException someException)
 *				public abstract void fatalError (JoeException someException)
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/22/01 3:35PM
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
.*/
 
package com.organic.maynard.outliner.io.formats;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;

// interface for Pdb parser error handlers
public interface PdbErrorHandler  {
	
	public abstract void warning (JoeException someException);
		
	public abstract void error (JoeException someException);
		
	public abstract void fatalError (JoeException someException);
		
	
	} // end interface PdbErrorHandler
	
