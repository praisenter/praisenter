package org.praisenter.easings;

public class TestEasings {
	public static void main(String[] args) {
		int n = 100;
		Easing easing = Easings.getEasingForId(BackEasing.ID);
		for (int i = 0; i < n; i++) {
			System.out.println(easing.easeInOut(i, n));
		}
	}
}
