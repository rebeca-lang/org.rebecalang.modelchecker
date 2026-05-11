package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ActorStateRepresentor implements Serializable {
	private int actorID;

	public ActorStateRepresentor(int id) {
		actorID = id;
	}

	public ActorStateRepresentor(AbstractActorState value) {
		this(value.getId());
	}

	public int getActorID() {
		return actorID;
	}

	@Override
	public ActorStateRepresentor clone() {
		return new ActorStateRepresentor(actorID);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actorID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorStateRepresentor other = (ActorStateRepresentor) obj;
		if (actorID != other.actorID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "actor->" + actorID;
	}
}