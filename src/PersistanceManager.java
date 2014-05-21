
/**
 * @author joantomaspape
 * This class is the Persistance Manager of the Database. Here all Transactions are logged and persisted.
 */

public class PersistanceManager{

	private static PersistanceManager instance = new PersistanceManager();
	
	private PersistanceManager(){}
	
	public static PersistanceManager getInstance(){
		return instance;
	}
	
}
