package org.praisenter.ui.slide;

final class PendingPreparedSlide implements Comparable<PendingPreparedSlide> {
	private final PreparedSlide slide;
	private final boolean transition;
	
	public PendingPreparedSlide(PreparedSlide slide, boolean transition) {
		super();
		this.slide = slide;
		this.transition = transition;
	}
	
	@Override
	public int compareTo(PendingPreparedSlide o) {
		if (o == null)
			return -1;
		
		PreparedSlide me = this.getSlide();
		PreparedSlide ot = o.getSlide();
		
		if (ot == null && me == null)
			return 0;
		
		if (ot == null && me != null)
			return me.compareTo(ot);
		
		if (ot != null && me == null)
			return 1;
		
		return me.compareTo(ot);
	}
	
	public PreparedSlide getSlide() {
		return this.slide;
	}
	
	public boolean isTransition() {
		return this.transition;
	}
	
	
}
