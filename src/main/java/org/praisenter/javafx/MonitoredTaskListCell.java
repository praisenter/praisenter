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

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 * A list cell specifically for {@link MonitoredTask}s.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class MonitoredTaskListCell extends ListCell<MonitoredTask<?>> {
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	/** The task status */
	private final ObjectProperty<Worker.State> status = new SimpleObjectProperty<>(Worker.State.READY);
	
	/** The result status */
	private final ObjectProperty<MonitoredTaskResultStatus> result = new SimpleObjectProperty<>();
	
	/** The progress indicator while working */
	private final ProgressIndicator indicator = new ProgressIndicator();
	
	/** The tooltip */
	private final Tooltip tooltip = new Tooltip();
	
	/**
	 * Default constructor.
	 */
	public MonitoredTaskListCell() {
		this.indicator.setPrefSize(30, 30);
		this.setTooltip(this.tooltip);
		this.setPrefWidth(180);
		this.setMaxWidth(USE_PREF_SIZE);
		this.setWrapText(false);
		this.setTextOverrun(OverrunStyle.ELLIPSIS);
		this.setContentDisplay(ContentDisplay.LEFT);
		this.setAlignment(Pos.BASELINE_LEFT);
		this.setGraphic(this.indicator);
		this.status.addListener((obs, ov, nv) -> {
			setTextGraphic();
		});
		this.result.addListener((obs, ov, nv) -> {
			setTextGraphic();
		});
	}
	
	/**
	 * Sets the graphic based on the task status and result status.
	 */
	private void setTextGraphic() {
		Worker.State nv = this.status.get();
		MonitoredTaskResultStatus status = this.result.get();
		
		// unless the task result has been set, use the task's status
		if (status == null) {
			if (nv == Worker.State.RUNNING || nv == Worker.State.READY || nv == Worker.State.SCHEDULED) {
				this.setGraphic(this.indicator);
			} else if (nv == Worker.State.CANCELLED) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.LIGHTGRAY));
			} else if (nv == Worker.State.FAILED) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED));
			} else if (nv == Worker.State.SUCCEEDED) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN));
			} else {
				this.setGraphic(null);
			}
		} else {
			// otherwise use the result status
			if (status == MonitoredTaskResultStatus.WARNING) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.GOLD));
			} else if (status == MonitoredTaskResultStatus.ERROR) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.REMOVE).color(Color.RED));
			} else if (status == MonitoredTaskResultStatus.SUCCESS) {
				this.setGraphic(FONT_AWESOME.create(FontAwesome.Glyph.CHECK).color(Color.LIMEGREEN));
			} else {
				this.setGraphic(null);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(MonitoredTask<?> item, boolean empty) {
		super.updateItem(item, empty);
		this.indicator.progressProperty().unbind();
		this.status.unbind();
		this.result.unbind();
		if (empty) {
			this.setGraphic(null);
		} else {
			this.indicator.progressProperty().bind(item.progressProperty());
			this.status.bind(item.stateProperty());
			this.result.bind(item.resultStatusProperty());
			setTextGraphic();
			this.setText(item.getName());
			this.tooltip.setText(item.getName());
		}
	}
}