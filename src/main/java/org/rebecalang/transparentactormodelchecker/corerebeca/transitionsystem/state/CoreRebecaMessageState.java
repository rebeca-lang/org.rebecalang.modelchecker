package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class CoreRebecaMessageState implements Serializable {
	
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
}
