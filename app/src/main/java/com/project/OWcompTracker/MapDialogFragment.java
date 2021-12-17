package com.project.OWcompTracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MapDialogFragment extends DialogFragment {

   public interface OnMapEnteredListener {
      void onMapEntered(String map);
   }

   private OnMapEnteredListener listener;

   @NonNull
   @Override
   public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
      final EditText mapEditText = new EditText(requireActivity());
      mapEditText.setInputType(InputType.TYPE_CLASS_TEXT);
      mapEditText.setMaxLines(1);

      return new AlertDialog.Builder(requireActivity())
         .setTitle(R.string.new_map)
         .setView(mapEditText)
         .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
              // Notify listener
              String map = mapEditText.getText().toString();
              listener.onMapEntered(map.trim());
           }
         })
         .setNegativeButton(R.string.cancel, null)
         .create();
   }

   @Override
   public void onAttach(@NonNull Context context) {
      super.onAttach(context);
      listener = (OnMapEnteredListener) context;
   }

   @Override
   public void onDetach() {
      super.onDetach();
      listener = null;
   }
}

