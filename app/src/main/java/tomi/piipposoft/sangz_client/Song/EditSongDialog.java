package tomi.piipposoft.sangz_client.Song;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import tomi.piipposoft.sangz_client.R;

/**
 * Created by Domu on 21-May-16.
 */
public class EditSongDialog extends DialogFragment {


    public interface NoticeDialogListener {
        void onDialogPositiveClick(String newName);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener myListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            myListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edit_song, null);
//        final EditText dialogEditText = (EditText) view.findViewById(R.id.edittext_dailypowerlistname);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_song_name);

        builder.setView(view)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.d("EditSongDialog", "now we should send PUT!");
                        myListener.onDialogPositiveClick(editText.getText().toString());

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        EditSongDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

//public class FireMissilesDialogFragment extends DialogFragment {
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(R.string.dialog_fire_missiles)
//                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }
//}
