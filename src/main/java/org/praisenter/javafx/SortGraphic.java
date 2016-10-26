/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

/**
 * Simple node used to draw stacked lines representing a sort direction.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SortGraphic extends Pane {
	/** The class name for the lines */
	private static final String LINE_CLASS_NAME = "sort-graphic-line";
	
	/** True if the graphic is flipped about the y axis */
	final BooleanProperty flip = new SimpleBooleanProperty(); 
	
	/**
	 * Default constructor.
	 */
	public SortGraphic() {
		this(17, 0, 4, 2, 4);
	}
	
	/**
	 * Full constructor.
	 * @param width the width of the graphic
	 * @param x the x coordinate of the graphic
	 * @param y the y coordinate of the graphic
	 * @param lineWidth the line width of the lines
	 * @param spacing the spacing of the lines
	 */
	public SortGraphic(double width, double x, double y, double lineWidth, double spacing) {
		final double w = width;
		final double h = lineWidth * 3 + spacing * 2;
		final double dx = Math.floor(0.30 * width);
		
		double ex = x + (width - dx);
		
		Line line1 = new Line(x, y, ex, y);
		line1.getStyleClass().add(LINE_CLASS_NAME);
		line1.setStrokeWidth(lineWidth);
		line1.setStrokeType(StrokeType.CENTERED);
		
		ex -= dx;
		y += spacing;
		Line line2 = new Line(x, y, ex, y);
		line2.getStyleClass().add(LINE_CLASS_NAME);
		line2.setStrokeWidth(lineWidth);
		line2.setStrokeType(StrokeType.CENTERED);
		
		ex -= dx;
		y += spacing;
		Line line3 = new Line(x, y, ex, y);
		line3.getStyleClass().add(LINE_CLASS_NAME);
		line3.setStrokeWidth(lineWidth);
		line3.setStrokeType(StrokeType.CENTERED);
		
		this.getChildren().addAll(line1, line2, line3);
		this.setPrefSize(w, h);
		
		this.flip.addListener((obs, ov, nv) -> {
			this.setScaleY(-1 * this.getScaleY());
		});
	}
	
	/**
	 * Returns the flip property.
	 * @return BooleanProperty
	 */
	public BooleanProperty flipProperty() {
		return this.flip;
	}
	
	/**
	 * Returns true if the sort graphic is flipped.
	 * @return boolean
	 */
	public boolean isFlipped() {
		return this.flip.get();
	}
	
	/**
	 * Sets the flipped state of this sort graphic.
	 * @param flag true if flipped
	 */
	public void setFlipped(boolean flag) {
		this.flip.set(flag);
	}
}
