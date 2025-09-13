package org.rebecalang.transparentactormodelchecker.corerebeca;

public class DiningPhilosophersSourceCodes {
	private final static String RC_DEFINITIONS = """
			reactiveclass Philosopher(3) {
				knownrebecs {
					Chopstick chpL, chpR;
				}
				statevars {
					boolean eating;
				}
				Philosopher() { 
					eating = false;
					self.arrive();
				}
				msgsrv arrive() {
					chpL.request();
				}
				msgsrv permit() {
					if (sender == chpL) {
						chpR.request();
					} else {
						self.eat();
					}
				}
				msgsrv eat() {
					eating = true;
					self.leave();
				}
				msgsrv leave() {
					eating = false;
					chpL.release();
					chpR.release();
					self.arrive();
				}
			}
			
			reactiveclass Chopstick(3) {
				knownrebecs {
					Philosopher philL, philR;
				}
				statevars {
					boolean lAssign, rAssign, leftReq, rightReq;
				}
				Chopstick() {
					lAssign = false;
					rAssign = false;
					leftReq = false;
					rightReq = false;
				}
			
				msgsrv request() {
					if (sender == philL) {
						leftReq = true;
						if (!rAssign) {
							lAssign = true;
							philL.permit();
						}
					} else {
						rightReq = true;
						if (!lAssign) {
							rAssign = true;
							philR.permit();
						}
					}
				}
				msgsrv release() {
					if (sender == philL){
						leftReq = false;
						lAssign = false;
						if (rightReq) {
							rAssign=true;
							philR.permit();
						}
					}
					if (sender == philR){
						rAssign = false;
						rightReq = false;
						if (leftReq) {
							lAssign=true;
							philL.permit();
						}
					}
				}
			}
			""";
	
	public final static String TWO_PHILOSOPHERS = RC_DEFINITIONS + """
			main {
				Philosopher phil0(chp0, chp1):();
				Philosopher phil1(chp0, chp1):();
			
				Chopstick chp0(phil0, phil1):();
				Chopstick chp1(phil1, phil0):();
			}
			"""; 
	public final static String THREE_PHILOSOPHERS = RC_DEFINITIONS + """
			main {
				Philosopher phil0(chp0, chp2):();
				Philosopher phil1(chp0, chp1):();
				Philosopher phil2(chp1, chp2):();
			
				Chopstick chp0(phil0, phil1):();
				Chopstick chp1(phil1, phil2):();
				Chopstick chp2(phil2, phil0):();
			}
			"""; 
	public final static String FOUR_PHILOSOPHERS = RC_DEFINITIONS + """
			main {
				Philosopher phil0(chp0, chp3):();
				Philosopher phil1(chp0, chp1):();
				Philosopher phil2(chp1, chp2):();
				Philosopher phil3(chp2, chp3):();
			
				Chopstick chp0(phil0, phil1):();
				Chopstick chp1(phil1, phil2):();
				Chopstick chp2(phil2, phil3):();
				Chopstick chp3(phil3, phil0):();
			}
			"""; 
	public final static String FIVE_PHILOSOPHERS = RC_DEFINITIONS + """
			main {
				Philosopher phil0(chp0, chp4):();
				Philosopher phil1(chp0, chp1):();
				Philosopher phil2(chp1, chp2):();
				Philosopher phil3(chp2, chp3):();
				Philosopher phil4(chp3, chp4):();
			
				Chopstick chp0(phil0, phil1):();
				Chopstick chp1(phil1, phil2):();
				Chopstick chp2(phil2, phil3):();
				Chopstick chp3(phil3, phil4):();
				Chopstick chp4(phil4, phil0):();
			}
			"""; 
}
