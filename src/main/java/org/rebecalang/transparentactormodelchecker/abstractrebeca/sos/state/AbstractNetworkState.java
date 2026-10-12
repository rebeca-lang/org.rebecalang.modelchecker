package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractNetworkState implements Serializable, Cloneable {
	
	public abstract boolean hasMessage();
	
	public abstract void addMessage(AbstractMessageState message);

}
