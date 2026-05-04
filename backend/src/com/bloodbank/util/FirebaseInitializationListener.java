package com.bloodbank.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FirebaseInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🌐 LifeFlow: Starting Global Firebase Initialization...");
        try {
            FirebaseConfig.getFirestore();
            System.out.println("✅ LifeFlow: Global Firebase Initialization SUCCESS!");
        } catch (Exception e) {
            System.err.println("❌ LifeFlow: Global Firebase Initialization FAILED!");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🌐 LifeFlow: Cleaning up Firebase connections...");
    }
}
