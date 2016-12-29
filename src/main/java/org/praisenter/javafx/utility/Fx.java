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

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Window;

/**
 * Helper class to assist in creating and manipulating Java FX objects.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Fx {
	/** Hidden default constructor */
	private Fx() {}
	
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
	 * Returns a uniformly scaled rectangle for the given width and height and target width and height.
	 * @param w the width
	 * @param h the height
	 * @param tw the target width
	 * @param th the target height
	 * @return Rectangle2D
	 */
	public static Rectangle2D getUniformlyScaledBounds(double w, double h, double tw, double th) {
		// compute the scale factors
		double sw = tw / w;
		double sh = th / h;

		// to scale uniformly we need to 
		// scale by the smallest factor
		if (sw < sh) {
			w = tw;
			h = sw * h;
		} else {
			w = sh * w;
			h = th;
		}

		// center the image
		double x = (tw - w) / 2.0;
		double y = (th - h) / 2.0;
		
		return new Rectangle2D(x, y, Math.max(w, 0), Math.max(h, 0));
	}
}
