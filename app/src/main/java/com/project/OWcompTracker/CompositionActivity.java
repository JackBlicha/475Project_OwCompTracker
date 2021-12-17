package com.project.OWcompTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CompositionActivity extends AppCompatActivity {

   public static final String EXTRA_MAP_ID = "com.project.OWcompTracker.map_id";

   private CompositionDatabase compDB;
   private long mapId;
   private List<Composition> compList;
   private int currentCompositionIndex;
   private ViewGroup showCompositionsLayout;
   private ViewGroup noCompositionsLayout;

   private CompositionAdapter compositionAdapter;
   private RecyclerView recyclerView_composition;
   private Composition selectedComposition;
   private int selectedCompositionPosition = RecyclerView.NO_POSITION;
   private ActionMode actionMode = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_composition);

      // Get map ID of compositions to display
      Intent intent = getIntent();
      mapId = intent.getLongExtra(EXTRA_MAP_ID, 0);

      // Get all compositions for that map
      compDB = CompositionDatabase.getInstance(getApplicationContext());
      compList = compDB.compositionDao().getCompositions(mapId);

      showCompositionsLayout = findViewById(R.id.show_composition_layout);
      noCompositionsLayout = findViewById(R.id.no_composition_layout);


      // Create 1 grid layout column for items
      recyclerView_composition = findViewById(R.id.composition_recycler_view);
      RecyclerView.LayoutManager gridLayoutManager_items =
              new GridLayoutManager(getApplicationContext(), 1);
      recyclerView_composition.setLayoutManager(gridLayoutManager_items);
   }

   @Override
   protected void onStart() {
      super.onStart();

      if (compList.size() == 0) {
         updateAppBarTitle();
         displayComposition(false);
      }
      else {
         updateAppBarTitle();
         displayComposition(true);
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.composition_menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      //  Determine which app bar item was chosen
      if (item.getItemId() == R.id.add) {
         addComposition();
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   public void addCompositionButtonClick(View view) {
      addComposition();
   }

   private void displayComposition(boolean display) {
      if (display) {
         showCompositionsLayout.setVisibility(View.VISIBLE);
         noCompositionsLayout.setVisibility(View.GONE);
      }
      else {
         showCompositionsLayout.setVisibility(View.GONE);
         noCompositionsLayout.setVisibility(View.VISIBLE);
      }
      loadRecyclerView();
   }

   private void updateAppBarTitle() {
      // Display map and number of compositions, in the app bar
      Map map = compDB.mapDao().getMap(mapId);
      String title = getResources().getString(R.string.match_number, map.getText(), compList.size()); // King's Row (3) (modern)
      setTitle(title);
   }

   private void addComposition() {
      Intent intent = new Intent(this, CompositionEditActivity.class);
      intent.putExtra(CompositionEditActivity.EXTRA_MAP_ID, mapId);
      addCompositionResultLauncher.launch(intent);
   }

   private final ActivityResultLauncher<Intent> addCompositionResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
         @Override
         public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
               Intent data = result.getData();
               if (data != null) {
                  long compositionId = data.getLongExtra(CompositionEditActivity.EXTRA_COMPOSITION_ID, -1);
                  Composition newComposition = compDB.compositionDao().getComposition(compositionId);

                  // Add newly created composition to the composition list and show it
                  compList.add(newComposition);

                  // Change layout in case this is the first composition
                  displayComposition(true);

                  Toast.makeText(CompositionActivity.this, R.string.composition_added, Toast.LENGTH_SHORT).show();
               }
            }
         }
      });

   // current version of editComposition for action mode menu edit
   private void editComposition(@NonNull Composition temp_composition) {
      Intent intent = new Intent(this, CompositionEditActivity.class);
      long compositionId = temp_composition.getId();
      intent.putExtra(CompositionEditActivity.EXTRA_COMPOSITION_ID, compositionId);
      editCompositionResultLauncher.launch(intent);
   }

   private final ActivityResultLauncher<Intent> editCompositionResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
         @Override
         public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
               Intent data = result.getData();
               if (data != null) {
                  // Get updated composition
                  long compositionId = data.getLongExtra(CompositionEditActivity.EXTRA_COMPOSITION_ID, -1);
                  Composition updatedComposition = compDB.compositionDao().getComposition(compositionId);

                  // Replace current composition in composition list with updated composition
                  Composition currentComposition = compList.get(currentCompositionIndex);
                  currentComposition.setText(updatedComposition.getText());
                  currentComposition.setAnswer(updatedComposition.getAnswer());
                  currentComposition.setMtank1(updatedComposition.getMtank1());
                  currentComposition.setMdps0(updatedComposition.getMdps0());
                  currentComposition.setMdps1(updatedComposition.getMdps1());
                  currentComposition.setMsupport0(updatedComposition.getMsupport0());
                  currentComposition.setMsupport1(updatedComposition.getMsupport1());

                  Toast.makeText(CompositionActivity.this, R.string.composition_updated, Toast.LENGTH_SHORT).show();
               }
            }
         }
      });


   /**
    * loadRecyclerView()
    * Populates compositions list (recycler view) with compositions for a given map via ID
    */
   public void loadRecyclerView() {
      // Create 1 grid layout column for items
      recyclerView_composition = findViewById(R.id.composition_recycler_view);
      RecyclerView.LayoutManager gridLayoutManager_items =
              new GridLayoutManager(getApplicationContext(), 1);
      recyclerView_composition.setLayoutManager(gridLayoutManager_items);

      // Load compositions here in case settings changed
      compositionAdapter = new CompositionAdapter(loadCompositions());
      recyclerView_composition.setAdapter(compositionAdapter);
   }

   /**
    * loadCompositions()
    * Grabs list of compositions from database.
    *    used for populating recycler views with compositions
    *
    * @return List<Composition> for map with id == mapId
    */
   private List<Composition> loadCompositions() {
      return compDB.mapDao().getCompositions(mapId);
   }


   // dependencies for composition list recycler view population (CompositionHolder, ActionMode.Callback, CompositionAdapter)
   private class CompositionHolder extends RecyclerView.ViewHolder
           implements View.OnClickListener, View.OnLongClickListener {

      private Composition composition;
      private TextView compositionName_display;

      private TextView rating_display;
      private TextView victory_display;

      private ImageView tank0_display;
      private ImageView tank1_display;
      private ImageView dps0_display;
      private ImageView dps1_display;
      private ImageView support0_display;
      private ImageView support1_display;

      public CompositionHolder(LayoutInflater inflater, ViewGroup parent) {
         super(inflater.inflate(R.layout.recycler_view_compositions, parent, false));
         itemView.setOnClickListener(this);
         itemView.setOnLongClickListener(this);

         compositionName_display = itemView.findViewById(R.id.composition_name_display);
         tank0_display = itemView.findViewById(R.id.tank0_display);
         tank1_display = itemView.findViewById(R.id.tank1_display);
         dps0_display = itemView.findViewById(R.id.dps0_display);
         dps1_display = itemView.findViewById(R.id.dps1_display);
         support0_display = itemView.findViewById(R.id.support0_display);
         support1_display = itemView.findViewById(R.id.support1_display);
         rating_display = itemView.findViewById(R.id.rating_display);
         victory_display = itemView.findViewById(R.id.victory_display);
      }

      @SuppressLint("UseCompatLoadingForDrawables")
      @RequiresApi(api = Build.VERSION_CODES.M)
      public void bind(Composition composition, int position) {
         this.composition = composition;
         compositionName_display.setText(composition.getText());


         // Color victory text accordingly
         switch(composition.getMvictory().toLowerCase(Locale.ROOT)){
            case "win":
            case "won":
            case "dub":
            case "victory":
               victory_display.setText(R.string.Victory);
               victory_display.setTextColor(getResources().getColor(R.color.win));
               break;
            case "loss":
            case "lose":
            case "loose":
            case "defeat":
               victory_display.setText(R.string.Defeat);
               victory_display.setTextColor(getResources().getColor(R.color.loss));
               break;
            case "draw":
            case "tie":
               victory_display.setText(R.string.Draw);
               victory_display.setTextColor(getResources().getColor(R.color.draw));
               break;
            default:
               victory_display.setText(R.string.Unknown);
               victory_display.setTextColor(getResources().getColor(R.color.draw));
               break;
         }

         // Set rating text
         String rating = composition.getMrating() + "/5";
         rating_display.setText(rating);
         // // Victory and rating combination (un-used but may switch back)
         // String temp = composition.getMvictory() + "  " + composition.getMrating() + "/5";
         // rating_display.setText(temp);


         // Load hero portrait images in recycler view items
         tank0_display = itemView.findViewById(R.id.tank0_display);
         tank1_display = itemView.findViewById(R.id.tank1_display);
         dps0_display = itemView.findViewById(R.id.dps0_display);
         dps1_display = itemView.findViewById(R.id.dps1_display);
         support0_display = itemView.findViewById(R.id.support0_display);
         support1_display = itemView.findViewById(R.id.support1_display);

         String tank0 = composition.getAnswer();
         String tank1 = composition.getMtank1();
         tank0_display.setImageDrawable(getDrawable(filter_tank(tank0)));
         tank1_display.setImageDrawable(getDrawable(filter_tank(tank1)));

         String dps0 = composition.getMdps0();
         String dps1 = composition.getMdps1();
         dps0_display.setImageDrawable(getDrawable(filter_dps(dps0)));
         dps1_display.setImageDrawable(getDrawable(filter_dps(dps1)));

         String support0 = composition.getMsupport0();
         String support1 = composition.getMsupport1();
         support0_display.setImageDrawable(getDrawable(filter_support(support0)));
         support1_display.setImageDrawable(getDrawable(filter_support(support1)));


         // If this composition in the list is selected
         if (selectedCompositionPosition == position) {
            // Make selected composition stand out
            int selectedColor = getColor(R.color.selected_shadow);
            compositionName_display.setBackgroundColor(selectedColor);
            tank0_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            tank1_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            dps0_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            dps1_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            support0_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            support1_display.setColorFilter(selectedColor, PorterDuff.Mode.SRC_OVER);
            rating_display.setBackgroundColor(selectedColor);
            victory_display.setBackgroundColor(selectedColor);
         }
      }

      @Override
      public void onClick(View view) {
         // Start CompositionActivity with the selected map
         Intent intent = new Intent(CompositionActivity.this, CompositionActivity.class);
         intent.putExtra(CompositionActivity.EXTRA_MAP_ID, composition.getId());
         startActivity(intent);
      }

      @Override
      public boolean onLongClick(View view) {
         if (actionMode != null) {
            return false;
         }

         selectedComposition = composition;
         selectedCompositionPosition = getAbsoluteAdapterPosition();

         // Re-bind the selected item
         compositionAdapter.notifyItemChanged(selectedCompositionPosition);

         // Show the CAB
         actionMode = CompositionActivity.this.startActionMode(actionModeCallback);

         return true;
      }
   }

   /**
    * Given the name of a tank, it returns the drawable resource ID of their icon-portrait
    * @param tank0 // tank name
    * @return drawable resource ID // icon-portrait
    */
   private int filter_tank(String tank0) {
      switch(tank0.toLowerCase(Locale.ROOT)){
         case "dva":
         case "d.va":
            return (R.drawable.dva);
         case "hammond":
            return (R.drawable.hammond);
         case "orisa":
            return (R.drawable.orisa);
         case "reinhardt":
            return (R.drawable.reinhardt);
         case "roadhog":
            return (R.drawable.roadhog);
         case "sigma":
            return (R.drawable.sigma);
         case "winston":
            return (R.drawable.winston);
         case "zarya":
            return (R.drawable.zarya);
         default:
            return filter_tank_jargon(tank0);
      }
   }

   /**
    * Given the name of a dps, it returns the drawable resource ID of their icon-portrait
    * @param dps0 // dps name
    * @return drawable resource ID // icon-portrait drawable ID
    */
   private int filter_dps(String dps0) {
      dps0 = dps0.toLowerCase(Locale.ROOT);
      switch(dps0){
         case "ashe":
            return (R.drawable.ashe);
         case "bastion":
            return (R.drawable.bastion);
         case "cassidy":
            return (R.drawable.cassidy);
         case "doomfist":
            return (R.drawable.doomfist);
         case "echo":
            return (R.drawable.echo);
         case "genji":
            return (R.drawable.genji);
         case "hanzo":
            return (R.drawable.hanzo);
         case "junkrat":
            return (R.drawable.junkrat);
         case "mei":
            return (R.drawable.mei);
         case "pharah":
            return (R.drawable.pharah);
         case "reaper":
            return (R.drawable.reaper);
         case "soldier":
            return (R.drawable.soldier);
         case "sombra":
            return (R.drawable.sombra);
         case "symmetra":
            return (R.drawable.symmetra);
         case "torbjorn":
            return (R.drawable.torbjorn);
         case "tracer":
            return (R.drawable.tracer);
         case "widowmaker":
            return (R.drawable.widowmaker);
         default:
            return filter_dps_jargon(dps0);
      }
   }

   private int filter_support(String support0) {
      support0 = support0.toLowerCase(Locale.ROOT);
      switch(support0){
         case "ana":
            return (R.drawable.ana);
         case "baptiste":
            return (R.drawable.baptiste);
         case "brigitte":
            return (R.drawable.brigitte);
         case "lucio":
            return (R.drawable.lucio);
         case "mercy":
            return (R.drawable.mercy);
         case "moira":
            return (R.drawable.moira);
         case "zenyatta":
            return (R.drawable.zenyatta);
         default:
            return filter_support_jargon(support0);
      }
   }

   private int filter_tank_jargon(String tank){
      tank = tank.toLowerCase(Locale.ROOT);
      switch(tank){
         case "ball":
         case "wrecking ball":
         case "demon": return (R.drawable.hammond);
         case "rein": return (R.drawable.reinhardt);
         case "zar": return (R.drawable.zarya);
         case "hog": return (R.drawable.roadhog);
         case "sig": return (R.drawable.sigma);
         case "monkey": return (R.drawable.winston);
         case "horse": return (R.drawable.orisa);
         default:
            Log.e("HNF", "filter_tank_jargon: tank not found" + tank); return (R.drawable.error_tank);
      }
   }

   private int filter_dps_jargon(String dps){
      dps = dps.toLowerCase(Locale.ROOT);
      switch(dps){
         case "mccree":
         case "cree":
         case "cole":
         case "jesse":
         case "cowboy":
         case "cass": return (R.drawable.cassidy);
         case "windowmaker":
         case "window":
         case "widow": return (R.drawable.widowmaker);
         case "soldier 76":
         case "soldier: 76":
         case "76": return (R.drawable.soldier);
         case "reyes": return (R.drawable.reaper);
         case "sym": return (R.drawable.symmetra);
         case "mie": return (R.drawable.mei);
         default:
            Log.e("HNF", "filter_dps_jargon: dps not found" + dps); return (R.drawable.error_dps);
      }
   }

   private int filter_support_jargon(String support){
      switch(support.toLowerCase(Locale.ROOT)){
         case "brigette":
         case "brig": return (R.drawable.brigitte);
         case "bap": return (R.drawable.baptiste);
         case "zen": return (R.drawable.zenyatta);
         case "frogger": return (R.drawable.lucio);
         default:
            Log.e("HNF", "filter_support_jargon: support not found" + support); return (R.drawable.error_support);
      }
   }

   private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

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
            compDB.compositionDao().deleteComposition(selectedComposition);
            compositionAdapter.removeComposition(selectedComposition);

            // Close the CAB
            mode.finish();
            return true;
         }
         else if (item.getItemId() == R.id.edit){
            editComposition(selectedComposition);

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
         compositionAdapter.notifyItemChanged(selectedCompositionPosition);
         selectedCompositionPosition = RecyclerView.NO_POSITION;
      }
   };

   private class CompositionAdapter extends RecyclerView.Adapter<CompositionHolder> {

      private List<Composition> compositionList;

      public CompositionAdapter(List<Composition> compositions) {
         compositionList = compositions;
      }

      @NonNull
      @Override
      public CompositionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
         return new CompositionHolder(layoutInflater, parent);
      }

      @RequiresApi(api = Build.VERSION_CODES.M)
      @Override
      public void onBindViewHolder(CompositionHolder holder, int position){
         holder.bind(compositionList.get(position), position);
      }

      @Override
      public int getItemCount() {
         return compositionList.size();
      }

//      public void addComposition(Composition composition) {
//         // Add the new composition at the beginning of the list
//         compositionList.add(0, composition);
//
//         // Notify the adapter that item was added to the beginning of the list
//         notifyItemInserted(0);
//
//         // Scroll to the top
//         mRecyclerView_composition.scrollToPosition(0);
//      }

      public void removeComposition(Composition composition) {
         // Find composition in the list
         int index = compositionList.indexOf(composition);
         if (index >= 0) {
            // Remove the composition
            compositionList.remove(index);

            // Notify adapter of composition removal
            notifyItemRemoved(index);
         }
      }
   }
}