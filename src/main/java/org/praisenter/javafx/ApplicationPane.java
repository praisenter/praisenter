package org.praisenter.javafx;

public interface ApplicationPane {
	
	// toggling visibility
	
	/**
	 * Returns true if the action is visible.
	 * <p>
	 * By default this should always return true.
	 * @param action the action
	 * @return boolean
	 */
	public boolean isApplicationActionVisible(ApplicationAction action);
	
	// toggling disabled/enabled
	
	/**
	 * Returns true if the action is enabled.
	 * <p>
	 * By default this should always return false. Doing so will ensure new actions that
	 * get added will not be enabled on controls that are not updated for the new actions.
	 * @param action the action
	 * @return boolean
	 */
	public boolean isApplicationActionEnabled(ApplicationAction action);
}
