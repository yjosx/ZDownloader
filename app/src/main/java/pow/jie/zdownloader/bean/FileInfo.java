package pow.jie.zdownloader.bean;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private int id;
    private String url;
    private String fileName;
    private long fileSize;
    private long finishedSize;

    public FileInfo(int id, String url, String fileName, long fileSize, long finishedSize) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.finishedSize = finishedSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFinishedSize() {
        return finishedSize;
    }

    public void setFinished(long finishedSize) {
        this.finishedSize = finishedSize;
    }
}