import com.administrator.DBConnection.ConnectionParameters;
import testrmi.RmiInterface1;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.JList;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;

/**
 *
 * @author Anchu Akhila
 */

/*
 * Home.java
 * This class implements the main functions of this application:
 * 1.Set Privileges for client applications
 *      -can block or unblock applications specified by the administrator on the client side
 * 2.Kill running applications of clients
 *      -can kill currently running console processes on the client side
 * 3.Shutdown of clients 
 *      - two options are given :
 *                      automatic:  a list of clients will be shut down at a set time.
 *                                  list by default is all registered clients
 *                                  list can be modified selecting settings button
 *                                  Implemented as thread so that until set time administrator will not be 
 *                                  interrupted from performing other activities
 *                      manual:     selected client will be shutdon at that time
 * 4.Register Clients 
 *      -to register new clients 
 */

public class Home extends javax.swing.JFrame implements Serializable {

    /**
     * Creates new form Home
     */
    ShutdownThread ST=new ShutdownThread();//Threads for controlling automatic shutting down
    ShutdownThread TS=new ShutdownThread();
    private Connection hcon;
    private ShutdownThread s;
    private List<String> pList;
    private String sel_ip;
    private DefaultListModel listModel, listModel1, listModel2, listModel3, listModel4;
    PreparedStatement pstmt;

    public Home() {
        this.setResizable(false);//making the frame non-closable
        initComponents();
        try {
            jPanel4.setVisible(false);
            jPanel1.setVisible(false);
            jPanel5.setVisible(false);
            jPanel7.setVisible(false);
            listModel = new DefaultListModel();
            listModel1 = new DefaultListModel();
            listModel2 = new DefaultListModel();
            listModel3 = new DefaultListModel();
            listModel4 = new DefaultListModel();
            jList6.setModel(listModel2);//list Model for manual shutting down
            jList4.setModel(listModel3);//list Model for list from which admin could select applications for blocking or unblocking
            jList5.setModel(listModel4);//list Model for listing exe names of applications to be dealt in privilege setting

            addtoListModel();   
            settoListModel2();
            settoListModel3();

           autoshutdown();
          
            jList6.addMouseListener(new MouseAdapter() {// for manual shutting down
                public void mouseClicked(MouseEvent evt) {
                    jList6MouseClicked(evt);

                }
            });

             jList1.addMouseListener(new MouseAdapter() {//in kill, for listing clients
                public void mouseClicked(MouseEvent evt) {

                    JList1MouseClicked(evt);

                }
            });
            jList2.addMouseListener(new MouseAdapter() {//in kill, listing exe names running on clients
                public void mouseClicked(MouseEvent evt) {
                    jList2MouseClicked(evt);

                }
            });
            jList4.addMouseListener(new MouseAdapter() {//in privilege setting,for application list
                public void mouseClicked(MouseEvent evt) {
                    jList4MouseClicked(evt);

                }
            });

            jList5.addMouseListener(new MouseAdapter() {//in privilege setting,for list of exe files of selected applications
                public void mouseClicked(MouseEvent evt) {
                    jList5MouseClicked(evt);

                }
            });





        } catch (Exception e) {
            e.getMessage();
        }

    }
    
