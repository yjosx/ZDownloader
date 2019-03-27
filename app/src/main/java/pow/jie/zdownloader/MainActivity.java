package pow.jie.zdownloader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pow.jie.zdownloader.bean.FileInfo;

public class MainActivity extends AppCompatActivity {
    String address;
    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.create();
            View view = View.inflate(this, R.layout.download_task_asker, null);
            dialog.setView(view);
            EditText editText = view.findViewById(R.id.et_download_asker);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "下载", (dialog1, which) -> {
                address = editText.getText().toString();
                Log.d("MainActivity", "onDownload: " + address);
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "地址被你吃了哇", Toast.LENGTH_SHORT).show();
                    return;
                }
                FileInfo fileInfo = new FileInfo(0, address, getFileName(address), 0, 0);
                Intent intent = new Intent(this, DownloadService.class);
                intent.putExtra("fileInfo", fileInfo);
                startService(intent);

                bindService(intent, connection, BIND_AUTO_CREATE);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (downloadBinder == null) {
                    Log.d("MainActivity", "downloadBinder==null");
                    return;
                }
                downloadBinder.startDownload(fileInfo);
                dialog.dismiss();
            });
            dialog.show();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
