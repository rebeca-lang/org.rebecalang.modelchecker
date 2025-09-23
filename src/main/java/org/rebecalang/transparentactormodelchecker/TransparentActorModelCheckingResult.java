package org.rebecalang.transparentactormodelchecker;

public class TransparentActorModelCheckingResult {

	public static final String DEADLOCK = "Deadlock";
	public static final String SATISFIED = "Satisfied";
	
	private long time;
	private String message;
	private TransparentActorTransitionSystem<?> transitionSystem;
	private int collisions;

	public TransparentActorModelCheckingResult(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public TransparentActorTransitionSystem<?> getTransitionSystem() {
		return transitionSystem;
	}
	
	public void setTransitionSystem(TransparentActorTransitionSystem<?> transitionSystem) {
		this.transitionSystem = transitionSystem;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}

	public void setCollisions(int collisions) {
		this.collisions = collisions;
	}
	public int getCollisions() {
		return collisions;
	}
}
