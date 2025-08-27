package org.rebecalang.modelchecker.corerebeca;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.ObjectModelUtils;
import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MainRebecDefinition;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.ModelChecker;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
//@Log4j2
public class CoreRebecaDFSModelChecker extends ModelChecker {
	
	int stateCounter;

	
	@Autowired
	@Override
	public void setTypeSystem(@Qualifier("CORE_REBECA") AbstractTypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}

	@Override
	protected State<ActorState> createFreshState() {
		return new State<ActorState>();
	}

	@Override
	protected BaseActorState<MessageSpecification> createInitialActorState() {
		return new ActorState();
	}

	@Override
	protected void doModelChecking(
			RILModel transformedRILModel
			, RebecaModel rebecaModel
	) throws ModelCheckingException {
		stateCounter = 1;
		
		State<ActorState> initialState = (State<ActorState>) stateSpace.getInitialState();
		expandState(initialState, transformedRILModel, rebecaModel);
	}

	protected void expandState(
			State<ActorState> currentState,
			RILModel transformedRILModel,
			RebecaModel rebecaModel) throws ModelCheckingException {
		
		List<ActorState> enabledActors = currentState.getEnabledActors();
		if (enabledActors.isEmpty())
			throw new ModelCheckingException("Deadlock");
		for (ActorState actorState : enabledActors) {
			do {
				statementInterpreterContainer.clearNondeterminism();
				State<ActorState> newState = executeNewState(currentState, actorState, transformedRILModel, rebecaModel);

				if (!newState.getParentStates().isEmpty()) {
//					nextStatesQueue.add(newState);
				}
			} while (statementInterpreterContainer.hasNondeterminism());
		}

	}

	@Override
	protected State<ActorState> createInitialStates(RebecaModel rebecaModel) {

		State<ActorState> initialState = createFreshState();
		for (MainRebecDefinition definition : ObjectModelUtils.getMainRebecDefinition(rebecaModel)) {
			ActorState actorState = (ActorState) createAnActorInitialState(rebecaModel, definition);
			initialState.putActorState(definition.getName(), actorState);
		}
		return initialState;
	}

	private State<ActorState> executeNewState(
			State<ActorState> currentState,
			ActorState actorState,
			RILModel transformedRILModel,
			RebecaModel rebecaModel) {
		State<ActorState> newState = (State<ActorState>) super.cloneState(currentState);

		ActorState newActorState = (ActorState) newState.getActorState(actorState.getName());
		newActorState.execute(newState, statementInterpreterContainer,
				transformedRILModel, rebecaModel, modelCheckingPolicy, null);
		String transitionLabel = calculateTransitionLabel(actorState, newActorState, null);
		Long stateKey = Long.valueOf(newState.hashCode());

		if (!stateSpace.hasStateWithKey(stateKey)) {
			newState.setId(stateCounter++);
			stateSpace.addState(stateKey, newState);
			newState.clearLinks();
			currentState.addChildState(transitionLabel, newState);
			newState.addParentState(transitionLabel, currentState);
		} else {
			State<ActorState> repeatedState = (State<ActorState>) stateSpace.getState(stateKey);
			currentState.addChildState(transitionLabel, repeatedState);
			repeatedState.addParentState(transitionLabel, currentState);
		}

		return newState;
	}
}