package rana.sumit.attendancesystem.Utils;

import java.io.Serializable;

/**
 * Created by ranaf on 5/3/2016.
 */
public class User implements Serializable {
    private String firstName, lastName, email;

    public String getFirstname() {
        return firstName;
    }

    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
