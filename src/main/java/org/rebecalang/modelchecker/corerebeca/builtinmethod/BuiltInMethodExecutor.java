package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.StatementInterpreterContainer;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class BuiltInMethodExecutor implements ExternalMethodExecutor {

	public static final String KEY = "BuiltIn";

	public Object execute(StatementInterpreterContainer statementInterpreterContainer, ExternalMethodCallInstructionBean methodCallInstructionBean, BaseActorState<?> baseActorState,
						  State<? extends BaseActorState<?>> globalState) {
		if(methodCallInstructionBean.getMethodName().equals("pow$double$double")) {
			Double firstValue = null, secondValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get("arg0"), baseActorState);
			secondValue = callGetDouble(methodCallInstructionBean.getParameters().get("arg1"), baseActorState);
			return Math.pow(firstValue, secondValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("sqrt$double")) {
			Double firstValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get("arg0"), baseActorState);
			return Math.sqrt(firstValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("delay$int")) {
			Integer delay = null;
			Object delayVal = methodCallInstructionBean.getParameters().get("arg0");
			if (delayVal instanceof NonDetValue) {
				delay = (Integer) InstructionUtilities.getValue(statementInterpreterContainer, delayVal, baseActorState);
			} else if (delayVal instanceof Variable) {
				Object delayValue = baseActorState.retrieveVariableValue((Variable) delayVal);

				if (delayValue instanceof Integer) {
					delay = (int) delayValue;
				} else if (delayValue instanceof String) {
					delay = Integer.parseInt((String) delayValue);
				}
			} else {
				delay = callGetInteger(delayVal, baseActorState);
			}
			TimedActorState timedBaseActorState = ((TimedActorState) baseActorState);
			if (timedBaseActorState.isFTTS()) {
				((TimedActorState)baseActorState).increaseCurrentTime(delay);
			} else {
				((TimedActorState)baseActorState).increaseResumingTime(delay);
			}
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean")) {
			Boolean firstValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get("arg0"), baseActorState);
			assert(firstValue);
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean$String")) {
			Boolean firstValue = null;
			String secondValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get("arg0"), baseActorState);
			secondValue = callGetString(methodCallInstructionBean.getParameters().get("arg1"), baseActorState);
			assert(firstValue) : secondValue;
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("getAllActors")) {
			return globalState.getAllActorStates();
		}
		
		throw new RuntimeException("unknown built-in method call");
	}

	private Integer callGetInteger(Object object, BaseActorState<?> baseActorState) {
		return (Integer) callAndGetResult(object, "intValue", baseActorState);
	}
	private Double callGetDouble(Object object, BaseActorState<?> baseActorState) {
		return (Double) callAndGetResult(object, "doubleValue", baseActorState);
	}
	private Boolean callGetBoolean(Object object, BaseActorState<?> baseActorState) {
		return (Boolean) callAndGetResult(object, "booleanValue", baseActorState);
	}
	private String callGetString(Object object, BaseActorState<?> baseActorState) {
		return (String) callAndGetResult(object, "stringValue", baseActorState);
	}
	
	private Object callAndGetResult(Object object, String methodName, BaseActorState<?> baseActorState) {
		Method method;
		try {
			if(object instanceof Variable) {
				object = baseActorState.retrieveVariableValue((Variable) object);
			}
			method = object.getClass().getMethod(methodName);
			return method.invoke(object);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
