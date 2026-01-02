package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public abstract class AbstractMessageState implements Serializable, Cloneable {
	
	public transient final static Variable SENDER = new Variable("sender"); 
	public transient final static Variable SELF = new Variable("self"); 
	
	protected int senderId;
	protected int receiverId;
	
	protected String name;
	protected HashMap<String, Object> parameters;
	
	public AbstractMessageState() {
		parameters = new HashMap<String, Object>();
	}
	public AbstractMessageState(String name, HashMap<String, Object> parameters) {
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

	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public int getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	
	public String toString() {
		return (senderId == AbstractActorState.NO_ACTOR_ID ? "main" : senderId) + "->" + receiverId + "." + name + "()"; 
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + receiverId;
		result = prime * result + senderId;
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
		AbstractMessageState other = (AbstractMessageState) obj;
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
		if (receiverId != other.receiverId)
			return false;
		if (senderId != other.senderId)
			return false;
		return true;
	}
	
	public abstract AbstractMessageState clone();
	
	protected <T extends AbstractMessageState> void clone(T clonedMessageState) {
		clonedMessageState.senderId = this.senderId;
		clonedMessageState.receiverId = this.receiverId;
		clonedMessageState.name = this.name;
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		for(Entry<String, Object> entry : this.parameters.entrySet()) {
			parameters.put(entry.getKey(), CloningRepository.cloneObject(entry.getValue()));
		}
		clonedMessageState.parameters = parameters;
	}
}
