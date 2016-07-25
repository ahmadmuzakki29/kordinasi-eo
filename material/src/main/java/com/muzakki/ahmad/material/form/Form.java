package com.muzakki.ahmad.material.form;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muzakki.ahmad.lib.Helper;
import com.muzakki.ahmad.lib.InternetConnection;
import com.muzakki.ahmad.material.R;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by jeki on 5/30/16.
 */
public abstract class Form extends LinearLayout implements FormInternetConnection.Listener{

    private static final int DEFAULT_MARGIN = 20;
    public static final int DEFAULT_PICTURE = R.drawable.picture;
    private final AppCompatActivity act;
    private final Fields fields;
    private boolean wide = false;
    private HashMap<String,View> views = new HashMap<>();
    private String mCurrentPhotoPath;
    int activityCode;
    private static final int MAX_SIZE_SQUARE = 600;
    private static final int MAX_SIZE_TOP = 750,MAX_SIZE_BOTTOM = 500;
    private static final String IMAGE_DIR = "images";
    private boolean loadingState;
    private SaveType saveType;
    private Listener listener;
    private Bundle imageMaster = new Bundle();
    private Action action = null;
    private String dataId;
    private String token;
    protected FormModel model;
    private HashMap<String,View> btnDelArray = new HashMap<>();

    public enum SaveType{
        SERVER,LOCAL, BOTH
    }

    public Form(AppCompatActivity act, Fields fields,SaveType saveType,Action action,Listener listener){
        super(act);
        this.act = act;
        this.fields = fields;
        this.listener = listener;
        this.saveType = saveType;
        this.action = action;
        initForm();
    }

    private void initForm() {
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOrientation(VERTICAL);
    }

    public void renderFields(){
        int screenSize = act.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        int orientation = act.getResources().getConfiguration().orientation;
        activityCode=1; // reset this

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                wide = true;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                wide = orientation==Configuration.ORIENTATION_LANDSCAPE;
                break;
            default: wide=false;
        }

