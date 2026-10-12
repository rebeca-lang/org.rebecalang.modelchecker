package org.rebecalang.transparentactormodelchecker.timedrebeca;

public class WSANSourceCodes {
	
	private final static String RC_DEFINITIONS = 				
	"""
		env int BUFFER_SIZE = 4;
		
		reactiveclass Sensor(3) {
			statevars{
				CompUnit cu;
			}
			
			Sensor(CompUnit cu1) {
				cu = cu1;
				self.gatherData();
			}
			
			msgsrv gatherData() {
				cu.receiveData(0);
				self.gatherData() after(2);
			}
		}
		
		
		reactiveclass CompUnit(3) {
			statevars {
				TDMANetwork network;
				int[4] buffer;
				int cnt;
			}
			
			CompUnit(TDMANetwork net) {
				network = net;
				self.process();
			}
		
			msgsrv receiveData(byte data) {
				buffer[cnt++] = data;
			}
			
			msgsrv process() {
				for (int i=cnt; cnt>0; cnt--)
					network.send((byte)buffer[i]);
				self.process() after(1);
			}
		}
		
		reactiveclass TDMANetwork (3) {
			msgsrv send(byte data) {
				// Send data according
				// to the TDMA protocol
			}
		}
		
		
	""";
	
	public final static String ONE_SENSOR = RC_DEFINITIONS + """
			main {
				Sensor sensor():(cu);
				CompUnit cu():(network);
				TDMANetwork network():();
			}
		"""; 
	public final static String TWO_SENSOR = RC_DEFINITIONS + """
			main {
				Sensor sensor1():(cu);
				Sensor sensor2():(cu);
				CompUnit cu():(network);
				TDMANetwork network():();
			}
		"""; 
}
