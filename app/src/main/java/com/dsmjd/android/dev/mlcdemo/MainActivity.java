package com.dsmjd.android.dev.mlcdemo;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SELECT_PICTURE = 0;
    private static final int SELECT_CAMER = 1;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//get NavigationView Obj
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

//get NavigationView's HeaderLayout View
//View header_view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View header_view = navigationView.getHeaderView(0);

//get HeaderLayout("nav_header_main")'s ImageView(header_imageView) obj
        imageView = (ImageView) header_view.findViewById(R.id.header_imageView);
//set imageView OnClick listener.
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = {"相册", "x相机"};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择图片来源")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == SELECT_PICTURE) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent, "选择图片"), SELECT_PICTURE);
                                } else {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, SELECT_CAMER);
                                }
                            }
                        })
                        .create().show();
            }
        });

//set navigationView's Items's Selected Listener.
        navigationView.setNavigationItemSelectedListener(this);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery(uri,
//                    proj,                 // Which columns to return
//                    null,       // WHERE clause; which rows to return (all rows)
//                    null,       // WHERE clause selection arguments (none)
//                    null);                 // Order-by clause (ascending by name)
//
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//
//            String path = cursor.getString(column_index);
//            Bitmap bmp = BitmapFactory.decodeFile(path);
//            imageView.setImageBitmap(bmp);
//            System.out.println("the path is :" + path);
//        } else {
//            Toast.makeText(MainActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //选择图片
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            Bitmap bmp = null;
            try {
                if (bmp != null)//如果不释放的话，不断取图片，将会内存不够
                    bmp.recycle();
                bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("the bmp toString: " + bmp);
            imageView.setMaxHeight(50);
            imageView.setImageBitmap(bmp);
        } else {
            Toast.makeText(MainActivity.this, "请重新选择图片", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * The backButton Action.onBackPressed.
     */
    @Override
    public void onBackPressed() {
        //get DrawerLayout Obj.(navigationView)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //check if Opened
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // if(is Opened) close.
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //if already closed. Call system back action.
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.f
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(); //调用照相机
            intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            CameraFragment fragment = new CameraFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_layout, fragment).commit();

        } else if (id == R.id.nav_slideshow) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
