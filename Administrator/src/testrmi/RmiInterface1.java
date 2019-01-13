/**
 *
 * @author Anchu Akhila
 */

package testrmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/*
 * Interface for all method remotely invoked by administrator
 */
public interface RmiInterface1 extends Remote{
     public void shutdown() throws RemoteException;
     public List<String> GetProcessListData()throws RemoteException;
     public void taskill(String k) throws RemoteException;
	 public int previlege(String a,int b) throws RemoteException;
     
   
}
