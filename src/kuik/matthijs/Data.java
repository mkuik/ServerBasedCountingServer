package kuik.matthijs;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.misc.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by Matthijs Kuik on 4-12-2015.
 */
public class Data {

    static int counter = 0;
    static int max = 50;
    static List<User> users = new ArrayList<User>();
    static String primaryColor = "#1a3669";
    static String secondayColor = "#ffffff";
    static String iconPath = "icon.png";

    public static void setPrimaryColor(final String color) {
        primaryColor = color;
    }
    public static void setSecondayColor(final String color) { secondayColor = color; }
    public static String getPrimaryColor() { return primaryColor; }
    public static String getSecondayColor() { return secondayColor; }
    public static int getCounterValue() {
        return counter;
    }
    public static int getMaxCounterValue() {
        return max;
    }
    public static void editCounterValue( final int value ) {
        counter += value;
    }
    public static List<User> getUsers() {
        return users;
    }
    public static void addUser( final User user ) {
        users.add(user);
    }
    public static boolean isNewUser( final String name ) {
        for (final User user : users ) {
            if (user.getName().compareTo(name) == 0) {
                return false;
            }
        }
        return true;
    }
    public static String getAppPackage() {
        return "dev.kuik.matthijs.serverbasedcounting";
    }
    public static String getIcon() throws IOException {
        File file = new File(iconPath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.encode(bytes);
    }
}
