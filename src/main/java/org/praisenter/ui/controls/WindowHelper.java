package org.praisenter.ui.controls;

import org.praisenter.ui.Praisenter;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
	
	public static final void setIcons(Stage stage) {
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon16x16alt.png"), 16, 16, true, true));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon32x32.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon48x48.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon64x64.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon96x96.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon128x128.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon256x256.png")));
    	stage.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon512x512.png")));
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