    /*
     * Method to determine if automatic shutdown has been activated or not
     * if not activated settime field will be set to "TSTHREAD"
     * else it will contain the time for shutting down set by the user earlier
     * This check will be performed everytime once logon, inorder to determine whether TS thread
     * for shutting down need to be started or not
     */
    public void autoshutdown(){
          try {
            if (hcon==null) {
                        hcon = ConnectionParameters.getCon();//establish connection
                    }
            PreparedStatement st = hcon.prepareStatement("SELECT * FROM ADMINREQ WHERE M=2");//M=2 is the record in adminreq that gets updated for autoshutdown
            ResultSet R = st.executeQuery();
            while (R.next()) {
                String time = R.getString("SETTIME");
                if(!time.equals("TSthrd"))//if autoshutdown is enabled
                {
                    if(ST.isAlive())
                        ST.stop();
                    TS=new ShutdownThread();
                    TS.start();
                    jButton7.setVisible(true);
                }
                else
                    jButton7.setVisible(false);
         
              
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
   
    /*
     * Method in kill 
     * If any ip of list 1 is double clicked, then rmi method will be called to list all running processes which will be added to list2 
     */
        private void JList1MouseClicked(MouseEvent evt) {
        try {
            pList = null;
            if (!listModel1.isEmpty()) {//if list2(list of exe files) is nonempty
                listModel1.clear();
            }
            int index = 2;
            JList list = (JList) evt.getSource();
            if (evt.getClickCount() == 2) {
                index = list.locationToIndex(evt.getPoint());//on double click,get index of that list object
                try {
                    int i = 0;

                    if (hcon==null) {
                        hcon = ConnectionParameters.getCon();
                    }

                    pstmt = hcon.prepareStatement("SELECT * FROM clients");
                    ResultSet R = pstmt.executeQuery();
                    while (R.next()) {
                        String ip = R.getString("IP");
                        if (i == index) {//fetch ip from database where index=selected object index
                            Registry registry;
                            try {
                                int port = 3220;
                                sel_ip = ip;
                                System.out.println(ip);
                                pList = new ArrayList<String>();
                                registry = LocateRegistry.getRegistry(ip, port);
                                RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
                                setpList(ri.GetProcessListData());//calls remote function
                            } catch (RemoteException ex) {
                                 } 
                            catch (NotBoundException ne) {
                            }

                        }
                        i = i + 1;

                    }

                    //changing

                    String convpList[] = pList.toArray(new String[pList.size()]);
                    for (int pk = 0; pk < convpList.length; pk++) {
                        listModel1.addElement(convpList[pk]);
                    }

                    //con.close();
                    jList2.setModel(listModel1);//add plist to list2(list of exe files)

                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
     * Method in kill 
     * If any i exe file is double clicked, then rmi method will be called to kill that in selected list1 client 
     */
    private void jList2MouseClicked(MouseEvent evt) {
        try {
            int index = 2;

            JList list = (JList) evt.getSource();
            if (evt.getClickCount() == 2) {
                index = list.locationToIndex(evt.getPoint());
                String listitem = list.getModel().getElementAt(index).toString();
                Registry registry;
                try {
                    int port = 3220;
                    registry = LocateRegistry.getRegistry(sel_ip, port);
                    RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
                    ri.taskill(listitem);//calls method for killing
                    listModel1.removeElement(listitem);
                } catch (RemoteException ex) {
                } catch (NotBoundException ne) {
                }

            }

        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     *Method in privilege setting if any application of list4 is selected then its exe file stored in database will be added to list 6 
     */
    private void jList4MouseClicked(MouseEvent evt) {
        int index = 2;
        String exename = null;
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            index = list.locationToIndex(evt.getPoint());
            String listitem = list.getModel().getElementAt(index).toString();
            try {
                int i = 0;
                if (hcon == null) {
                    hcon = ConnectionParameters.getCon();
                }
                String updateTableSQL = "SELECT * FROM applications";
                pstmt = hcon.prepareStatement(updateTableSQL);
                ResultSet R = pstmt.executeQuery();
                while (R.next()) {
                    if (i == index) {
                        exename = R.getString("EXENAME");
                    }
                    i++;
                }
                listModel4.addElement(exename);
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }
   
    /*
     *Method in privilege setting if any exe file of list5 is selected then its exe file will be removed from the list to be blocked/unblocked 
     */
    private void jList5MouseClicked(MouseEvent evt) {
        int index = 2;
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            index = list.locationToIndex(evt.getPoint());
            String listitem = list.getModel().getElementAt(index).toString();

            listModel4.removeElement(listitem);

        }
    }
/*
 * Method in manual shutting down . Double clicked client ip will be set in label field
 */
    private void jList6MouseClicked(MouseEvent evt) {
        int index = 2;

        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            index = list.locationToIndex(evt.getPoint());
            String listitem = list.getModel().getElementAt(index).toString();
            jLabel7.setText(listitem);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Enter shut down time");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 160, 22));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 50, -1));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jPanel4.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 70, 50, -1));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "am", "pm" }));
        jPanel4.add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 60, -1));

        jLabel4.setText(" HH");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 30, 30));

        jLabel5.setText(" MM");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, 30, 30));

        jButton1.setText("Activate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, -1, -1));

        jButton2.setText("settings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, -1, -1));

        jButton7.setText("Deactivate");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, -1, -1));
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 234, 170, 20));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 350));

