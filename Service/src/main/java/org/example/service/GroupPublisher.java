package org.example.service;

import jakarta.xml.ws.Endpoint;

public class GroupPublisher {
    public static void main(String[] args) {
        String url = "http://localhost:8888/myserv";
        System.out.println("Publishing service...");
        try {
            Endpoint.publish(url, new Group());
            System.out.println("Service published at: " + url);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}