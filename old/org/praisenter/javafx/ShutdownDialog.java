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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.ui.controls.Alerts;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A specialized dialog for awaiting termination of background threads.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class ShutdownDialog extends VBox {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// properties
	
	/** The complete flag */
	private final BooleanProperty complete = new SimpleBooleanProperty(false);
	
	// data
	
	/** The background threads */
	private final ExecutorService workers;

	// nodes
	
	/** The dialog */
	private final Stage dialog;
	
	/** The loading indicator */
	private final ProgressIndicator progress;

	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 * @param threadService the initial value
	 */
	public ShutdownDialog(
			Window owner,
			ExecutorService threadService) {
		this.workers = threadService;
		final double width = 300;
		
		// build the dialog
		this.dialog = new Stage();
		if (owner != null) {
			this.dialog.initOwner(owner);
			
			// center the dialog with the owner
			this.dialog.setWidth(width);
			this.dialog.setX(owner.getX() + owner.getWidth() / 2 - width / 2);
	        this.dialog.setY(owner.getY() + owner.getHeight() / 2 - width / 2);
		}
		this.dialog.setTitle("Shutting down, please wait...");
		this.dialog.initModality(Modality.APPLICATION_MODAL);
		this.dialog.initStyle(StageStyle.UTILITY);
		// NOTE: this makes the title portion of the modal shorter
		this.dialog.setResizable(false);
		this.dialog.setOnCloseRequest((e) -> {
			e.consume();
		});
		
		this.progress = new ProgressIndicator();

		Label lblMessage = new Label("Allowing background processes to complete...");
		
		this.setPadding(new Insets(20));
		this.setSpacing(10);
		this.getChildren().addAll(lblMessage, this.progress);
		this.dialog.setScene(Fx.newSceneInheritCss(this, owner));
		
		this.getStyleClass().add("shutdown-dialog");
		this.progress.getStyleClass().add("shutdown-dialog-progress");
		lblMessage.getStyleClass().add("shutdown-dialog-message");
	}
	
	/**
	 * Shows this dialog.
	 */
	public void show() {
		this.dialog.show();
		
		// wait until the executor shuts down
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
	    			try {
	    				boolean finished = workers.awaitTermination(100, TimeUnit.MILLISECONDS);
	    				if (finished) {
	    					// all tasks finished so, shutdown
	    					break;
	    				}
					} catch (Exception ex) {
						LOGGER.error("An error occurred while waiting for termination of background processes", ex);
						Platform.runLater(() -> {
							Alert alert = Alerts.exception(dialog, null, null, "", ex);
							alert.showAndWait();
						});
						break;
					}
    			}
				Platform.runLater(() -> {
					LOGGER.info("Background processes completed.");
					dialog.hide();
					complete.set(true);
				});
			}
		});
		t.start();
	}

	/**
	 * Returns true if the shutdown is complete.
	 * @return boolean
	 */
	public boolean isComplete() {
		return this.complete.get();
	}
	
	/**
	 * Returns the complete property.
	 * @return ReadOnlyBooleanProperty
	 */
	public ReadOnlyBooleanProperty completeProperty() {
		return this.complete;
	}
}
