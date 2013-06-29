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
 
package com.organic.maynard.outliner.guitree;

import com.organic.maynard.outliner.*;
import java.util.*;

public class GUITreeComponentList {

	// Fields
	private GUITreeComponent components[];
	private int size;

	// Constructors
	public GUITreeComponentList() {
		this(10);
	}
   
	public GUITreeComponentList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
		}
		this.components = new GUITreeComponent[initialCapacity];
	}


	// Accessors
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public GUITreeComponent get(int index) {
		RangeCheck(index);
		return components[index];
	}

	public void set(int index, GUITreeComponent component) {
		RangeCheck(index);
		components[index] = component;
	}

	public void add(GUITreeComponent component) {
		ensureCapacity(size + 1);
		components[size++] = component;
	}
	
	public void add(int index, GUITreeComponent component) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		ensureCapacity(size + 1);
		System.arraycopy(components, index, components, index + 1, size - index);
		components[index] = component;
		size++;
	}

	public void remove(int index) {
		RangeCheck(index);

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(components, index + 1, components, index, numMoved);
		}
		components[--size] = null; 
	}

	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(components, toIndex, components, fromIndex, numMoved);

		int newSize = size - (toIndex - fromIndex);
		while (size != newSize) {
			components[--size] = null;
		}
	}

	public void trim(int newSize) {
		if (newSize > size || newSize < 0) {
			throw new IndexOutOfBoundsException("Index: " + newSize + ", Size: " + size);
		}
		
		while (size > newSize) {
			components[--size] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			components[i] = null;
		}

		size = 0;
	}

	// Index Of
	public boolean contains(GUITreeComponent component) {
		return indexOf(component) >= 0;
	}

	public int indexOf(GUITreeComponent component) {
		return firstIndexOf(component);
	}

	public int firstIndexOf(GUITreeComponent component) {
		for (int i = 0; i < size; i++) {
			if (components[i] == component) {
				return i;
			}
		}
		
		return -1;
	}

	public int lastIndexOf(GUITreeComponent component) {
		for (int i = size - 1; i >= 0; i--) {
			if (components[i] == component) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(GUITreeComponent component, int start, int end) {
		RangeCheck(start);
		RangeCheck(end);
		
		for (int i = start; i <= end; i++) {
			if (components[i] == component) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Data Conversion
	public GUITreeComponent[] toArray() {
		GUITreeComponent[] result = new GUITreeComponent[size];
		System.arraycopy(components, 0, result, 0, size);
		return result;
	}
    
    
	// Misc Methods
	private void RangeCheck(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	public void ensureCapacity(int minCapacity) {
		int oldCapacity = components.length;
		
		if (minCapacity > oldCapacity) {
			GUITreeComponent oldData[] = components;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			components = new GUITreeComponent[newCapacity];
			System.arraycopy(oldData, 0, components, 0, size);
		}
	}
}