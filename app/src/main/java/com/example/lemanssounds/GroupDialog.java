package com.example.lemanssounds;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * class for group window display
 */
public class GroupDialog extends DialogFragment implements OnClickListener {

    private String temp1, temp2, temp3;
    private SuperBubble s;
    private ImageView img;
    private TextView txtVw, txtVw2;
    private  Button btn;
    /**
     * can be created only with SuperBubble object - for takes its data
     */
    public GroupDialog(SuperBubble s)
    {
        this.s = s;
        temp1 = s.getImageLink();
        temp2 = s.getDescription();
        temp3 = s.getName();
    }
    /**
     * set interface from appropriate xml file
     * and define actions when clicked on button or image
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(temp3);
        View v = inflater.inflate(R.layout.groupdialog, null);

        img = v.findViewById(R.id.imageGroup);
        txtVw = v.findViewById(R.id.textGroup);
        txtVw2 = v.findViewById(R.id.textGroup2);
        btn = v.findViewById(R.id.button_group);

        if (s.getAllBubble().get(0).getAudioLink() == ""){ btn.setClickable(false); btn.setEnabled(false);}

        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!s.getPlaying())s.play_go();
                else s.play_stop();
            }
        });

        new DownloadImageTask(img).execute(temp1);
        txtVw.setText(temp3 +" ( " + s.getAllBubble().size() +" bubbles )");
        txtVw2.setText("\n" + temp2 + "\n");

        img.setOnClickListener(this);
        txtVw.setOnClickListener(this);

        return v;
    }
    /**
     * close if tap out of window range
     */
    public void onClick(View v) {
        dismiss();
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

}