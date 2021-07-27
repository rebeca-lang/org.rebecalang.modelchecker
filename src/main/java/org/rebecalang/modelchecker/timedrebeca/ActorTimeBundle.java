package org.rebecalang.modelchecker.timedrebeca;

import java.util.LinkedList;

public class ActorTimeBundle {

	private int now;
	private LinkedList<TimeBundleElement> queueBundles;
	
	
	public int getNow() {
		return now;
	}
	public void setNow(int now) {
		this.now = now;
	}
	public LinkedList<TimeBundleElement> getQueueBundles() {
		return queueBundles;
	}
	public void setQueueBundles(LinkedList<TimeBundleElement> queueBundles) {
		this.queueBundles = queueBundles;
	}
}
