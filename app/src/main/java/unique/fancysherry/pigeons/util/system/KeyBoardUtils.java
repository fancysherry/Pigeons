package unique.fancysherry.pigeons.util.system;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import unique.fancysherry.pigeons.util.config.PigeonsApplication;

/**
 * Created by fancysherry on 15-4-22.
 */
public class KeyBoardUtils {
  /**
   * 打卡软键盘
   *
   * @param mEditText
   *          输入框
   * @param mContext
   *          上下文
   */
  public static void openKeybord(EditText mEditText, Context mContext)
  {
    InputMethodManager imm = (InputMethodManager) mContext
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  /**
   * 关闭软键盘
   *
   * @param mEditText
   *          输入框
   * @param mContext
   *          上下文
   */
  public static void closeKeybord(EditText mEditText, Context mContext) {
    InputMethodManager imm = (InputMethodManager) mContext
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
  }

  /**显示键盘**/
  public static void showKeyBoard(final View paramEditText) {
    paramEditText.requestFocus();
    paramEditText.post(new Runnable() {
      @Override
      public void run() {
        ((InputMethodManager) PigeonsApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramEditText, 0);
      }
    });
  }

  /**关闭键盘**/
  public static void hideSoftInput(View paramEditText) {
    ((InputMethodManager) PigeonsApplication.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
  }

}
