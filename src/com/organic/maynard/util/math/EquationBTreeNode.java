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
import com.organic.maynard.util.*;

public class EquationBTreeNode {
	public static final String ADD = "+";
	public static final String SUBTRACT = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";	
	
	protected EquationBTreeNode parent = null;
	protected EquationBTreeNode child1 = null;
	protected EquationBTreeNode child2 = null;
	
	protected String op = null;
	protected double value = 0;

	// The Constructors
	public EquationBTreeNode() {
		this(0);
	}

	public EquationBTreeNode(double value) {
		this(ADD,value);
	}
	
	public EquationBTreeNode(String op, double value) {
		this(op,value,null,null);
	}

	public EquationBTreeNode(String op, EquationBTreeNode child1, EquationBTreeNode child2) {
		this(op,0,child1,child2);
	}

	public EquationBTreeNode(String op, double value, EquationBTreeNode child1, EquationBTreeNode child2) {
		setOperator(op);
		setValue(value);
		setChild1(child1);
		setChild2(child2);
	}
	
	// The Structure Accessors
	public synchronized void setParent(EquationBTreeNode node) {this.parent = node;}
	public synchronized EquationBTreeNode getParent() {return parent;}
	
	public synchronized void setChild1(EquationBTreeNode node) {
		if (node != null) {
			node.setParent(this);
		}
		this.child1 = node;
	}
	public synchronized EquationBTreeNode getChild1() {return child1;}

	public synchronized void setChild2(EquationBTreeNode node) {
		if (node != null) {
			node.setParent(this);
		}
		this.child2 = node;
	}
	public synchronized EquationBTreeNode getChild2() {return child2;}
	
	// The Equation Accessors
	public synchronized void setValue(double value) {this.value = value;}
	public synchronized double getValue() {return value;}
	
	public synchronized void setOperator(String op) {
		if (op.equals(ADD)) {
			this.op = ADD;
		} else if (op.equals(SUBTRACT)) {
			this.op = SUBTRACT;
		} else if (op.equals(MULTIPLY)) {
			this.op = MULTIPLY;
		} else if (op.equals(DIVIDE)) {
			this.op = DIVIDE;
		} else {
			System.out.println("Undefined Operator in EquationBTreeNode.");
		}
	}
	public synchronized String getOperator() {return op;}
	
	// The Equation Computation
	public synchronized double compute() {
		if ((getChild1() != null) && (getChild2() != null)) {
			double val1 = getChild1().compute();
			double val2 = getChild2().compute();
		
			if (getOperator().equals(ADD)) {
				setValue(val1 + val2);
			} else if (getOperator().equals(SUBTRACT)) {
				setValue(val1 - val2);
			} else if (getOperator().equals(MULTIPLY)) {
				setValue(val1 * val2);
			} else if (getOperator().equals(DIVIDE)) {
				setValue(val1 / val2);
			}
		}
		
		return getValue();
	}
}