/**
 *
 * @author Anchu Akhila
 *
 */
package testrmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;

/*
 *Class where all remote methods are implemented 
 */
public class RmiImplementation extends UnicastRemoteObject implements RmiInterface1 {

    public RmiImplementation() throws RemoteException {
        super();
    }

    @Override
    public void shutdown() throws RemoteException {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr;
            pr = rt.exec("shutdown -s");

        } catch (IOException ex) {
        }
    }

    @Override
    public List<String> GetProcessListData() {
        List<String> pList = new ArrayList<String>();
        Process p;
        Runtime runTime;
        String process = null;
        try {
            System.out.println("Processes Reading is started...");
            runTime = Runtime.getRuntime();

            //Execute command thru Runtime
            p = runTime.exec("tasklist");

            //Create Inputstream for Read Processes
            InputStream inputStream = p.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //Read the processes from sysrtem and add & as delimeter for tokenize the output
            String line;
            process = "&";
            while ((line = bufferedReader.readLine()) != null) {

                if (StringUtils.contains(line, "Console")) {
                    String arr[] = line.split(" ");
                    pList.add(arr[0]);
                }

            }

            //Close the Streams
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            System.out.println("Processes are read.");
        } catch (IOException e) {
            System.out.println("Exception arise during the read Processes");
            e.printStackTrace();
        }
        return pList;
    }

//    }
    public void taskill(String k) throws RemoteException {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr;
            String S = "taskkill /F /IM " + k;
            pr = rt.exec(S);

        } catch (IOException ex) {
            //return ex.getMessage();
        }
    }
//    @Override

    public int previlege(String a, int b) throws RemoteException {
        int p2 = 0;
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr;

            String S = "where /r C:\\ " + a;

            pr = rt.exec(S);
            int j = pr.waitFor();
            if (j != 0) {
                S = "where /r D:\\ " + a;
                pr = rt.exec(S);
                j = pr.waitFor();
                if (j != 0) {
                    S = "where /r E:\\ " + a;
                    pr = rt.exec(S);
                    j = pr.waitFor();
                    if (j != 0) {
                        System.out.println("NOT FOUND");
                    }

                }
            }
            if (j == 0) {
                InputStream inputStream = pr.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                int i = 0;
                String line = "";
                String path = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (i == 0) {
                        path = line;
                    }
                    i++;
                    if (i > 0) {
                        break;
                    }
                }
                String user = System.getProperty("user.name");
                String execsetacl = "SetACL -on \"" + path + "\" -ot file -actn ace -ace \"n:" + user + ";p:";
                if (b == 1) {
                    taskill(a);
                    execsetacl = execsetacl + "change;m:deny\"";
                    S = "takeown /f " + path + " /R /D Y";
                    pr = rt.exec(S);
                    int p1 = pr.waitFor();
                    System.out.println(p1);
                    System.out.println(execsetacl);
                    pr = rt.exec(execsetacl);
                    p2 = pr.waitFor();
                    System.out.println(p2);
                }
                if (b == 0) {
                    execsetacl = execsetacl + "full\"";
                    System.out.println(execsetacl);
                    S = "takeown /f " + path + " /R /D Y";
                    pr = rt.exec(S);
                    int p1 = pr.waitFor();
                    pr = rt.exec(execsetacl);
                    p2 = pr.waitFor();
                }
            }



        } catch (Exception ex) {
        }
        return p2;//return exit status of setacl
    }
}
