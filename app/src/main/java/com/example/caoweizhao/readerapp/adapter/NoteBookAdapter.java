package com.example.caoweizhao.readerapp.adapter;

import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.bean.Note;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by caoweizhao on 2018-1-25.
 */

public class NoteBookAdapter extends BaseQuickAdapter<Note, BaseViewHolder> implements View.OnCreateContextMenuListener {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NoteBookAdapter(@Nullable List<Note> data) {
        super(R.layout.note_book_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Note item) {
        Timestamp timestamp = new Timestamp(item.getCreateTime());
        helper.setText(R.id.note_title, item.getTitle())
                .setText(R.id.page_belong_to, String.valueOf(item.getPage()))
                .setText(R.id.last_modify_time, sdf.format(timestamp));
        helper.itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Delete");
    }

}
