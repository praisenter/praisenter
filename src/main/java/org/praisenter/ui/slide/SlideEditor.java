package org.praisenter.ui.slide;

import java.util.concurrent.CompletableFuture;

import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentEditor;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public final class SlideEditor extends BorderPane implements DocumentEditor<Slide> {

	private final GlobalContext context;
	private final DocumentContext<Slide> document;
	
	private final MappedList<EditNode, SlideComponent> componentMapping;
	private final ObjectProperty<Slide> slide;
	private final ObservableList<SlideComponent> components;
	private final ObjectProperty<EditNode> selected;
	
	private final StackPane view;
	private final SlideView slideView;
	
	public SlideEditor(
			GlobalContext context, 
			DocumentContext<Slide> document) {
		this.context = context;
		this.document = document;
		
		this.slide = new SimpleObjectProperty<>();
		this.components = FXCollections.observableArrayList();
		this.selected = new SimpleObjectProperty<>();
		
		this.view = new StackPane();
		this.slideView = new SlideView(context);
		this.slideView.setViewMode(SlideMode.VIEW);
		this.slideView.setViewScalingEnabled(true);
		
		this.slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.components, ov.getComponents());
			}
			if (nv != null) {
				Bindings.bindContent(this.components, nv.getComponents());
			}
		});
		
		this.slide.bind(document.documentProperty());
		this.componentMapping = new MappedList<>(this.components, (c) -> {
			EditNode n = new EditNode(c);
			n.scaleProperty().bind(this.slideView.viewScaleXProperty());
			n.selectedProperty().addListener((obs, ov, nv) -> {
				if (nv) {
					this.selected.set(n);
				}
			});
			return n;
		});
		
		this.selected.addListener((obs, ov, nv) -> {
			clearSelectionExceptFor(nv);
		});

		this.slideView.slideProperty().bind(document.documentProperty());
		
		Pane editContainer = new Pane();
		Bindings.bindContent(editContainer.getChildren(), this.componentMapping);
		
		this.view.getChildren().addAll(this.slideView, editContainer);
		
//		this.view.setMinSize(0, 0);
//		this.view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//		this.slideView.setMinSize(0, 0);
//		this.slideView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		this.setCenter(this.view);
		//this.setBorder(new Border(new BorderStroke(Color.BLACK, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 4, 0, null), null, new BorderWidths(4))));
	}
	
	private void clearSelectionExceptFor(EditNode node) {
		for (EditNode n : this.componentMapping) {
			if (n != node) {
				n.setSelected(false);
			}
		}
	}
	
	@Override
	public DocumentContext<Slide> getDocumentContext() {
		return this.document;
	}

	@Override
	public boolean isActionEnabled(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActionVisible(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CompletableFuture<Void> executeAction(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultFocus() {
		// TODO Auto-generated method stub
		
	}
}
