package org.rebecalang.modelchecker.corerebeca.builtinmethod;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ExternalMethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class IndependentMethodExecutor implements ExternalMethodExecutor {

	public static final String KEY = "Independent";

	public Object execute(ExternalMethodCallInstructionBean methodCallInstructionBean, BaseActorState baseActorState,
			State globalState) {
		if(methodCallInstructionBean.getMethodName().equals("pow$double$double")) {
			Double firstValue = null, secondValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get(0), baseActorState);
			secondValue = callGetDouble(methodCallInstructionBean.getParameters().get(1), baseActorState);
			return Math.pow(firstValue, secondValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("sqrt$double")) {
			Double firstValue = null;
			firstValue = callGetDouble(methodCallInstructionBean.getParameters().get(0), baseActorState);
			return Math.sqrt(firstValue);
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean")) {
			Boolean firstValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get(0), baseActorState);
			assertTrue(firstValue);
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("assertion$boolean$String")) {
			Boolean firstValue = null;
			String secondValue = null;
			firstValue = callGetBoolean(methodCallInstructionBean.getParameters().get(0), baseActorState);
			secondValue = callGetString(methodCallInstructionBean.getParameters().get(1), baseActorState);
			assertTrue(secondValue, firstValue);
			return null;
		}
		if(methodCallInstructionBean.getMethodName().equals("getAllActors")) {
			return globalState.getAllActorStates();
		}
		
		throw new RuntimeException("unknown built-in method call");
	}

	private Double callGetDouble(Object object, BaseActorState baseActorState) {
		return (Double) callAndGetResult(object, "doubleValue", baseActorState);
	}
	private Boolean callGetBoolean(Object object, BaseActorState baseActorState) {
		return (Boolean) callAndGetResult(object, "booleanValue", baseActorState);
	}
	private String callGetString(Object object, BaseActorState baseActorState) {
		return (String) callAndGetResult(object, "stringValue", baseActorState);
	}
	
	private Object callAndGetResult(Object object, String methodName, BaseActorState baseActorState) {
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
