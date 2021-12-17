package com.project.OWcompTracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    // allow users with matching usernames
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(UserClass user);

    @Query("SELECT * FROM users WHERE username=(:username) AND password=(:password)")
    UserClass login(String username, String password);

    // Disabled for security reasons
//    @Query("SELECT * FROM users ORDER BY username COLLATE NOCASE")
//    List<UserClass> getUsers();
}
