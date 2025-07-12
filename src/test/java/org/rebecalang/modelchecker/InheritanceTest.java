package org.rebecalang.modelchecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.*;
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modelchecker.setting.CoreRebecaModelCheckerSetting;
import org.rebecalang.modelchecker.setting.ModelCheckerSetting;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class InheritanceTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/inheritance/";

    @Autowired
    public CoreRebecaModelChecker coreRebecaModelChecker;

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Test
    @Disabled
    public void checkInstanceof() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "pingpongpung.rebeca");
        ModelCheckerSetting modelCheckerSetting = new CoreRebecaModelCheckerSetting(new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3, Policy.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, modelCheckerSetting);

        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
        StateSpace<State<? extends BaseActorState<?>>> statespace = coreRebecaModelChecker.getStateSpace();
		Assertions.assertEquals(14, statespace.size());
//		StateSpaceUtil.printStateSpace(statespace.getInitialState(),
//				new PrintStream(new FileOutputStream(new File("ppp.dot"))));
    }

    @Test
    @Disabled
    public void useParentMsgsrvs() throws ModelCheckingException, FileNotFoundException {
        File model = new File(MODEL_FILES_BASE + "useParentMsgsrvs.rebeca");
        ModelCheckerSetting modelCheckerSetting = new CoreRebecaModelCheckerSetting(new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3, Policy.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, modelCheckerSetting);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
        StateSpace<State<? extends BaseActorState<?>>> statespace = coreRebecaModelChecker.getStateSpace();
		Assertions.assertEquals(2, statespace.size());
//		StateSpaceUtil.printStateSpace(statespace.getInitialState(),
//				new PrintStream(new FileOutputStream(new File("ppp.dot"))));
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
