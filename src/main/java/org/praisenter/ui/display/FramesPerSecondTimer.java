package org.praisenter.ui.display;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.AnimationTimer;

/**
 * Class that executes a specified runnable at the desired
 * frames-per-second (FPS) on the Java FX UI thread.
 * <p>
 * Since this class is bound to the Java FX UI thread it's possible
 * the desired FPS cannot be met. This class makes no attempt to reconcile
 * this gap and will just run on every tick if it can't keep up.
 * <p>
 * Since the given runnable runs on the Java FX UI thread you should keep
 * the processing to a minimum and pass off to another thread as early as
 * possible.
 * @author William Bittle
 * 
 */
public final class FramesPerSecondTimer extends AnimationTimer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final double NANOSECONDS_TO_SECONDS = 1e9;
	
	@SuppressWarnings("unused")
	private final int fps;
	private final double nanosBetweenFrames;
	private final Consumer<Long> fn;
	
	private long lastTimeStamp;
	private long elapsedTime;
	private long frameNumber;
	
	public FramesPerSecondTimer(int fps, Consumer<Long> fn) {
		this.fps = fps;
		this.fn = fn;
		this.nanosBetweenFrames = NANOSECONDS_TO_SECONDS / (double)(fps + 1);
		
		this.lastTimeStamp = -1;
		this.elapsedTime = 0;
		this.frameNumber = 0;
	}
	
	@Override
	public void handle(long now) {
		if (this.lastTimeStamp == -1) {
			this.lastTimeStamp = now;
			return;
		}
		
		// 60 frames / 1 second
		// 1 second = 1e9 nanoseconds
		// 1e9 / 60 => every frame should take 16666666.6667 nanoseconds
		
		// nanos between last and now
		long elapsedTime = now - this.lastTimeStamp;
		this.lastTimeStamp = now;
		
		this.elapsedTime += elapsedTime;
		
		// check if we've waited long enough
		if (this.elapsedTime >= this.nanosBetweenFrames) {
			this.frameNumber += 1;
			this.elapsedTime -= this.nanosBetweenFrames;
			
			if (this.elapsedTime >= this.nanosBetweenFrames) {
				int n = (int)Math.ceil(this.elapsedTime / this.nanosBetweenFrames);
				this.frameNumber += n;
				LOGGER.debug("Java FX AnimationTimer dropped {} frames", n);
				this.elapsedTime = 0;
			}
			
			this.fn.accept(this.frameNumber);
		}
	}
}
