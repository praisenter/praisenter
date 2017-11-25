package org.praisenter.javafx.slide.editor;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableSlideShow;
import org.praisenter.javafx.slide.SlideLibrarySlideListView;
import org.praisenter.javafx.slide.SlideShowSlideListView;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideAssignment;
import org.praisenter.slide.SlideShow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

// TODO implement reordering of slides (buttons)
// TODO implement saving the show

public final class SlideShowEditorPane extends BorderPane {
	private final ObjectProperty<ObservableSlideShow> show = new SimpleObjectProperty<ObservableSlideShow>();
	
	private final TextField txtName;
	private final CheckBox chkLoop;
	private final SlideLibrarySlideListView allSlides;
	private final SlideShowSlideListView showSlides;
	
	public SlideShowEditorPane(PraisenterContext context) {
		this.txtName = new TextField();
		this.chkLoop = new CheckBox();
		this.allSlides = new SlideLibrarySlideListView(context);
		this.showSlides = new SlideShowSlideListView(context);
		
		Button btnAdd = new Button("Add", ApplicationGlyphs.ADD_RIGHT);
		Button btnRemove = new Button("Remove", ApplicationGlyphs.REMOVE_LEFT);
		
		btnAdd.setContentDisplay(ContentDisplay.RIGHT);
		
		btnAdd.setMaxWidth(Double.MAX_VALUE);
		btnRemove.setMaxWidth(Double.MAX_VALUE);
		
		HBox top = new HBox(5, new Label("Name"), this.txtName, new Label("Loop when end is reached"), this.chkLoop);
//		HBox bot = new HBox(5, btnAdd, btnRemove);
		
		top.setAlignment(Pos.BASELINE_LEFT);
		top.setPadding(new Insets(5));
		HBox.setHgrow(this.txtName, Priority.ALWAYS);
//		bot.setPadding(new Insets(5));
		
		VBox left = new VBox(btnAdd, this.allSlides);
		VBox right = new VBox(btnRemove, this.showSlides);
		
		VBox.setVgrow(this.allSlides, Priority.ALWAYS);
		VBox.setVgrow(this.showSlides, Priority.ALWAYS);
		
		SplitPane split = new SplitPane(left, right);
		split.setDividerPosition(0, 0.5);
		
		this.setTop(top);
		this.setCenter(split);
//		this.setBottom(bot);
		
		this.showSlides.valueProperty().bind(this.show);
		this.show.addListener((obs, ov, nv) -> {
			this.txtName.textProperty().unbind();
			this.chkLoop.selectedProperty().unbind();
			
			if (nv != null) {
				this.txtName.textProperty().bindBidirectional(nv.nameProperty());
				this.chkLoop.selectedProperty().bindBidirectional(nv.loopProperty());
			}
		});
		
		btnAdd.setOnAction(e -> {
			List<Slide> selected = new ArrayList<Slide>(this.allSlides.getSelectionModel().getSelectedItems());
			if (selected.size() > 0) {
				for (Slide slide : selected) {
					this.show.get().getSlides().add(new SlideAssignment(slide.getId()));
				}
			}
		});
		
		btnRemove.setOnAction(e -> {
			List<SlideAssignment> selected = new ArrayList<SlideAssignment>(this.showSlides.getSelectionModel().getSelectedItems());
			if (selected.size() > 0) {
				for (SlideAssignment assignment : selected) {
					this.show.get().getSlides().remove(assignment);
				}
			}
		});
		
		this.allSlides.setOnCellClick((e, s) -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && s != null) {
				this.show.get().getSlides().add(new SlideAssignment(s.getId()));
			}
		});
		
		this.allSlides.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				List<Slide> slides = this.allSlides.getSelectionModel().getSelectedItems();
				if (slides != null) {
					for (Slide slide : slides) {
						this.show.get().getSlides().add(new SlideAssignment(slide.getId()));
					}
				}
			}
		});
		
		this.showSlides.setOnCellClick((e, a) -> {
			if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && a != null) {
				this.show.get().getSlides().remove(a);
			}
		});
		
		this.showSlides.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				List<SlideAssignment> assignments = new ArrayList<SlideAssignment>(this.showSlides.getSelectionModel().getSelectedItems());
				if (assignments != null) {
					for (SlideAssignment assignment : assignments) {
						this.show.get().getSlides().remove(assignment);
					}
				}
			}
		});
	}
	
	public void setSlideShow(SlideShow show) {
		this.show.set(show == null ? null : new ObservableSlideShow(show));
	}
	
	public SlideShow getSlideShow() {
		return this.show.get().getSlideShow();
	}
}
