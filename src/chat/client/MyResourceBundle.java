/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author m_hus
 */
public class MyResourceBundle {

    public static void main(String[] args) {
        try ( var input = MyResourceBundle.class.getClassLoader().getResourceAsStream("data.properties")) {
            var prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find data.properties");
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            // print everything
            prop.forEach((k, v) -> System.out.println(k + "=" + v));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}