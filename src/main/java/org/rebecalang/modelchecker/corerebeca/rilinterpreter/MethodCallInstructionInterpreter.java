package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class MethodCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		actorState.increasePC();

		MethodCallInstructionBean mcib = (MethodCallInstructionBean) ib;
		List<Object> calculatedValuesOfParams = new LinkedList<Object>();
		for(int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
			Object paramValue = mcib.getParameters().get(cnt);
			if (paramValue instanceof Variable)
				calculatedValuesOfParams.add(actorState.retreiveVariableValue((Variable) paramValue));
			else
				calculatedValuesOfParams.add(paramValue);
		}
		actorState.pushInActorScope();
		for(int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
			Object paramValue = calculatedValuesOfParams.get(cnt);
			String paramName = mcib.getParametersNames().get(cnt);
			actorState.addVariableToRecentScope(paramName, paramValue);
		}
		actorState.adjustLinkToPreviousScopeForMethodCall();
		actorState.initializePC(mcib.getMethodName(), 0);
		return;
	}

}
