package org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.RebecInstantiationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaRebecInstantiationSOSRule extends RebecInstantiationSOSRule {

	@Override
	protected AbstractActorState createTempActorState(Type type) {
		return CoreRebecaActorState.createTempCoreRebecaActorState(type);
	}

}