        jLabel1.setText("Select a client to list its process");

        jScrollPane1.setToolTipText("");

        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addGap(21, 21, 21))
        );

        jScrollPane2.setViewportView(jList2);

        jLabel2.setText("Select application to be killed");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 490, 350));

        jButton4.setText("Shut down");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jList6.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList6);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Clients");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(jButton4))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(156, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(jButton4)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 350));

        jScrollPane6.setViewportView(jList4);

        jLabel6.setText("Double click to set previlege");

        jButton3.setText("Add/Remove");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(81, 81, 81))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addGap(16, 16, 16))
        );

        jScrollPane7.setViewportView(jList5);

        jLabel10.setText("Selected list: Double click to remove");

        jButton5.setText("BLOCK");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("UNBLOCK");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton6)
                .addGap(37, 37, 37))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 350));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Home-Icon.png"))); // NOI18N
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 350));

        jLabel12.setFont(new java.awt.Font("Sylfaen", 3, 36)); // NOI18N
        jLabel12.setText("Welcome.....");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 200, 70));

        getContentPane().add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 350));

        jMenu2.setText("Kill");
        jMenu2.setMaximumSize(new java.awt.Dimension(150, 32767));
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Set Previlege");
        jMenu3.setMaximumSize(new java.awt.Dimension(150, 32767));

        jMenuItem3.setText("For all");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Shutdown");
        jMenu4.setMaximumSize(new java.awt.Dimension(150, 32767));
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu4MouseClicked(evt);
            }
        });
        jMenu4.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu4MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenu4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu4ActionPerformed(evt);
            }
        });

        jMenuItem1.setText("Automatic");
        jMenuItem1.setPreferredSize(new java.awt.Dimension(150, 22));
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItem1MouseClicked(evt);
            }
        });
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuItem2.setText("Manual");
        jMenuItem2.setPreferredSize(new java.awt.Dimension(150, 22));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem2);

        jMenuBar1.add(jMenu4);

        jMenu1.setText("Add/Remove clients");
        jMenu1.setMaximumSize(new java.awt.Dimension(150, 32767));
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
 * Function executed if automatic shutdown is selected
 */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        listModel1.clear();
        listModel4.clear();
        jPanel1.setVisible(false);
        jPanel5.setVisible(false);
        jPanel7.setVisible(false);
        jPanel4.setVisible(true);
        try {
            if (hcon==null) {
                        hcon = ConnectionParameters.getCon();
                    }
            PreparedStatement st = hcon.prepareStatement("SELECT * FROM ADMINREQ WHERE M=2");
            ResultSet R = st.executeQuery();
            while (R.next()) {
                String time = R.getString("SETTIME");
                if(!time.equals("TSthrd"))//If autoshutdown is activated already
                {
                   String forlab9="Shutdown is set at "+time;
                   jLabel9.setText(forlab9);
                   jLabel9.setVisible(true);
                   jButton7.setVisible(true);
                   
                }
                else
                    jLabel9.setVisible(false);
                   // jButton7.setVisible(false);
         
              
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed
/*
 * Function executed if manual shutdown is selected
 */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        listModel2.clear();
            settoListModel2();
        listModel1.clear();
        listModel4.clear();
        jPanel4.setVisible(false);
        jPanel1.setVisible(false);
        jPanel7.setVisible(false);
        jPanel5.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed

    }//GEN-LAST:event_jComboBox2ActionPerformed
/*
 * Function executed if automatic shut down is activated
 */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String S = (String) jComboBox1.getSelectedItem() + (String) jComboBox2.getSelectedItem() + (String) jComboBox3.getSelectedItem();
        try {


            if (hcon.isClosed()) {
                hcon = ConnectionParameters.getCon();
            }
            String insertTableSQL = "UPDATE ADMINREQ SET SETTIME=" + "(?)" + "WHERE M=" + "(?)";

            PreparedStatement stmt = hcon.prepareStatement(insertTableSQL);
            stmt.setString(1, S);
            stmt.setInt(2, 2);
            stmt.executeUpdate();
            String forlab9="Shutdown is set at "+S;
                   jLabel9.setText(forlab9);
                   jLabel9.setVisible(true);
                   jButton7.setVisible(true);
                           }
        catch (Exception e) {
            System.out.println(e);
        }
        if(TS.isAlive())//if already activated deactivate
            TS.stop();
        if(ST.isAlive())//if already activated deactivate
            ST.stop();
        ST=new ShutdownThread();
        setS(ST);//serialisable S
        getS().start();
        
    }//GEN-LAST:event_jButton1ActionPerformed
