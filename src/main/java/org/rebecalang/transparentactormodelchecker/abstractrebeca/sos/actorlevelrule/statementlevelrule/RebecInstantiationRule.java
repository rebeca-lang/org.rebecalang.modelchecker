package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import java.util.Collections;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorsContainer;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class RebecInstantiationRule extends AbstractSOSRule<AbstractActorState> {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {

		RebecInstantiationInstructionBean riib = 
				(RebecInstantiationInstructionBean) additional[0];
		state.movePCtoTheNextInstruction();
		
		Type newInstanceType = riib.getType();
		AbstractActorState newInstance = state.createNewActorState(newInstanceType);
		ActorsContainer container = (ActorsContainer) 
				state.getEnvironment().getVariableValue(
				ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME);
		int max = Collections.max(container.getActorsIds());
		newInstance.setId(max + 1);
		newInstance.addVariableToScope("self", newInstance);
		newInstance.setEnvironment(state.getEnvironment());
		container.setActor(newInstance.getId(), newInstance);
		
		state.addVariableToScope(riib.getResultTarget().getVarName(), 
				newInstance);
		newInstance.setRILModel(state.getRILModel());
		NewInstanceAction resultAction = new NewInstanceAction(
				newInstance, newInstanceType);
		
		return Transition.createDeterministicTransition(resultAction, state);
	}
}