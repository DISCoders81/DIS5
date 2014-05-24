/**
 * @author joantomaspape Main class in which the application can be started
 */

public class Main {

	public static void main(String[] args) {

		Client client1 = new Client(1);
		Client client2 = new Client(2);
		Client client3 = new Client(3);
		Client client4 = new Client(4);
		Client client5 = new Client(5);

		client1.run();
		client2.run();
		client3.run();
		client4.run();
		client5.run();

		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		client1.interrupt();
		client2.interrupt();
		client3.interrupt();
		client4.interrupt();
		client5.interrupt();

	}

}
