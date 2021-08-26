package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.AbstractExpressionTranslator;

public class EndMethodInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        Object retreivedReturnVariableValue = baseActorState.retrieveVariableValue(AbstractExpressionTranslator.RETURN_VALUE);
        baseActorState.popFromActorScope();
        baseActorState.setVariableValue(AbstractExpressionTranslator.RETURN_VALUE, retreivedReturnVariableValue);
    }

    @Override
    public String toString() {
        return "endMethod";
    }
}
