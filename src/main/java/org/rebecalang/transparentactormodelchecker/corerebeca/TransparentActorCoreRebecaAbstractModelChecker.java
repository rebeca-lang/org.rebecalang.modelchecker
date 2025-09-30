package org.rebecalang.transparentactormodelchecker.corerebeca;

import org.rebecalang.transparentactormodelchecker.corerebeca.sos.CoreRebecaSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("CORE_REBECA")
public abstract class TransparentActorCoreRebecaAbstractModelChecker extends TransparentActorAbstractModelChecker<CoreRebecaSystemState> {

	
	@Autowired
	protected CoreRebecaSOSRule sosRule;

	public TransparentActorCoreRebecaAbstractModelChecker() {
		super();
	}
	@Override
	protected CoreRebecaSystemState createSystemState() {
		return new CoreRebecaSystemState();
	}

}