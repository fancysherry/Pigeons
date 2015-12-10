package unique.fancysherry.pigeons.account;


import android.util.Log;
import android.widget.Toast;

public class UserBean {
  public AccountBean mAccountBean;

  private CookieHolder mCookieHolder;
  private String token;

  public UserBean(AccountBean accountBean) {
    this.mAccountBean = accountBean;
    this.mCookieHolder = new CookieHolder();
  }

  public CookieHolder getCookieHolder() {
    return mCookieHolder;
  }

  public void setAccountBean(AccountBean mAccountBean) {
    this.mAccountBean = mAccountBean;
  }

  public AccountBean getAccountBean() {
    return mAccountBean;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void updateToken() {
    loginBackground(mAccountBean.username, mAccountBean.pwd);
  }

  private void loginBackground(final String username, final String passwordEncrypted) {

  }

}
