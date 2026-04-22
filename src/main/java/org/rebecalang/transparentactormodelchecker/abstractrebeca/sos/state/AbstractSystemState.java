package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;


@SuppressWarnings("serial")
public abstract class AbstractSystemState implements Serializable, Cloneable {

	protected volatile ActivationRecord environment;

	protected ActorsContainer actorsContainer;

	public AbstractSystemState() {
		actorsContainer = new ActorsContainer();
	}
	
	public Collection<? extends AbstractActorState> getActorsStatesValues() {
		return actorsContainer.getActorsStates();
	}
	
	public Set<Integer> getActorsIds() {
		return actorsContainer.getActorsIds();
	}
	
//	public HashMap<Integer, ? extends AbstractActorState> getActorsState() {
//		return actorsState;
//	}


	public AbstractActorState getActorState(int id) {
		return actorsContainer.getActorState(id);
	}

	public void setActorState(int id, AbstractActorState newState) {
		newState.setEnvironment(environment);
		actorsContainer.setActor(id, newState);
	}

	public void setActorState(AbstractActorState newState) {
		setActorState(newState.getId(), newState);
	}
	
	public void destroyActorState(AbstractActorState newState) {
		actorsContainer.destroyActorState(newState.getId());
	}
	
//	public void addNewActorState(AbstractActorState newState) {
//		int max = Collections.max(getActorsIds());
//		newState.setId(max + 1);
//		setActorState(newState);
//	}

	public void setEnvironment(ActivationRecord environment) {
		this.environment = environment;
		actorsContainer.setEnvironment(environment);
		environment.setVariableValue(ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME, actorsContainer);
	}
	
	public String toString() {
		String result = "{\nEnv:" + environment + "|\n";
		result += actorsContainer.deepToString() + "}";
		return result;
	}
	
	public int hashCode() {

		/**
		 * The following code is the unfold version of:
		 * <code>
		 * int result = 1;
		 * result = prime * result + ((actorsState == null) ? 0 : actorsState.hashCode());
		 * </code>
		 */
		final int prime = 31;
		int result = prime;
//		result = prime * result + ((actorsContainer == null) ? 0 : actorsContainer.hashCode());		
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		AbstractNetworkState networkState = getNetworkState();
		result = prime * result + ((networkState == null) ? 0 : networkState.hashCode());
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
		AbstractSystemState other = (AbstractSystemState) obj;
//		if (actorsContainer == null) {
//			if (other.actorsContainer != null)
//				return false;
//		} else if (!actorsContainer.equals(other.actorsContainer)) {
//			return false;
//		}
			
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;

		AbstractNetworkState networkState = getNetworkState();
		if (networkState == null) {
			if (other.getNetworkState() != null)
				return false;
		} else if (!networkState.equals(other.getNetworkState()))
			return false;
		return true;
	}
	
	public abstract AbstractNetworkState getNetworkState();
	
	public abstract AbstractSystemState clone();
	
	protected void clone(AbstractSystemState clonedState) {
		CloningRepository.resetRepository();
		clonedState.environment = environment.clone();
		clonedState.actorsContainer = 
				(ActorsContainer) clonedState.environment.getVariableValue(
						ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME);
		for(AbstractActorState actorState : clonedState.actorsContainer.getActorsStates()) {
			actorState.setEnvironment(clonedState.environment);
		}
	}
}
