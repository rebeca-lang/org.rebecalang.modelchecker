package org.rebecalang.modelchecker.corerebeca;

import java.util.Hashtable;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.StatementInterpreter;

public class StatementContainer {

	public StatementContainer() {

		statementsInterpretersContainer = new Hashtable<Class<? extends Statement>, StatementInterpreter>();
	}

	Hashtable<Class<? extends Statement>, StatementInterpreter> statementsInterpretersContainer;

	private SymbolTable symbolTable;

	static StatementContainer statementContainer = new StatementContainer();

	public static StatementContainer getInstance() {
		return statementContainer;
	}

	public void registerInterpreter(Class<? extends Statement> registerClass, StatementInterpreter interpreter) {
		statementsInterpretersContainer.put(registerClass, interpreter);

	}

	public Object interpret(Statement statement, ActorState actorState) {

		return statementsInterpretersContainer.get(statement.getClass()).interpret(statement, actorState);

	}

	public void registerSymbolTable(SymbolTable symbolTable) {

		this.symbolTable = symbolTable;
	}

	public SymbolTable getSymbolTable() {

		return symbolTable;
	}

	public StatementInterpreter getInterpreter(Class<? extends Statement> statementClass) {
		return statementsInterpretersContainer.get(statementClass);
	}
}
