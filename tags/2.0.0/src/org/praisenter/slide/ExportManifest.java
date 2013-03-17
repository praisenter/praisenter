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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a list of export items for exported slide or templates.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "ExportManifest")
@XmlAccessorType(XmlAccessType.NONE)
public class ExportManifest {
	/** The export types */
	@XmlElement(name = "ExportItem", required = true, nillable = false)
	protected List<ExportItem> exportItems;
	
	/**
	 * Default constructor.
	 */
	public ExportManifest() {
		this(new ArrayList<ExportItem>());
	}
	
	/**
	 * Optional constructor.
	 * @param exportItem the export item
	 */
	public ExportManifest(ExportItem exportItem) {
		this.exportItems = new ArrayList<ExportItem>();
		this.exportItems.add(exportItem);
	}
	
	/**
	 * Full constructor.
	 * @param exportItems the export items
	 */
	public ExportManifest(List<ExportItem> exportItems) {
		if (exportItems == null) {
			exportItems = new ArrayList<ExportItem>();
		}
		this.exportItems = exportItems;
	}
	
	/**
	 * Returns the export item for the given file name.
	 * <p>
	 * Returns null if the file name was not found.
	 * @param exportFileName the in-export file name
	 * @return {@link ExportItem}
	 */
	public ExportItem getExportItem(String exportFileName) {
		for (ExportItem item : this.exportItems) {
			if (item.getExportFileName().equals(exportFileName)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Returns the list of export items.
	 * @return List&lt;{@link ExportItem}&gt;
	 */
	public List<ExportItem> getExportItems() {
		return this.exportItems;
	}
}
