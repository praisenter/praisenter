package org.praisenter.javafx.slide;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.text.TextMeasurer;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.TextComponent;

public class FxSlideTextComponent extends FxSlideComponent<TextComponent> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	VBox wrapperNode;
	Text textNode;
	
	public FxSlideTextComponent(PraisenterContext context, TextComponent component, SlideMode mode) {
		super(context, component, mode);
		
		SlideStroke bdr = this.component.getBorder();
		
		int w = this.component.getWidth();
		int h = this.component.getHeight();
		
		TextComponent tc = this.component;
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		double padding = tc.getPadding();
		double pw = this.component.getWidth() - padding * 2;
		double ph = this.component.getHeight() - padding * 2;
		
		// set the wrapping width and the bounds type
		Text text = new Text();
		text.setWrappingWidth(pw);
		text.setBoundsType(TextBoundsType.VISUAL);
		
		// component.getText()
		String str = tc.getText();
		
		// compute a fitting font, if necessary
		Font base = getFont(tc.getFont());
		Font font = base;
		if (tc.getFontScaleType() == FontScaleType.REDUCE_SIZE_ONLY) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, base.getSize(), pw, ph, tc.getLineSpacing(), TextBoundsType.VISUAL);
		} else if (tc.getFontScaleType() == FontScaleType.BEST_FIT) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, tc.getLineSpacing(), TextBoundsType.VISUAL);
		}
		System.out.print(font.getSize());
		
		// the text, font, text fill, line spacing and horizontal alignment
		text.setText(str);
		text.setFont(font);
		text.setFill(this.getPaint(tc.getTextPaint()));
		text.setLineSpacing(tc.getLineSpacing());
		text.setTextAlignment(this.getTextAlignment(tc.getHorizontalTextAlignment()));
		
		// text border
		SlideStroke ss = tc.getTextBorder();
		if (ss != null) {
			text.setStroke(this.getPaint(ss.getPaint()));
			text.setStrokeLineCap(this.getStrokeLineCap(ss.getStyle().getCap()));
			text.setStrokeLineJoin(this.getStrokeLineJoin(ss.getStyle().getJoin()));
			text.setStrokeType(this.getStrokeType(ss.getStyle().getType(), false));
			text.setStrokeWidth(ss.getWidth());
			text.getStrokeDashArray().addAll(ss.getStyle().getDashes());
		}
		
		VBox contentWrapper = new VBox();
		contentWrapper.setPadding(new Insets(padding));
		Fx.setSize(contentWrapper, w, h);
		
		// vertical alignment
		contentWrapper.setAlignment(this.getPos(tc.getVerticalTextAlignment()));
		
		contentWrapper.getChildren().add(text);
		
		this.wrapperNode = contentWrapper;
		this.textNode = text;
		
		this.contentNode.getChildren().addAll(
				this.backgroundMedia != null ? this.backgroundMedia : this.backgroundPaint,
				this.wrapperNode,
				this.borderNode);
	}
}
