package org.rebecalang.transparentactormodelchecker.transitionsystem;

@SuppressWarnings("serial")
public class RuleIsDisabledException extends Exception {
	
	public RuleIsDisabledException() {
		
	}
	
	public RuleIsDisabledException(String message) {
		super(message);
	}

}
