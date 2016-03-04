package org.praisenter.javafx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

// Simple class to draw stacked lines to look like a sorting icon
public final class SortGraphic extends Pane {
	final BooleanProperty flip = new SimpleBooleanProperty(); 
	
	public SortGraphic(double width, double x, double y, double lineWidth, double spacing, Color color) {
		final double w = width;
		final double h = lineWidth * 3 + spacing * 2;
		final double dx = Math.floor(0.30 * width);
		
		double ex = x + (width - dx);
		
		Line line1 = new Line(x, y, ex, y);
		line1.setStroke(color);
		line1.setStrokeWidth(lineWidth);
		line1.setStrokeType(StrokeType.CENTERED);
		
		ex -= dx;
		y += spacing;
		Line line2 = new Line(x, y, ex, y);
		line2.setStroke(color);
		line2.setStrokeWidth(lineWidth);
		line2.setStrokeType(StrokeType.CENTERED);
		
		ex -= dx;
		y += spacing;
		Line line3 = new Line(x, y, ex, y);
		line3.setStroke(color);
		line3.setStrokeWidth(lineWidth);
		line3.setStrokeType(StrokeType.CENTERED);
		
		this.getChildren().addAll(line1, line2, line3);
//		this.setBorder(Testing.border(Color.YELLOW));
		this.setPrefSize(w, h);
		
		this.flip.addListener((obs, ov, nv) -> {
			this.setScaleY(-1 * this.getScaleY());
		});
	}
	
	public BooleanProperty flipProperty() {
		return this.flip;
	}
	
	public boolean isFlipped() {
		return this.flip.get();
	}
	
	public void setFlipped(boolean flag) {
		this.flip.set(flag);
	}
}
