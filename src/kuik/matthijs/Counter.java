package kuik.matthijs;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.json.JSONObject;
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
public class Counter {

    int counter = 0;
    int max = 50;
    List<User> users = new ArrayList<User>();
    String primaryColor;
    String secondayColor;
    String iconPath;
    String password;

    public Counter() {
        this(0, 0, "#000000", "#ffffff", "", "");
    }

    public Counter(int counter, int max, String primaryColor, String secondayColor, String iconPath, String password) {
        this.counter = counter;
        this.max = max;
        this.primaryColor = primaryColor;
        this.secondayColor = secondayColor;
        this.iconPath = iconPath;
        this.password = password;
    }

    public JSONObject getServerMeta() {
        JSONObject json = new JSONObject();
        json.put("color1", getPrimaryColor());
        json.put("color2", getSecondayColor());
        try {
            json.put("icon", getIcon());
        } catch (IOException e) {
            json.put("icon", "");
        }
        json.put("count", counter);
        json.put("max", max);
        json.put("password", password);
        return json;
    }

    public void setPrimaryColor(final String color) {
        primaryColor = color;
    }
    public void setSecondayColor(final String color) { secondayColor = color; }
    public String getPrimaryColor() { return primaryColor; }
    public String getSecondayColor() { return secondayColor; }
    public int getCounterValue() {
        return counter;
    }
    public int getMaxCounterValue() {
        return max;
    }
    public void editCounterValue( final int value ) {
        counter += value;
    }
    public List<User> getUsers() {
        return users;
    }
    public void addUser( final User user ) {
        users.add(user);
    }
    public boolean isNewUser( final String name ) {
        for (final User user : users ) {
            if (user.getName().compareTo(name) == 0) {
                return false;
            }
        }
        return true;
    }
    public String getIcon() throws IOException {
        File file = new File(iconPath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.encode(bytes);
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
