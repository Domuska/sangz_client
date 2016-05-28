package tomi.piipposoft.sangz_client.Songs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import tomi.piipposoft.sangz_client.R;

/**
 * Fragment that is shown when add song button is clicked in SongListActivity, calls
 * interface onDialogPositiveClick when ok is clicked, and arraylist that contains
 * text from all the text fields is given as parameter
 *
 * 1st element is song name
 * 2nd element is media location
 */
public class AddSongDialog extends DialogFragment{

    public interface NoticeDialogListener {
        void onDialogPositiveClick(ArrayList<String> textInputs);
    }

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
    public Dialog onCreateDialog(Bundle savedInstanceState){


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayList<String> inputs = new ArrayList<>();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_song, null);

        final TextInputEditText songNameEditText = (TextInputEditText) view.findViewById(R.id.dialog_add_song_name);
        final TextInputEditText songLocationEditText = (TextInputEditText) view.findViewById(R.id.dialog_add_media_location);



        builder.setView(view)
                .setMessage("Add new song")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputs.add(songNameEditText.getText().toString());
                        inputs.add(songLocationEditText.getText().toString());
                        myListener.onDialogPositiveClick(inputs);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddSongDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
