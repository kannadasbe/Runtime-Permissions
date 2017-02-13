package com.crazygeeksblog.runtimepermission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_ALL = 1;
    final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonRequestPermission, buttonEnablePermission;
        buttonRequestPermission = (Button) findViewById(R.id.btnRequestPermission);
        buttonEnablePermission = (Button) findViewById(R.id.btnEnablePermission);

        // Define onclick listeners for buttons
        buttonRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check android OS version is less than M. If yes runtime permissions are not required
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                {
                    Toast.makeText(getApplicationContext(), "Runtime permissions are required only in Marshmallow and above.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                // Check for pending permissions first, then if any ask for it.
                if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            PERMISSIONS, PERMISSION_ALL);
                }
                //If all the permissions are already granted then show the toast.
                else {
                    Toast.makeText(getApplicationContext(), "Permissions are already received.", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Open app's setting page to manage app permissions
        buttonEnablePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(intent);
            }
        });
    }


    public boolean hasPermissions(Context context, String... permissions)
    {
        boolean hasAllPermissions=true;

        // If android OS version is greater than or equal to M(Marshmallow) then
        // check whether all the permissions are already granted if not ask for permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermissions = false;
                    break;
                }
            }
        }

        return hasAllPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        // Check all the permissions are granted
        // else check the no of permissions granted and show the toast.
        if (hasPermissions(getApplicationContext(), PERMISSIONS)) {
            Toast.makeText(this, "Permissions Received.", Toast.LENGTH_LONG).show();
        } else {
            int i = 0;
            for (String permission : permissions) {
                // Returns 0 if permission is granted else -1
                i = i + ContextCompat.checkSelfPermission(this, permission);
            }

            // Convert negative number to positive
            i = Math.abs(i);
            String message = String.valueOf(i) + " permission(s) received out of "
                    + String.valueOf(permissions.length);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}
