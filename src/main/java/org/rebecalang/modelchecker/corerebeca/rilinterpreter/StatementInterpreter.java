package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;

public interface StatementInterpreter {
	public Object interpret(Statement statement , BaseActorState baseActorState);
}
