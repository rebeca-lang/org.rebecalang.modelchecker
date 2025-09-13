package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaMessageState extends AbstractMessageState implements Serializable, Cloneable {
	
	public CoreRebecaMessageState() {
		super();
	}
	public CoreRebecaMessageState(String name, HashMap<String, Object> parameters) {
		super(name, parameters);
	}
	
//	public String toString() {
//		return (sender == null ? "main" : sender.getId()) + "->" + receiver.getId() + "." + name + "()"; 
//	}
	
	public CoreRebecaMessageState clone() {
		CoreRebecaMessageState clonedMessageState = new CoreRebecaMessageState();
		if(this.sender != null)
			clonedMessageState.sender = this.sender.clone();
		clonedMessageState.receiver = this.receiver.clone();
		clonedMessageState.name = this.name;
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		for(Entry<String, Object> entry : this.parameters.entrySet()) {
			parameters.put(entry.getKey(), CloningRepository.cloneObject(entry.getValue()));
		}
		clonedMessageState.parameters = parameters;
		
		return clonedMessageState;
	}
}
