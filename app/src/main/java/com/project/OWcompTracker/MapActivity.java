package com.project.OWcompTracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.graphics.Color;

public class MapActivity extends AppCompatActivity
        implements MapDialogFragment.OnMapEnteredListener {

   private CompositionDatabase compDB;
   private MapAdapter mapAdapter;
   private RecyclerView recyclerView;
   private int[] mapColors;
   private Map selectedMap;
   private int selectedMapPosition = RecyclerView.NO_POSITION;
   private ActionMode actionMode = null;
   private SharedPreferences sharedPrefs;

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      // Change the theme if preference is true
      sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
      boolean darkTheme = sharedPrefs.getBoolean("dark_theme", false);
      if (darkTheme) {
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      }

      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_map);

      // Singleton
      compDB = CompositionDatabase.getInstance(getApplicationContext());

      mapColors = getResources().getIntArray(R.array.mapColors);

      // Create 2 grid layout columns for maps
      recyclerView = findViewById(R.id.map_recycler_view);
      RecyclerView.LayoutManager gridLayoutManager =
              new GridLayoutManager(getApplicationContext(), 2);
      recyclerView.setLayoutManager(gridLayoutManager);
   }

   @Override
   protected void onResume() {
      super.onResume();

      // Load maps here in case settings changed
      mapAdapter = new MapAdapter(loadMaps());
      recyclerView.setAdapter(mapAdapter);
   }

   @Override
   public void onMapEntered(String mapText) {
      if (mapText.length() > 0) {
         Map map = new Map(mapText);
         long mapId = compDB.mapDao().insertMap(map);
         map.setId(mapId);

         // Add map to RecyclerView
         mapAdapter.addMap(map);

         Toast.makeText(this, "Added " + mapText, Toast.LENGTH_SHORT).show();
      }
   }

   public void addMapClick(View view) {
      FragmentManager manager = getSupportFragmentManager();
      MapDialogFragment dialog = new MapDialogFragment();
      dialog.show(manager, "mapDialog");
   }

   private List<Map> loadMaps() {
      String order = sharedPrefs.getString("map_order", "alpha");
      if (order.equals("alpha")) {
         return compDB.mapDao().getMaps();
      }
      else if (order.equals("new_first")) {
         return compDB.mapDao().getMapsNewerFirst();
      }
      else {
         return compDB.mapDao().getMapsOlderFirst();
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.map_menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle item selection
      if (item.getItemId() == R.id.settings) {
         Intent intent = new Intent(this, SettingsActivity.class);
         startActivity(intent);
         return true;
      } else if (item.getItemId() == R.id.twitter) {
         Intent intent = new Intent(this, TwitterActivity.class);
         startActivity(intent);
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   private class MapHolder extends RecyclerView.ViewHolder
           implements View.OnClickListener, View.OnLongClickListener {

      private Map map;
      private TextView textView;

      public MapHolder(LayoutInflater inflater, ViewGroup parent) {
         super(inflater.inflate(R.layout.recycler_view_maps, parent, false));
         itemView.setOnClickListener(this);
         itemView.setOnLongClickListener(this);
         textView = itemView.findViewById(R.id.map_text_view);
      }

      @SuppressLint("UseCompatLoadingForDrawables")
      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      public void bind(Map map, int position) {
         this.map = map;
         String mapName = map.getText();
         textView.setText(mapName);

         if (selectedMapPosition == position) {
            // Make selected map stand out
            textView.setBackgroundColor(Color.RED);
         }
         else {
            // Make the background image dependent on the map name in the map string
            switch (mapName.toLowerCase(Locale.ROOT)) {
               case "blizzard-world":
               case "blizzard world":
                  textView.setBackground(getDrawable(R.drawable.blizzard_world));
                  break;
               case "busan":
                  textView.setBackground(getDrawable(R.drawable.busan));
                  break;
               case "dorado":
                  textView.setBackground(getDrawable(R.drawable.dorado));
                  break;
               case "eichenwalde":
               case "eichenvalde":
                  textView.setBackground(getDrawable(R.drawable.eichenwalde));
                  break;
               case "hanamura":
                  textView.setBackground(getDrawable(R.drawable.hanamura));
                  break;
               case "hollywood":
                  textView.setBackground(getDrawable(R.drawable.hollywood));
                  break;
               case "horizon_lunar_colony":
               case "horizon lunar colony":
               case "horizon":
                  textView.setBackground(getDrawable(R.drawable.horizon_lunar_colony));
                  break;
               case "ilios":
               case "illios":
                  textView.setBackground(getDrawable(R.drawable.ilios));
                  break;
               case "junkertown":
                  textView.setBackground(getDrawable(R.drawable.junkertown));
                  break;
               case "kings_row":
               case "kings row":
               case "king's row":
                  textView.setBackground(getDrawable(R.drawable.kings_row));
                  break;
               case "lijiang_tower":
               case "lijiang tower":
               case "lijiang":
                  textView.setBackground(getDrawable(R.drawable.lijiang_tower));
                  break;
               case "nepal":
                  textView.setBackground(getDrawable(R.drawable.nepal));
                  break;
               case "numbani":
                  textView.setBackground(getDrawable(R.drawable.numbani));
                  break;
               case "oasis":
               case "university":
                  textView.setBackground(getDrawable(R.drawable.oasis));
                  break;
               case "paris":
               case "bad":
                  textView.setBackground(getDrawable(R.drawable.paris));
                  break;
               case "rialto":
                  textView.setBackground(getDrawable(R.drawable.rialto));
                  break;
               case "route_66":
               case "route 66":
                  textView.setBackground(getDrawable(R.drawable.route_66));
                  break;
               case "temple_of_anubis":
               case "temple of anubis":
               case "anubis":
                  textView.setBackground(getDrawable(R.drawable.temple_of_anubis));
                  break;
               case "volskaya_industries":
               case "volskaya industries":
               case "volskaya":
                  textView.setBackground(getDrawable(R.drawable.volskaya_industries));
                  break;
               case "watchpoint_gibraltar":
               case "watchpoint: gibraltar":
               case "watchpoint gibraltar":
               case "gibraltar":
                  textView.setBackground(getDrawable(R.drawable.watchpoint_gibraltar));
                  break;
               case "havana":
                  textView.setBackground(getDrawable(R.drawable.havana));
                  break;
               default:
                  // Map unknown atm
                  // so make the background color dependent on the length of the map's name
                  int colorIndex = map.getText().length() % mapColors.length;
                  textView.setBackgroundColor(mapColors[colorIndex]);
                  break;
            }
         }
      }

      @Override
      public void onClick(View view) {
         // Start CompositionActivity with the selected map
         Intent intent = new Intent(MapActivity.this, CompositionActivity.class);
         intent.putExtra(CompositionActivity.EXTRA_MAP_ID, map.getId());
         startActivity(intent);
      }

      @Override
      public boolean onLongClick(View view) {
         if (actionMode != null) {
            return false;
         }

         selectedMap = map;
         selectedMapPosition = getAbsoluteAdapterPosition();

         // Re-bind the selected item
         mapAdapter.notifyItemChanged(selectedMapPosition);

         // Show the CAB
         actionMode = MapActivity.this.startActionMode(mActionModeCallback);

         return true;
      }
   }

   private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

      @Override
      public boolean onCreateActionMode(ActionMode mode, Menu menu) {
         // Provide context menu for CAB
         MenuInflater inflater = mode.getMenuInflater();
         inflater.inflate(R.menu.context_menu, menu);
         return true;
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
         return false;
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
         // Process action item selection
         if (item.getItemId() == R.id.delete) {
            // Delete from the database and remove from the RecyclerView
            compDB.mapDao().deleteMap(selectedMap);
            mapAdapter.removeMap(selectedMap);

            // Close the CAB
            mode.finish();
            return true;
         } else if (item.getItemId() == R.id.settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);

            // Close the CAB
            mode.finish();
            return true;
        }

         return false;
      }

      @Override
      public void onDestroyActionMode(ActionMode mode) {
         actionMode = null;

         // CAB closing, need to deselect item if not deleted
         mapAdapter.notifyItemChanged(selectedMapPosition);
         selectedMapPosition = RecyclerView.NO_POSITION;
      }
   };


   private class MapAdapter extends RecyclerView.Adapter<MapHolder> {

      private List<Map> mapList;

      public MapAdapter(List<Map> maps) {
         mapList = maps;
      }

      @NonNull
      @Override
      public MapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
         return new MapHolder(layoutInflater, parent);
      }

      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onBindViewHolder(MapHolder holder, int position){
         holder.bind(mapList.get(position), position);
      }

      @Override
      public int getItemCount() {
         return mapList.size();
      }

      public void addMap(Map map) {
         // Add the new map at the beginning of the list
         mapList.add(0, map);

         // Notify the adapter that item was added to the beginning of the list
         notifyItemInserted(0);

         // Scroll to the top
         recyclerView.scrollToPosition(0);
      }

      public void removeMap(Map map) {
         // Find map in the list
         int index = mapList.indexOf(map);
         if (index >= 0) {
            // Remove the map
            mapList.remove(index);

            // Notify adapter of map removal
            notifyItemRemoved(index);
         }
      }
   }
}

