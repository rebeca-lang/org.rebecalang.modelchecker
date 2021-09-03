package org.rebecalang.modelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.timedrebeca.TimedRebecaModelChecker;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class TimedRebecaTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/timedrebeca/";

    @Autowired
    public TimedRebecaModelChecker timedRebecaModelChecker;

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Test
    public void pingPongTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "ping_pong.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        extension.add(CompilerExtension.TIMED_REBECA);
        timedRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        timedRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
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
