import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * @author joantomaspape Main class in which the application can be started
 */

public class Main {

	public static void main(String[] args) {

		start();

	}


	private static void startClients(Client[] clients) {

		for (int i = 0; i < clients.length; i++)
		{
			clients[i].run();
		}

		try
		{
			Thread.sleep(5000 + 1000 * clients.length);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		for (int i = 0; i < clients.length; i++)
		{
			clients[i].interrupt();
		}
	}


	private static void start() {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
		int amountOfClients = 1;
		boolean crashRecoveryEnabled = false;

		System.out.print("This is the database builder. \n\nHow many clients do you want to create (min 1, max 10)?\n-> ");
		try
		{
			amountOfClients = Integer.parseInt(inputReader.readLine()) % 10;

			System.out.println("\nOk. " + amountOfClients + " Clients will be created.");

			while (true)
			{
				System.out.print("\nDo you want to enable the crash recovery? \n(Yes/No) ->");
				String enable = inputReader.readLine();

				if (enable.equalsIgnoreCase("Yes"))
				{
					crashRecoveryEnabled = true;
					break;
				} else if (enable.equalsIgnoreCase("No"))
				{
					crashRecoveryEnabled = false;
					break;
				} else
				{
					System.err.println("Input incorrect. Try again..");
					Thread.sleep(150);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				inputReader.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		System.out.println("\nThanks. Have fun!");
		System.out.println("--------------------");
		System.out.println("-------- DB --------");
		System.out.println("--------------------");

		if (crashRecoveryEnabled)
		{
			PersistanceManager.getInstance().crashRecovery();
		}

		System.out.println("--------------------");
		System.out.println("- Creating Clients -");
		System.out.println("---------...--------");

		startClients(createClients(amountOfClients));

	}


	private static Client[] createClients(int n) {

		ArrayList<Client> clientList = new ArrayList<Client>();
		for (int i = 0; i < n; i++)
		{
			clientList.add(new Client(i));
		}
		return clientList.toArray(new Client[clientList.size()]);
	}
}
