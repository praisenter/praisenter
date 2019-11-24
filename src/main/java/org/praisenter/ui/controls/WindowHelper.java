package org.praisenter.ui.controls;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

public final class WindowHelper {
	private WindowHelper() {}
	
	public static final Scene createSceneWithOwnerCss(Parent root, Window owner) {
		Scene scene = new Scene(root);
		if (owner != null) {
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
		}
		return scene;
	}
	
	public static final void centerOnParent(Window parent, Window child) {
		double px = parent.getX();
		double py = parent.getY();
		double pw = parent.getWidth();
		double ph = parent.getHeight();
		
		double cw = child.getWidth();
		double ch = child.getHeight();
		
		double cx = px + pw / 2.0  - cw / 2.0;
		double cy = py + ph / 2.0  - ch / 2.0;
		
		child.setX(cx);
		child.setY(cy);
	}
	
	public static final void centerOnParent(Window parent, Dialog<?> child) {
		double px = parent.getX();
		double py = parent.getY();
		double pw = parent.getWidth();
		double ph = parent.getHeight();
		
		double cw = child.getWidth();
		double ch = child.getHeight();
		
		double cx = px + pw / 2.0  - cw / 2.0;
		double cy = py + ph / 2.0  - ch / 2.0;
		
		child.setX(cx);
		child.setY(cy);
	}
}
