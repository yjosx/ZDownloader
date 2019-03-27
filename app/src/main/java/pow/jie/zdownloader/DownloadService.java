package pow.jie.zdownloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import pow.jie.zdownloader.bean.FileInfo;
import pow.jie.zdownloader.util.DownloadListener;
import pow.jie.zdownloader.util.DownloadTask;

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    private FileInfo fileInfo;

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(false);

        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCancled() {
            downloadTask = null;
            stopForeground(true);
        }

    };

    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {
        public void startDownload(FileInfo fileInfo) {
            if (downloadTask == null) {
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(fileInfo);
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            }
            if (fileInfo.getUrl() != null) {
                String fileName = fileInfo.getFileName();
                String dic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(dic + fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public static void startService(Context context, FileInfo fileInfo) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("fileInfo", fileInfo);
        context.startService(intent);
    }

}