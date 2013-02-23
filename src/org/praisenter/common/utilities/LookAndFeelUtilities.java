/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.common.utilities;

import javax.swing.UIManager;

/**
 * Utility class for managing look and feels.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class LookAndFeelUtilities {
	/** The Nimbus look and feel name */
	public static final String NIMBUS = "Nimbus";
	
	/** The Metal (default) look and feel name */
	public static final String METAL = "Metal";
	
	/** Hidden default constructor */
	private LookAndFeelUtilities() {}
	
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
