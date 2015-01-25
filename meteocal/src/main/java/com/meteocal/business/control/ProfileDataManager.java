package com.meteocal.business.control;

import com.meteocal.business.entity.User;

/**
 * FROM/TO - B:RegistrationPage TO - E:User
 *
 * @author Manuel
 */
public class ProfileDataManager {

    /**
     * It creates a new user, giving the previously submitted and checked data.
     *
     * @param first first name
     * @param last last name
     * @param username user name
     * @param email email
     * @param password password
     * @return User saved in db, null
     */
    public User newUser(String first, String last, String username, String email, String password) {
        return new User(username, first, last, email, password, true);
    }
}
