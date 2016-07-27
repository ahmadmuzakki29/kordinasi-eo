package com.event.kordinasi.kordinasievent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.muzakki.ahmad.material.detail.Detail;
import com.muzakki.ahmad.material.detail.DetailActivity;
import com.muzakki.ahmad.material.detail.DetailLocal;
import com.muzakki.ahmad.material.detail.DetailModel;
import com.muzakki.ahmad.material.detail.DetailTabActivity;
import com.muzakki.ahmad.material.form.Fields;

import java.util.concurrent.ExecutionException;

public class DetailEventActivity extends DetailTabActivity {

    private String id;
    private Fields fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        fields = new EventFields();
        render();
    }

    @Override
    protected Detail getDetail() {
        return new EventDetail(this,id,fields);
    }

    private class EventDetail extends DetailLocal{

        public EventDetail(DetailActivity ctx, String id,Fields fields) {
            super(ctx, id,fields);
        }

        @Override
        protected DetailModel getLocalModel() {
            return new DetailModel(DetailEventActivity.this,"event");
        }

        @Override
        protected String getTitle(Bundle data) {
            return data.getString("nama");
        }

        @Override
        protected String getSubtitle(Bundle data) {
            return data.getString("tempat");
        }

        @Override
        protected Bitmap getImage(Bundle data) {
            new LoadImage().execute(data.getString("foto"));
            return BitmapFactory.decodeResource(getResources(),R.drawable.picture);
        }
    }

    private class LoadImage extends AsyncTask<String,Object,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return Glide.
                        with(DetailEventActivity.this).
                        load(strings[0]).
                        asBitmap().
                        into(300, 300). // Width and height
                        get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap==null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.picture);
            }
            setCoverImage(bitmap);
        }
    }
}
