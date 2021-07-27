package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ActorState;

@SuppressWarnings("serial")
public class TimedActorState extends ActorState {
	private int now;

	public int getNow() {
		return now;
	}

	public void setNow(int now) {
		this.now = now;
	}

}
