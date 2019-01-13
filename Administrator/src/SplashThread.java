/*
 * SplashThread
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import javax.swing.JProgressBar;

/*
 * SplashThread used to display progressbar
 *
 * @author AnchuAkhila
 */
public class SplashThread extends Thread {

    private boolean flag = true;
    private int count = 2;
    private JProgressBar jpb;
    private JFrame screen;
    /*
     * Method to display progressbar
     */

    public SplashThread(JProgressBar jProgressBar1, JFrame jFrame) {
        this.jpb = jProgressBar1;
        this.screen = jFrame;

    }

    @Override
    /*
     * run method of thread
     * to display progressbar
     */
    public void run() {
        while (flag) {
            if (count <= 100) {
                // System.out.println("Thread --->  "+ count);
                this.jpb.setValue(count);

                this.count += 2;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SplashThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                jpb.setValue(0);
                this.screen.dispose();
                this.flag = false;

                WelcomeAdmin.main(null);
            }
        }
    }
}
