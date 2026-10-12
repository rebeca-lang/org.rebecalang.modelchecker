package org.rebecalang.transparentactormodelchecker.abstractrebeca.util;

import java.lang.reflect.Array;

import org.rebecalang.transparentactormodelchecker.ModelCheckingRuntimeException;

public class HashcodeUtil {
	public static int hashCode(Object value) {
        int hashCode = 1;
		if(value == null)
			return 0;
		if(value.getClass().isArray()) {
			int length = Array.getLength(value);
	        for (int cnt = 0; cnt < length; cnt++) {
//	            Object e = Array.get(value, cnt);
	            hashCode = 31 * hashCode + hashCode(Array.get(value, cnt));
	        }
		}
//		if(value instanceof AbstractActorState) {
//			AbstractActorState actorState = (AbstractActorState) value;
//			AbstractActorState retrievedActorState = clonedActors.get(actorState.getId());
//			if(retrievedActorState == null) {
//				retrievedActorState = actorState.memoizedClone();
//			}
//			return retrievedActorState;
//			return actorState.memoizedClone();
//		}
//		if(value instanceof ActorsContainer)
//			return ((ActorsContainer) value).clone();
//
//		if(value instanceof ActorStateRepresentor)
//			return ((ActorStateRepresentor)value).clone();
//		
//		if(value instanceof Object[]) {
//			Object[] valueData = (Object[]) value;
//			Object[] cloned = new Object[valueData.length];
//			for(int cnt = 0; cnt < valueData.length; cnt++)
//				cloned[cnt] = cloneObject(valueData[cnt]);
//			return cloned;
//		}
		throw new ModelCheckingRuntimeException(null, "Unknown cloning strategy for " + value.getClass());
	}
}
