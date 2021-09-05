package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;

public interface ExternalMethodExecutor {
	public Object execute(ExternalMethodCallInstructionBean methodCallInstructionBean, BaseActorState baseActorState, State globalState);
}
