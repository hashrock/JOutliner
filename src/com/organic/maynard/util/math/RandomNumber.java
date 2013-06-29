/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util.math;

import java.util.*;

public class RandomNumber {
	// Distribution Constants
	public static final int FLAT = 0;
	public static final int TOP = 1;
	public static final int BOTTOM = 2;
	public static final int MIDDLE = 3;
	public static final int EDGES = 4;
	
	// Methods
	
	// Note: rounding errors at the upper and lower bounds are significant for small ranges.
	public static int get(int start, int end, int distribution, double factor) {
		if (end < start) {
			int temp = start;
			start = end;
			end = temp;
		}
		try {
			double rand = Math.random();
			switch (distribution) {
				case TOP:
					// factor == 1, no skewing
					// factor >= 2, skewed to top
					double topSkewed = Math.pow(rand, (1/factor)); 
					return (int) Math.floor(topSkewed * (end - start + 1)) + start;
					
				case BOTTOM:
					// factor == 1, no skewing
					// factor >= 2, skewed to bottom
					double bottomSkewed = 1 - Math.pow(rand, (1/factor)); 
					return (int) Math.floor(bottomSkewed * (end - start + 1)) + start;
					
				case MIDDLE:
					double middleSkewed = 0;
					if (rand < 0.5) {
						middleSkewed = Math.pow(rand*2, (1/factor))/2;
					} else {
						middleSkewed = (1 - Math.pow((rand - 0.5)*2, (1/factor)))/2 + 0.5;
					}
					return (int) Math.floor(middleSkewed * (end - start + 1)) + start;

				case EDGES:
					double edgeSkewed = 0;
					if (rand < 0.5) {
						edgeSkewed = (1 - Math.pow(rand*2, (1/factor)))/2;
					} else {
						edgeSkewed = (Math.pow((rand - 0.5)*2, (1/factor)))/2 + 0.5;
					}
					return (int) Math.floor(edgeSkewed * (end - start + 1)) + start;					
				case FLAT:
				
				default:
					return (int) Math.floor(Math.random() * (end - start + 1)) + start;
			}
		} catch (Exception ex) {
			return -1;
		}
	}
	
	public static int get(int start, int end) {
		return get(start,end,FLAT,1.0);
	}
}