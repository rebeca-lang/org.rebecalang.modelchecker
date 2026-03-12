package org.rebecalang.transparentactormodelchecker.timedrebeca;

public class TicketServiceSourceCodes {
	
	private final static String RC_DEFINITIONS = 				
	"""
		reactiveclass Customer(3) {
		    knownrebecs { Agent a; }
		    statevars {
				byte id;
		    }
		    Customer(byte myId) {
		        id = myId;
				self.try();
		    }
		    msgsrv try() {
		    	a.requestTicket();
		    }
		    msgsrv ticketIssued() {
		        self.try() after(30);
		    }
		}
		reactiveclass Agent(10) {
		    knownrebecs { TicketService ts; }
		    msgsrv requestTicket() {
		        ts.requestTicket((Customer)sender) deadline(24);
		    }
		    msgsrv ticketIssued(Customer customer) {
				customer.ticketIssued();
		    }
		}
		reactiveclass TicketService(10) {
		    knownrebecs { Agent a; }
		    statevars {
		        int issueDelay;
		    }
		    TicketService(int myIssueDelay) {
		        issueDelay = myIssueDelay;
		    }
		    msgsrv requestTicket(Customer customer) {
		        delay(?(2,3));
		        a.ticketIssued(customer);
		    }
		}
	""";
	
	public final static String ONE_CUSTOMER = RC_DEFINITIONS + """
			main {
			    Customer c1(a):(1);
			    Agent a(ts):();
			    TicketService ts(a):(2);
			}				
			"""; 
	public final static String TWO_CUSTOMERS = RC_DEFINITIONS + """
			main {
			    Customer c1(a):(1);
			    Customer c2(a):(2);
			    Agent a(ts):();
			    TicketService ts(a):(2);
			}				
			"""; 
	public final static String THREE_CUSTOMERS = RC_DEFINITIONS + """
			main {
			    Customer c1(a):(1);
			    Customer c2(a):(2);
			    Customer c3(a):(3);
			    Agent a(ts):();
			    TicketService ts(a):(2);			
			}
			"""; 
}
