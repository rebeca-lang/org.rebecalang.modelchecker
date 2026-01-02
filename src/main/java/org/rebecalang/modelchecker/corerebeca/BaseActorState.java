package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.rebecalang.compiler.modelcompiler.abstractrebeca.AbstractTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AbstractCallingInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MsgsrvCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

@SuppressWarnings("serial")
public abstract class BaseActorState<T extends MessageSpecification> implements Serializable {
    protected ActorScopeStack actorScopeStack;
    protected String name;
    protected String typeName;
    transient protected AbstractTypeSystem typeSystem;

    public BaseActorState() {
        initializeQueue();
    }

    public abstract void initializeQueue();

    public abstract void addToQueue(T msgSpec);

    public abstract boolean actorQueueIsEmpty();

    public abstract T getMessage(boolean isPeek);

    protected abstract void exportQueueContent(PrintStream output);

    public void initializePC(String methodName, int lineNum) {
        addVariableToRecentScope(InstructionUtilities.PC_STRING, new ProgramCounter(methodName, lineNum));
//        addVariableToRecentScope(AbstractExpressionTranslator.RETURN_VALUE, 0);
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

    public void setTypeSystem(AbstractTypeSystem typeSystem) {
        this.typeSystem = typeSystem;
    }
    
	public AbstractTypeSystem getTypeSystem() {
		return typeSystem;
	}

	public void export(PrintStream output) {
		output.println("<rebec name=\""+ name + "\">");
		output.println("<statevariables>");
		actorScopeStack.export(output);
		exportQueueContent(output);
		output.println("</statevariables>");
		output.println("</rebec>");
	}

	public String toString() {
		String retValue = "name:" + typeName + "." + name;
		retValue += "\n vars: [";
		for(ActivationRecord ar : actorScopeStack.getActivationRecords()) {
			retValue += ar;
		}
		return retValue + "]";
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseActorState<T> other = (BaseActorState<T>) obj;
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
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

	public void pushInScopeStackForInsideMethod() {
		actorScopeStack.pushInScopeStackForInsideMethod();
	}

	public void pushInScopeStackForMethodCallInitialization(String relatedRebecType) {
		actorScopeStack.pushInScopeStackForMethodCallInitialization(relatedRebecType);
	}
	
	public void pushInScopeStackForInheritanceStack(String typeName) {
		actorScopeStack.pushInScopeStackForInheritanceStack(typeName);
	}

    public void addParamersValuesToScope(T executableMessage) {
        for (Map.Entry<String, Object> entry : executableMessage.getParameters().entrySet()) {
            addVariableToRecentScope(entry.getKey(), entry.getValue());
        }
    }

    protected InstructionBean getInheritanceInstruction(RILModel transformedRILModel, InstructionBean instruction){
        if(instruction instanceof MsgsrvCallInstructionBean) {
            MsgsrvCallInstructionBean mcib = (MsgsrvCallInstructionBean) instruction;
            String newMethodName = resolveDynamicBindingOfMethodCall(transformedRILModel, mcib);
            if(!mcib.getMethodName().equals(newMethodName)) {
                instruction = new MsgsrvCallInstructionBean(
                        mcib.getBase(), newMethodName);
                ((MsgsrvCallInstructionBean)instruction).setParameters(
                        mcib.getParameters());
            }
        } else if(instruction instanceof MethodCallInstructionBean) {
            MethodCallInstructionBean mcib = (MethodCallInstructionBean) instruction;
            String newMethodName = resolveDynamicBindingOfMethodCall(transformedRILModel, mcib);
            if(!mcib.getMethodName().equals(newMethodName)) {
                instruction = new MethodCallInstructionBean(
                        mcib.getBase(), newMethodName, mcib.getParameters());
                
                ((MethodCallInstructionBean)instruction).setFunctionCallResult(mcib.getFunctionCallResult());
            }
        }

        return instruction;
    }

    @SuppressWarnings("unchecked")
	protected String resolveDynamicBindingOfMethodCall(
            RILModel transformedRILModel,
            AbstractCallingInstructionBean instruction) {
        BaseActorState<T> baseActorState = (BaseActorState<T>) actorScopeStack.retrieveVariableValue(instruction.getBase().getVarName());
        if (baseActorState == null) return null;
        String typeName = callActorTypeName(baseActorState);

        String methodName =
                rewriteMethodNameType(typeName,
                        instruction.getMethodName());
        try {
            Type currentType = typeSystem.getType(typeName);
            while (transformedRILModel.getInstructionList(methodName) == null) {
                ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
                currentType = rcd.getExtends();
                methodName = rewriteMethodNameType(currentType.getTypeName(), methodName);
            }
            return methodName;
        } catch (CodeCompilationException e) {
            throw new RebecaRuntimeInterpreterException(e.getMessage());
        }
    }

    protected void startExecutionOfNewMessageServer(RILModel transformedRILModel, AbstractPolicy policy, T executableMessage) {
        if(variableIsDefined(InstructionUtilities.PC_STRING))
            return;

        if (executableMessage == null) {
            executableMessage = getMessage(false);
        }

        policy.pick(executableMessage);

        String msgName = getMessageName(executableMessage.getMessageName(),
                transformedRILModel);

        String relatedRebecType = msgName.split("\\.")[0];
        actorScopeStack.pushInScopeStackForMethodCallInitialization(relatedRebecType);
        addVariableToRecentScope("sender", executableMessage.getSenderActorState());
        initializePC(msgName, 0);

        addParamersValuesToScope((T) executableMessage);
    }

    protected void resumeExecution(State<? extends BaseActorState<T>> systemState,
                                   StatementInterpreterContainer statementInterpreterContainer,
                                   RILModel transformedRILModel, RebecaModel rebecaModel, AbstractPolicy policy) {
        do {
            ProgramCounter pc = getPC();
            int lineNumber = pc.getLineNumber();
            String methodName = getPC().getMethodName();

            ArrayList<InstructionBean> instructionsList =
                    transformedRILModel.getInstructionList(methodName);

            InstructionBean instruction = instructionsList.get(lineNumber);
            InstructionInterpreter interpreter = statementInterpreterContainer.retrieveInterpreter(instruction);
            policy.executedInstruction(instruction);

            interpreter.interpret(getInheritanceInstruction(transformedRILModel, instruction), this, systemState, rebecaModel);
        } while (!policy.isBreakable());
    }

    public void execute(State<? extends BaseActorState<T>> systemState,
                        StatementInterpreterContainer statementInterpreterContainer,
                        RILModel transformedRILModel,
                        RebecaModel rebecaModel,
                        AbstractPolicy policy,
                        T executableMessage) {

        startExecutionOfNewMessageServer(transformedRILModel, policy, executableMessage);
        resumeExecution(systemState, statementInterpreterContainer, transformedRILModel, rebecaModel, policy);
    }

    protected String callActorTypeName(BaseActorState<T> baseActorState) {
        return baseActorState.getTypeName();
    };

    protected String getMessageName(String messageName, RILModel transformedRILModel) {
        String msgName = messageName;
        Type currentType = null;
        try {
            currentType = typeSystem.getType(messageName.split("\\.")[0]);
        } catch (CodeCompilationException e) {
            e.printStackTrace();
        }

        while (!transformedRILModel.getMethodNames().contains(msgName)) {
            try {
                ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) typeSystem.getMetaData(currentType);
                if (rcd.getExtends() == null)
                    break;
                currentType = rcd.getExtends();
                msgName = currentType.getTypeName() + "." + messageName.split("\\.")[1];
            } catch (CodeCompilationException e) {
                e.printStackTrace();
            }
        }
        return msgName;
    }

    private String rewriteMethodNameType(String typeName, String methodName) {
        return typeName + "." + methodName.split("\\.")[1];
    }
}
