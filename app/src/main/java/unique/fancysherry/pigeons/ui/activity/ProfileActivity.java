package unique.fancysherry.pigeons.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.socket.client.Socket;
import okhttp3.Request;
import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.util.LogUtil;
import unique.fancysherry.pigeons.util.config.SApplication;

/**
 * Created by fancysherry on 15-12-22.
 */
public class ProfileActivity extends ToolbarCastActivity {
    @InjectView(R.id.float_camera)
    FloatingActionButton float_camera;
    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;


    private Uri photoUri;
    /**
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /**
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    /**
     * 获取到的图片路径
     */
    private String picPath = "";
    private Socket mSocket = SocketIOUtil.getSocket();
    private String sessionid;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.inject(this);
        sessionid = AccountManager.getInstance().sessionid;
        activity = this;
    }


    @OnClick({R.id.float_camera})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.float_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("选择发送图片方式?")
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                takePhoto();
                            }
                        })
                        .setNegativeButton("本地", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pickPhoto();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
    }


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不使用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 点击取消按钮
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // 可以使用同一个方法，这里分开写为了防止以后扩展不同的需求
        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 如果是直接从相册获取
                doPhoto(requestCode, data);
                break;
            case SELECT_PIC_BY_TACK_PHOTO:// 如果是调用相机拍照时
                doPhoto(requestCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode, Intent data) {

        // 从相册取图片，有些手机有异常情况，请注意
        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            if (photoUri == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String[] pojo = {MediaStore.MediaColumns.DATA};
        // The method managedQuery() from the type Activity is deprecated
        // Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        Cursor cursor = activity.getContentResolver().query(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);

            // 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
            if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                cursor.close();
            }
        }

        // 如果图片符合要求将其上传到服务器
        if (picPath != null && (picPath.endsWith(".png") ||
                picPath.endsWith(".PNG") ||
                picPath.endsWith(".jpg") ||
                picPath.endsWith(".JPG"))) {


            BitmapFactory.Options option = new BitmapFactory.Options();
            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
            option.inSampleSize = 1;
            // 根据图片的SDCard路径读出Bitmap
            Bitmap bm = BitmapFactory.decodeFile(picPath, option);
            // 显示在图片控件上
//            user_information_portrait.setImageURI(photoUri);
            LogUtil.e("before thread image address   " + picPath);
            // pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
            // new Thread(uploadImageRunnable).start();
            uploadFile(picPath, true);
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }

    }


    public void uploadFile(final String file_path, final boolean isimage) {
        File file = new File(file_path);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.e("start upload");
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", sessionid);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data");

        String url = Constants.BASE_URL + "avatar";

        OkHttpUtils.post()//
                .addFile("avatar", file_path, file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request pRequest, Exception e) {
                    }

                    @Override
                    public void onResponse(String s) {
                        LogUtil.e(s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String username = AccountManager.getInstance().getCurrentUser().getAccountBean().username;
                                avatar.setImageURI(Uri.parse(Constants.BASE_URL + "avatar/" + username));
                            }
                        });
                    }
                });
    }


}
