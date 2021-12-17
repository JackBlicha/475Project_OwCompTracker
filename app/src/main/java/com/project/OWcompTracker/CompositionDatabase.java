package com.project.OWcompTracker;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Composition.class, Map.class}, version = 1)
public abstract class CompositionDatabase extends RoomDatabase {

   private static final String DATABASE_NAME = "composition.db";

   private static CompositionDatabase mCompositionDatabase;

   // Singleton
   public static CompositionDatabase getInstance(Context context) {
      if (mCompositionDatabase == null) {
         mCompositionDatabase = Room.databaseBuilder(context, CompositionDatabase.class, DATABASE_NAME)
                 .allowMainThreadQueries()
                 .fallbackToDestructiveMigration()
                 .build();
         mCompositionDatabase.addStarterData();
      }

      return mCompositionDatabase;
   }

   public abstract CompositionDao compositionDao();
   public abstract MapDao mapDao();

   private void addStarterData() {

      // Add a few maps and compositions if database is empty
      if (mapDao().getMaps().isEmpty()) {

         // Execute code on a background thread
         runInTransaction(() -> {
            Map map = new Map("King's Row");
            long mapId = mapDao().insertMap(map);

            Composition composition = new Composition();
            composition.setText("King's Row Classic");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("reinhardt");
            composition.setMtank1("zarya");
            composition.setMdps0("symmetra");
            composition.setMdps1("cassidy");
            composition.setMsupport0("ana");
            composition.setMsupport1("zenyatta");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            composition = new Composition();
            composition.setText("Rein+ (last point)");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("reinhardt");
            composition.setMtank1("dva");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("ana");
            composition.setMsupport1("zenyatta");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            composition = new Composition();
            composition.setText("Double Shield (last point)");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("orisa");
            composition.setMtank1("sigma");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("baptiste");
            composition.setMsupport1("ana");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            map = new Map("Havana");
            mapId = mapDao().insertMap(map);

            composition = new Composition();
            composition.setText("Double Shield");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("orisa");
            composition.setMtank1("sigma");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("baptiste");
            composition.setMsupport1("ana");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            map = new Map("Busan");
            mapId = mapDao().insertMap(map);

            composition = new Composition();
            composition.setText("Double Shield");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("orisa");
            composition.setMtank1("sigma");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("baptiste");
            composition.setMsupport1("ana");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            composition = new Composition();
            composition.setText("Rein+ (last point)");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("reinhardt");
            composition.setMtank1("dva");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("ana");
            composition.setMsupport1("zenyatta");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            composition = new Composition();
            composition.setText("Double Shield (last point)");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("orisa");
            composition.setMtank1("sigma");
            composition.setMdps0("soldier");
            composition.setMdps1("cassidy");
            composition.setMsupport0("baptiste");
            composition.setMsupport1("ana");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);

            map = new Map("Hanamura");
            mapId = mapDao().insertMap(map);

            composition = new Composition();
            composition.setText("Double Shield");
            composition.setMvictory("Victory");
            composition.setMrating("3");
            composition.setAnswer("orisa");
            composition.setMtank1("sigma");
            composition.setMdps0("junkrat");
            composition.setMdps1("ashe");
            composition.setMsupport0("ana");
            composition.setMsupport1("mercy");
            composition.setmMapId(mapId);
            compositionDao().insertComposition(composition);
         });
      }
   }
}