/*
 * Function executed if kill is selected
 */   
    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        listModel.clear();
        addtoListModel();   
       listModel1.clear();
        listModel4.clear();
        jPanel4.setVisible(false);
        jPanel5.setVisible(false);
        jPanel7.setVisible(false);
        jPanel1.setVisible(true);
        jList1.setModel(listModel);
    }//GEN-LAST:event_jMenu2MouseClicked
/*
 * Function activated if automatic shutdown is selected
 */
    private void jMenuItem1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MouseClicked

        jPanel1.setVisible(false);
        jPanel5.setVisible(false);
        jPanel7.setVisible(false);
        jPanel4.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1MouseClicked

    private void jMenu4MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu4MenuSelected
       
    }//GEN-LAST:event_jMenu4MenuSelected

    private void jMenu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu4MouseClicked
       
    }//GEN-LAST:event_jMenu4MouseClicked
/*
 * If settings button of automatic shutdown is clicked
 */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jPanel1.setVisible(false);
        jPanel5.setVisible(false);
        jPanel4.setVisible(false);
        jPanel7.setVisible(false);
        Settings s3 = new Settings();
        s3.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed
/*
 * if ok button of manual shut down is selected
 */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Registry registry;
        try {
            int port = 3220;
            String ip = jLabel7.getText();
            System.out.println(ip);
            registry = LocateRegistry.getRegistry(ip, port);
            RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
            ri.shutdown();//remotely calls shutdown method
        } catch (RemoteException ex) {
        } catch (NotBoundException ne) {
        }
    }//GEN-LAST:event_jButton4ActionPerformed
/*
 * Function is executed if manual shutdown is selected
 */
    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        jPanel1.setVisible(false);
        jPanel5.setVisible(false);
        jPanel4.setVisible(false);
        jPanel7.setVisible(false);
        listModel1.clear();
        listModel4.clear();
        RegisterClients rc = new RegisterClients();
        rc.setVisible(true);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jMenu4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu4ActionPerformed
        
    }//GEN-LAST:event_jMenu4ActionPerformed
/*
 * Function executed if privilege setting for all is selected
 */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        listModel3.clear();
        settoListModel3();
        listModel1.clear();
        listModel4.clear();
        jPanel1.setVisible(false);
        jPanel4.setVisible(false);
        jPanel5.setVisible(false);
        jPanel7.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed
