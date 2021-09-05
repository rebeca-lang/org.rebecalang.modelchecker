package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.util.Hashtable;

public class StatementInterpreterContainer {

    Hashtable<Class<? extends InstructionBean>, InstructionInterpreter> interpreters;
    private static StatementInterpreterContainer instance = new StatementInterpreterContainer();

    private StatementInterpreterContainer() {
        interpreters = new Hashtable<Class<? extends InstructionBean>, InstructionInterpreter>();
    }

    public static StatementInterpreterContainer getInstance() {
        return instance;
    }

    public void registerInterpreter(Class<? extends InstructionBean> clazz,
                                    InstructionInterpreter instructionInterpreter) {
        interpreters.put(clazz, instructionInterpreter);
    }

    public InstructionInterpreter retrieveInterpreter(InstructionBean ib) {
        return (interpreters.get(ib.getClass()));
    }

    private boolean hasNonterminism;

    public void reportNondeterminism() {
        hasNonterminism = true;
    }

    public void clearNondeterminism() {
        hasNonterminism = false;
    }

    public boolean hasNondeterminism() {
        return hasNonterminism;
    }

}
