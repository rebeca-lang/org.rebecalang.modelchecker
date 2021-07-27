package org.rebecalang.modelchecker.corerebeca.policy;

import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public abstract class AbstractPolicy {
	boolean breakable;
	
	public boolean isBreakable() {
		return breakable;
	}
	public abstract void executedInstruction(InstructionBean ib);
	public abstract void pick(MessageSpecification executableMessage);
}
