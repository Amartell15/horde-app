package Horde;

import java.io.Serializable;

public class User implements Serializable {
    String name;
    String email;
    int orderIndex;

    boolean isActive;
    public User(String email, int orderIndex, boolean isActive, String name) {
        this.name = name;
        this.email = email;
        this.orderIndex = orderIndex;
        this.isActive = isActive;
    }

    public int getOrderIndex() {
        return orderIndex;
    }
}
