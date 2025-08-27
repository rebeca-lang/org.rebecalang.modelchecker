package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaRebecInstantiationSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Pair<CoreRebecaActorState, InstructionBean> source) {

		RebecInstantiationInstructionBean riib = 
				(RebecInstantiationInstructionBean) source.getSecond();
		CoreRebecaActorState actorState = source.getFirst();
		actorState.movePCtoTheNextInstruction();
		
		Type newInstanceType = riib.getType();
		CoreRebecaActorState newInstance = 
				CoreRebecaActorState.createTempCoreRebecaActorState(newInstanceType);
		actorState.addVariableToScope(riib.getResultTarget().getVarName(), 
				newInstance);
		newInstance.setRILModel(actorState.getRILModel());
		NewInstanceAction resultAction = new NewInstanceAction(
				newInstance, newInstanceType);
		
		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = 
				new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState,InstructionBean>>();
		result.setDestination(source);
		result.setAction(resultAction);

		return result;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Action action, Pair<CoreRebecaActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}
