package com.example.keychainapp.startup;


import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        
            
                // Startup startup = new Startup();
                Startup.configureService("KeychainApp", "keystore-password");
                Startup.tests();

            
        
    }
}