package org.praisenter.video;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) throws IOException {
		Video video = VideoLibrary.getVideo("C:\\Users\\uswibit\\Desktop\\William\\Video\\big_buck_bunny.ogv");
		ImageIO.write(video.firstFrame, "png", new File("C:\\Users\\uswibit\\Desktop\\test.png"));
	}
}
