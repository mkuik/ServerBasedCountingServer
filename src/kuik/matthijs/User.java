package kuik.matthijs;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONObject;

/**
 * Created by Matthijs Kuik on 4-12-2015.
 */
public class User {

    String name;
    int id;
    static int id_counter = 0;
    boolean edit_rights = false;
    boolean admin_rights = false;

    User( final String name ) {
        this.name = name;
        id = id_counter++;
    }

    User( final String name, final int id ) {
        this.name = name;
        this.id = id;
    }

    User(final JSONObject json ) {
        this( json.getString("NAME"), json.getInt("ID") );
    }

    JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("NAME", name);
        json.put("ID", id);
        json.put("EDIT", edit_rights);
        json.put("ADMIN", admin_rights);
        return json;
    }

    public int getID() {
        return id;
    }

    public String getName() { return name; }

    public boolean isEditor() {
        return edit_rights;
    }

    public boolean isAdmīn() {
        return admin_rights;
    }

    public void setEditorRights(boolean edit_rights) {
        this.edit_rights = edit_rights;
    }

    public void setAdminRights(boolean admin_rights) {
        this.admin_rights = admin_rights;
    }
}
