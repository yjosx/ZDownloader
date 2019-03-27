package pow.jie.zdownloader.util;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import pow.jie.zdownloader.bean.FileInfo;

public class DownloadTask extends AsyncTask<FileInfo, Integer, Integer> {
    private static final int TYPE_SUCCEED = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELED = 3;

    private DownloadListener listener;
    private boolean isCanceled;
    private boolean isPaused;
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(FileInfo... fileInfos) {
        HttpURLConnection conn;
        InputStream is = null;
        RandomAccessFile raf = null;
        File file = null;
        try {
            long downloadedLength = 0;
            String downloadUrl = fileInfos[0].getUrl();
            String fileName = fileInfos[0].getFileName();
            String dictionary = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(dictionary, fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            long contentLenth = getContentLenth(downloadUrl);
            if (contentLenth == 0) {
                return TYPE_FAILED;
            } else if (contentLenth == downloadedLength) {
                return TYPE_SUCCEED;
            }
            URL url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            // 設置下載文件開始到結束的位置
            conn.setRequestProperty("Range", "bytes=" + downloadedLength + "-");
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(downloadedLength);
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_PARTIAL) {
                is = conn.getInputStream();
                byte[] bt = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(bt)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        raf.write(bt, 0, len);
                        int progress = (int) ((total + downloadedLength) * 100 / contentLenth);
                        publishProgress(progress);
                    }
                }
                conn.disconnect();
                return TYPE_SUCCEED;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_SUCCEED:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCancled();
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress>lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    private long getContentLenth(String downloadUrl) throws IOException {
        HttpURLConnection conn;
        URL url = new URL(downloadUrl);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10 * 1000);
        conn.setRequestMethod("GET");
        conn.setReadTimeout(10 * 1000);
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return conn.getContentLength();
        }
        return 0;
    }
    public void pauseDownload(){
        isPaused = true;
    }
    public void cancelDownload(){
        isCanceled = true;
    }
}
