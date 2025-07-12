package org.rebecalang.modelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.setting.TimedRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelCheckerFactory;
import org.rebecalang.modelchecker.timedrebeca.utils.SchedulingPolicy;
import org.rebecalang.modelchecker.timedrebeca.utils.TransitionSystem;
import org.rebecalang.modelchecker.utils.StateSpaceUtil;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class TimedRebecaTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/timedrebeca/";

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Autowired
    protected TimedRebecaModelCheckerFactory timedRebecaModelCheckerFactory;

    @ParameterizedTest
    @Disabled
    @MethodSource("modelToStateSpace")
    public void GIVEN_TimedRebecaModel_WHEN_No_Error(String filename, int stateSpaceSize, TransitionSystem transitionSystem) throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + filename);
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = new TimedRebecaModelCheckerSetting(extension, CoreVersion.CORE_2_3, transitionSystem, SchedulingPolicy.SCHEDULING_ALGORITHM_FIFO);

        TimedRebecaModelChecker timedRebecaModelChecker = timedRebecaModelCheckerFactory.getModelChecker(timedRebecaModelCheckerSetting.getTransitionSystem());
        timedRebecaModelChecker.modelCheck(model, timedRebecaModelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

        StateSpace<State<? extends BaseActorState<?>>> stateSpace = timedRebecaModelChecker.getStateSpace();
        State<TimedActorState> initialState = (State<TimedActorState>) stateSpace.getInitialState();
        StateSpaceUtil.printTimedStateSpace(initialState,
                new PrintStream(new FileOutputStream(new File(transitionSystem + filename))));

        Assertions.assertEquals(stateSpaceSize, stateSpace.size());
        printTime(timedRebecaModelChecker.time);
        printTransitionsCount(stateSpace.size());
        printTransitionsCount(timedRebecaModelChecker.numberOfTransitions);
    }

    @Test
    @Disabled
    public void testDynamicPolymorphism() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "dynamic_polymorphism_in_time.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        TimedRebecaModelCheckerSetting timedRebecaModelCheckerSetting = new TimedRebecaModelCheckerSetting(extension, CoreVersion.CORE_2_3, TransitionSystem.TRANSITION_SYSTEM_FGTS);

        TimedRebecaModelChecker timedRebecaModelChecker = timedRebecaModelCheckerFactory.getModelChecker(timedRebecaModelCheckerSetting.getTransitionSystem());
        timedRebecaModelChecker.modelCheck(model, timedRebecaModelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    protected static Stream<Arguments> modelToStateSpace() {
        return Stream.of(
                Arguments.arguments("ping_pong.rebeca", 3, TransitionSystem.TRANSITION_SYSTEM_FTTS),
                Arguments.arguments("ping_pong.rebeca", 15, TransitionSystem.TRANSITION_SYSTEM_FGTS)
        );
    }

    private void printTime(long time) {
        System.out.println(time / 1_000_000_000.0);
    }

    private void printTransitionsCount(int numberOfTransitions) {
        System.out.println(numberOfTransitions);
    }

    private void printExceptions() {
        Collection<Set<Exception>> exceptions = exceptionContainer.getExceptions().values();
        for (Set<Exception> exceptionCollection : exceptions) {
            for (Exception exception : exceptionCollection) {
                System.out.println(exception);
            }
        }
    }
}
