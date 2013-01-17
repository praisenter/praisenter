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
package org.praisenter.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxEditor;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Represents a ComboBoxEditor featuring an auto-complete feature using the decorator pattern.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AutoCompleteComboBoxEditor extends KeyAdapter implements ComboBoxEditor, FocusListener {
	/** The root editor */
	private ComboBoxEditor editor;
	
	/**
	 * Minimal constructor.
	 * @param editor the ComboBoxEditor to decorate.
	 */
	public AutoCompleteComboBoxEditor(ComboBoxEditor editor) {
		this.editor = editor;
		this.editor.getEditorComponent().addKeyListener(this);
		this.editor.getEditorComponent().addFocusListener(this);
	}
	
	/**
	 * Returns the text of a matching item for the given text.
	 * <p>
	 * Returns null if no match was found.
	 * @param text the text to search
	 * @return String
	 */
	public abstract String match(String text);
	
	/**
	 * Returns the matching object for the given text.
	 * <p>
	 * Returns null if no match was found.
	 * @param text the text to search
	 * @return Object
	 */
	public abstract Object getItem(String text);
	
	/**
	 * Returns the text value of the given item.
	 * @param o the item
	 * @return String
	 */
	public abstract String getValue(Object o);
	
	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		if (!e.isTemporary()) {
			// add a runnable to be executed later
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					selectAll();
				}
			});
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.isActionKey() || event.isControlDown() || event.isAltDown() || event.isAltGraphDown() || event.isMetaDown()) return;
		if (keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_ALT_GRAPH || keyCode == KeyEvent.VK_SHIFT) return;
		if (keyCode != KeyEvent.VK_BACK_SPACE && keyCode != KeyEvent.VK_DELETE) {
			JTextComponent editor = (JTextComponent)this.getEditorComponent();
			// get the current text for the editor
			String text = editor.getText();
			// see if there is a match
			String match = this.match(text);
			
			if (match != null) {
				editor.setText(match);
				editor.setSelectionStart(text.length());
				editor.setSelectionEnd(match.length());
			} else {
				// save the selection start/end
				int ss = editor.getSelectionStart();
				int se = editor.getSelectionEnd();
				// reset the text to the original text
				editor.setText(text);
				// reset the selection start/end
				editor.setSelectionStart(ss);
				editor.setSelectionEnd(se);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#getItem()
	 */
	@Override
	public Object getItem() {
		JTextComponent editor = (JTextComponent)this.getEditorComponent();
		// get the current text for the editor
		String text = editor.getText();
		// get the item for the text
		return this.getItem(text);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
	 */
	@Override
	public void setItem(Object o) {
		if (o == null) {
			this.editor.setItem(null);
		} else {
			this.editor.setItem(this.getValue(o));
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
	 */
	@Override
	public void addActionListener(ActionListener l) {
		this.editor.addActionListener(l);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#getEditorComponent()
	 */
	@Override
	public Component getEditorComponent() {
		return this.editor.getEditorComponent();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
	 */
	@Override
	public void removeActionListener(ActionListener l) {
		this.editor.removeActionListener(l);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxEditor#selectAll()
	 */
	@Override
	public void selectAll() {
		this.editor.selectAll();
	}
}
