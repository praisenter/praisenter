package org.praisenter.javafx.transition;

class Swap extends CustomTransition {
	/** The {@link Swap} transition id */
	static final int ID = 10;
	
	/**
	 * Full constructor.
	 */
	public Swap() {
		super(TransitionType.IN);
	} 
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getId() {
		return ID;
	}
	
	//no interpolation required
	@Override
	protected void interpolate(double frac) {}
}
