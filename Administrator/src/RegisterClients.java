/*
 * RegisterClients
 * @author AkhilaAnchu
 */

import com.administrator.DBConnection.ConnectionParameters;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * RegisterClients: Class used to register clients to the application.
 * Only if a client is registered, any function of application say, 
 * kill,shutdown,setprivilege can be done on that client. 
 * Object of RegisteredClients:created in Home class where menubutton RegisterClients is clicked.
 */
public class RegisterClients extends javax.swing.JFrame {

    private boolean b;
    private javax.swing.DefaultListModel listModel;
    private Connection hcon;

    /*
     * Creates new form RegisterClients
     */
    public RegisterClients() {
        this.setResizable(false);
        initComponents();
        jLabel5.setVisible(false);

        listModel = new javax.swing.DefaultListModel();
        jList1.setModel(listModel);

        addtolist();  //All Registered clients are added to list 

        /*
         * MouseListener is added to list displaying all registered clients.
         * This is to select and remove a registered client.
         */
        jList1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jList1MouseClicked(evt);

            }
        });

        /*
         * DocumentListener is added to check valid of IP Field.
         * Until a valid IP entered, warning shown as invalid 
         */
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                // text was changed
            }

            public void removeUpdate(DocumentEvent e) {
                /* text was deleted say if one character deleted
                 * ip validation
                 */
                b = validateIPAddress(jTextField1.getText());
                if (!b) {
                    jLabel5.setText("Invalid ip");
                    jLabel5.setVisible(true);
                } else {
                    jLabel5.setText("Valid");
                    jLabel5.setVisible(true);
                }
            }

            public void insertUpdate(DocumentEvent e) {
                /* text was inserted 
                 * ip validation
                 */

                b = validateIPAddress(jTextField1.getText());
                if (!b) {
                    jLabel5.setText("Invalid ip");
                    jLabel5.setVisible(true);
                } else {
                    jLabel5.setText("Valid");
                    jLabel5.setVisible(true);
                }

            }
        });
    }
    /*
     * To select an item i.e. client from listed RegisteredClients
     * and set to TextField for removing.
     */

    private void jList1MouseClicked(MouseEvent evt) {
        int index;
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
            index = list.locationToIndex(evt.getPoint());
            String listitem = list.getModel().getElementAt(index).toString();

            jTextField1.setText(listitem);

        }
    }
    
    /*
     * Method addtolist reads all registered clients from database
     * and add to list of Registered clients
     * Table used to store RegisteredClients is clients
     */
    private void addtolist() {
        try {
            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            PreparedStatement st = hcon.prepareStatement("SELECT * FROM clients");
            ResultSet R = st.executeQuery();
            while (R.next()) {
                String ip = R.getString("IP");
                listModel.addElement(ip);
                System.out.println(ip);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /*
     * Method to validate IP entered.
     * Valid IP should be i form [1-255].[1-255].[1-255].[1-255]
     * if valid method returns true.
     * if not valid returns false.
     * Method invoked by Document listener whenever IP field updated.
     * Argument passed is string to be validated.
     */
    public boolean validateIPAddress(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String str : tokens) {
            int i = Integer.parseInt(str);
            if ((i < 0) || (i > 255)) {
                return false;
            }
        }
        return true;
    }

    /* initcomponents method definition
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setViewportView(jList1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 158, 280));

        jButton1.setText("Remove");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 160, -1, -1));

        jButton2.setText("Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 160, 70, -1));

        jLabel1.setText("Registered Clients");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 158, -1));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 50, 122, -1));

        jLabel2.setText("Enter IP address:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 30, -1, 10));

        jButton3.setText("back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 260, -1, -1));

        jLabel4.setText("Enter Domain name:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 120, -1));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 120, 120, -1));

        jLabel5.setText("Invalid IP address");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 80, 120, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
     * jButton1-Remove
     * Used to remove regstered clients from application
     *      -Removed from list and database
     *      -Removed only if such a client exist
     *      -if tried to remove aclient not yet registered warning-"NO SUCH CLIENT EXIST"
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            String ip = jTextField1.getText();
            String name;
            int f = 0;
            java.sql.PreparedStatement st = hcon.prepareStatement("SELECT * FROM CLIENTS");
            ResultSet R = st.executeQuery();
            while (R.next()) {
                name = R.getString("IP");
                if (ip.equals(name)) {
                    f = 1;
                }
            }
            if (f == 1) {
                String deleteTableSQL = "DELETE FROM CLIENTS WHERE IP=" + "(?)";
                PreparedStatement stmt = hcon.prepareStatement(deleteTableSQL);
                stmt.setString(1, jTextField1.getText());
                stmt.executeUpdate();
                listModel.removeElement(jTextField1.getText());
            }
            else {
                JOptionPane.showMessageDialog(null, "NO SUCH CLIENT EXISTS");
            }
            jTextField1.setText(null);
            jTextField2.setText(null);
            jLabel5.setVisible(false);
        }
        catch (Exception e) {
            System.out.println(e);

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    
    /*
     * jButton2-ADD
     * Used to register clients to the application
     *      -Add to list showing registered clients
     *      -Add to database table clients
     *      -If already registered client is added error message diaplayed.
     *      -Also if invalid ip entered diasplays error
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            if (hcon == null) {
                hcon = ConnectionParameters.getCon();
            }
            
            if (b) {
                //mod
                String ip = jTextField1.getText();
            String name;
            int f = 0;
            java.sql.PreparedStatement st = hcon.prepareStatement("SELECT * FROM CLIENTS");
            ResultSet R = st.executeQuery();
            while (R.next()) {
                name = R.getString("IP");
                if (ip.equals(name)) {
                    f = 1;
                }
            }
            if(f==0){
                //fin
                String insertTableSQL = "INSERT INTO CLIENTS VALUES" + "(?,?,?)";

                PreparedStatement stmt = hcon.prepareStatement(insertTableSQL);
                stmt.setString(1, jTextField1.getText());
                stmt.setString(2, jTextField2.getText());
                stmt.setInt(3, 1);
                stmt.executeUpdate();
                b = false;
                listModel.addElement(jTextField1.getText());
                jTextField1.setText(null);
                jTextField2.setText(null);
                jLabel5.setVisible(false);
            }
             else {
                JOptionPane.showMessageDialog(null, "CLIENT ALREADY REGISTERED");
            }
            
            } else {
                JOptionPane.showMessageDialog(null, "INVALID IP!! enter an valid ip");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /*
     * To get back to page called that is Home page.
     * Disposes REgisterClients.
     */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    /*Main Method
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RegisterClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegisterClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegisterClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegisterClients.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 try {
                    UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel");
                new RegisterClients().setVisible(true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
