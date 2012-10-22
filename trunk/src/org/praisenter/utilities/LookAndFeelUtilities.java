package org.praisenter.utilities;

import javax.swing.UIManager;

/**
 * Utility class for managing look and feels.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class LookAndFeelUtilities {
	/** The Nimbus look and feel name */
	public static final String NIMBUS = "Nimbus";
	
	/** The Metal (default) look and feel name */
	public static final String METAL = "Metal";
	
	/**
	 * Returns true if the Nimbus look and feel is used.
	 * @return boolean
	 */
	public static final boolean IsNimbusLookAndFeel() {
		return NIMBUS.equalsIgnoreCase(UIManager.getLookAndFeel().getName());
	}
	
	/**
	 * Returns true if the Metal look and feel is used.
	 * @return boolean
	 */
	public static final boolean IsMetalLookAndFeel() {
		return METAL.equalsIgnoreCase(UIManager.getLookAndFeel().getName());
	}
}
