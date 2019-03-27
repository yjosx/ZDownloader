package pow.jie.zdownloader.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import pow.jie.zdownloader.DownloadService;
import pow.jie.zdownloader.R;
import pow.jie.zdownloader.bean.FileInfo;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {

    private Context mContext;
    private List<FileInfo> mFileInfoList;
    private DownloadService.DownloadBinder downloadBinder;

    public MainContentAdapter(Context context, List<FileInfo> fileInfoList) {
        mContext = context;
        mFileInfoList = fileInfoList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileName;
        private ProgressBar progressBar;
        private Button stop;
        private Button resume;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.tv_card_fileInfo);
            progressBar = itemView.findViewById(R.id.progressBar_main);
            stop = itemView.findViewById(R.id.bt_stop);
            resume = itemView.findViewById(R.id.bt_resume);
        }
    }

    @NonNull
    @Override
    public MainContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_download_info, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainContentAdapter.ViewHolder viewHolder, int i) {
        viewHolder.fileName.setText(mFileInfoList.get(i).getFileName());
        FileInfo fileInfo = mFileInfoList.get(mFileInfoList.size() - 1);
        viewHolder.stop.setOnClickListener(v -> downloadBinder.cancelDownload());
        viewHolder.resume.setOnClickListener(v -> downloadBinder.pauseDownload());
    }

    @Override
    public int getItemCount() {
        return mFileInfoList.size();
    }

}
