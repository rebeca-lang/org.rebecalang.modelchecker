package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class TakeMessageRule extends AbstractSOSRule<AbstractActorState> {

	protected void prepareScope(AbstractActorState state, AbstractMessageState message){
		state.newCallPushToScope(null);
		state.addVariableToScope(AbstractMessageState.SENDER.getVarName(), message.getSenderId());
		HashMap<String,Object> parameters = message.getParameters();
		for(Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			state.addVariableToScope(entry.getKey(), value);
		}
		Pair<String, Integer> pc = new Pair<String, Integer>(message.getName(), 0);
		state.addVariableToScope(AbstractActorState.PC, pc);
	}

}