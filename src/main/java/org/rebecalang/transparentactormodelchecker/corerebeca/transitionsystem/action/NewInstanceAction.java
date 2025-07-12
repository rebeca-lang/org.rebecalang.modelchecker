package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;

public class NewInstanceAction extends Action {

	private CoreRebecaActorState newInstanceReference;
	private Type type;

	
	public NewInstanceAction(CoreRebecaActorState newInstanceReference, Type type) {
		super();
		this.newInstanceReference = newInstanceReference;
		this.type = type;
	}

	public CoreRebecaActorState getNewInstanceReference() {
		return newInstanceReference;
	}
	
	@Override
	public String getActionLable() {
		return "new " + type.getTypeName();
	}

}
