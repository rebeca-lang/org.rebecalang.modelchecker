package org.rebecalang.transparentactormodelchecker;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.ExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.SendMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelrule.CoreRebecaTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule.TimedRebecaTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule.statementlevelrule.TimedRebecaSendMessageSOSRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan(basePackages = { "org.rebecalang.transparentactormodelchecker" })
public class TransparentActorModelCheckerConfig {

	@Autowired
	ApplicationContext appContext;

	
	@Bean
	@Qualifier("CORE_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CompositionLevelExecuteStatementRule getCoreRebecaCompositionExecuteStatementSOSRule() {
		ExecuteStatementRule executeStatement = appContext.getBean(ExecuteStatementRule.class);
		executeStatement.setSendMessageRule(new SendMessageRule());
		CompositionLevelExecuteStatementRule compositionExecuteStatement =
				new CompositionLevelExecuteStatementRule();
		compositionExecuteStatement.setExecuteStatementSOSRule(executeStatement);
		return compositionExecuteStatement;
	}

	@Bean
	@Qualifier("TIMED_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CompositionLevelExecuteStatementRule getTimedRebecaCompositionExecuteStatementSOSRule() {
		ExecuteStatementRule executeStatement = appContext.getBean(ExecuteStatementRule.class);
		executeStatement.setSendMessageRule(new TimedRebecaSendMessageSOSRule());
		CompositionLevelExecuteStatementRule compositionExecuteStatement =
				new CompositionLevelExecuteStatementRule();
		compositionExecuteStatement.setExecuteStatementSOSRule(executeStatement);
		return compositionExecuteStatement;
	}
	
	@Bean
	@Qualifier("CORE_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CompositionLevelTakeMessageRule getCoreRebecaCompositionTakeMessageRule() {
		CoreRebecaTakeMessageRule takeMessage = appContext.getBean(CoreRebecaTakeMessageRule.class);
		CompositionLevelTakeMessageRule compositionTakeMessage =
				new CompositionLevelTakeMessageRule();
		compositionTakeMessage.setActorLevelTakeMessageRule(takeMessage);
		return compositionTakeMessage;
	}

	@Bean
	@Qualifier("TIMED_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CompositionLevelTakeMessageRule getTimedRebecaCompositionTakeMessageRule() {
		TimedRebecaTakeMessageRule executeStatement = appContext.getBean(TimedRebecaTakeMessageRule.class);
		CompositionLevelTakeMessageRule compositionTakeMessage =
				new CompositionLevelTakeMessageRule();
		compositionTakeMessage.setActorLevelTakeMessageRule(executeStatement);
		return compositionTakeMessage;
	}

}
