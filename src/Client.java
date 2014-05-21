/**
 * @author joantomaspape
 * This class represents a database client. The 5 different clients have access to disjunctive sets of pages, thus making
 * the access mutually exclusive.
 */

public class Client extends Thread {

	int clientId;
	
	public Client (int clientId){
		this.clientId = clientId;
	}
}
