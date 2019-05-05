package com.example.wsins.xmly;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Trace;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsins.xmly.base.BaseActivity;
import com.example.wsins.xmly.interfaces.IAlbumDetailViewCallBack;
import com.example.wsins.xmly.presenters.AlbumDetailPresenter;
import com.example.wsins.xmly.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by Sin on 2019/1/19
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack {

    private ImageView largeCover;
    private RoundRectImageView smallCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallBack(this);
    }

    private void initView() {
        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.viv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
    }

    @Override
    public void onDetailListLoaded(List<Trace> traces) {

    }

    @Override
    public void onAlbumLoaded(Album album) {
        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }
        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (largeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(largeCover);
        }
        if (smallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(smallCover);
        }
    }
}
