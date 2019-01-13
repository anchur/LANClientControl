/**
 *
 * @author Anchu Akhila
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import javax.swing.AbstractButton;
import javax.swing.*;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
/*
 * Splash screen has a progress bar whose progression is controlled by a splash thread
 */
public class SplashScreen {

    private JLabel jLabel1, jLabel2;
    public JFrame jFrame;
    private JPanel jPanel1;
    private JProgressBar jProgressBar1;
    private SplashThread st;

    public void buildGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        //-- initializing
        jFrame = new JFrame();
        jPanel1 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        //----------- JFrame design


        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setBackground(new java.awt.Color(51, 51, 51));

        jFrame.setSize(new java.awt.Dimension(878, 340));
        jFrame.getContentPane().setLayout(new AbsoluteLayout());
        jFrame.setResizable(false);

        //------------- JPanel design

        jPanel1.setSize(new java.awt.Dimension(978, 600));
        //jPanel1.setMinimumSize(new java.awt.Dimension(678, 400));
        //jPanel1.setPreferredSize(new java.awt.Dimension(678, 400));
        jPanel1.setLayout(new AbsoluteLayout());

        //----------- JLabel2 design
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FIR.jpg"))); // NOI18N
        jLabel2.setSize(new java.awt.Dimension(978, 600));
        jPanel1.add(jLabel2, new AbsoluteConstraints(-3, -3, 890, 300));

        //-------------- JProgressbar design
        jProgressBar1.setBackground(Color.WHITE);
        jProgressBar1.setForeground(Color.BLACK);
        jProgressBar1.setStringPainted(true);
        jPanel1.add(jProgressBar1, new AbsoluteConstraints(180, 240, 250, 15));

        jFrame.getContentPane().add(jPanel1, new AbsoluteConstraints(5, 5, -1, -1));
        jFrame.setLocationRelativeTo(null);
        jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        removeMinMaxClose(jFrame);

        jFrame.setVisible(true);

    }

    private void formWindowOpened(WindowEvent evt) {
        st = new SplashThread(jProgressBar1, jFrame);
        st.start();
    }

    public void removeMinMaxClose(Component comp) {
        if (comp instanceof AbstractButton) {
            comp.getParent().remove(comp);
        }
        if (comp instanceof Container) {
            Component[] comps = ((Container) comp).getComponents();
            for (int x = 0, y = comps.length; x < y; x++) {
                removeMinMaxClose(comps[x]);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                 new SplashScreen().buildGUI();
               
                
            }
        });
    }
}
