package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modelchecker.corerebeca.ActorState;

public interface StatementInterpreter {
	public Object interpret(Statement statement , ActorState actorState);
}
