package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;

@SuppressWarnings("serial")
public class CoreRebecaMessageState extends AbstractMessageState implements Serializable, Cloneable {
	
	public CoreRebecaMessageState() {
		super();
	}
	public CoreRebecaMessageState(String name, HashMap<String, Object> parameters) {
		super(name, parameters);
	}
	
	public AbstractMessageState clone() {
		CoreRebecaMessageState clonedMessageState = new CoreRebecaMessageState();
		clone(clonedMessageState);
		return clonedMessageState;
	}
}
