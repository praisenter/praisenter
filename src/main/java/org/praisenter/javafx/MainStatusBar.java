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

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.resources.translations.Translations;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Callback;

/**
 * The main status bar (bottom bar) of the application.
 * @author William Bittle
 * @version 3.0.0
 */
final class MainStatusBar extends HBox {
	/** The context */
	private final PraisenterContext context;
	
	/**
	 * Constructor.
	 * @param context the context
	 */
	public MainStatusBar(PraisenterContext context) {
		this.getStyleClass().add("main-status-bar");
		
		this.context = context;

		Button button = new Button(Translations.get("task.progress"));
		button.getStyleClass().add("main-status-bar-task-button");
		
		ProgressBar progress = new ProgressBar();
		progress.getStyleClass().add("main-status-bar-task-progress");
		progress.setProgress(0);
		this.context.getExecutorService().runningProperty().addListener((obs, ov, nv) -> {
			if (nv.intValue() > 0) {
				progress.setProgress(-1);
			} else {
				progress.setProgress(0);
			}
		});
		progress.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			// for whatever reason, mouse events are not propagated
			// from the progress bar to the button so do it manually
			button.getOnAction().handle(new ActionEvent());
		});
		
		button.setGraphic(progress);
		
		Label lblNoTasks = new Label(Translations.get("task.progress.none"));
		lblNoTasks.getStyleClass().add("monitored-task-list-placeholder");
		
		ListView<AsyncTask<?>> view = new ListView<AsyncTask<?>>(context.getExecutorService().tasksProperty());
		view.getStyleClass().add("monitored-task-list");
		view.setPlaceholder(lblNoTasks);
		view.setCellFactory(new Callback<ListView<AsyncTask<?>>, ListCell<AsyncTask<?>>>() {
			@Override
			public ListCell<AsyncTask<?>> call(ListView<AsyncTask<?>> view) {
				return new MonitoredTaskListCell();
			}
		});
		BorderPane layout = new BorderPane(view);
		
		PopOver pop = new PopOver(layout);
		pop.getStyleClass().add("monitored-task-list-popover");
		pop.setDetachable(false);
		pop.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		pop.setArrowLocation(ArrowLocation.BOTTOM_CENTER);
		pop.setAutoFix(false);
		
		button.setOnAction(e -> {
			// show task detail view
			pop.show(button);
		});
		
		button.addEventFilter(KeyEvent.ANY, e -> {
			for (ApplicationAction action : ApplicationAction.values()) {
				if (action.getAccelerator() != null && action.getAccelerator().match(e)) {
					e.consume();
					fireEvent(new ApplicationEvent(this, this, ApplicationEvent.ALL, action));
				}
			}
		});
		
		this.getChildren().addAll(button);
	}
}
