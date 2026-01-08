package org.example.service;

import org.example.client.Group;
import org.example.client.GroupService;

public class GroupServiceClient {

    private Group port;

    public GroupServiceClient() {
        try {
            GroupService service = new GroupService();
            this.port = service.getGroupPort();
        } catch (Exception e) {
            System.err.println("Error initializing web service client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getStudentsXml() {
        try {
            return port.getStudents();
        } catch (Exception e) {
            System.out.println(e);
            return "<error>" + e.getMessage() + "</error>";
        }
    }

    public int getNext(int i) {
        try {
            return port.getnext(i);
        } catch (Exception e) {
            return -1;
        }
    }
}
