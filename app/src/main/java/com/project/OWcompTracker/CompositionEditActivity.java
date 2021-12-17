package com.project.OWcompTracker;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CompositionEditActivity extends AppCompatActivity {

   public static final String EXTRA_COMPOSITION_ID = "com.project.OWcompTracker.composition_id";
   public static final String EXTRA_MAP_ID = "com.project.OWcompTracker.map_id";

   private EditText compositionText;
   private EditText victoryText;
   private EditText ratingText;

   private EditText tank0Text;
   private EditText tank1Text;
   private EditText dps0Text;
   private EditText dps1Text;
   private EditText support0Text;
   private EditText support1Text;

   private CompositionDatabase compDB;
   private long compositionId;
   private Composition mComposition;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_composition_edit);

      compositionText = findViewById(R.id.composition_edit_text);

      victoryText = findViewById(R.id.victory_edit_text);
      ratingText = findViewById(R.id.rating_edit_text);

      tank0Text = findViewById(R.id.answer_edit_text);
      tank1Text = findViewById(R.id.tank1_edit_text);
      dps0Text = findViewById(R.id.dps0_edit_text);
      dps1Text = findViewById(R.id.dps1_edit_text);
      support0Text = findViewById(R.id.support0_edit_text);
      support1Text = findViewById(R.id.support1_edit_text);

      compDB = CompositionDatabase.getInstance(getApplicationContext());

      // Get composition ID from CompositionActivity
      Intent intent = getIntent();
      compositionId = intent.getLongExtra(EXTRA_COMPOSITION_ID, -1);

      if (compositionId == -1) {
         // Add new composition
         mComposition = new Composition();
         long mapId = intent.getLongExtra(EXTRA_MAP_ID, 0);
         mComposition.setmMapId(mapId);


         setTitle(R.string.add_composition);
      }
      else {
         // Update existing composition
         mComposition = compDB.compositionDao().getComposition(compositionId);
         compositionText.setText(mComposition.getText());

         victoryText.setText(mComposition.getMvictory());
         ratingText.setText(mComposition.getMrating());

         tank0Text.setText(mComposition.getAnswer());
         tank1Text.setText(mComposition.getMtank1());
         dps0Text.setText(mComposition.getMdps0());
         dps1Text.setText(mComposition.getMdps1());
         support0Text.setText(mComposition.getMsupport0());
         support1Text.setText(mComposition.getMsupport1());

         setTitle(R.string.update_composition);
      }
   }

   public void saveButtonClick(View view) {

      mComposition.setText(compositionText.getText().toString());

      mComposition.setMvictory(victoryText.getText().toString());
      mComposition.setMrating(ratingText.getText().toString());

      mComposition.setAnswer(tank0Text.getText().toString());
      mComposition.setMtank1(tank1Text.getText().toString());
      mComposition.setMdps0(dps0Text.getText().toString());
      mComposition.setMdps1(dps1Text.getText().toString());
      mComposition.setMsupport0(support0Text.getText().toString());
      mComposition.setMsupport1(support1Text.getText().toString());

      if (compositionId == -1) {
         // New composition
         long newId = compDB.compositionDao().insertComposition(mComposition);
         mComposition.setId(newId);
      }
      else {
         // Existing composition
         compDB.compositionDao().updateComposition(mComposition);
      }

      // Return composition ID
      Intent intent = new Intent();
      intent.putExtra(EXTRA_COMPOSITION_ID, mComposition.getId());
      setResult(RESULT_OK, intent);
      finish();
   }
}
