package org.praisenter.javafx;

import java.util.concurrent.ExecutorService;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import org.praisenter.javafx.utility.Fx;

public final class ShutdownDialog extends BorderPane {
	/** The dialog */
	private final Stage dialog;
	
	/** The loading indicator */
	private final ProgressBar bar;

	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 * @param paint the initial value
	 */
	public ShutdownDialog(
			Window owner,
			ExecutorService theadService) {
		// build the dialog
		this.dialog = new Stage();
		if (owner != null) {
			this.dialog.initOwner(owner);
		}
		this.dialog.setTitle("Closing...");
		this.dialog.initModality(Modality.APPLICATION_MODAL);
		this.dialog.initStyle(StageStyle.UNDECORATED);
		// NOTE: this makes the title portion of the modal shorter
		this.dialog.setResizable(false);
		this.dialog.setOnCloseRequest((e) -> {
			e.consume();
		});
		
		this.bar = new ProgressBar();

		this.setCenter(this.bar);
		this.dialog.setScene(Fx.newSceneInheritCss(this, owner));
	}
	
	/**
	 * Shows this dialog.
	 */
	public void show() {
		this.dialog.show();
	}
	
//	/**
//	 * Returns the selected gradient property.
//	 * @return ObjectProperty&lt;Paint&gt;
//	 */
//	public ObjectProperty<Paint> valueProperty() {
//		return this.gradientPane.paintProperty();
//	}
//	
//	/**
//	 * Returns the selected gradient.
//	 * @return Paint
//	 */
//	public Paint getValue() {
//		return this.gradientPane.getPaint();
//	}
//	
//	/**
//	 * Sets the selected gradient.
//	 * @param gradient the gradient
//	 */
//	public void setValue(Paint gradient) {
//		this.gradientPane.setPaint(gradient);
//	}
}
