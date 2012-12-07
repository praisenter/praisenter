package org.praisenter.preferences;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.easings.CubicEasing;
import org.praisenter.transitions.PushDown;
import org.praisenter.transitions.VerticalSplitCollapse;

/**
 * Class used to store notification preferences.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "NotificationPreferences")
@XmlAccessorType(XmlAccessType.NONE)
public class NotificationPreferences {
	// general
	
	/** The notification wait period */
	@XmlElement(name = "WaitPeriod", required = true, nillable = false)
	protected int waitPeriod;

	// template
	
	/** The template to use for bible slides */
	@XmlElement(name = "Template", required = false, nillable = true)
	protected String template;
	
	// transitions
	
	/** The send transition id */
	@XmlElement(name = "SendTransitionId", required = true, nillable = false)
	protected int sendTransitionId;
	
	/** The send transition duration */
	@XmlElement(name = "SendTransitionDuration", required = true, nillable = false)
	protected int sendTransitionDuration;
	
	/** The send transition easing id */
	@XmlElement(name = "SendTransitionEasingId", required = true, nillable = false)
	protected int sendTransitionEasingId;
	
	/** The clear transition id */
	@XmlElement(name = "ClearTransitionId", required = true, nillable = false)
	protected int clearTransitionId;
	
	/** The clear transition duration */
	@XmlElement(name = "ClearTransitionDuration", required = true, nillable = false)
	protected int clearTransitionDuration;
	
	/** The clear transition easing id */
	@XmlElement(name = "ClearTransitionEasingId", required = true, nillable = false)
	protected int clearTransitionEasingId;

	/** Default constructor. */
	protected NotificationPreferences() {
		this.waitPeriod = 5000;
		
		this.template = null;
		
		this.sendTransitionId = PushDown.ID;
		this.sendTransitionDuration = 400;
		this.sendTransitionEasingId = CubicEasing.ID;
		this.clearTransitionId = VerticalSplitCollapse.ID;
		this.clearTransitionDuration = 300;
		this.clearTransitionEasingId = CubicEasing.ID;
	}
	
	/**
	 * Returns the wait period in milliseconds.
	 * @return int
	 */
	public int getWaitPeriod() {
		return this.waitPeriod;
	}
	
	/**
	 * Sets the wait period.
	 * @param waitPeriod the wait period in milliseconds
	 */
	public void setWaitPeriod(int waitPeriod) {
		this.waitPeriod = waitPeriod;
	}

	// template
	
	/**
	 * Gets the default template for notifications.
	 * @return String
	 */
	public String getTemplate() {
		return this.template;
	}
	
	/**
	 * Sets the default template for notifications.
	 * @param template the template file path
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	// transitions
	
	/**
	 * Returns the send transition id.
	 * @return int
	 */
	public int getSendTransitionId() {
		return this.sendTransitionId;
	}

	/**
	 * Sets the send transition id.
	 * @param sendTransitionId the send transition id
	 */
	public void setSendTransitionId(int sendTransitionId) {
		this.sendTransitionId = sendTransitionId;
	}

	/**
	 * Returns the send transition duration in milliseconds.
	 * @return int
	 */
	public int getSendTransitionDuration() {
		return this.sendTransitionDuration;
	}

	/**
	 * Sets the send transition duration.
	 * @param sendTransitionDuration the send transition duration in milliseconds
	 */
	public void setSendTransitionDuration(int sendTransitionDuration) {
		this.sendTransitionDuration = sendTransitionDuration;
	}

	/**
	 * Returns the send transition easing id.
	 * @return int
	 */
	public int getSendTransitionEasingId() {
		return this.sendTransitionEasingId;
	}

	/**
	 * Sets the send transition easing id.
	 * @param sendTransitionEasingId the send transition easing id
	 */
	public void setSendTransitionEasingId(int sendTransitionEasingId) {
		this.sendTransitionEasingId = sendTransitionEasingId;
	}

	/**
	 * Returns the clear transition id.
	 * @return int
	 */
	public int getClearTransitionId() {
		return this.clearTransitionId;
	}

	/**
	 * Sets the clear transition id.
	 * @param clearTransitionId the clear transition id
	 */
	public void setClearTransitionId(int clearTransitionId) {
		this.clearTransitionId = clearTransitionId;
	}

	/**
	 * Returns the clear transition duration.
	 * @return int
	 */
	public int getClearTransitionDuration() {
		return this.clearTransitionDuration;
	}

	/**
	 * Sets the clear transition duration.
	 * @param clearTransitionDuration the clear transition duration in milliseconds
	 */
	public void setClearTransitionDuration(int clearTransitionDuration) {
		this.clearTransitionDuration = clearTransitionDuration;
	}

	/**
	 * Returns the clear transition easing id.
	 * @return int
	 */
	public int getClearTransitionEasingId() {
		return this.clearTransitionEasingId;
	}

	/**
	 * Sets the clear transition easing id.
	 * @param clearTransitionEasingId the clear transition easing id
	 */
	public void setClearTransitionEasingId(int clearTransitionEasingId) {
		this.clearTransitionEasingId = clearTransitionEasingId;
	}
}
