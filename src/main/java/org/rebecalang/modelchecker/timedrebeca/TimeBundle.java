package org.rebecalang.modelchecker.timedrebeca;

import java.util.Hashtable;

public class TimeBundle {

	private Hashtable<String, ActorTimeBundle> actorsTimeBundle;

	public ActorTimeBundle getActorTimeBundle(String actorName) {
		return actorsTimeBundle.get(actorName);
	}

	public void setActorsTimeBundle(Hashtable<String, ActorTimeBundle> timeBundle) {
		this.actorsTimeBundle = timeBundle;
	}
}
