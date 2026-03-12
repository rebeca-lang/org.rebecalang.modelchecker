package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.MethodLookup;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MethodCallAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class MethodCallRule extends AbstractSOSRule<AbstractActorState> {

//	private HashMap<String, String> methodLookupTable;
	private MethodLookup methodLookup;

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {

		MethodCallInstructionBean mcib = 
				(MethodCallInstructionBean) additional[0];
		state.movePCtoTheNextInstruction();
		ArrayList<Pair<String, Object>> paramValues = new ArrayList<Pair<String,Object>>();
		for(String paramName : mcib.getParameters().keySet()) {
			Object paramValue = mcib.getParameters().get(paramName);
			if(paramValue instanceof Variable)
				paramValue = base.getVariableValue(((Variable)paramValue));
			paramValues.add(new Pair<String, Object>(paramName, paramValue));
		}
		if(mcib.getBase() != null) {
			state = (AbstractActorState) base.getVariableValue(
					((Variable)mcib.getBase()));
		}
		state.newCallPushToScope(mcib.getFunctionCallResult());
		for(Pair<String, Object>paramValue : paramValues) {
			state.addVariableToScope(paramValue.getFirst(), paramValue.getSecond());
		}
		String methodName = methodLookup.resolveName(mcib.getMethodName());
		state.addVariableToScope(AbstractActorState.PC, 
				new Pair<String, Integer>(methodName, 0));
		
		return Transition.createDeterministicTransition(new MethodCallAction(methodName), state);
	}

	public void setMethodLookup(MethodLookup methodLookup) {
		this.methodLookup = methodLookup;
	}

}
