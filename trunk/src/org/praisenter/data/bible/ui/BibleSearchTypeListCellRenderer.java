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
	private static final long serialVersionUID = 4263685716936842797L;
	
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
