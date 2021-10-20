package org.praisenter.data;

import java.text.Collator;
import java.util.Comparator;

public final class PersistableComparator<T extends Persistable> implements Comparator<T> {
	private static final Collator COLLATOR = Collator.getInstance();

	@Override
	public int compare(T o1, T o2) {
		if (o1 == o2) return 0;
		if (o1 == null) return 1;
		if (o2 == null) return -1;

		// sort by name
		int diff = COLLATOR.compare(o1.getName(), o2.getName());
		if (diff == 0) {
			// then by modified date
			diff = o1.getModifiedDate().compareTo(o2.getModifiedDate());
			if (diff == 0) {
				// then by id
				diff = o1.getId().compareTo(o2.getId());
			}
		}
		
		return diff;	
	}
}
