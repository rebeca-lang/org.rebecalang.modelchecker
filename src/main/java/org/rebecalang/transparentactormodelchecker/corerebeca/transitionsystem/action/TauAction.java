package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

public class TauAction extends Action {

	public final static TauAction TAU = new TauAction("Tau");
	
	private String label;
	
	public TauAction(String label) {
		this.label = label;
	}
	
	@Override
	public String getActionLabel() {
		return "tau[" + this.label + "]";
	}
	
	public String toString() {
		return label.toString();
	}

}
