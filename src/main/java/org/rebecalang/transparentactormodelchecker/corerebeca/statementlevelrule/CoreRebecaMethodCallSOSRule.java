package org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule;

import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MethodCallAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaMethodCallSOSRule extends AbstractSOSRule<Pair<CoreRebecaActorState, InstructionBean>> {

	private HashMap<String, String> methodLookupTable;

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Pair<CoreRebecaActorState, InstructionBean> source) {

		MethodCallInstructionBean mcib = 
				(MethodCallInstructionBean) source.getSecond();
		CoreRebecaActorState actorState = source.getFirst();
		actorState.movePCtoTheNextInstruction();
		if(mcib.getBase() != null) {
			actorState = (CoreRebecaActorState) actorState.getVariableValue(
					((Variable)mcib.getBase()).getVarName());
		}
		actorState.pushToScope();
		for(String paramName : mcib.getParameters().keySet()) {
			Object paramValue = mcib.getParameters().get(paramName);
			if(paramValue instanceof Variable)
				paramValue = source.getFirst().getVariableValue(((Variable)paramValue).getVarName());
			actorState.addVariableToScope(paramName, paramValue);
		}
		String methodName = methodLookupTable.get(mcib.getMethodName());
		actorState.addVariableToScope(CoreRebecaActorState.PC, 
				new Pair<String, Integer>(methodName, 0));
		
		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = 
				new CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState,InstructionBean>>();
		result.setDestination(source);
		result.setAction(new MethodCallAction(methodName));

		return result;
	}

	@Override
	public CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> 
			applyRule(Action action, Pair<CoreRebecaActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

	public void setMethodLookupTable(HashMap<String, String> methodLookupTable) {
		this.methodLookupTable = methodLookupTable;
	}

}
