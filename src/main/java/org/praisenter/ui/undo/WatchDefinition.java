package org.praisenter.ui.undo;

import java.util.function.BiConsumer;

final class WatchDefinition<T> {
	private final Class<T> clazz;
	private final BiConsumer<T, Watcher> registrar;
	
	public WatchDefinition(Class<T> clazz, BiConsumer<T, Watcher> registrar) {
		this.clazz = clazz;
		this.registrar = registrar;
	}
	
	public Class<T> getWatchClass() {
		return this.clazz;
	}
	
	public BiConsumer<T, Watcher> getRegistrar() {
		return this.registrar;
	}
}
