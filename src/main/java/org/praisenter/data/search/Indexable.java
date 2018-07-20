package org.praisenter.data.search;

import java.util.List;

import org.apache.lucene.document.Document;
import org.praisenter.data.Identifiable;

public interface Indexable extends Identifiable {
	
	public static final String FIELD_ID = "docId";
	public static final String FIELD_TEXT = "docText";
	public static final String FIELD_TYPE = "docType";
	
	public List<Document> index();
}
