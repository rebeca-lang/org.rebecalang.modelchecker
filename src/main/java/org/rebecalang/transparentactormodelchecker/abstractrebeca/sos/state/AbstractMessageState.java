package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public abstract class AbstractMessageState implements Serializable, Cloneable {
	
	protected AbstractActorState sender;
	protected AbstractActorState receiver;
	
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
	public AbstractActorState getSender() {
		return sender;
	}
	public void setSender(AbstractActorState sender) {
		this.sender = sender;
	}
	public AbstractActorState getReceiver() {
		return receiver;
	}
	public void setReceiver(AbstractActorState receiver) {
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
	
	public abstract AbstractMessageState clone();
}
