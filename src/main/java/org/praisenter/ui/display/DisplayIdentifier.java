package org.praisenter.ui.display;

import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.ui.Praisenter;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class DisplayIdentifier extends Stage {
	public DisplayIdentifier(DisplayConfiguration configuration) {
		super(StageStyle.TRANSPARENT);

    	// icons
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon16x16alt.png"), 16, 16, true, true));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon32x32.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon48x48.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon64x64.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon96x96.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon128x128.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon256x256.png")));
		this.getIcons().add(new Image(Praisenter.class.getResourceAsStream("/org/praisenter/logo/icon512x512.png")));
    	
		this.initModality(Modality.NONE);
		this.setResizable(false);
		
		this.setX(configuration.getX());
		this.setY(configuration.getY());
		this.setWidth(configuration.getWidth());
		this.setHeight(configuration.getHeight());
		this.setMinWidth(configuration.getWidth());
		this.setMinHeight(configuration.getHeight());
		this.setMaxWidth(configuration.getWidth());
		this.setMaxHeight(configuration.getHeight());
		
		EventHandler<WindowEvent> block = (WindowEvent ev) -> {
			ev.consume();
		};
		this.setOnCloseRequest(block);
		this.setOnHiding(block);

		Label lblNumber = new Label(String.valueOf(configuration.getId() + 1));
		lblNumber.setFont(Font.font(150));
		lblNumber.setTextFill(Color.WHITE);
		
		Pane container = new StackPane();
		container.setBackground(new Background(new BackgroundFill(new Color(0,0,0,0.5), null, null)));
		container.getChildren().add(lblNumber);
		container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.setScene(new Scene(container, Color.TRANSPARENT));
	}
}
