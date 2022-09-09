package Horde;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.Serializable;
import java.util.Collection;


public class Database implements Serializable {
    private static DB instance = DBMaker.fileDB("HordeDB").make(); //instance of database
    private static HTreeMap<String, User> usersMap = (HTreeMap<String, User>) instance.hashMap("usersMap").createOrOpen();

    static void newUser(String name, User user) {
        usersMap.put(name, user);
    }

    static Collection<User> getUsers() {
        return usersMap.getValues();
    }

    static HTreeMap<String, User> getUsersMap() {
        return usersMap;
    }

    static boolean isActive(String name) {
        if (usersMap.containsKey(name)) {
            return usersMap.get(name).isActive;
        }
        return false;
    }


    static void deleteUser(User user) {
        usersMap.remove(user.name);
    }

    static void close() {
        instance.close();
    }


}