        for (Field field: fields) {
            View v = getView(field);
            addView(v);
        }
    }


    //////// GET VIEW //////////
    private View getView(Field field){
        switch (field.getType()){
            case TEXT:
                return getText(field);
            case TEXTAREA:
                return getTextArea(field);
            case RADIO:
                return getRadio(field);
            case IMAGE:
                return getImage(field);
            case SPINNER:
                return getSpinner(field);
            case NUMBER:
                return getNumber(field);
            case PICKER:
                return getPicker(field);
            case DATE:
                return getDate(field);
            case SEPARATOR:
                return getSeparator();
            default: return null;
        }
    }

    private View getText(Field field){
        EditText txt = new EditText(act);
        txt.setLayoutParams(getDefaultLayoutParams());

        if(field.getDrawable()!=0 && !wide){
            txt.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, 0, 0);
            txt.setCompoundDrawablePadding(getDp(7));
        }

        txt.setInputType(InputType.TYPE_CLASS_TEXT);

        if(field.getBackground()!=0){
            txt.setBackground(act.getResources().getDrawable(field.getBackground()));
        }

        txt.setPadding(getDp(7),0,0,getDp(7));
        if(field.getValue()!=null) txt.setText(field.getValue());

        views.put(field.getName(),txt);

        if(wide) {
            return getTitleWrap(txt,field);
        }

        txt.setHint(field.getTitle());
        return txt;
    }

    private View getTextArea(Field field){
        EditText txt = new EditText(act);
        txt.setLayoutParams(getDefaultLayoutParams());


        if(field.getDrawable()!=0){
            txt.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, 0, 0);
            txt.setCompoundDrawablePadding(getDp(7));
        }

        txt.setInputType(InputType.TYPE_CLASS_TEXT);

        if(field.getBackground()!=0){
            txt.setBackground(act.getResources().getDrawable(field.getBackground()));
        }

        txt.setPadding(getDp(7),0,0,getDp(7));
        txt.setLines(3);
        if(field.getValue()!=null) txt.setText(field.getValue());

        views.put(field.getName(),txt);

        if(wide){
            return getTitleWrap(txt, field);
        }
        txt.setHint(field.getTitle());
        return txt;
    }

    private View getRadio(Field field) {
        ArrayList<Item> items = field.getItems();
        RadioGroup rg = new RadioGroup(act);
        rg.setOrientation(LinearLayout.HORIZONTAL);
        rg.setLayoutParams(getDefaultLayoutParams());

        int i = -1;
        for(Item item:items){
            i++;
            AppCompatRadioButton rb = new AppCompatRadioButton(act);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,0,getDp(10),0);

            if(field.getValue()!=null){
                String val = field.getValue();
                if(item.getKode()==null){
                    if(val.equals(item.getValue())){
                        rb.setChecked(true);
                    }
                }else{
                    if(val.equals(item.getKode())){
                        rb.setChecked(true);
                    }
                }
            }else {
                if (i == 0) {
                    rb.setChecked(true);
                }
            }

            if(i!=items.size()-1){
                rb.setLayoutParams(lp);
            }

            rb.setText(item.getValue());
            rb.setId(myGenerateViewId());
            rb.setTextSize(getTextSize());

            rb.setSupportButtonTintList(ContextCompat.getColorStateList(act, R.color.primary));
            rg.addView(rb);
        }
        views.put(field.getName(),rg);

        if(wide) {
            return getTitleWrap(rg,field);
        }
        return rg;
    }

    private View getImage(Field field){
        LinearLayout wrapper = new LinearLayout(act);
        wrapper.setLayoutParams(getDefaultLayoutParams());
        wrapper.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(act);
        LayoutParams lp = getLayoutParamsWrap();
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        tv.setLayoutParams(lp);
        tv.setText(field.getTitle());
        tv.setTextSize(getTextSize());
        wrapper.addView(tv);

        ImageView delBtn = new ImageView(act);
        delBtn.setImageBitmap(BitmapFactory.decodeResource(act.getResources(),
                android.R.drawable.ic_menu_close_clear_cancel));
        LayoutParams lp0 = getLayoutParamsWrap();
        lp0.gravity = Gravity.RIGHT|Gravity.END;
        delBtn.setLayoutParams(lp0);
        delBtn.setOnClickListener(new DeleteImageClick(field));
        boolean notNull = field.getValue()!=null && !field.getValue().equals("");
        delBtn.setVisibility(notNull?VISIBLE:GONE);
        wrapper.addView(delBtn);
        btnDelArray.put(field.getName(),delBtn);


        ImageView img = new ImageView(act);
        lp.setMargins(0,getDp(10),0,0);
        img.setLayoutParams(lp);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        wrapper.addView(img);
        field.setImageView(img);


        LinearLayout wrapBtn = new LinearLayout(act);
        LayoutParams lpw = getLayoutParamsWrap();
        lpw.gravity = Gravity.CENTER_HORIZONTAL;
        wrapBtn.setLayoutParams(lpw);


        // Button gallery
        Button btnGallery = new Button(act);
        LayoutParams lp2 = getLayoutParamsWrap();
        lp2.setMargins(getDp(15),getDp(15),getDp(15),getDp(15));

        btnGallery.setLayoutParams(lp2);
        btnGallery.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_gallery, 0, 0, 0);
        btnGallery.setCompoundDrawablePadding(getDp(7));
        btnGallery.setBackground(act.getResources().getDrawable(R.drawable.bg_btn));
        btnGallery.setPadding(getDp(15),0,getDp(15),0);
        btnGallery.setTextSize(getTextSizeSmall());
        btnGallery.setText(act.getString(R.string.galeri));
        wrapBtn.addView(btnGallery);

        // Button Camera

        Button btnCamera = new Button(act);
        btnCamera.setLayoutParams(lp2);
        btnCamera.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_camera, 0, 0, 0);
        btnCamera.setCompoundDrawablePadding(getDp(7));
        btnCamera.setText(act.getString(R.string.kamera));
        btnCamera.setBackground(act.getResources().getDrawable(R.drawable.bg_btn));
        btnCamera.setPadding(getDp(15),0,getDp(15),0);
        btnCamera.setTextSize(getTextSizeSmall());
        wrapBtn.addView(btnCamera);
        wrapper.addView(wrapBtn);


        // assign listener
        field.setCameraCode(activityCode);
        btnCamera.setOnClickListener(new CameraClick(activityCode++));
        field.setGalleryCode(activityCode);
        btnGallery.setOnClickListener(new GalleryClick(activityCode++));

        if(notNull){
            field.showImage();
        }else{
            img.setImageBitmap(BitmapFactory.decodeResource(act.getResources(), DEFAULT_PICTURE));
        }

        return wrapper;
    }

    private View getSpinner(Field field){
        Spinner sp = new Spinner(act);
        sp.setLayoutParams(getDefaultLayoutParams());
        sp.setGravity(Gravity.CENTER);

        ArrayList<Item> items = field.getItems();
        int size = items.size();
        int i = 0;
        String[] newItems;

        size++;
        newItems = new String[size];

        newItems[0] = "-- Pilih " + field.getTitle() + " --";
        i =1;

        for(Item item: items){
            newItems[i] = item.getValue();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (act, android.R.layout.simple_spinner_item, newItems){
            public View getView(int position, View convertView,ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.CENTER);

                return v;

            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;

            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        //restore value
        if(field.getValue()!=null&&!field.getValue().equals("")){
            int pos = getSelectedItemPos(field.getItems(),field.getValue());
            sp.setSelection(pos+1);
        }

        views.put(field.getName(),sp);
        if(wide){
            return getTitleWrap(sp,field);
        }
        return sp;
    }

    private View getNumber(Field field){
        EditText txt = new EditText(act);
        txt.setLayoutParams(getDefaultLayoutParams());

        if(field.getDrawable()!=0 && !wide){
            txt.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, 0, 0);
            txt.setCompoundDrawablePadding(getDp(7));
        }

        txt.setInputType(InputType.TYPE_CLASS_NUMBER);

        if(field.getBackground()!=0){
            txt.setBackground(act.getResources().getDrawable(field.getBackground()));
        }

        txt.setPadding(getDp(7),0,0,getDp(7));
        if(field.getValue()!=null) txt.setText(field.getValue());

        views.put(field.getName(),txt);

        if(wide) {
            return getTitleWrap(txt,field);
        }

        txt.setHint(field.getTitle());
        return txt;
    }

    private View getPicker(Field field){
        EditText txt = new EditText(act);
        txt.setLayoutParams(getDefaultLayoutParams());

        if(field.getDrawable()!=0 && !wide){
            txt.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, R.drawable.popup, 0);
        }else{
            txt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.popup, 0);
        }
        txt.setCompoundDrawablePadding(getDp(7));
        txt.setInputType(InputType.TYPE_CLASS_TEXT);

        if(field.getBackground()!=0){
            txt.setBackground(act.getResources().getDrawable(field.getBackground()));
        }

        txt.setPadding(getDp(7),0,0,getDp(7));
        if(field.getValue()!=null) txt.setText(field.getValue());


        field.setPickerCode(activityCode);
        txt.setOnFocusChangeListener(new PickerClickListener(activityCode++,field));

        views.put(field.getName(),txt);

        if(wide) {
            ViewGroup wrap = getTitleWrap(txt, field);
            txt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.popup, 0);
            return wrap;
        }

        txt.setHint(field.getTitle());
        return txt;
    }

    private View getDate(Field field){
        EditText txt = new EditText(act);
        txt.setLayoutParams(getDefaultLayoutParams());

        if(field.getDrawable()!=0 && !wide){
            txt.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, R.drawable.calendar, 0);
        }else{
            txt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.calendar, 0);
        }
        txt.setCompoundDrawablePadding(getDp(7));
        txt.setInputType(InputType.TYPE_CLASS_TEXT);

        if(field.getBackground()!=0){
            txt.setBackground(act.getResources().getDrawable(field.getBackground()));
        }

        txt.setPadding(getDp(7),0,0,getDp(7));
        txt.setOnFocusChangeListener(new DateFocusListener(field));
        if(field.getValue()!=null) txt.setText(field.getValue());

        views.put(field.getName(),txt);

        if(wide) {
            ViewGroup wrap = getTitleWrap(txt, field);
            txt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.calendar, 0);
            return wrap;
        }

        txt.setHint(field.getTitle());
        return txt;
    }

    private View getSeparator(){
        View v = new View(act);
        LayoutParams lp =
                new LayoutParams(LayoutParams.MATCH_PARENT, getDp(DEFAULT_MARGIN));
        v.setLayoutParams(lp);
        return v;
    }


    // FOR EDITING PURPOSE
    protected void initData(){
        Bundle data = model.select(getDataId());

        for(String key:data.keySet()){
            Field f = fields.getField(key);
            if(f!=null){
                String v = data.getString(key);
                if(v==null || v.equals("null")) continue;
                switch (f.getType()){
                    case DATE:
                        v = Helper.decodeDate(v);
                        break;
                    case PICKER:
                        f.setKode(v);
                        v = getPickerValue(f,v);
                }
                f.setValue(v);
            }
        }
        for(Field field: fields){
            if(field.getType()!= Field.Type.IMAGE) continue;
            imageMaster.putString(field.getName(),field.getValue());
        }
    }

    /* GET VALUE */

    private void enumerateValues(){
        for (Field field: fields) {
            field.setValue(getValue(field));
        }
    }

    @Nullable
    private String getValue(Field f){
        switch (f.getType()){
            case TEXT:
            case TEXTAREA:
            case NUMBER:
                EditText txt = (EditText) views.get(f.getName());
                return txt.getText().toString();
            case RADIO:
                return getRadioValue(f);
            case SPINNER:
                return getSpinnerValue(f);
            case IMAGE:
            case DATE:
            case PICKER:
                return f.getValue();
            default: return null;
        }
    }

    private String getSpinnerValue(Field f) {
        int pos = ((Spinner) views.get(f.getName())).getSelectedItemPosition();
        if(pos==0) return null;
        ArrayList<Item> items = f.getItems();
        for (int i=0;i<items.size();i++){
            if(i==pos-1) {
                Item item = items.get(i);
                return item.getKode()==null?item.getValue():item.getKode();
            }
        }
        return null;
    }

    private String getRadioValue(Field field){
        RadioGroup rg = (RadioGroup) views.get(field.getName());
        String value = ((RadioButton) act.findViewById(rg.getCheckedRadioButtonId())).
                getText().toString();

        for(Item item:field.getItems()){
            if(value.equals(item.getValue()) && item.getKode()!=null){
                return item.getKode();
            }
        }

        return value;
    }

    private int getSelectedItemPos(ArrayList<Item> items,String val){
        for (int i=0;i<items.size();i++){
            Item item = items.get(i);
            String selectedVal = item.getKode()==null?item.getValue():item.getKode();
            if(val.equals(selectedVal)) {
                return i;
            }
        }
        return 0;
    }

    public Bundle getBundleValue() throws NullPointerException{
        enumerateValues();
        Bundle val = new Bundle();
        for(Field f: fields){
            if(f.getType()== Field.Type.SEPARATOR) continue;

            String value = null;
            switch (f.getType()){
                case TEXT:
                case TEXTAREA:
                case IMAGE:
                case NUMBER:
                    value = f.getValue();
                    break;
                case SPINNER:
                case PICKER:
                case RADIO:
                    value = f.getKode()==null?f.getValue():f.getKode();
                    break;
                case DATE:
                    value = f.getValue();
                    try {
                        Date thedate = new SimpleDateFormat("dd/MM/yyyy").parse(value);
                        value = new SimpleDateFormat("yyyy-MM-dd").format(thedate);
                    }catch (ParseException | NullPointerException e){}
            }

            if((value==null || value.equals("")) && f.isRequired()){
                alertNull(f.getTitle());
                throw new NullPointerException(f.getTitle());
            }

            val.putString(f.getName(),value!=null?value:"");
        }
        return val;
    }




    /* HELPER  */

    private int myGenerateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        }else{
            Random r = new Random();
            return r.nextInt(1000+1);
        }
    }

    private LayoutParams getLayoutParamsWrap(){
        LayoutParams lp =
                new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        return lp;
    }

    private LayoutParams getDefaultLayoutParams(){
        LayoutParams lp =
                new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,getDp(DEFAULT_MARGIN),0, getDp(DEFAULT_MARGIN));
        return lp;
    }

    private int getDp(int dp){
        return Helper.getPxFromDp(dp, act.getResources());
    }

    public HashMap<String, View> getViews() {
        return views;
    }

    private float getTextSize(){
        return (act.getResources().getDimension(R.dimen.TextSize) /
                act.getResources().getDisplayMetrics().density);
    }

    private float getTextSizeSmall(){
        return (act.getResources().getDimension(R.dimen.TextSizeSmall) /
                act.getResources().getDisplayMetrics().density);
    }

    private ViewGroup getTitleWrap(View v,Field field){
        LinearLayout wrap = new LinearLayout(act);
        wrap.setLayoutParams(getDefaultLayoutParams());
        TextView fieldTitle = new TextView(act);
        TextView colon = new TextView(act);
        LayoutParams lpcolon = getLayoutParamsWrap();
        lpcolon.setMargins(getDp(1),0,getDp(1),0);
        lpcolon.gravity = Gravity.CENTER_VERTICAL;
        colon.setLayoutParams(lpcolon);
        colon.setText(" : ");colon.setTextSize(getTextSize());


        TableLayout.LayoutParams lptitle = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, .75f);
        lptitle.gravity = Gravity.CENTER_VERTICAL;
        fieldTitle.setLayoutParams(lptitle);

        v.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, .25f));

        if(field.getDrawable()!=0){
            fieldTitle.setCompoundDrawablesWithIntrinsicBounds( field.getDrawable(), 0, 0, 0);
            fieldTitle.setCompoundDrawablePadding(getDp(7));
        }

        fieldTitle.setText(field.getTitle());
        fieldTitle.setTextSize(getTextSize());

        wrap.addView(fieldTitle);
        wrap.addView(colon);
        wrap.addView(v);

        return wrap;
    }

    public void notifyOrientationChanged(){
        enumerateValues();
        removeAllViews();
        renderFields();
    }

    public void alertNull(String title){
        String name = title.replace("*","");
        new AlertDialog.Builder(act)
                .setTitle(act.getString(R.string.perhatian))
                .setMessage(name+ act.getString(R.string.not_empty))
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void onResult(int requestCode,Intent data) {
        for(Field field: fields){
            if(field.getType()==Field.Type.IMAGE) {

                if (requestCode == field.getCameraCode()) {
                    saveCamera(field);
                }
                if (requestCode == field.getGalleryCode()) {
                    Uri uri = data.getData();
                    saveGallery(field, uri);
                }
            }
        }
    }

    public enum Action{
        ADD,EDIT
    }

    private class DeleteImageClick implements OnClickListener{
        private final Field field;

        DeleteImageClick(Field field){
            this.field = field;
        }
        @Override
        public void onClick(View view) {
            if(action== Action.ADD){
                File f= new File(field.getValue());
                f.delete();
            }
            field.setValue(null);
            field.getImageView().setImageBitmap(
                    BitmapFactory.decodeResource(act.getResources(), DEFAULT_PICTURE));
            view.setVisibility(GONE);
        }
    }





    /* IMAGE LISTENER */

    private class GalleryClick implements OnClickListener{
        private final int code;
        private GalleryClick(int activityCode){
            this.code = activityCode;
        }
        @Override
        public void onClick(View view) {
            startGallery(code);
        }
    }

    private class CameraClick implements OnClickListener{
        private final int code;

        private CameraClick(int activityCode){
            this.code = activityCode;
        }

        @Override
        public void onClick(View view) {
            startCamera(code);
        }
    }

    private void startCamera(int code){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(act.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                act.startActivityForResult(takePictureIntent, code);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void startGallery(int code){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, act.getString(R.string.pilih_gambar));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        act.startActivityForResult(Intent.createChooser(chooserIntent, act.getString(R.string.pilih_gambar)), code);
    }

    public void saveCamera(Field field){
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
        int orientation = getOrientation(mCurrentPhotoPath);

        if(orientation!=0){
            Bitmap bMapRotate;
            Matrix mat=new Matrix();
            mat.postRotate(orientation);
            bMapRotate = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), mat, true);
            bm.recycle();
            bm=bMapRotate;
        }

        saveImage(field,bm);
    }

    public void saveGallery(Field field,Uri uri){
        Bitmap bm = getPictureFromMedia(uri);
        int orientation = getOrientation(uri);
        if(orientation!=0){
            Bitmap bMapRotate;
            Matrix mat=new Matrix();
            mat.postRotate(orientation);
            bMapRotate = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), mat, true);
            bm.recycle();
            bm=bMapRotate;
        }
        saveImage(field,bm);
    }

    private void saveImage(Field field,Bitmap bm){
        if(field.getOrientation()== Field.Orientation.SQUARE) {
            int min = bm.getWidth()<bm.getHeight()? bm.getWidth():bm.getHeight();
            bm = Bitmap.createBitmap(bm,(bm.getWidth()-min)/2,
                    (bm.getHeight()-min)/2
                    ,min, min);
            if (min > MAX_SIZE_SQUARE) {
                bm = Bitmap.createScaledBitmap(bm, MAX_SIZE_SQUARE, MAX_SIZE_SQUARE, true);
            }
        }else if(field.getOrientation()== Field.Orientation.POTRAIT){
            if(bm.getHeight()>MAX_SIZE_TOP){
                bm = Bitmap.createScaledBitmap(bm, MAX_SIZE_BOTTOM, MAX_SIZE_TOP, true);
            }
            if(bm.getWidth()>bm.getHeight()){ //square it down
                int min = bm.getHeight();
                bm = Bitmap.createBitmap(bm,(bm.getWidth()-min)/2,
                        (bm.getHeight()-min)/2
                        ,min, min);
            }
        }else if(field.getOrientation()== Field.Orientation.LANDSCAPE){
            if(bm.getWidth()>MAX_SIZE_TOP){
                bm = Bitmap.createScaledBitmap(bm, MAX_SIZE_TOP,MAX_SIZE_BOTTOM, true);
            }
            if(bm.getHeight()>bm.getWidth()){ //square it down
                int min = bm.getWidth();
                bm = Bitmap.createBitmap(bm,(bm.getWidth()-min)/2,
                        (bm.getHeight()-min)/2
                        ,min, min);
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = field.getName()+"_"+timestamp+"_"+Helper.randomString();
        File dir = act.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File f = new File(dir,filename);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e){ e.printStackTrace();}


        //if replaced delete the old file
        String oldpath = field.getValue();
        if(oldpath!=null){
            File oldFile = new File(oldpath);
            if(oldFile.delete()){
                Log.i("jeki",oldpath+" deleted");
            }
        }
        String path = f.getPath();
        field.setValue(path);
        field.showImage();
        btnDelArray.get(field.getName()).setVisibility(VISIBLE);
    }

    private int getOrientation(String imagePath){
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private Bitmap getPictureFromMedia(Uri uri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = act.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }

    private int getOrientation(Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = act.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    /****** PICKER LISTENER ******/
    private class PickerClickListener implements OnFocusChangeListener{
        private final int code;
        private final Field field;

        PickerClickListener(int code,Field field){
            this.code = code;
            this.field = field;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                onPickerClick(code,field);
                requestFocus();
            }
        }
    }
    // OVERRIDE THIS!
    protected void onPickerClick(int code,Field field){
        throw new NotImplementedException("Picker "+field.getName()+" not implemented");
    }

    public String getPickerValue(Field f,String value) {
        throw new NotImplementedException("Picker value of "+f.getName()+" not implemented");
    }


    /**** DATE ***/
    private class DateFocusListener implements OnFocusChangeListener{
        private final Field field;

        DateFocusListener(Field field){
            this.field = field;
        }
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                Bundle arg = new Bundle();
                final Calendar c = Calendar.getInstance();
                if(field.getValue()!=null){
                    String dateString = field.getValue();
                    try {
                        Date thedate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                        c.setTime(thedate);
                    } catch (ParseException e) {
                        //ignore
                    }
                }
                arg.putInt("year",c.get(Calendar.YEAR));
                arg.putInt("month",c.get(Calendar.MONTH));
                arg.putInt("day",c.get(Calendar.DAY_OF_MONTH));

                arg.putString("name",field.getName());

                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.setArguments(arg);
                dateFragment.show(act.getSupportFragmentManager(), "datePicker");
                requestFocus();
            }
        }
    }

    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle b = getArguments();
            int year = b.getInt("year");
            int month = b.getInt("month");
            int day = b.getInt("day");

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.DATE,d);
            c.set(Calendar.MONTH,m);
            c.set(Calendar.YEAR,y);
            String dateText = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());

            String name = getArguments().getString("name");
            EditText et = ((EditText) views.get(name));
            et.setText(dateText);

            fields.getField(name).setValue(dateText);
        }
    }






    ///// save ////////

    public boolean save() {
        View view = act.getCurrentFocus();
        if (view != null) { // hide keyboard
            InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(loadingState){
            Toast.makeText(act,"Proses penyimpanan sedang berlangsung...",Toast.LENGTH_SHORT).show();
            return false;
        }
        try{
            Bundle b = getBundleValue();
            loading(true);
            doSave(b);
            return true;
        }catch (NullPointerException e){ e.printStackTrace(); return false;}
    }

    private void loading(boolean b) {
        loadingState=b;
        listener.setLoading(b);
    }


    protected void doSave(Bundle data){
        if(data==null) return;

        switch (saveType){
            case BOTH:
            case SERVER:
                saveToServer(data);
                break;
            case LOCAL:
                String id = saveToLocal(null,data);
                onSaveSuccess(id);
        }
    }




    private void saveToServer(Bundle data){
        Bundle newData = new Bundle();
        for(Field field: fields){
            if(field.getName()==null) continue;
            String value = data.getString(field.getName());
            Bundle b = new Bundle();
            if(field.getType()== Field.Type.IMAGE){

                String master = imageMaster.getString(field.getName());
                // if picture not changing dont upload it
                if(master!=null ){
                    if(master.equals(value)) continue;
                    // if picture deleted
                    if(value==null || value.equals("")){
                        b.putSerializable("type", InternetConnection.Type.TEXT);
                        b.putString("value","");
                        newData.putBundle(field.getName(),b);
                        continue;
                    }
                }
            }

            b.putString("value",value);
            if(field.getType()== Field.Type.IMAGE){
                b.putSerializable("type", InternetConnection.Type.FILE);
            }else{
                b.putSerializable("type", InternetConnection.Type.TEXT);
            }

            newData.putBundle(field.getName(),b);
        }

        if(action== Action.ADD){
            insertToServer(newData);
        }else{
            updateToServer(getDataId(),newData);
        }
    }

    protected void insertToServer(Bundle data){
        getInternetConnection(data,this).insert();
    }

    protected void updateToServer(String id,Bundle data){
        getInternetConnection(data,this).update(id);
    }

    private String saveToLocal(@Nullable String id, Bundle b){
        if(action== Action.ADD){
            return insertToLocal(id,b);
        }else{
            return updateToLocal(id,b);
        }
    }

    protected String insertToLocal(@Nullable  String id,Bundle b){
        if(model==null) throw new NullPointerException("call setmodel() first");
        model.setData(b);
        return model.insert(id);
    }

    protected String updateToLocal(@Nullable  String id,Bundle b){
        model.setData(b);
        return model.update(id);
    }



    public void onServerSuccess(JSONObject result, Bundle data){
        try {
            if(!result.getBoolean("success")){
                throw new Exception(result.getString("message"));
            }
            String id = result.getString("id");
            if(saveType== Form.SaveType.BOTH){
                saveToLocal(id,data);
            }
            onSaveSuccess(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onSaveSuccess(String id){
        clearUnusedImage();
        loading(false);
        listener.onSaveSuccess(id);
    }

    private void clearUnusedImage(){
        for(Field field: fields) {
            if (field.getType() != Field.Type.IMAGE) continue;

            String master = imageMaster.getString(field.getName());
            // if picture null but master not null, delete it
            if (master != null && field.getValue()==null){
                File oldFile = new File(master);
                if(oldFile.delete()){
                    Log.i("jeki",master+" deleted");
                }
            }
        }
    }

    public void onTimeout(){
        loading(false);
        Toast.makeText(act,"Sayangnya, Penyimpanan Gagal...",Toast.LENGTH_LONG).show();
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataId(){
        if(dataId==null){
            throw new NullPointerException("Data id not set");
        }
        return dataId;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setModel(FormModel model) {
        this.model = model;
    }



    protected abstract FormInternetConnection getInternetConnection(Bundle b, FormInternetConnection.Listener listener);

    public interface Listener{
        void setLoading(boolean b);
        void onSaveSuccess(String id);
    }
}
