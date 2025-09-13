package org.rebecalang.transparentactormodelchecker.corerebeca.utils;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;

public class RebecaStateSerializationUtil {

 	public static CoreRebecaNetworkState clone(CoreRebecaNetworkState object) {
 		CloningRepository.resetRepository();
 		return object.clone();
 	}
 	
	public static CoreRebecaActorState clone(CoreRebecaActorState object) {
 		CloningRepository.resetRepository();
		return object.clone();
	}
 	
	public static CoreRebecaSystemState clone(CoreRebecaSystemState object) {
 		CloningRepository.resetRepository();
		return object.clone();
	}


//	public static CoreRebecaNetworkState clone(CoreRebecaNetworkState object) {
//		CoreRebecaNetworkState clone = SerializationUtils.clone(object);
//		return clone;
//	}
//	
//	public static CoreRebecaActorState clone(CoreRebecaActorState object) {
//		CoreRebecaActorState clone = SerializationUtils.clone(object);
//		clone.setRILModel(object.getRILModel());
//		return clone;
//	}
//	
//	public static CoreRebecaSystemState clone(CoreRebecaSystemState object) {
//		CoreRebecaSystemState clone = SerializationUtils.clone(object);
//		
//		RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
//		for(CoreRebecaActorState state : clone.getActorsStatesValues())
//			state.setRILModel(rilModel);
//
//		return clone;
//	}
}
