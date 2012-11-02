package org.praisenter.media;

public interface MediaLoader<E extends Media> {
	// FIXME public String[] getSupportedExtensions(); 
	public E load(String fileName) throws MediaException;
}
