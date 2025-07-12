package org.rebecalang.transparentactormodelchecker.corerebeca.utils;

import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.util.SerializationUtils;

public class RebecaStateSerializationUtils {
	
	public static CoreRebecaNetworkState clone(CoreRebecaNetworkState object) {
		CoreRebecaNetworkState clone = SerializationUtils.clone(object);
		return clone;
	}
	
	public static CoreRebecaActorState clone(CoreRebecaActorState object) {
		CoreRebecaActorState clone = SerializationUtils.clone(object);
		clone.setRILModel(object.getRILModel());
		return clone;
	}
	
	public static CoreRebecaSystemState clone(CoreRebecaSystemState object) {
		CoreRebecaSystemState clone = SerializationUtils.clone(object);
		
		RILModel rilModel = object.getActorsState().values().iterator().next().getRILModel();
		for(CoreRebecaActorState state : clone.getActorsStatesValues())
			state.setRILModel(rilModel);

		return clone;
	}
}
