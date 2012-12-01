package org.praisenter.notification.ui.present;

/**
 * The state of the notification window.
 * <p>
 * The notification window has a transition in, a delay period, and a transition
 * out.  This enumeration represents the states the notification window can
 * be in.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum NotificationState {
	/** The notification is transitioning in */
	IN,
	
	/** The notification is currently displaying */
	WAIT,
	
	/** The notification is transitioning out */
	OUT,
	
	/** The notification is complete (no longer visible) */
	DONE
}
