package org.rebecalang.transparentactormodelchecker;

@SuppressWarnings("serial")
public class ModelCheckingRuntimeException extends RuntimeException {

	private String additionalMessage;
	private Exception e;

	public ModelCheckingRuntimeException(RuntimeException re, String additionalMessage) {
		this.e = re;
		this.additionalMessage = additionalMessage;
	}

	public String getAdditional() {
		return additionalMessage;
	}

	@Override
	public void printStackTrace() {
		System.err.println(additionalMessage);
		e.printStackTrace();
	}

	
//	public ModelCheckingRuntimeException(String message) {
//		super(message);
//	}
//	
//	public ModelCheckingRuntimeException(String message, Throwable cause) {
//		super(message, cause);
//	}

}
