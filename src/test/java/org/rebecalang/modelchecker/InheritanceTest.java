package org.rebecalang.modelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modelchecker.corerebeca.CoreRebecaModelChecker;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class InheritanceTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/";

    @Autowired
    public CoreRebecaModelChecker coreRebecaModelChecker;

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Test
    public void useParentsStateVarsInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_state_vars_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);

        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsKnownActorInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_known_actors_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);

        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsMsgSrvInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_msgsrv_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);

        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }
}
