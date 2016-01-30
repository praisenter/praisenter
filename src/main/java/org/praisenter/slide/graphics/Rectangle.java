package org.praisenter.slide.graphics;

public class Rectangle {
	final int x;
	final int y;
	final int width;
	final int height;
	
	public Rectangle(int width, int height) {
		super();
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
