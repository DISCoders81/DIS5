/**
 * @author joantomaspape
 * This class represents a database client. The 5 different clients have access to disjunctive sets of pages, thus making
 * the access mutually exclusive.
 */

public class Client extends Thread {

	int clientId;
	int minPage;
	int maxPage;
	int currentTaId;
	
	public Client (int clientId){
		this.clientId = clientId;
		this.minPage = clientId * 10;
		this.maxPage = minPage + 9;
	}
	
	public void run(){
		int actPage = minPage;
		while(actPage <= maxPage){
			try{
				beginTransaction();
				Client.sleep(2000);
				
				write("Erster Eintrag Für Transaction " + currentTaId, actPage);
				actPage++;
				Client.sleep(2000);
	
				write("Zweiter Eintrag Für Transaction " + currentTaId, actPage);
				actPage++;
				Client.sleep(2000);
	
				commit();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void beginTransaction(){
		currentTaId = PersistanceManager.getInstance().beginTransaction();
	}
	
	private void write(String data, int actPage){
		PersistanceManager.getInstance().write(currentTaId, actPage, data);
	}
	
	private void commit(){
		PersistanceManager.getInstance().commit(currentTaId);
	}
}
