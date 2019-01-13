/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.administrator.DBConnection;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author MY PC
 */
public class ConnectionParameters implements Serializable {

    public static Connection con = null;

    /**
     * @return the con
     */
    public static Connection getCon() {

        if (con == null) {
            setCon();
        }


        return con;
    }

    /**
     * @param aCon the con to set
     */
    public static void setCon() {
        try {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection aCon = DriverManager.getConnection("jdbc:mysql://localhost:3308/admindetails", "root", "raset");
                con = aCon;
            } catch (Exception ex) {
                Logger.getLogger(ConnectionParameters.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(ConnectionParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
