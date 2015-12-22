package unique.fancysherry.pigeons.io;

/**
 * Created by fancysherry on 15-12-10.
 */
public class Constants {
  public static final String BASE_URL = "http://t0.evshiron.info:8100/";

  /**
   * http api
   */
  public static final String AVATAR_UPLOAD = "avatar/upload";
  public static final String AVATAR_GET = "avatar/";
  public static final String FILE_UPLOAD = "file/upload";
  public static final String FILE_GET = "file/";

  /**
   * socket.io api
   */
  public static final String EVENT_SESSION = "session";
  public static final String EVENT_REGISTER = "register";
  public static final String EVENT_LOGIN = "login";
  public static final String EVENT_USER_SERACH = "user.search";
  public static final String EVENT_CONTACT_ADD = "contact.add";
  public static final String EVENT_PROFILE_GET = "profile.get";
  public static final String EVENT_PROFILE_EDIT = "profile.edit";
}
