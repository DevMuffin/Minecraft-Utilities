package com.dungeonbuilder.utils.java;

public class Point2D {

	public int x, y;

	private Point2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point2D() {
		this(0, 0);
	}

	public static Point2D at(int x, int y) {
		return new Point2D(x, y);
	}
}
