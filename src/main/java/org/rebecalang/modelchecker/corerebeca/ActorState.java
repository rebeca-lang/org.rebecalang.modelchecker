package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator.AbstractExpressionTranslator;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class ActorState implements Serializable {

    private LinkedList<MessageSpecification> queue;
    protected ActorScopeStack actorScopeStack;
    private String name;
    private String typeName;
    private CoreRebecaTypeSystem typeSystem;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((queue == null) ? 0 : queue.hashCode());
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActorState other = (ActorState) obj;
        if (actorScopeStack == null) {
            if (other.actorScopeStack != null)
                return false;
        } else if (!actorScopeStack.equals(other.actorScopeStack))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (queue == null) {
            if (other.queue != null)
                return false;
        } else if (!queue.equals(other.queue))
            return false;
        if (typeName == null) {
            return other.typeName == null;
        } else return typeName.equals(other.typeName);
    }

    public LinkedList<MessageSpecification> getQueue() {
        return queue;
    }

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

    public void setQueue(LinkedList<MessageSpecification> queue) {
        this.queue = queue;
    }

    public void addToQueue(MessageSpecification msgSpec) {
        queue.add(msgSpec);
    }

    public boolean actorQueueIsEmpty() {
        return queue.isEmpty();
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

    public void execute(State state, RILModel transformedRILModel,
                        AbstractPolicy policy) {

        do {
            if (variableIsDefined(InstructionUtilities.PC_STRING)) {
                ProgramCounter pc = getPC();

                String methodName = pc.getMethodName();
                Type currentType = null;
                try {
                    currentType = typeSystem.getType(pc.getMethodName().split("\\.")[0]);
                } catch (CodeCompilationException e) {
                    e.printStackTrace();
                }

                while (transformedRILModel.getInstructionList(methodName) == null) {
                    try {
                        ReactiveClassDeclaration rcd = (ReactiveClassDeclaration)typeSystem.getMetaData(currentType);
                        if (rcd.getExtends() == null)
                            break;
                        currentType = rcd.getExtends();
                        methodName = currentType.getTypeName() + "." + pc.getMethodName().split("\\.")[1];
                    } catch (CodeCompilationException e) {
                        e.printStackTrace();
                    }
                }

                int lineNumber = pc.getLineNumber();

                InstructionBean instruction = transformedRILModel.getInstructionList(methodName).get(lineNumber);
                InstructionInterpreter interpreter = StatementInterpreterContainer.getInstance()
                        .retrieveInterpreter(instruction);
                policy.executedInstruction(instruction);
                interpreter.interpret(instruction, this, state);
                int a = 2;

            } else if (!queue.isEmpty()) {
                MessageSpecification executableMessage = queue.poll();
                policy.pick(executableMessage);


                String msgName = executableMessage.getMessageName();
                Type currentType = null;
                try {
                    currentType = typeSystem.getType(executableMessage.getMessageName().split("\\.")[0]);
                } catch (CodeCompilationException e) {
                    e.printStackTrace();
                }

                while (!transformedRILModel.getMethodNames().contains(msgName)) {
                    try {
                        ReactiveClassDeclaration rcd = (ReactiveClassDeclaration)typeSystem.getMetaData(currentType);
                        if (rcd.getExtends() == null)
                            break;
                        currentType = rcd.getExtends();
                        msgName = currentType.getTypeName() + "." + executableMessage.getMessageName().split("\\.")[1];
                    } catch (CodeCompilationException e) {
                        e.printStackTrace();
                    }
                }

                String relatedRebecType = msgName.split("\\.")[0];
                actorScopeStack.pushInScopeStack(getTypeName(), relatedRebecType);
                addVariableToRecentScope("sender", executableMessage.getSenderActorState());
                initializePC(msgName, 0);
                int a = 2;

            } else
                throw new RebecaRuntimeInterpreterException("this case should not happen!");
        } while (!policy.isBreakable());
    }

    public ActorScopeStack getActorScopeStack() {
        return actorScopeStack;
    }

    public void setTypeSystem(CoreRebecaTypeSystem typeSystem) {
        this.typeSystem = typeSystem;
    }
}