/*
 * Function is exexuted if block button of previlege setting is selected
 * Each exe is got from list and is passed to remote function 
 */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        Registry registry;
        try {
            int port = 3220;
            int siZe = jList5.getModel().getSize();
            try {


                if (hcon.isClosed()) {
                    hcon = ConnectionParameters.getCon();
                }
                pstmt = hcon.prepareStatement("SELECT * FROM clients");
                ResultSet R = pstmt.executeQuery();
                while (R.next()) {
                    String sel_ip = R.getString("IP");
                    registry = LocateRegistry.getRegistry(sel_ip, port);
                    RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
                    for (int i = 0; i < siZe; i++) {   
                        String listitem = jList5.getModel().getElementAt(i).toString();
                        System.out.println(listitem);
                        int x=ri.previlege(listitem, 1);//remote method is called
                        System.out.println(x);
                        
                        if(x==0)
                        {String insertTableSQL = "UPDATE APPLICATIONS SET FLAG=" + "(?)" + "WHERE EXENAME=" + "(?)";
                        PreparedStatement stmt = hcon.prepareStatement(insertTableSQL);
                        stmt.setInt(1, 1);
                        stmt.setString(2, listitem);
                        stmt.executeUpdate();
                        }

                    }

                }
                listModel4.clear();
                listModel3.clear();
                settoListModel3();
            } catch (Exception e) {
                System.out.println(e);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_jButton5ActionPerformed
/*
 * To add new applications for privilege setting
 */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        AddPrevilege a = new AddPrevilege();
        dispose();
        a.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed
/*
 * Function is exexuted if unblock button of previlege setting is selected
 * Each exe is got from list and is passed to remote function
 */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
      Registry registry;
        try {
            int port = 3220;
            int siZe = jList5.getModel().getSize();
            try {


                if (hcon.isClosed()) {
                    hcon = ConnectionParameters.getCon();
                }
                pstmt = hcon.prepareStatement("SELECT * FROM clients");
                ResultSet R = pstmt.executeQuery();
                while (R.next()) {
                    String sel_ip = R.getString("IP");



                    registry = LocateRegistry.getRegistry(sel_ip, port);
                    RmiInterface1 ri = (RmiInterface1) registry.lookup(RmiInterface1.class.getName());
                    for (int i = 0; i < siZe; i++) {
                        String listitem = jList5.getModel().getElementAt(i).toString();
                        System.out.println(listitem);
                        int x=ri.previlege(listitem, 0);
                        if(x==0)
                        {String insertTableSQL = "UPDATE APPLICATIONS SET FLAG=" + "(?)" + "WHERE EXENAME=" + "(?)";

                        PreparedStatement stmt = hcon.prepareStatement(insertTableSQL);
                        stmt.setInt(1, 0);
                        stmt.setString(2, listitem);
                        stmt.executeUpdate();
                        }
                        }

                }
                listModel4.clear();
                listModel3.clear();
                settoListModel3();
            } catch (Exception e) {
                System.out.println(e);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }  
    }//GEN-LAST:event_jButton6ActionPerformed
/*
 * Function executed if deactivate button of automatic shutdown is selected
 * settime in datbase is modified to "TSthrd" in record where m=2
 */
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
jButton7.setVisible(false);
        if(ST.isAlive())
       {
           System.out.println("a");
        ST.stop();
       }
       
       if(TS.isAlive())
        TS.stop();
        try {


            if (hcon.isClosed()) {
                hcon = ConnectionParameters.getCon();
            }
            String insertTableSQL = "UPDATE ADMINREQ SET SETTIME=" + "(?)" + "WHERE M=" + "(?)";

            PreparedStatement stmt = hcon.prepareStatement(insertTableSQL);
            stmt.setString(1, "TSthrd");
            stmt.setInt(2, 2);
            stmt.executeUpdate();
            jLabel9.setText("Auto shutdown deactivated");
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                  try {
                    UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel");
                   new Home().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
               
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList4;
    private javax.swing.JList jList5;
    private javax.swing.JList jList6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the s
     */
    public ShutdownThread getS() {
        return s;
    }

    /**
     * @param s the s to set
     */
    public void setS(ShutdownThread s) {
        this.s = s;
    }

    /**
     * @return the pList
     */
    public List<String> getpList() {
        return pList;
    }

    /**
     * @param pList the pList to set
     */
    public void setpList(List<String> pList) {
        this.pList = pList;
    }
/*
 * Function executed to add ip addresses of clients to list1 in kill
 */
    private void addtoListModel() {
        try {

            ///con = ConnectionParameters.getCon();
            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            pstmt = hcon.prepareStatement("SELECT * FROM clients");
            ResultSet R = pstmt.executeQuery();
            while (R.next()) {
                String ip = R.getString("IP");
                listModel.addElement(ip);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
/*
 * Function executed to add ip addresses of clients to list6 in privilege setting
 */
    private void settoListModel2() {
        try {

            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            pstmt = hcon.prepareStatement("SELECT * FROM clients");
            ResultSet R = pstmt.executeQuery();
            while (R.next()) {
                String ip = R.getString("IP");
                listModel2.addElement(ip);
            }

            //con.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pstmt.close();
                /// hcon.close();
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
/*
 * Function executed to add ip addresses of clients to list4 in manual shutting down
 */
    private void settoListModel3() {
        try {

            //con = ConnectionParameters.getCon();
            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            pstmt = hcon.prepareStatement("SELECT * FROM applications");
            ResultSet R = pstmt.executeQuery();
            while (R.next()) {
                String name = R.getString("NAME");
                int fg = R.getInt("FLAG");
                if (fg == 0) {
                    name = name + "/Unblocked";
                } else {
                    name = name + "/Blocked";
                }
                listModel3.addElement(name);
            }
            //con.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                pstmt.close();
               //hcon.close();
            } catch (SQLException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



    }
}
