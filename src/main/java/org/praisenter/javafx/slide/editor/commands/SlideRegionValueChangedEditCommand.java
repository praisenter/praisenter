package org.praisenter.javafx.slide.editor.commands;

import java.util.Objects;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.ValueChangedEditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.Slide;
import org.praisenter.slide.effects.SlideShadow;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.utility.Colors;

import javafx.beans.property.ObjectProperty;

public abstract class SlideRegionValueChangedEditCommand<T, V extends ObservableSlideRegion<?>> extends ValueChangedEditCommand<T> implements EditCommand {
	protected final ObjectProperty<ObservableSlideRegion<?>> selection;
	protected final V region;
	
	public SlideRegionValueChangedEditCommand(T oldValue, T newValue, V region, ObjectProperty<ObservableSlideRegion<?>> selection) {
		super(oldValue, newValue);
		this.region = region;
		this.selection = selection;
	}
	
	protected SlideRegionValueChangedEditCommand(T oldValue, T newValue, SlideRegionValueChangedEditCommand<T, V> command) {
		super(oldValue, newValue);
		this.region = command.region;
		this.selection = command.selection;
	}
	
	@Override
	public boolean isValid() {
		return this.region != null;
	}
	
	public final void select(ObservableSlideRegion<?> region) {
		if (this.selection != null) {
			this.selection.set(region);
		}
	}
	
	public final void selectRegion() {
		if (this.selection != null) {
			this.selection.set(this.region);
		}
	}

	/**
	 * Used to help reduce the number of undos for a given set of consecutive commands.
	 * @param p0 the first paint
	 * @param p1 the second paint
	 * @return boolean
	 */
	protected final boolean isClose(SlidePaint p0, SlidePaint p1) {
		if (p0 != null && p1 != null) {
			if (p0 instanceof SlideColor && p1 instanceof SlideColor) {
				SlideColor c0 = (SlideColor)p0;
				SlideColor c1 = (SlideColor)p1;
				return this.isClose(c0, c1);
			} else if (p0 instanceof SlideLinearGradient && p1 instanceof SlideLinearGradient) {
				SlideLinearGradient g0 = (SlideLinearGradient)p0;
				SlideLinearGradient g1 = (SlideLinearGradient)p1;
				return this.isClose(g0, g1);
			} else if (p0 instanceof SlideRadialGradient && p1 instanceof SlideRadialGradient) {
				SlideRadialGradient g0 = (SlideRadialGradient)p0;
				SlideRadialGradient g1 = (SlideRadialGradient)p1;
				return this.isClose(g0, g1);
			}
		}
		return false;
	}
	
	protected final boolean isClose(SlideColor c0, SlideColor c1) {
		if (c0 == null || c1 == null) {
			return false;
		}
		
		double diff = Math.abs(Math.sqrt(Colors.distanceSquared(
				c0.getRed(),
				c0.getGreen(),
				c0.getBlue(),
				c0.getAlpha(),
				c1.getRed(),
				c1.getGreen(),
				c1.getBlue(),
				c1.getAlpha())));
		// compare color diff (distance in color units)
		return diff < 0.2;
	}
	
	protected final boolean isClose(SlideRadialGradient g0, SlideRadialGradient g1) {
		if (g0 == null || g1 == null) {
			return false;
		}
		
		// the number of stops should be identical
		if (g0.getStops().size() == g1.getStops().size()) {
			// check the cycle type
			if (g0.getCycleType() == g1.getCycleType()) {
				// get the start/end
				if (g0.getCenterX() == g1.getCenterX() &&
					g0.getCenterY() == g1.getCenterY() &&
					g0.getRadius() == g1.getRadius()) {
					// compare stops
					boolean same = true;
					for (int i = 0; i < g0.getStops().size(); i++) {
						SlideGradientStop s0 = g0.getStops().get(i);
						SlideGradientStop s1 = g1.getStops().get(i);
						// compare offsets
						if (s1.getOffset() - s0.getOffset() > 0.1) {
							same = false;
							break;
						}
						// compare the color
						SlideColor c0 = s0.getColor();
						SlideColor c1 = s1.getColor();
						double diff = Math.abs(Math.sqrt(Colors.distanceSquared(
								c0.getRed(),
								c0.getGreen(),
								c0.getBlue(),
								c0.getAlpha(),
								c1.getRed(),
								c1.getGreen(),
								c1.getBlue(),
								c1.getAlpha())));
						if (diff > 0.2) {
							same = false;
							break;
						}
					}
					return same;						
				}
			}
		}
		
		return false;
	}
	
	protected final boolean isClose(SlideLinearGradient g0, SlideLinearGradient g1) {
		if (g0 == null || g1 == null) {
			return false;
		}
		
		// the number of stops should be identical
		if (g0.getStops().size() == g1.getStops().size()) {
			// check the cycle type
			if (g0.getCycleType() == g1.getCycleType()) {
				// get the start/end
				if (g0.getStartX() == g1.getStartX() &&
					g0.getStartY() == g1.getStartY() &&
					g0.getEndX() == g1.getEndX() &&
					g0.getEndY() == g1.getEndY()) {
					// compare stops
					boolean same = true;
					for (int i = 0; i < g0.getStops().size(); i++) {
						SlideGradientStop s0 = g0.getStops().get(i);
						SlideGradientStop s1 = g1.getStops().get(i);
						// compare offsets
						if (s1.getOffset() - s0.getOffset() > 0.1) {
							same = false;
							break;
						}
						// compare the color
						SlideColor c0 = s0.getColor();
						SlideColor c1 = s1.getColor();
						double diff = Math.abs(Math.sqrt(Colors.distanceSquared(
								c0.getRed(),
								c0.getGreen(),
								c0.getBlue(),
								c0.getAlpha(),
								c1.getRed(),
								c1.getGreen(),
								c1.getBlue(),
								c1.getAlpha())));
						if (diff > 0.2) {
							same = false;
							break;
						}
					}
					return same;						
				}
			}
		}
		
		return false;
	}

	protected final boolean isClose(SlideStroke s0, SlideStroke s1) {
		if (s0 == null || s1 == null) {
			return false;
		}
		
		if (s0.getRadius() == s1.getRadius() &&
			Objects.equals(s0.getStyle(), s1.getStyle()) &&
			s0.getWidth() == s1.getWidth() &&
			this.isClose(s0.getPaint(), s1.getPaint())) {
			return true;
		}
		
		return false;
	}

	protected final boolean isClose(SlideShadow s0, SlideShadow s1) {
		if (s0 == null || s1 == null) {
			return false;
		}
		
		if (s0.getRadius() == s1.getRadius() &&
			s0.getSpread() == s1.getSpread() &&
			s0.getType() == s1.getType() &&
			s0.getOffsetX() == s1.getOffsetX() &&
			s0.getOffsetY() == s1.getOffsetY() &&
			this.isClose(s0.getColor(), s1.getColor())) {
			return true;
		}
		
		return false;
	}
}
