package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;

public class TransitionSystemFactory {
    public static TimedRebecaModelChecker getTransitionSystem(TransitionSystem transitionSystem) throws ModelCheckingException {
        return switch (transitionSystem) {
            case TRANSITION_SYSTEM_FTTS -> new FTTSModelChecker();
            case TRANSITION_SYSTEM_FGTS -> new FGTSModelChecker();
        };
    }
}
