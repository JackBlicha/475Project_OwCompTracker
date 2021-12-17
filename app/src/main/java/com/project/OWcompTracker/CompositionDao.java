package com.project.OWcompTracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CompositionDao {
   @Query("SELECT * FROM Composition WHERE id = :id")
   public Composition getComposition(long id);

   @Query("SELECT * FROM Composition WHERE map_id = :mapId ORDER BY id")
   public List<Composition> getCompositions(long mapId);

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   public long insertComposition(Composition composition);

   @Update
   public void updateComposition(Composition composition);

   @Delete
   public void deleteComposition(Composition composition);
}

