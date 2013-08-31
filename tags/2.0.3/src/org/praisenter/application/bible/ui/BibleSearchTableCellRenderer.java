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
package org.praisenter.application.bible.ui;

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.praisenter.data.bible.BibleSearchType;

/**
 * Custom cell renderer for Bible searching used to highlight matched text.
 * @author William Bittle
 * @version 2.0.1
 * @since 2.0.1
 */
public class BibleSearchTableCellRenderer extends DefaultTableCellRenderer {
	/** The version id */
	private static final long serialVersionUID = 2857988371072615926L;
	
	/** The search performed */
	private BibleSearch search;
	
	/**
	 * Full constructor.
	 * @param search the search performed
	 */
	public BibleSearchTableCellRenderer(BibleSearch search) {
		this.search = search;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		// make sure the value is of type string (sanity check)
		if (value instanceof String) {
			String text = (String)value;
			
			// determine the matching pattern
			// we use the Pattern/Matcher classes here to facilitate case insensitive matching
			// and more efficient replacement.
			Pattern pattern = null;
			if (search.getType() == BibleSearchType.PHRASE) {
				// just use the text
				pattern = Pattern.compile(search.getText(), Pattern.CASE_INSENSITIVE);
			} else if (search.getType() == BibleSearchType.ALL_WORDS ||
					   search.getType() == BibleSearchType.ANY_WORD) {
				// split the search text and rejoin by an or regex
				pattern = Pattern.compile(StringUtils.join(search.getText().split("(\\s*,)?\\s+"), "|"), Pattern.CASE_INSENSITIVE);
			} else {
				// if its search by location, then don't do anything
				return this;
			}
			
			Matcher matcher = pattern.matcher(text);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				// wrap each matched text with a span with a background color of yellow
				// when the row is selected, make sure the text shows through as black instead of white or whatever the default select color is
				matcher.appendReplacement(sb, "<span style='background-color: #FFFF00;" + (isSelected ? " color: black;" : "") + "'>" + matcher.group() + "</span>");
			}
			matcher.appendTail(sb);
			// use <nobr> tag to force nowrap on the text
			this.setText("<html><nobr>" + sb.toString() + "</nobr></html>");
		}
		
		return this;
	}
}
