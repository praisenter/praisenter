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
package org.praisenter.slide;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.common.xml.ClassTypeAdapter;

/**
 * Class containing metadata information about an export file.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "ExportItem")
@XmlAccessorType(XmlAccessType.NONE)
public class ExportItem {
	/** The original file name */
	@XmlElement(name = "FileName", nillable = false, required = true)
	protected String fileName;
	
	/** The in-export file name */
	@XmlElement(name = "ExportFileName", nillable = false, required = true)
	protected String exportFileName;
	
	/** The class type */
	@XmlElement(name = "Class", nillable = false, required = true)
	@XmlJavaTypeAdapter(value = ClassTypeAdapter.class)
	protected Class<?> type;
	
	/**
	 * Default constructor.
	 */
	protected ExportItem() {}
	
	/**
	 * Full constructor.
	 * @param fileName the original file name
	 * @param exportFileName the in-export file name
	 * @param type the class type
	 */
	public ExportItem(String fileName, String exportFileName, Class<?> type) {
		this.fileName = fileName;
		this.exportFileName = exportFileName;
		this.type = type;
	}

	/**
	 * Returns the original file name.
	 * @return String
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Returns the in-export file name.
	 * @return String
	 */
	public String getExportFileName() {
		return this.exportFileName;
	}
	
	/**
	 * Returns the class type.
	 * @return Class&lt;?&gt;
	 */
	public Class<?> getType() {
		return this.type;
	}
}
