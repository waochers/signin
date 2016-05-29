package project.stutisrivastava.waochers.model;

/**
 * Created by stutisrivastava on 10/12/15.
 */
public class Customer {

    private String TAG = "Customer";

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    public String getId() {
        if(id==null){                               //gives user a random id if logging in through ID and Password.
           int randId =  (int) Math.floor(Math.random() * (999999 - 100000 + 1)) + 100000;
            id="n"+randId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return id+", "+name+", "+email+", "+phoneNumber+", "+password;
    }
}
