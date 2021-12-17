package com.project.OWcompTracker;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserClass.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

   private static final String DATABASE_NAME = "users.db";

   private static UserDatabase mUserDatabase;

   // Singleton
   public static UserDatabase getInstance(Context context) {
      // Enforce Singleton Pattern
      if (mUserDatabase == null) {
         // Create if doesn't exist
         mUserDatabase = Room.databaseBuilder(context, UserDatabase.class, DATABASE_NAME)
                 .fallbackToDestructiveMigration()
                 .allowMainThreadQueries()
                 .build();
      }

      return mUserDatabase;
   }

   public abstract UserDao UserDao();
}
