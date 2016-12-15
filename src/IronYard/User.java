package IronYard;

import java.util.ArrayList;

/**
 * Created by scofieldservices on 12/8/16.
 */
public class User {
    String name;
    String password;
    ArrayList<Film> films = new ArrayList<>();

    public User (String n, String p){

        this.name = n;
        this.password = p;
    }
}