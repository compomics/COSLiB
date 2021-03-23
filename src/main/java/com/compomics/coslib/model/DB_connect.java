package com.compomics.coslib.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Genet
 */
public class DB_connect {

    public static void main(String args[]) {
        Statement stmt = null;
        ResultSet rs = null;
        Connection con = null;
        String query = "INSERT INTO Customers (precursor_mss, memory_position, sequence, modification, charge) "
                + "VALUES (13.5, 567, 'ertfgdg', 'mod', '4+')";

        System.out.println("printing text here");
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations    
//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/index_db", "root", "admin");
            System.out.println("printing text here");
            if (con != null) {
                System.out.println("Successfully connected to MySQL database test");
            } else {
                System.out.println("not connected to MySQL database test");
            }
//            rs = stmt.executeQuery(query);
//            while (rs.next()) {
//                int count = rs.getInt(1);
//                System.out.println("count of stock : " + count);
//            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }
}
