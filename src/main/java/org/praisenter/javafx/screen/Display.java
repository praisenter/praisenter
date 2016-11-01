package org.praisenter.javafx.screen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

@XmlRootElement(name = "display")
@XmlAccessorType(XmlAccessType.NONE)
public final class Display {
	@XmlAttribute(name = "id")
	private final int id;
	
	@XmlAttribute(name = "x")
	private final int x;
	
	@XmlAttribute(name = "y")
	private final int y;
	
	@XmlAttribute(name = "w")
	private final int width;
	
	@XmlAttribute(name = "h")
	private final int height;

	private Display() {
		this.id = 0;
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	public Display(int id, Screen screen) {
		super();
		this.id = id;
		Rectangle2D bounds = screen.getBounds();
		this.x = (int)bounds.getMinX();
		this.y = (int)bounds.getMinY();
		this.width = (int)bounds.getWidth();
		this.height = (int)bounds.getHeight();
	}
	
	public int getId() {
		return id;
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
