package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class IndependentMethodExecutor implements ExternalMethodExecutor {

	public static final String KEY = "Independent";

	public Object execute(ExternalMethodCallInstructionBean methodCallInstructionBean, ActorState actorState,
			State globalState) {
		if(methodCallInstructionBean.getMethodName().equals("pow$double$double")) {
			Double firstValue = null, secondValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get(0), actorState);
			secondValue = callGetDouble(methodCallInstructionBean.getParameters().get(1), actorState);
			return Math.pow(firstValue, secondValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("sqrt$double")) {
			Double firstValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get(0), actorState);
			return Math.sqrt(firstValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean")) {
			Boolean firstValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get(0), actorState);
			assertTrue(firstValue);
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean$String")) {
			Boolean firstValue = null;
			String secondValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get(0), actorState);
			secondValue = callGetString(methodCallInstructionBean.getParameters().get(1), actorState);
			assertTrue(secondValue, firstValue);
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("getAllActors")) {
			return globalState.getAllActorStates();
		}
		
		throw new RuntimeException("unknown built-in method call");
	}

	private Double callGetDouble(Object object, ActorState actorState) {
		return (Double) callAndGetResult(object, "doubleValue", actorState);
	}
	private Boolean callGetBoolean(Object object, ActorState actorState) {
		return (Boolean) callAndGetResult(object, "booleanValue", actorState);
	}
	private String callGetString(Object object, ActorState actorState) {
		return (String) callAndGetResult(object, "stringValue", actorState);
	}
	
	private Object callAndGetResult(Object object, String methodName, ActorState actorState) {
		Method method;
		try {
			if(object instanceof Variable) {
				object = actorState.retrieveVariableValue((Variable) object);
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
