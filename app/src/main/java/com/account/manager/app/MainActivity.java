package com.account.manager.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int MULTIPLE_PERMISSIONS = 1;
    MainApplication mainApplication;
    TextView add_person;
    TextView trancation;
    TextView report;
    TextView generate_report;
    Dialog dialog;

    Button backup,restore;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        mainApplication = (MainApplication) getApplicationContext();
        add_person = findViewById(R.id.add_party);
        trancation = findViewById(R.id.trancation);
        report = findViewById(R.id.report);
        generate_report = findViewById(R.id.generate_report);
        if (VERSION.SDK_INT >= 23) {
            checkPermissions();
        }

        add_person.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open add person activity
                startActivity(new Intent(getApplicationContext(), AddPersonActivity.class));
            }
        });

        trancation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open add trancation activity
                startActivity(new Intent(getApplicationContext(), AddViewDeleteTransactionActivity.class));
            }
        });

        report.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open show transation date wise activity
                startActivity(new Intent(getApplicationContext(), GetDateWiseReportActivity.class));
            }
        });

        generate_report.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO open show all generated reports activity
                startActivity(new Intent(getApplicationContext(), PdfGenrateActivity.class));
            }
        });

        backup = findViewById(R.id.btnbackup);
        restore = findViewById(R.id.btnrestore);

        backup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                //performBackup(databaseHelper,"BackUp");

                BackUpData();
            }
        });

        restore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Builder builder = new Builder(MainActivity.this);
                builder.setTitle("Restore Data").setMessage("Do you want restore data?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
//                        performRestore(databaseHelper);
                        RestoreData();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog = builder.show();
            }
        });
    }

    private boolean checkPermissions() {
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        ArrayList arrayList = new ArrayList();
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(this, str) != 0) {
                arrayList.add(str);
            }
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), MULTIPLE_PERMISSIONS);
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                Toast.makeText(this, "Please allow all Permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitDialoge();
    }

    private void ExitDialoge() {
        Builder builder = new Builder(this);
        builder.setTitle("Exit").setMessage("Do You Want To Exit ?").setCancelable(true).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        this.dialog = builder.show();
    }

    @SuppressLint({"WrongConstant"})
    public void BackUpData() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append("/AccountManager");
            File file = new File(sb.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            File dataDirectory = Environment.getDataDirectory();
            if (file.canWrite()) {
                File file2 = new File(dataDirectory, "//data/com.account.manager.app/databases/Account_DB");
                File file3 = new File(file, "Account_DB");
                if (file2.exists()) {
                    FileChannel channel = new FileInputStream(file2).getChannel();
                    FileChannel channel2 = new FileOutputStream(file3).getChannel();
                    channel2.transferFrom(channel, 0, channel.size());
                    channel.close();
                    channel2.close();
                    Toast.makeText(getApplicationContext(), "Backup Successfully Complete..", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "No Data Found For BackUP ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception unused) {
            Toast.makeText(this, "Error please try again after some time!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void RestoreData() {
        try {

            File file = new File(Environment.getExternalStorageDirectory()+"/AccountManager");
            File dataDirectory = Environment.getDataDirectory();
            if (file.canWrite()) {
                File file2 = new File(dataDirectory, "//data/com.account.manager.app/databases/Account_DB");
                File file3 = new File(file, "Account_DB");
                if (file3.exists()) {
                    FileChannel channel = new FileInputStream(file3).getChannel();
                    FileChannel channel2 = new FileOutputStream(file2).getChannel();
                    channel2.transferFrom(channel, 0, channel.size());
                    channel.close();
                    channel2.close();
                    Toast.makeText(getApplicationContext(), "Restore Successfully Complete..", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "No Data Found For Restore", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "No Data Found For Restore", Toast.LENGTH_SHORT).show();
        } catch (Exception unused) {
            Toast.makeText(this, "Error please try again after some time!!", Toast.LENGTH_SHORT).show();
        }
    }


    public void performBackup(final DatabaseHelper db, final String outFileName) {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.this.getResources().getString(R.string.app_name));

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Backup Name");
            final EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    String out = outFileName + m_Text + ".db";
                    db.backup(out);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else
            Toast.makeText(MainActivity.this, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
    }

    //ask to the user what backup to restore
    public void performRestore(final DatabaseHelper db) {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + MainActivity.this.getResources().getString(R.string.app_name));
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("Restore:");
            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                db.importDB(files[which].getPath());
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Unable to restore. Retry", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(MainActivity.this, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
    }


}
