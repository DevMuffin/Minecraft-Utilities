package com.dungeonbuilder.utils.java;

public class MutableInt {

	private int i;

	public MutableInt(int i) {
		this.i = i;
	}

	public MutableInt() {
		this(0);
	}

	public void increment() {
		this.i++;
	}

	public void decrement() {
		this.i--;
	}

	public void setValue(int i) {
		this.i = i;
	}

	public int getValue() {
		return this.i;
	}

	public int getValThenInc() {
		return this.i++;
	}
}
