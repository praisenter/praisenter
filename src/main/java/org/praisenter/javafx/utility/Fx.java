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
package org.praisenter.javafx.utility;

import org.praisenter.utility.ClasspathLoader;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Helper class to assist in creating and manipulating Java FX objects.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Fx {
	/** Hidden default constructor */
	private Fx() {}

	/** The transparent pattern */
	public static final Image TRANSPARENT_PATTERN = ClasspathLoader.getImage("org/praisenter/resources/transparent.png");
	
	/**
	 * Returns a new scene for the given root node inheriting the stylesheets
	 * from the given window owner.
	 * @param root the root
	 * @param owner the window owner
	 * @return Scene
	 */
	public static final Scene newSceneInheritCss(Parent root, Window owner) {
		Scene scene = new Scene(root);
		if (owner != null) {
			scene.getStylesheets().addAll(owner.getScene().getStylesheets());
		}
		return scene;
	}
	
	/**
	 * Returns a new border for the given color.
	 * @param color the color
	 * @return Border
	 */
	public static final Border newBorder(Color color) {
		return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, null, new BorderWidths(5)));
	}

	/**
	 * Sets the size of the node to the given width and height.
	 * <p>
	 * This method attempts to force the node to stay the given size by setting
	 * the min, max, and preferred sizes.
	 * @param region the node
	 * @param width the desired width
	 * @param height the desired height
	 */
	public static final void setSize(Region region, double width, double height) {
		region.setPrefSize(width, height);
		region.setMinSize(width, height);
		region.setMaxSize(width, height);
	}
	
	/**
	 * Ensures the given runnable runs on the Java FX UI thread.
	 * @param runnable the runnable to run
	 */
	public static final void runOnFxThead(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		} else {
			Platform.runLater(runnable);
		}
	}
	
	/**
	 * Returns true if any of the given nodes are in the chain from the root node
	 * to the given focused node.
	 * @param focused the currently focused node
	 * @param nodes the nodes to look for
	 * @return boolean
	 */
	public static boolean isNodeInFocusChain(Node focused, Node... nodes) {
		boolean isFocused = false;
		while (nodes != null && nodes.length > 0 && focused != null) {
			for (int i = 0; i < nodes.length; i++) {
				if (focused == nodes[i]) {
					isFocused = true;
					break;
				}
			}
			if (isFocused) {
				break;
			}
			focused = focused.getParent();
		}
		return isFocused;
	}
	
	/**
	 * Returns a Java FX cursor for the current x-y location.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width
	 * @param h the height
	 * @param rw the resize-width
	 * @return Cursor
	 */
	public static final Cursor getCursorForPosition(double x, double y, double w, double h, double rw) {
		if (x <= rw && y <= rw) { 
			// top left corner
			return Cursor.NW_RESIZE;
		} else if (x >= w - rw && y >= h - rw) {
			// bottom right corner
			return Cursor.SE_RESIZE;
		} else if (x <= rw && y >= h - rw) {
			// bottom left corner
			return Cursor.SW_RESIZE;
		} else if (x >= w - rw &&	y <= rw) {
			// top right corner
			return Cursor.NE_RESIZE;
		} else if (x <= rw) {
			// left
			return Cursor.W_RESIZE;
		} else if (x >= w - rw) {
			// right
			return Cursor.E_RESIZE;
		} else if (y <= rw) {
			// top
			return Cursor.N_RESIZE;
		} else if (y >= h - rw) {
			// bottom
			return Cursor.S_RESIZE;
		} else {
			return Cursor.MOVE;
		}
	}
	
	/**
	 * Center's the given dialog relative to it's parent (if present), otherwise it
	 * is centered on the screen.
	 * @param dialog the dialog to center
	 */
	public static void centerOnParent(Stage dialog) {
		Window parent = dialog.getOwner();
		if (parent != null) {
			dialog.setX(parent.getX() + parent.getWidth() / 2 - dialog.getWidth() / 2);
	        dialog.setY(parent.getY() + parent.getHeight() / 2 - dialog.getHeight() / 2);
		} else {
			dialog.centerOnScreen();
		}
	}
}
