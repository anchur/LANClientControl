/**
 *
 * @author Anchu Akhila
 *
 */
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import testrmi.RmiInterface1;
/*
 * ShutdownThread.java
 *  -Runs once automatic shutdown is selected
 *  -Fetches shutdown time from database and system time is continuously compared with that
 *  -When a match is found rmi method for shutdown is called
 */

public class ShutdownThread extends Thread {
    String time;
    String r="No time";
    public ShutdownThread()
    {
         try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/admindetails", "root", "raset");
            PreparedStatement st = con.prepareStatement("SELECT * FROM ADMINREQ WHERE M=2");
            ResultSet R = st.executeQuery();
            while (R.next()) {
                String tym = R.getString("SETTIME");//get set time from database
                
                time=tym;
                System.out.println(tym);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
         
    }
    public void run() {
        while (!time.equals(r)) {
            Calendar calendar = new GregorianCalendar();
            String am_pm, h, m;
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            if (calendar.get(Calendar.AM_PM) == 0) {
                am_pm = "am";
            } else {
                am_pm = "pm";
            }
            if (hour < 10) {
                h = "0" + Integer.toString(hour);
            } else {
                h = Integer.toString(hour);
            }
            if (minute < 10) {
                m = "0" + Integer.toString(minute);
            } else {
                m = Integer.toString(minute);
            }

            r = h + m + am_pm;//get system time
            System.out.println("time=" + time + "r=" + r);
        }
        if (time.equals(r)) {
            int t = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3308/admindetails", "root", "raset");
                PreparedStatement st = con.prepareStatement("SELECT * FROM CLIENTS");
                ResultSet R = st.executeQuery();
                while (R.next()) {
                    int flag = R.getInt("A");
                    if (flag == 1) {
                        t = 1;
                        Registry registry;
                        try {
                            int port = 3220;
                            String client = R.getString("IP");
                            registry = LocateRegistry.getRegistry(client, port);
                            RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
                            ri.shutdown();
                        } catch (RemoteException ex) {
                        } catch (NotBoundException ne) {
                        }
                    }

                }
                if (t == 0)//if no clients are there
                {
                    JOptionPane.showMessageDialog(null, "No Clients are in list for auto shutdown");
                }
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(null, "No Clients in list for auto shutdown");
            }

        }


    }
}
