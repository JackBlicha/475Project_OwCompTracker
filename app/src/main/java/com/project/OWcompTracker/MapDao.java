package com.project.OWcompTracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MapDao {
   @Query("SELECT * FROM Map WHERE id = :id")
   public Map getMap(long id);

   @Query("SELECT * FROM Map WHERE text = :mapText")
   public Map getMapByText(String mapText);

   @Query("SELECT * FROM Map ORDER BY text COLLATE NOCASE")
   public List<Map> getMaps();

   @Query("SELECT * FROM Composition WHERE map_id = :mapId ORDER BY id")
   public List<Composition> getCompositions(long mapId);

   @Query("SELECT * FROM Map ORDER BY updated DESC")
   public List<Map> getMapsNewerFirst();

   @Query("SELECT * FROM Map ORDER BY updated ASC")
   public List<Map> getMapsOlderFirst();

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   public long insertMap(Map map);

   @Update
   public void updateMap(Map map);

   @Update
   public void updateMapName(Map map);

   @Delete
   public void deleteMap(Map map);
}

