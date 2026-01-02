package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;

public interface ExternalMethodExecutor {
	public Object execute(StatementInterpreterContainer statementInterpreterContainer, ExternalMethodCallInstructionBean methodCallInstructionBean, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState);
}
