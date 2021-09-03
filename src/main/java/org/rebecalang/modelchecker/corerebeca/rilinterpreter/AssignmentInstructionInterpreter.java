package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.SemanticCheckerUtils;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class AssignmentInstructionInterpreter extends InstructionInterpreter {

    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        AssignmentInstructionBean aib = (AssignmentInstructionBean) ib;
        Object valueFirst = InstructionUtilities.getValue(aib.getFirstOperand(), baseActorState);
        Object valueSecond = InstructionUtilities.getValue(aib.getSecondOperand(), baseActorState);
        Object result = valueFirst;
        String operator = aib.getOperator();
        if (operator != null) {
            if (valueFirst instanceof BaseActorState) {
                if (operator.equals("=="))
                    result = (((BaseActorState) valueFirst).getName().
                            equals(((BaseActorState) valueSecond).getName()));
                else if (operator.equals("!="))
                    result = !(((BaseActorState) valueFirst).getName().
                            equals(((BaseActorState) valueSecond).getName()));
                else if (operator.equals("instanceof")) //ToDo: polymorphism remaining
                    result = checkSecondIsAncestor(((BaseActorState) valueFirst).getActorScopeStack(), valueSecond.toString());
                else
                    throw new RebecaRuntimeInterpreterException(
                            "this case should not happen!! should've been reported as an error by compiler!");
            } else
                result = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
        }

        baseActorState.setVariableValue(((Variable) aib.getLeftVarName()).getVarName(), result);
        baseActorState.increasePC();
    }

    private boolean checkSecondIsAncestor(ActorScopeStack currentScope, String actorType) {
        ActivationRecord cursor = currentScope.getActivationRecords().getLast();
        while (cursor != null) {
            if (cursor.getRelatedRebecType().equals(actorType))
                return true;
            cursor = cursor.getPreviousScope();
        }
        return false;
    }
}
