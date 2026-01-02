package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

public abstract class AbstractNetworkState {
	
	public abstract boolean hasMessage();
	
	public abstract void addMessage(AbstractMessageState message);

}
