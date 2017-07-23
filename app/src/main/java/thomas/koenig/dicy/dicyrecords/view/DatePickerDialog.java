package thomas.koenig.dicy.dicyrecords.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import thomas.koenig.dicy.dicyrecords.AndroidUtils;
import thomas.koenig.dicy.dicyrecords.R;

/**
 * Created by Thomas on 16.10.2016.
 */

public class DatePickerDialog {

    private Context context;
    private DatePickerListener listener;

    public DatePickerDialog(Context context, DatePickerListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View layout = LayoutInflater.from(context).inflate(R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) layout.findViewById(R.id.datePicker);

        builder.setView(layout);
        builder.setTitle(R.string.date);
        builder.setNegativeButton(R.string.cancel, null);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                DateTime date = AndroidUtils.getDateFromDatePicker(datePicker);
                listener.onConfirm(date);
            }
        });

        builder.create().show();
    }

    public interface DatePickerListener
    {
        void onConfirm(DateTime dateTime);
    }
}


