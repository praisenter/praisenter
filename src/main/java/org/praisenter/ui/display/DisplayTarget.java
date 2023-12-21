package org.praisenter.ui.display;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.data.workspace.DisplayType;

public interface DisplayTarget extends Comparable<DisplayTarget> {
	public void displaySlide(final Slide slide, final TextStore data);
	public void displaySlide(final Slide slide, final TextStore data, boolean transtion);
	public void displayNotification(final Slide slide, final TextStore data);
	public void displayNotification(final Slide slide, final TextStore data, boolean transtion);
	public void clear();
	public void clear(boolean transition);
	
	public DisplayConfiguration getDisplayConfiguration();
	
	public void dispose();
	
	@Override
	default int compareTo(DisplayTarget o) {
		if (o == null)
			return -1;
	
		DisplayType tType = this.getDisplayConfiguration().getType();
		DisplayType oType = o.getDisplayConfiguration().getType();
		
		if (tType == oType) {
			// then sort by id
			int tId = this.getDisplayConfiguration().getId();
			int oId = o.getDisplayConfiguration().getId();
			return tId - oId;
		} else if (tType == DisplayType.NDI) {
			// then this one is larger
			return 1;
		} else {
			return -1;
		}
	}
}
