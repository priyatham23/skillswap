package com.skillswap;

import com.skillswap.controller.ApiController;
import com.skillswap.controller.StaticFileHandler;
import com.skillswap.repository.DatabaseManager;
import com.skillswap.repository.HybridDataRepository;
import com.skillswap.service.*;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=================================================");
            System.out.println("   SkillSwap - Campus Peer Learning & Barter");
            System.out.println("   Enterprise Java OOP Backend + MySQL DB Ready");
            System.out.println("=================================================");

            int port = 8080;
            String envPort = System.getenv("PORT");
            if (envPort != null && !envPort.trim().isEmpty()) {
                try {
                    port = Integer.parseInt(envPort.trim());
                } catch (NumberFormatException ignored) {}
            } else {
                File cfg = new File("config.properties");
                if (cfg.exists()) {
                    Properties p = new Properties();
                    try (FileInputStream fis = new FileInputStream(cfg)) {
                        p.load(fis);
                        port = Integer.parseInt(p.getProperty("server.port", "8080"));
                    } catch (Exception ignored) {}
                }
            }

            // Initialize Repository (Hybrid with MySQL persistence & In-Memory/JSON Snapshot)
            HybridDataRepository repository = HybridDataRepository.getInstance();

            // Dependency Injection of Services (OOP Inversion of Control)
            AuthService authService = new AuthService(repository);
            CreditService creditService = new CreditService(repository);
            UserService userService = new UserService(repository);
            SkillService skillService = new SkillService(repository);
            MatchingService matchingService = new MatchingService(repository);
            SessionService sessionService = new SessionService(repository, creditService);
            CommunityService communityService = new CommunityService(repository);
            AdminService adminService = new AdminService(repository);

            // Controllers
            ApiController apiController = new ApiController(
                authService, userService, skillService, matchingService,
                creditService, sessionService, communityService, adminService
            );
            StaticFileHandler staticFileHandler = new StaticFileHandler("web");

            // HTTP Server (binds to 0.0.0.0 for containerized and cloud deployment)
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/api", apiController);
            server.createContext("/", staticFileHandler);
            server.setExecutor(Executors.newFixedThreadPool(20));

            server.start();

            System.out.println(" [OK] Server running at: http://localhost:" + port);
            System.out.println(" [OK] MySQL Status: " + DatabaseManager.getInstance().getStatusMessage());
            System.out.println(" ------------------------------------------------");
            System.out.println(" Demo Accounts Ready for instant login:");
            System.out.println("   1. Priya Sharma (Pro Frontend): priya@skillswap.edu / priya123");
            System.out.println("   2. Rahul Verma (ML / Python):   rahul@skillswap.edu / rahul123");
            System.out.println("   3. Ananya Patel (Java / DSA):   ananya@skillswap.edu / ananya123");
            System.out.println("   4. Campus Admin:                admin@skillswap.edu / admin123");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("[FATAL] Failed to start SkillSwap server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

