package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;

public class NewInstanceAction extends Action {

	private AbstractActorState newInstanceReference;
	private Type type;

	
	public NewInstanceAction(AbstractActorState newInstanceReference, Type type) {
		super();
		this.newInstanceReference = newInstanceReference;
		this.type = type;
	}

	public AbstractActorState getNewInstanceReference() {
		return newInstanceReference;
	}
	
	@Override
	public String getActionLabel() {
		return "new " + type.getTypeName();
	}

}
