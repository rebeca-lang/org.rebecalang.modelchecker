package org.rebecalang.modelchecker;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class}) 
@SpringJUnitConfig
public class CoreRebecaModelCheckerTest {
	
	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/modelchecker/"; 

	@Autowired
	public CoreRebecaModelChecker coreRebecaModelChecker;
	
	@Autowired
	public ExceptionContainer exceptionContainer;

	@Test
	public void GIVEN_CorrectCoreRebecaModelWithInitialMethod_WHEN_CoreIs2_0_THEN_1Error() throws ModelCheckingException {
		File model = new File(MODEL_FILES_BASE + "DiningPhilosophers.rebeca");
		Set<CompilerExtension> extension = new HashSet<CompilerExtension>();
		coreRebecaModelChecker.configPolicy(CoreRebecaModelChecker.COARSE_GRAINED_POLICY);
		coreRebecaModelChecker.modelCheck(model, extension, CoreVersion.CORE_2_1);
		
		System.out.println(exceptionContainer);
		
		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());

		Assertions.assertEquals(coreRebecaModelChecker.getStatespace().size(), 105);
		
		RebecaModelChecker.printStateSpace(coreRebecaModelChecker.getStatespace().getInitialState());
	}
}
