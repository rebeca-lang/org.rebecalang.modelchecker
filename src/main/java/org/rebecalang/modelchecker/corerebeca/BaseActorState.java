package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.AbstractExpressionTranslator;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseActorState implements Serializable {
    protected ActorScopeStack actorScopeStack;
    protected String name;
    protected String typeName;

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public abstract void addToQueue(MessageSpecification msgSpec);

    public abstract boolean actorQueueIsEmpty();

    public abstract void execute(State state, RILModel transformedRILModel, AbstractPolicy policy);

    public abstract MessageSpecification getMessage();

    public void initializePC(String methodName, int lineNum) {
//		String location = getLocationName(methodName);
        addVariableToRecentScope(InstructionUtilities.PC_STRING, new ProgramCounter(methodName, lineNum));
        addVariableToRecentScope(AbstractExpressionTranslator.RETURN_VALUE, 0);
    }

    public void clearPC() {
        actorScopeStack.removeVariable(InstructionUtilities.PC_STRING);
    }

    public void setPC(String methodName, int lineNum) {
        ProgramCounter pc = (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
        pc.setLineNumber(lineNum);
        pc.setMethodName(methodName);
    }

    public void increasePC() {
        ProgramCounter pc = (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
        pc.setLineNumber(pc.getLineNumber() + 1);
    }

    public ProgramCounter getPC() {
        return (ProgramCounter) retrieveVariableValue(InstructionUtilities.PC_STRING);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void pushInActorScope(String relatedRebecType) {
        actorScopeStack.pushInScopeStack(relatedRebecType);
    }

    public void pushInActorScope(String relatedRebecType, String prevRebecType) {
        actorScopeStack.pushInScopeStack(relatedRebecType, prevRebecType);
    }

    public void popFromActorScope() {
        actorScopeStack.popFromScopeStack();
    }

    public void addVariableToRecentScope(String varName, Object valueObject) {
        actorScopeStack.addVariable(varName, valueObject);
    }

    public void addVariableToExactScope(String varName, Object valueObject, int scopeIndex) {
        actorScopeStack.addVariable(varName, valueObject, scopeIndex);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void initializeScopeStack() {
        actorScopeStack = new ActorScopeStack();
        actorScopeStack.initialize();
    }

    public Object retrieveVariableValue(Variable variable) {
        return retrieveVariableValue(variable.getVarName());
    }

    public Object retrieveVariableValue(String varName) {
        return actorScopeStack.retrieveVariableValue(varName);
    }

    public void setVariableValue(String varName, Object valueObject) {
        actorScopeStack.setVariableValue(varName, valueObject);
    }

    public boolean variableIsDefined(String varName) {
        return actorScopeStack.variableIsDefined(varName);
    }

    public ActorScopeStack getActorScopeStack() {
        return actorScopeStack;
    }
}
