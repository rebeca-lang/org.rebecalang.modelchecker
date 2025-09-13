package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public abstract class RebecInstantiationSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>> {

	protected abstract AbstractActorState createTempActorState(Type type);
	
	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {

		RebecInstantiationInstructionBean riib = 
				(RebecInstantiationInstructionBean) state.getSecond();
		AbstractActorState actorState = state.getFirst();
		actorState.movePCtoTheNextInstruction();
		
		Type newInstanceType = riib.getType();
		AbstractActorState newInstance = 
				createTempActorState(newInstanceType);
		actorState.addVariableToScope(riib.getResultTarget().getVarName(), 
				newInstance);
		newInstance.setRILModel(actorState.getRILModel());
		NewInstanceAction resultAction = new NewInstanceAction(
				newInstance, newInstanceType);
		
		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
				new DeterministicTransition<Pair<? extends AbstractActorState,InstructionBean>>();
		result.setDestination(state);
		result.setAction(resultAction);

		return result;
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base,
					Action action, Pair<? extends AbstractActorState, InstructionBean> state) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}
