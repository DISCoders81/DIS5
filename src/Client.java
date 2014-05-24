/**
 * @author joantomaspape This class represents a database client. The 5
 *         different clients have access to disjunctive sets of pages, thus
 *         making the access mutually exclusive.
 */

public class Client extends Thread {

	int			clientId;
	int			minPage;
	int			maxPage;
	Transaction	currentTa;


	public Client(int clientId) {

		this.clientId = clientId;
		this.minPage = clientId * 10;
		this.maxPage = minPage + 9;
	}


	public void run() {

		int actPage = minPage;
		while (actPage <= maxPage)
		{
			try
			{
				beginTransaction();
				Client.sleep(1000);

				write("Erster Eintrag Für Transaction " + currentTa.getTaId(),
						actPage);
				actPage++;
				Client.sleep(1500);

				write("Zweiter Eintrag Für Transaction " + currentTa.getTaId(),
						actPage);
				actPage++;
				Client.sleep(1200);

				commit();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}


	private void beginTransaction() {

		currentTa = PersistanceManager.getInstance().beginTransaction();
	}


	private void write(String data, int actPage) {

		currentTa.write(actPage, data);
	}


	private void commit() {

		PersistanceManager.getInstance().commit(currentTa);
	}
}
