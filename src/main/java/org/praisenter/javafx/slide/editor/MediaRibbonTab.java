package org.praisenter.javafx.slide.editor;

import org.controlsfx.control.SegmentedButton;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.media.Media;
import org.praisenter.resources.OpenIconic;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class MediaRibbonTab extends ComponentEditorRibbonTab {

	private static final GlyphFont OPEN_ICONIC = GlyphFontRegistry.font("Icons");
	
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final PraisenterContext context;
	
	private final MediaPicker pkrMedia;
	private final SegmentedButton segScaling;
	private final ToggleButton tglLoop;
	private final ToggleButton tglMute;

	public MediaRibbonTab(PraisenterContext context) {
		super("Media");
		
		this.context = context;
		
		this.pkrMedia = new MediaPicker(context);
		this.pkrMedia.setValue(null);
		this.pkrMedia.managedProperty().bind(pkrMedia.visibleProperty());
		
		ToggleButton tglImageScaleNone = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.CROP));
		ToggleButton tglImageScaleNonUniform = new ToggleButton("", OPEN_ICONIC.create(OpenIconic.Glyph.RESIZE_BOTH));
		ToggleButton tglImageScaleUniform = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.ARROWS));
		tglImageScaleNone.setSelected(true);
		tglImageScaleNone.setUserData(ScaleType.NONE);
		tglImageScaleNonUniform.setUserData(ScaleType.NONUNIFORM);
		tglImageScaleUniform.setUserData(ScaleType.UNIFORM);
		this.segScaling = new SegmentedButton(tglImageScaleNone, tglImageScaleNonUniform, tglImageScaleUniform);
		
		this.tglLoop = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.REPEAT));
		this.tglLoop.setSelected(false);
		
		this.tglMute = new ToggleButton("", FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_OFF));
		this.tglMute.setSelected(false);
		
		// tooltips
		this.pkrMedia.setTooltip(new Tooltip("Choose the media"));
		tglImageScaleNone.setTooltip(new Tooltip("No scaling"));
		tglImageScaleUniform.setTooltip(new Tooltip("Keep aspect ratio"));
		tglImageScaleNonUniform.setTooltip(new Tooltip("Scale to fit"));
		this.tglLoop.setTooltip(new Tooltip("Toggles looping of the media"));
		this.tglMute.setTooltip(new Tooltip("Toggles muting the media"));
		
		// layout

		HBox row1 = new HBox(2, this.pkrMedia);
		HBox row2 = new HBox(2, this.segScaling, this.tglLoop, this.tglMute);
		VBox layout = new VBox(2, row1, row2);
		this.container.setCenter(layout);
	
		// events
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null && nv instanceof ObservableMediaComponent) {
				ObservableMediaComponent omc = (ObservableMediaComponent)nv;
				setControlValues(omc.getMedia());
				setDisable(false);
			} else {
				setControlValues(null);
				setDisable(true);
			}
			mutating = false;
		});
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (mutating) return;
				ObservableMediaComponent comp = (ObservableMediaComponent)component.get();
				if (comp != null) {
					comp.setMedia(getControlValues());
				}
			}
		};
		
		this.pkrMedia.valueProperty().addListener(listener);
		this.segScaling.getToggleGroup().selectedToggleProperty().addListener(listener);
		this.tglLoop.selectedProperty().addListener(listener);
		this.tglMute.selectedProperty().addListener(listener);
	}
	
	private MediaObject getControlValues() {
		Toggle scaleToggle = segScaling.getToggleGroup().getSelectedToggle();
		ScaleType scaleType = scaleToggle != null && scaleToggle.getUserData() != null ? (ScaleType)scaleToggle.getUserData() : ScaleType.NONE;
		Media media = this.pkrMedia.getValue();
		
		if (media == null) {
			return null;
		}
		
		return new MediaObject(media.getId(), scaleType, tglLoop.isSelected(), tglMute.isSelected());
	}
	
	private void setControlValues(MediaObject mediaObject) {
		if (mediaObject != null) {
			Media media = context.getMediaLibrary().get(mediaObject.getId());
			// the media could have been removed, so check for null
			if (media == null) {
				this.pkrMedia.setValue(null);
			} else {
				this.pkrMedia.setValue(media);
				tglLoop.setSelected(mediaObject.isLoop());
				tglMute.setSelected(mediaObject.isMute());
				for (Toggle toggle : segScaling.getButtons()) {
					if (toggle.getUserData() == mediaObject.getScaling()) {
						toggle.setSelected(true);
						break;
					}
				}
			}
		} else {
			this.pkrMedia.setValue(null);
		}
	}
}
