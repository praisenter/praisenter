package org.praisenter.song;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(value = {
		Br.class,
		Chord.class,
		Comment.class,
		Text.class
})
public interface VerseFragment {

}
