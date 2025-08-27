package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.corerebeca.utils.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaMessageState implements Serializable, Cloneable {
	
	private CoreRebecaActorState sender;
	private CoreRebecaActorState receiver;
	
	private String name;
	private HashMap<String, Object> parameters;
	
	
	public CoreRebecaMessageState() {
		parameters = new HashMap<String, Object>();
	}
	public CoreRebecaMessageState(String name, HashMap<String, Object> parameters) {
		this.name = name;
		this.parameters = parameters;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
	public void addParameter(String name, Object value) {
		parameters.put(name, value);
	}
	public CoreRebecaActorState getSender() {
		return sender;
	}
	public void setSender(CoreRebecaActorState sender) {
		this.sender = sender;
	}
	public CoreRebecaActorState getReceiver() {
		return receiver;
	}
	public void setReceiver(CoreRebecaActorState receiver) {
		this.receiver = receiver;
	}
		
	public String toString() {
		return (sender == null ? "main" : sender.getId()) + "->" + receiver.getId() + "." + name + "()"; 
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
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
		CoreRebecaMessageState other = (CoreRebecaMessageState) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		return true;
	}
	
	public CoreRebecaMessageState clone() {
		CoreRebecaMessageState clonedMessageState = new CoreRebecaMessageState();
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
