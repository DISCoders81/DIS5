
/**
 * @author joantomaspape
 * Main class in which the application can be started
 */

public class Main {

	public static void main(String[] args) {
		
		Client client1 = new Client(1);
		Client client2 = new Client(2);
		
		client1.run();
		client2.run();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client1.interrupt();
		client2.interrupt();
		
	}

}
