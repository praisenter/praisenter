package org.praisenter.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.ui.Praisenter;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class WindowHelper {
	private WindowHelper() {}
	
	private static final List<Image> ICONS;
	
	static {
		// load window icons once
		ICONS = new ArrayList<>();
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon16x16alt.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon32x32.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon48x48.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon64x64.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon96x96.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon128x128.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon256x256.png").toExternalForm()));
		ICONS.add(new Image(Praisenter.class.getResource("/org/praisenter/logo/icon512x512.png").toExternalForm()));
	}
	
	public static final Scene createSceneWithOwnerCss(Parent root, Window owner) {
		Scene scene = new Scene(root);
		if (owner != null) {
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
		}
		return scene;
	}
	
	public static final void inheritStylesheets(Scene scene, Window owner) {
		if (owner != null) {
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
		}
	}
	
	public static final void inheritPseudoStates(Parent root, Window owner) {
		for (var pseudo : owner.getScene().getRoot().getPseudoClassStates()) {
			root.pseudoClassStateChanged(pseudo, true);
		}
	}
	
	public static final void setIcons(Stage stage) {
    	stage.getIcons().addAll(ICONS);
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
