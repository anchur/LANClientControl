/*
 * @ author Akhila Anchu
 */
package testrmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * This class is run by client system
 */
public class RmiServer {

    int thisPort;
    String thisAddress;
    Registry registry;    // rmi registry for lookup the remote objects.

    // This method is called from the remote client by the RMI.
    public RmiServer() throws RemoteException, AlreadyBoundException {

        try {
            thisAddress = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            throw new RemoteException("can't get inet address.");
        }
        thisPort = 3220;  // this port(registry?fs port)
        System.out.println("this address=" + thisAddress + ",port=" + thisPort);
        try {
            // create the registry and bind the name and object.
            registry = LocateRegistry.createRegistry(thisPort);
            registry.rebind(RmiInterface1.class.getName(), new RmiImplementation());
            System.out.println("A new regisrty is created....");
        } catch (RemoteException e) {
            try {
                registry = LocateRegistry.getRegistry(thisPort);
                registry.rebind(RmiInterface1.class.getName(), new RmiImplementation());
                System.out.println("Old rmi registry is fetched...");
            } catch (AccessException ex) {
                Logger.getLogger(RmiServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    static public void main(String args[]) {

        try {
            RmiServer s = new RmiServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}