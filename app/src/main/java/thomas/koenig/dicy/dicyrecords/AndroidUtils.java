package thomas.koenig.dicy.dicyrecords;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import java.io.File;

/**
 * Created by Thomas on 15.10.2016.
 */

public class AndroidUtils {

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void adaptDialogSize(Dialog dialog, float widthFactor, float heightFactor)
    {
        DisplayMetrics metrics = dialog.getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((int) (widthFactor * width), (int) (heightFactor * height));
    }

    public static DateTime getDateFromDatePicker(DatePicker datePicker)
    {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        return new DateTime(year, month, day, 0, 0, 0);
    }

    public static void setDateToDatePicker(DatePicker datePicker, DateTime time)
    {
        int year = time.getYear();
        int month = time.getMonthOfYear() - 1;      // Need to subtract 1 here.
        int day = time.getDayOfMonth();

        datePicker.updateDate(year, month, day);
    }

    public static void sendFilePerMail(Context context, File file, String email)
    {
        try
        {
            File sd = Environment.getExternalStorageDirectory();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            String date = DateTime.now().toString("yyyy.MM.dd");
            intent.putExtra(Intent.EXTRA_SUBJECT, "RECORDS BACKUP " + date);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            //intent.putExtra(Intent.EXTRA_TEXT, "test");
            if (email != null)
            {
                intent.setData(Uri.parse("mailto:" + email));
            } else
            {
                intent.setData(Uri.parse("mailto:"));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            context.startActivity(intent);
        } catch (Exception e)
        {
            System.out.println("is exception raises during sending mail" + e);
        }
    }
}
