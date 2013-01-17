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
package org.praisenter.data.bible.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.praisenter.data.bible.BibleSearchType;
import org.praisenter.resources.Messages;

/**
 * ListCellRenderer for the {@link BibleSearchType}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleSearchTypeListCellRenderer extends DefaultListCellRenderer {
	/** The version id */
	private static final long serialVersionUID = -4646920382842267739L;
	
	/* (non-Javadoc)
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value instanceof BibleSearchType) {
			BibleSearchType type = (BibleSearchType)value;
			if (type == BibleSearchType.ALL_WORDS) {
				this.setText(Messages.getString("panel.bible.search.type.allWords"));
				this.setToolTipText(Messages.getString("panel.bible.search.type.allWords.tooltip"));
			} else if (type == BibleSearchType.ANY_WORD) {
				this.setText(Messages.getString("panel.bible.search.type.anyWord"));
				this.setToolTipText(Messages.getString("panel.bible.search.type.anyWord.tooltip"));
			} else if (type == BibleSearchType.PHRASE) {
				this.setText(Messages.getString("panel.bible.search.type.phrase"));
				this.setToolTipText(Messages.getString("panel.bible.search.type.phrase.tooltip"));
			} else if (type == BibleSearchType.LOCATION) {
				this.setText(Messages.getString("panel.bible.search.type.location"));
				this.setToolTipText(Messages.getString("panel.bible.search.type.location.tooltip"));
			}
		}
		
		return this;
	}
}
