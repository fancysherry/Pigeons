package unique.fancysherry.pigeons.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.Constants;
import unique.fancysherry.pigeons.io.SocketIOUtil;
import unique.fancysherry.pigeons.io.model.Message;
import unique.fancysherry.pigeons.ui.adapter.ChatAdapter;
import unique.fancysherry.pigeons.util.LogUtil;
import unique.fancysherry.pigeons.util.system.KeyBoardUtils;
import unique.fancysherry.pigeons.util.system.ScreenUtils;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends ToolbarCastActivity
    implements
      EmojiconGridFragment.OnEmojiconClickedListener,
      EmojiconsFragment.OnEmojiconBackspaceClickedListener {
  @InjectView(R.id.chat_toolbar)
  Toolbar chat_toolbar;
  @InjectView(R.id.chat_list)
  RecyclerView chat_list;

  @InjectView(R.id.chat_emoji)
  ImageView chat_emoji;
  @InjectView(R.id.chat_other)
  ImageView chat_other;
  @InjectView(R.id.chat_send)
  ImageView chat_send;

  @InjectView(R.id.chat_text)
  EmojiconEditText chat_text;
  @InjectView(R.id.content_lay)
  LinearLayout contentLay;

  @InjectView(R.id.appbarlayout_content_framlayout)
  LinearLayout emojiconsContainer;

  @InjectView(R.id.emojicons_layout)
  RelativeLayout emojiconsLayout;
  @InjectView(R.id.function_layout)
  RelativeLayout function_layout;
  @InjectView(R.id.picture)
  ImageView picture;
  @InjectView(R.id.function_and_emotion_parent)
  FrameLayout function_and_emotion_parent;
  public boolean isgroup = false;
  public long gid;

  private final LayoutTransition transitioner = new LayoutTransition();// 键盘和表情切换
  private int emotionHeight;
  private EmojiconsFragment emojiconsFragment;


  private Socket mSocket = SocketIOUtil.getSocket();


  private String current_chat_username;
  private Message current_send_message = new Message();
  private ChatAdapter chatAdapter;
  private Activity activity;
  private String sessionid;
  private List<Message> messageList = new ArrayList<>();
  private String current_username =
      AccountManager.getInstance().getCurrentUser().getAccountBean().username;

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
   * 使用相册中的文件
   */
  public static final int SELECT_PIC_BY_PICK_FILE = 3;
  /**
   * 获取到的图片路径
   */
  private String picPath = "";


  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (isgroup) {
      mSocket.off(Constants.EVENT_GROUP_CHAT, onGroupChat);
      mSocket.off(Constants.EVENT_GROUP_MESSAGE, onGroupMessage);
    } else {
      mSocket.off(Constants.EVENT_CHAT, onChat);
      mSocket.off(Constants.EVENT_MESSAGE, onMessage);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    ButterKnife.inject(this);
    initializeToolbar(chat_toolbar);
    current_chat_username = getIntent().getStringExtra("username");
    activity = this;

    gid = getIntent().getLongExtra("gid", -1);
    if (gid != -1)
      isgroup = true;

    sessionid = AccountManager.getInstance().sessionid;
    if (isgroup) {
      LogUtil.e("group");
      mSocket.on(Constants.EVENT_GROUP_MESSAGE, onGroupMessage);
      mSocket.on(Constants.EVENT_GROUP_CHAT, onGroupChat);
    } else {
      LogUtil.e("no group");
      mSocket.on(Constants.EVENT_CHAT, onChat);
      mSocket.on(Constants.EVENT_MESSAGE, onMessage);
    }
    initView();
    loadEmojiAnimator(savedInstanceState);
    setData();
  }

  @OnClick({R.id.chat_other, R.id.chat_send, R.id.chat_emoji, R.id.chat_text, R.id.picture,
      R.id.file})
  public void click(View view) {
    switch (view.getId()) {
      case R.id.chat_send:
        String message_text = chat_text.getText().toString();
        attemptSend(message_text);
        break;
      case R.id.chat_emoji:
        if (emojiconsLayout.isShown()) {
          hideEmotionView(true);
        } else if (function_layout.isShown()) {
          hideFunctionView(false);
          showEmotionView(ScreenUtils.isKeyBoardShow(this));
        } else {
          hideKeyBoard();
          showEmotionView(ScreenUtils.isKeyBoardShow(this));
        }
        break;
      case R.id.chat_other:
        if (function_layout.isShown()) {
          hideFunctionView(true);
        } else if (emojiconsLayout.isShown()) {
          hideEmotionView(false);
          showFunctionView(ScreenUtils.isKeyBoardShow(this));
        } else {
          hideKeyBoard();
          showFunctionView(ScreenUtils.isKeyBoardShow(this));
        }
        break;
      case R.id.chat_text:
        if (emojiconsLayout.isShown()) {
          hideEmotionView(true);
        } else if (function_layout.isShown()) {
          hideFunctionView(true);
        }
        break;
      case R.id.picture:
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
      case R.id.file:
        pickFile();
        break;
      case R.id.voice:
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

  /**
   * 从相册中文件
   */
  private void pickFile() {
    Intent intent = new Intent();
    // 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, SELECT_PIC_BY_PICK_FILE);
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
      case SELECT_PIC_BY_PICK_FILE:// 如果是调用相机拍照时
        GetFile(data);
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
      // user_information_portrait.setImageURI(photoUri);
      LogUtil.e("before thread image address   " + picPath);
      // pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
      // new Thread(uploadImageRunnable).start();
      uploadFile(picPath, true);
    } else {
      Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
    }

  }


  private void GetFile(Intent data) {
    // 从相册取文件，有些手机有异常情况，请注意
    if (data == null) {
      Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
      return;
    }
    photoUri = data.getData();
    if (photoUri == null) {
      Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
      return;
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
    BitmapFactory.Options option = new BitmapFactory.Options();
    // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图
    option.inSampleSize = 1;
    // 根据图片的SDCard路径读出Bitmap
    Bitmap bm = BitmapFactory.decodeFile(picPath, option);
    // 显示在图片控件上
    // user_information_portrait.setImageURI(photoUri);
    LogUtil.e("before thread image address   " + picPath);
    // pd = ProgressDialog.show(mContext, null, "正在上传图片，请稍候...");
    // new Thread(uploadImageRunnable).start();
    uploadFile(picPath, false);
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

    String url = Constants.BASE_URL + "upload";

    OkHttpUtils.post()//
        .addFile("file", file_path, file)//
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
            try {
              JSONObject mJSONObject = new JSONObject(s);
              if (isimage)
                attemptSend("image:" + mJSONObject.getString("hash"));
              else
                attemptSend("file:" + mJSONObject.getString("hash") + "," + file_path);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }


  @Override
  public void initView() {
    initAdapter();
  }

  public void initAdapter() {
    chat_list.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.VERTICAL, false));
    chatAdapter = new ChatAdapter(this);
    chat_list.setAdapter(chatAdapter);

  }

  @Override
  public void initToolbar() {

  }

  @Override
  public void setData() {

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }


  private void attemptSend(String message) {
    // Store values at the time of the login attempt.
    LogUtil.e("send:" + message);

    if (isgroup) {
      if (!TextUtils.isEmpty(message)) {
        JSONObject data = new JSONObject();
        try {
          data.put("sessionId", sessionid);
          data.put("gid", gid);
          data.put("message", message);
          current_send_message.from = current_username;
          current_send_message.gid = gid;
          current_send_message.message = message;
          if (message.startsWith("image:"))
            current_send_message.type = Message.Type.IMAGE;
          else if (message.startsWith("file:"))
            current_send_message.type = Message.Type.FILE;
          else
            current_send_message.type = Message.Type.TEXT;
        } catch (JSONException e) {
          e.printStackTrace();
        }
        mSocket.emit(Constants.EVENT_GROUP_CHAT, data);
      }
    } else {
      if (!TextUtils.isEmpty(message)) {
        JSONObject data = new JSONObject();
        try {
          data.put("sessionId", sessionid);
          data.put("to", current_chat_username);
          data.put("message", message);
          current_send_message.from = current_username;
          current_send_message.to = current_chat_username;
          current_send_message.message = message;
          if (message.startsWith("image:"))
            current_send_message.type = Message.Type.IMAGE;
          else if (message.startsWith("file:"))
            current_send_message.type = Message.Type.FILE;
          else
            current_send_message.type = Message.Type.TEXT;
        } catch (JSONException e) {
          e.printStackTrace();
        }
        mSocket.emit(Constants.EVENT_CHAT, data);
      }
    }
  }

  private Emitter.Listener onChat = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (args[0] == null)
            Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
          else {
            JSONObject data = (JSONObject) args[0];
            String err;
            LogUtil.e(data.toString());
            try {
              err = data.getString("err");
            } catch (JSONException e) {
              return;
            }
            if (err.equals("null")) {
              Message temp_message = new Message();
              temp_message.from = current_send_message.from;
              temp_message.to = current_send_message.to;
              temp_message.message = current_send_message.message;
              temp_message.type = current_send_message.type;
              temp_message.gid = current_send_message.gid;
              messageList.add(temp_message);
              chatAdapter.setData(messageList);
              Toast.makeText(activity, "success send", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(activity, "error send", Toast.LENGTH_SHORT).show();
            }
            LogUtil.e("chat end");
          }
        }
      });
    }
  };

  private Emitter.Listener onMessage = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (args[0] == null)
            Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
          else {
            JSONObject data = (JSONObject) args[0];
            String err;
            String from;
            String message;
            LogUtil.e(data.toString());
            try {
              err = data.getString("err");
              from = data.getString("from");
              message = data.getString("message");
            } catch (JSONException e) {
              return;
            }
            Message mMessage = new Message();
            mMessage.message = message;
            mMessage.from = from;
            if (mMessage.message.startsWith("image:"))
              mMessage.type = Message.Type.IMAGE_OTHER;
            else if (mMessage.message.startsWith("file:"))
              mMessage.type = Message.Type.FILE_OTHER;
            else
              mMessage.type = Message.Type.TEXT_OTHER;
            messageList.add(mMessage);
            if (err.equals("null")) {
              chatAdapter.setData(messageList);
              Toast.makeText(activity, "success get", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(activity, "error get", Toast.LENGTH_SHORT).show();
            }
            LogUtil.e("message end");
          }
        }
      });
    }
  };


  private Emitter.Listener onGroupChat = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (args[0] == null)
            Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
          else {
            JSONObject data = (JSONObject) args[0];
            String err;
            LogUtil.e(data.toString());
            try {
              err = data.getString("err");
            } catch (JSONException e) {
              return;
            }
            if (err.equals("null")) {
              messageList.add(current_send_message);
              chatAdapter.setData(messageList);
              Toast.makeText(activity, "success send", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(activity, "error send", Toast.LENGTH_SHORT).show();
            }
            LogUtil.e("chat end");
          }
        }
      });
    }
  };

  private Emitter.Listener onGroupMessage = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (args[0] == null)
            Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
          else {
            JSONObject data = (JSONObject) args[0];
            String err;
            String from;
            String message;
            LogUtil.e(data.toString());
            try {
              err = data.getString("err");
              from = data.getString("from");
              message = data.getString("message");
            } catch (JSONException e) {
              return;
            }
            Message mMessage = new Message();
            mMessage.message = message;
            mMessage.from = from;
            if (mMessage.message.startsWith("image:"))
              mMessage.type = Message.Type.IMAGE_OTHER;
            else if (mMessage.message.startsWith("file:"))
              mMessage.type = Message.Type.FILE_OTHER;
            else
              mMessage.type = Message.Type.TEXT_OTHER;
            messageList.add(mMessage);
            if (err.equals("null")) {
              chatAdapter.setData(messageList);
              Toast.makeText(activity, "success get", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(activity, "error get", Toast.LENGTH_SHORT).show();
            }
            LogUtil.e("message end");
          }
        }
      });
    }
  };


  public void loadEmojiAnimator(Bundle savedInstanceState) {
    /** 上下平移动画 **/
    ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY",
        ScreenUtils.getScreenHeight(), emotionHeight)
        .setDuration(transitioner.getDuration(LayoutTransition.APPEARING));
    transitioner.setAnimator(LayoutTransition.APPEARING, animIn);
    ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "translationY",
        emotionHeight,
        ScreenUtils.getScreenHeight())
        .setDuration(transitioner.getDuration(LayoutTransition.DISAPPEARING));
    transitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
    contentLay.setLayoutTransition(transitioner);
    /** 安全判断 有些情况会出现异常 **/
    if (savedInstanceState == null) {
      emojiconsFragment = EmojiconsFragment.newInstance(false);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.emojicons_layout, emojiconsFragment, "EmotionFragemnt").commit();
    } else {
      emojiconsFragment =
          (EmojiconsFragment) getSupportFragmentManager().findFragmentByTag("EmotionFragemnt");
    }
    /** 先弹出软键盘 **/
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    KeyBoardUtils.showKeyBoard(chat_text);
    chat_text.postDelayed(new Runnable() {

      @Override
      public void run() {
        unlockContainerHeightDelayed();
      }

    }, 200L);
  }

  @Override
  public void onEmojiconBackspaceClicked(View v) {
    EmojiconsFragment.backspace(chat_text);
  }

  @Override
  public void onEmojiconClicked(Emojicon emojicon) {
    EmojiconsFragment.input(chat_text, emojicon);
  }

  private void hideKeyBoard() {
    KeyBoardUtils.hideSoftInput(chat_text);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  /**
   * 隐藏emoji
   */
  private void hideEmotionView(boolean showKeyBoard) {
    if (emojiconsLayout.isShown()) {
      if (showKeyBoard) {
        Log.e("hide emotionview", "hide emotionview");
        LinearLayout.LayoutParams localLayoutParams =
            (LinearLayout.LayoutParams) emojiconsContainer.getLayoutParams();
        // localLayoutParams.height = emojiconsLayout.getTop();
        localLayoutParams.weight = 0.0F;

        emojiconsLayout.getLayoutParams().height = 0;

//        Log.e("function_and_emotion2", String.valueOf(function_and_emotion_parent.getHeight()));
        emojiconsLayout.setVisibility(View.GONE);
//        Log.e("function_and_emotion3", String.valueOf(function_and_emotion_parent.getHeight()));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        KeyBoardUtils.showKeyBoard(chat_text);
        chat_text.postDelayed(new Runnable() {
          @Override
          public void run() {
            unlockContainerHeightDelayed();
          }
        }, 200L);
      } else {
        emojiconsLayout.setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unlockContainerHeightDelayed();
      }
    }
  }

  private void showEmotionView(boolean showAnimation) {
    if (showAnimation) {
      transitioner.setDuration(200);
    } else {
      transitioner.setDuration(0);
    }
    int statusBarHeight = ScreenUtils.getStatusBarHeight();
    emotionHeight = ScreenUtils.getKeyboardHeight(this);

//    Log.e("function_and_emotion1", String.valueOf(function_and_emotion_parent.getHeight()));
    emojiconsLayout.getLayoutParams().height = emotionHeight;
    emojiconsLayout.setVisibility(View.VISIBLE);

    // 在5.0有navigationbar的手机，高度高了一个statusBar
    int lockHeight = ScreenUtils.getAppContentHeight(this);
    // lockHeight = lockHeight - statusBarHeight;
    lockContainerHeight(lockHeight);
  }

  private void showFunctionView(boolean showAnimation) {
    if (showAnimation) {
      transitioner.setDuration(200);
    } else {
      transitioner.setDuration(0);
    }
    int statusBarHeight = ScreenUtils.getStatusBarHeight();
    emotionHeight = ScreenUtils.getKeyboardHeight(this);

    function_layout.getLayoutParams().height = emotionHeight;
    function_layout.setVisibility(View.VISIBLE);

    // 在5.0有navigationbar的手机，高度高了一个statusBar
    int lockHeight = ScreenUtils.getAppContentHeight(this);
    // lockHeight = lockHeight - statusBarHeight;
    lockContainerHeight(lockHeight);
  }

  private void hideFunctionView(boolean showKeyBoard) {
    if (function_layout.isShown()) {
      if (showKeyBoard) {
        LinearLayout.LayoutParams localLayoutParams =
            (LinearLayout.LayoutParams) emojiconsContainer.getLayoutParams();
        // localLayoutParams.height = function_layout.getTop();
        localLayoutParams.weight = 0.0F;

        function_layout.getLayoutParams().height = 0;
        function_layout.setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        KeyBoardUtils.showKeyBoard(chat_text);
        chat_text.postDelayed(new Runnable() {
          @Override
          public void run() {
            unlockContainerHeightDelayed();
          }
        }, 200L);
      } else {
        function_layout.setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        unlockContainerHeightDelayed();
      }
    }
  }

  private void lockContainerHeight(int paramInt) {
    LinearLayout.LayoutParams localLayoutParams =
        (LinearLayout.LayoutParams) emojiconsContainer.getLayoutParams();
    localLayoutParams.height = paramInt;
    localLayoutParams.weight = 0.0F;
  }

  public void unlockContainerHeightDelayed() {
    ((LinearLayout.LayoutParams) emojiconsContainer.getLayoutParams()).weight = 1.0F;
  }
}
