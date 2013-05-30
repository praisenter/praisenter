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
package org.praisenter.common.xml;

import java.awt.Dimension;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;

/**
 * Dimension type adapter for xml output.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class DimensionTypeAdapter extends XmlAdapter<String, Dimension> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(DimensionTypeAdapter.class);
	
	/** The color components delimiter */
	private static final String DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Dimension v) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(v.width).append(DELIMITER).append(v.height);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Dimension unmarshal(String v) throws Exception {
		String[] typeData = v.split(DELIMITER);
		if (typeData.length == 2) {
			try {
			return new Dimension(
					Integer.parseInt(typeData[0]),
					Integer.parseInt(typeData[1]));
			} catch (Exception e) {
				LOGGER.error("Unable to parse dimension value [" + v + "]: ", e);
			}
		} else {
			LOGGER.warn("Data not in the correct format [" + v + "].");
		}
		return null;
	}
}
