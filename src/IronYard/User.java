package IronYard;

import java.util.ArrayList;

/**
 * Created by scofieldservices on 12/8/16.
 */
public class User {
    int id;
    String name;
    String password;

    public User(){
    }

    public User (String n, String p){
        this.name = n;
        this.password = p;
    }
    public User (int i, String n, String p){
        this.id = i;
        this.name = n;
        this.password = p;
    }
}