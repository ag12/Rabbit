package amgh.no.rabbitapp.user;

import com.parse.ParseUser;

public class User extends ParseUser {
    public User(String username, String password, String email) {
        super();
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }
}
