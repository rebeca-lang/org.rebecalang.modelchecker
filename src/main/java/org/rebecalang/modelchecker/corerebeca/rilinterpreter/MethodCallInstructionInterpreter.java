package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MethodCallInstructionInterpreter extends InstructionInterpreter {

	public final static String CALL_RESULT = "$CALL_RESULT$";
	
    @Override
    public void interpret(InstructionBean ib, BaseActorState<?> baseActorState, State<? extends BaseActorState<?>> globalState, RebecaModel rebecaModel) {
        baseActorState.increasePC();
        MethodCallInstructionBean mcib = (MethodCallInstructionBean) ib;
        baseActorState.pushInScopeStackForMethodCallInitialization(mcib.getMethodName().split("\\.")[0]);
        for (Entry<String, Object> entry : mcib.getParameters().entrySet()) {
            String paramName = entry.getKey();
			Object paramValue = entry.getValue();
            if (paramValue instanceof Variable)
                paramValue = baseActorState.retrieveVariableValue((Variable) paramValue);
			baseActorState.addVariableToRecentScope(paramName, paramValue);
        }
        baseActorState.addVariableToRecentScope(InstructionUtilities.RETURN_VALUE, 0);
        baseActorState.addVariableToRecentScope(CALL_RESULT, 
        		mcib.getFunctionCallResult() == null ? null : mcib.getFunctionCallResult().getVarName());
        baseActorState.initializePC(mcib.getMethodName(), 0);
    }
}
