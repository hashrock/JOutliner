/**
 * BitsAndBytes class
 * 
 * Useful utilitiy methods for playin' with bits and bytes
 *
 * 
 * Members
 *	constants
 *		class
 *			public
 *				int HI_TO_LO
 *				int BIG_ENDIAN
 *				int LO_TO_HI
 *				int LITTLE_ENDIAN
 *
 *	methods
 *		class
 *			public
 *				long unsignedInt (byte[], int, int)
 *				int unsignedShort (byte[], int, int)
 *				short unsignedByte (byte[], int)
 *				int unsignedBytesToInt (byte[], int, int, int)
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/27/01 12:02AM
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
 
// we're part of this
package com.organic.maynard.outliner;

// we play with bits and bytes
public class BitsAndBytes  {
	
	// public class constants
	
	// byte order constants
	public static final int HI_TO_LO = 100 ;
	public static final int BIG_ENDIAN = 100 ;	// mac, 'nix/nux, motorola, palm
	
	public static final int LO_TO_HI =200 ;
	public static final int LITTLE_ENDIAN = 200 ;	// pc, intel
	
	// public class methods
	
	// grab a 4-byte unsigned value from an array of bytes
	public static long unsignedInt (byte[] byteArray, int selector, int byteOrdering)
		{
		// byteOrdering tells how bytes are stored
		// selector should always point to lowest byte position of range
		
		// local vars
		int a = 0 ;
		
		// case out on the storage scheme
		switch (byteOrdering) {
			
			case HI_TO_LO:	// aka BIG_ENDIAN
			
				a = ((int) (byteArray[selector++] & 0xFF)) << 24;
				a += ((int) (byteArray[selector++] & 0xFF)) << 16;
				a += ((int) (byteArray[selector++] & 0xFF)) << 8;
				a += ((int) (byteArray[selector] & 0xFF)) ;
				break ;
				
			case LO_TO_HI:	// aka LITTLE_ENDIAN
			
				a = ((int) (byteArray[selector++] & 0xFF)) ;
				a += ((int) (byteArray[selector++] & 0xFF)) << 8;
				a += ((int) (byteArray[selector++] & 0xFF)) << 16;
				a += ((int) (byteArray[selector] & 0xFF)) << 24;
				break ;
				
			default:
				break ;
				
			} // end switch
		
		// bye bye
		return a ;

		
		} // end method unsignedInt

	// grab a 2-byte unsigned value from an array of bytes
	public static int unsignedShort (byte[] byteArray, int selector, int byteOrdering)
		{
		// byteOrdering tells how bytes are stored
		// selector should always point to lowest byte position of range
		
		// local vars
		int a = 0 ;
		
		// case out on the storage scheme
		switch (byteOrdering) {
			
			case HI_TO_LO:	// aka BIG_ENDIAN
			
				a = ((int) (byteArray[selector++] & 0xFF)) << 8;
				a += ((int) (byteArray[selector] & 0xFF)) ;
				break ;
				
			case LO_TO_HI:	// aka LITTLE_ENDIAN
			
				a = ((int) (byteArray[selector++] & 0xFF)) ;
				a += ((int) (byteArray[selector++] & 0xFF)) << 8;
				break ;
				
			default:
				break ;
				
			} // end switch
		
		// bye bye
		return a ;
		
		} // end method unsignedShort


	// grab a 1-byte unsigned value from an array of bytes
	public static short unsignedByte (byte[] byteArray, int selector)
		{
		
//		short a = 	(short)(char)byteArray[selector] ;
		return (short) (byteArray[selector] & 0xFF);
		
		} // end method unsignedByte


	// grab a 1-, 2-,. or 3-byte unsigned value from an array of bytes
	public static int unsignedBytesToInt (byte[] byteArray, int selector, int howMany, int byteOrdering)
		{
		
		// byteOrdering 
		// selector should always point to lowest byte in array
		
		// local vars
		int a = 0 ;
		
		
		switch (byteOrdering) {
			
			case HI_TO_LO:	// aka BIG_ENDIAN
			
				switch (howMany) {
					
					case 1:
						return ((int) (byteArray[selector] & 0xFF)) ;
						
				
					case 2:
						a = ((int) (byteArray[selector++] & 0xFF)) << 8;
						a += ((int) (byteArray[selector] & 0xFF)) ;
						return a;
				
					case 3:
						a = ((int) (byteArray[selector++] & 0xFF)) << 16;
						a += ((int) (byteArray[selector++] & 0xFF)) << 8;
						a += ((int) (byteArray[selector] & 0xFF)) ;
						return a;
									
					default:
						return 0 ;
						
					} // end switch
					
								
			case LO_TO_HI:	// aka LOW_ENDIAN
			
				switch (howMany) {
					
					case 1:
						return ((int) (byteArray[selector] & 0xFF)) ;
						
				
					case 2:
						a = ((int) (byteArray[selector++] & 0xFF)) ;
						a += ((int) (byteArray[selector] & 0xFF)) << 8;
						return a;
				
					case 3:
						a = ((int) (byteArray[selector++] & 0xFF)) ;
						a += ((int) (byteArray[selector++] & 0xFF)) << 8;
						a += ((int) (byteArray[selector] & 0xFF)) << 16;
						return a;
									
					default:
						return 0 ;
						
					} // end switch
			default:
				return 0 ;
					
			} // end switch

		} // end method unsignedBytesToUInt;

	} // end class BitsAndBytes.java
	
