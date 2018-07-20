/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.javafx;

import java.util.function.Supplier;

import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.RuntimeProperties;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Enumeration of application level actions.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public enum ApplicationAction {

	// shared
	
	/** Trigger an edit for the selected item */
	OPEN(Translations.get("action.open")),
	
	/** Open the editor for the event's data */
	EDIT(Translations.get("action.edit")),
	
	/** The undo action */
	UNDO(Translations.get("action.undo"), () -> { 
		return ApplicationGlyphs.MENU_UNDO.duplicate();
	}, new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN)),
	
	/** The redo action */
	REDO(Translations.get("action.redo"), () -> { 
		return ApplicationGlyphs.MENU_REDO.duplicate();
	}, RuntimeProperties.IS_WINDOWS_OS
			// windows
			? new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)
			// mac/ubuntu
			: new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN)),
	
	/** Close the current editor */
	CLOSE(Translations.get("action.close"), () -> {
		return ApplicationGlyphs.MENU_CLOSE.duplicate();
	}),
	
	/** Save the current document */
	SAVE(Translations.get("action.save"), () -> { 
		return ApplicationGlyphs.MENU_SAVE.duplicate(); 
	}, new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)),
	
	/** Save the current document with a different name  */
	SAVE_AS(Translations.get("action.saveas"), () -> { 
		return ApplicationGlyphs.MENU_SAVE_AS.duplicate();
	}, new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN)),

	/** Rename the item */
	RENAME(Translations.get("action.rename"), () -> { 
		return ApplicationGlyphs.MENU_RENAME.duplicate();
	}, new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN)),
	
	/** Delete the item */
	DELETE(Translations.get("action.delete"), () -> { 
		return ApplicationGlyphs.MENU_DELETE.duplicate(); 
	}, new KeyCodeCombination(KeyCode.DELETE)),
	
	/** Select all the items */
	SELECT_ALL(Translations.get("action.selectall"), new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN)),
	
	/** Select no items */
	SELECT_NONE(Translations.get("action.selectnone")),
	
	/** Select all other items */
	SELECT_INVERT(Translations.get("action.selectinvert")),
	
	/** Copy the item */
	COPY(Translations.get("action.copy"), () -> { 
		return ApplicationGlyphs.MENU_COPY.duplicate();
	}, new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN)),
	
	/** Cut the item */
	CUT(Translations.get("action.cut"), () -> { 
		return ApplicationGlyphs.MENU_CUT.duplicate();
	}, new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN)),
	
	/** Paste the item */
	PASTE(Translations.get("action.paste"), () -> { 
		return ApplicationGlyphs.MENU_PASTE.duplicate();
	}, new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN)),
	
	// root level
	
	/** Show the preferences view */
	PREFERENCES(Translations.get("action.preferences"), () -> { 
		return ApplicationGlyphs.MENU_PREFERENCES.duplicate();
	}),
	
	/** Prompt to import slides */
	IMPORT_SLIDES(Translations.get("action.import.slides")),
	
	/** Prompt to import songs */
	IMPORT_SONGS(Translations.get("action.import.songs")),
	
	/** Prompt to import media */
	IMPORT_MEDIA(Translations.get("action.import.media")),

	/** Prompt to import bibles */
	IMPORT_BIBLES(Translations.get("action.import.bibles")),
	
	/** Prompt to export */
	EXPORT(Translations.get("action.export"), () -> { 
		return ApplicationGlyphs.MENU_EXPORT.duplicate();
	}),

	// navigation
	
	/** Show the present view */
	PRESENT(Translations.get("action.present")),
	
	/** Show the media list view */
	MANAGE_MEDIA(Translations.get("action.manage.media")),
	
	/** Show the bibles list view */
	MANAGE_BIBLES(Translations.get("action.manage.bibles")),
	
	/** Show the slides list view */
	MANAGE_SLIDES(Translations.get("action.manage.slides")),
	
	/** Show the slide shows list view */
	MANAGE_SHOWS(Translations.get("action.manage.shows")),
	
	/** Show the songs list view */
	MANAGE_SONGS(Translations.get("action.manage.songs")),
	
	/** Show the about info */
	ABOUT(Translations.get("action.about"), () -> { 
		return ApplicationGlyphs.MENU_ABOUT.duplicate();
	}),
	
	/** View the application logs */
	LOGS(Translations.get("action.viewlogs")),
	
	/** Exit the application */
	EXIT(Translations.get("action.exit")),
	
	// bible
	
	/** Create a new bible */
	NEW_BIBLE(Translations.get("action.bible.newbible")),
	
	/** Create a new book for a bible */
	NEW_BOOK(Translations.get("action.bible.newbook")),
	
	/** Create a new chapter for a book */
	NEW_CHAPTER(Translations.get("action.bible.newchapter")),
	
	/** Create a new verse for a chapter */
	NEW_VERSE(Translations.get("action.bible.newverse")),
	
	/** Renumbers the selection */
	RENUMBER(Translations.get("action.bible.renumber")),
	
	/** Reorders the selection */
	REORDER(Translations.get("action.bible.reorder")),
	
	/** Re-indexes the bible search index */
	REINDEX_BIBLES(Translations.get("action.reindex")),
	
	// slide
	
	/** Create a new slide */
	NEW_SLIDE(Translations.get("action.slide.newslide")),
	
	NEW_SLIDE_SHOW(Translations.get("action.slide.newslideshow"))
	
	;
	
	/** The action label */
	private String label;
	
	/** The action graphic */
	private Supplier<Node> graphic;
	
	/** The action accelerator */
	private KeyCombination accelerator;
	
	/**
	 * Minimal constructor.
	 * @param label the text
	 */
	private ApplicationAction(String label) {
		this(label, null, null);
	}
	
	/**
	 * Optional constructor.
	 * @param label the text
	 * @param graphic the graphic supplier function (to avoid nodes being used twice in the scene graph)
	 */
	private ApplicationAction(String label, Supplier<Node> graphic) {
		this(label, graphic, null);
	}
	
	/**
	 * Optional constructor.
	 * @param label the text
	 * @param accelerator the accelerator
	 */
	private ApplicationAction(String label, KeyCombination accelerator) {
		this(label, null, accelerator);
	}
	
	/**
	 * Full constructor.
	 * @param label the text
	 * @param graphic the graphic supplier function (to avoid nodes being used twice in the scene graph)
	 * @param accelerator the accelerator
	 */
	private ApplicationAction(String label, Supplier<Node> graphic, KeyCombination accelerator) {
		this.label = label;
		this.graphic = graphic;
		this.accelerator = accelerator;
	}
	
	/**
	 * Creates a new MenuItem for use in a menu for this {@link ApplicationAction}.
	 * @return MenuItem
	 */
	public MenuItem toMenuItem() {
		return this.toMenuItem(this.label);
	}
	
	/**
	 * Creates a new MenuItem for use in a menu for this {@link ApplicationAction}.
	 * @param label a different label for an action
	 * @return MenuItem
	 */
	public MenuItem toMenuItem(String label) {
		return this.toMenuItem(label, this.graphic != null ? this.graphic.get() : null);
	}

	/**
	 * Creates a new MenuItem for use in a menu for this {@link ApplicationAction}.
	 * @param label a different label for the action
	 * @param graphic a different graphic for the action
	 * @return MenuItem
	 */
	public MenuItem toMenuItem(String label, Node graphic) {
		MenuItem item = new MenuItem(label);
		item.setGraphic(graphic);
		item.setAccelerator(this.accelerator);
		item.setUserData(this);
		return item;
	}
	
	/**
	 * Creates a new Button for use in the application for this {@link ApplicationAction}.
	 * @return Button
	 */
	public Button toButton() {
		return this.toButton(this.label);
	}

	/**
	 * Creates a new Button for use in the application for this {@link ApplicationAction}.
	 * @param label a different label for an action
	 * @return Button
	 */
	public Button toButton(String label) {
		return this.toButton(label, this.graphic != null ? this.graphic.get() : null);
	}

	/**
	 * Creates a new Button for use in the application for this {@link ApplicationAction}.
	 * @param label a different label for the action
	 * @param graphic a different graphic for the action
	 * @return Button
	 */
	public Button toButton(String label, Node graphic) {
		Button button = new Button(label);
		button.setGraphic(graphic);
		button.setUserData(this);
		return button;
	}
	
	/**
	 * Returns the accelerator for this action.
	 * @return KeyCombination
	 */
	public KeyCombination getAccelerator() {
		return this.accelerator;
	}
	
	/**
	 * Returns the label for this action.
	 * @return String
	 */
	public String getLabel() {
		return this.label;
	}
}
