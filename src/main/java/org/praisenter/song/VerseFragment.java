package org.praisenter.song;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(value = {
		Br.class,
		Chord.class,
		Comment.class,
		TextFragment.class
})
public interface VerseFragment extends SongOutput {

}
