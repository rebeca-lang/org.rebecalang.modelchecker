package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import java.util.Hashtable;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExternalMethodRepository {

	@Autowired
	protected StatementInterpreterContainer statementInterpreterContainer;

	private Hashtable<String, ExternalMethodExecutor> executors;

	private ExternalMethodRepository() {
		executors = new Hashtable<String, ExternalMethodExecutor>();
	}

	public Object execute(ExternalMethodCallInstructionBean methodCallInstructionBean, BaseActorState<?> baseActorState,
			State<? extends BaseActorState<?>> globalState) {
		ExternalMethodExecutor externalMethodExecutor;
		if (methodCallInstructionBean.getBase() != null)
			throw new RuntimeException("This version does not support none-independent built-in method calls");
		externalMethodExecutor = executors.get(BuiltInMethodExecutor.KEY);
		Object returnValue = externalMethodExecutor.execute(statementInterpreterContainer, methodCallInstructionBean, baseActorState, globalState);
		if(methodCallInstructionBean.getFunctionCallResult() != null)
			baseActorState.setVariableValue(methodCallInstructionBean.getFunctionCallResult().toString(), returnValue);
		return null;
	}

	public void registerExecuter(String executerKey, BuiltInMethodExecutor methodExecutor) {
		executors.put(executerKey, methodExecutor);

	}

	public void clear() {
		executors.clear();
	}
}
