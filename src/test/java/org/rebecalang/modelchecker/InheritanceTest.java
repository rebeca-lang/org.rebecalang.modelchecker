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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class})
@SpringJUnitConfig
public class InheritanceTest {
    public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/inheritance/";

    @Autowired
    public CoreRebecaModelChecker coreRebecaModelChecker;

    @Autowired
    public ExceptionContainer exceptionContainer;

    @Test
    public void useParentStateVarsInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parent_statevars_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsKnownActorInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_known_actors_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void useParentsMsgSrvInChildTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_msgsrv_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
        RebecaModelChecker.printStateSpace(coreRebecaModelChecker.getStateSpace().getInitialState());
    }

    @Test
    public void useParentsMethodsInChild() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "use_parents_methods_in_child.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void CheckKnownRebecsSequence() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "known_rebecs_sequence.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void CheckSelfAndNormalCalls() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "self_and_normal_calls.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void CheckHomonymous() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "homonymous_attributes.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    void CheckInterfaceAndAbstract() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "interface_abstract.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    public void checkInstanceof() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "instanceof_expression.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
        printExceptions();
        Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
    }

    @Test
    void dynamicPolymorphismTest() throws ModelCheckingException {
        File model = new File(MODEL_FILES_BASE + "dynamic_polymorphism.rebeca");
        Set<CompilerExtension> extension = new HashSet<>();
        coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
        coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_3);
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
