package com.example.lemanssounds;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupDialog extends DialogFragment implements OnClickListener {

    String temp1, temp2, temp3;
    ImageView img;
    TextView txtVw;

    public GroupDialog(String imgLink, String txt, String nm)
    {
        temp1 = imgLink;
        temp2 = txt;
        temp3 = nm;

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(temp3);
        View v = inflater.inflate(R.layout.groupdialog, null);

        img = v.findViewById(R.id.imageGroup);
        txtVw = v.findViewById(R.id.textGroup);

        new DownloadImageTask(img).execute(temp1);
        txtVw.setText(temp3+"\n"+temp2);

        img.setOnClickListener(this);
        txtVw.setOnClickListener(this);

        return v;
    }

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