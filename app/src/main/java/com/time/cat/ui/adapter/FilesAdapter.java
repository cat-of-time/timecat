package com.time.cat.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.R;
import com.time.cat.data.Constants;
import com.time.cat.data.model.entity.FileEntity;
import com.time.cat.ui.activity.main.MainActivity;
import com.time.cat.ui.modules.editor.EditorActivity;
import com.time.cat.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
    private List<FileEntity> dataSet;
    private AppCompatActivity context;

    public FilesAdapter(List<FileEntity> entityList) {
        dataSet = (entityList == null) ? new ArrayList<FileEntity>() : entityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = (MainActivity) parent.getContext();
        }
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_file_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FileEntity entity = dataSet.get(position);
        String fileName = entity.getName();
        holder.fileName.setText(fileName);

        String content = FileUtils.readContentFromFile(new File(entity.getAbsolutePath()), false);
        if (content.length() == 0) {
            holder.fileContent.setVisibility(View.GONE);
        } else {
            content = content.length() > 500 ? content.substring(0, 500) : content;
            holder.fileContent.setText(content);
        }

        holder.fileDate.setText(DateUtils.getRelativeTimeSpanString(entity.getLastModified(),
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2MainActivity = new Intent();
                Bundle args = new Bundle();
                args.putBoolean(Constants.BUNDLE_KEY_SAVED, true);
                args.putBoolean(Constants.BUNDLE_KEY_FROM_FILE, true);
                args.putString(Constants.BUNDLE_KEY_FILE_NAME,
                        FileUtils.stripExtension(entity.getName()));
                args.putString(Constants.BUNDLE_KEY_FILE_PATH, entity.getAbsolutePath());
                intent2MainActivity.putExtras(args);
                intent2MainActivity.setClass(context, EditorActivity.class);
                context.startActivity(intent2MainActivity);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileContent, fileDate;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileContent = itemView.findViewById(R.id.file_content);
            fileDate = itemView.findViewById(R.id.file_date);
        }
    }
}
