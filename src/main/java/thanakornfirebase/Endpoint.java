package thanakornfirebase;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.repackaged.com.google.common.flogger.FluentLogger;
import com.google.firebase.auth.UserRecord;
import java.util.Optional;

/**
 * Add your first API methods in this class, or you may create another class. In that case, please
 * update your web.xml accordingly.
 **/
@Api(name = "skeleton",
    version = "v1")
public class Endpoint {
  private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
  private static final FirebaseAuthService AUTH_SERVICE = new FirebaseAuthService();

  @ApiMethod(name = "hello")
  public Message hello(@Named("name") @Nullable String name) {
    Message message = new Message();
    message.setMessage("Hello " + name + "!");
    return message;
  }

  /**
   * Returns a login link response.
   *
   * @param email
   * @return
   */
  // Used by the front end login page.
  // curl -X POST https://thanakorn-firebase-be.appspot.com/_ah/api/skeleton/v1/login?email=youremail@gmail.com
  @ApiMethod(name = "login")
  public void login(@Named("email") @Nullable String email) {
    LOGGER.atInfo().log("Getting login link for email %s.", email);
    try {
      Optional<UserRecord> userRecord = AUTH_SERVICE.getUserByEmail(email);
      if (!userRecord.isPresent()) {
        LOGGER.atWarning().log("Email %s does not exist.", email);
        return;
      }
      String link = AUTH_SERVICE.generateEmailLinkForSignIn(email);
      AUTH_SERVICE.sendCustomEmail(email, link);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Notifies the recipient of the login link.
   *
   * <p>Internally creates a Firebase account if it doesn't exist yet.</p>
   *
   * @param email
   */
  // Will be used internally in SafePublish operation.
  // curl -X POST https://thanakorn-firebase-be.appspot.com/_ah/api/skeleton/v1/notify?email=youremail@gmail.com
  @ApiMethod(name = "notify")
  public void notify(@Named("email") @Nullable String email) {
    LOGGER.atInfo().log("Getting login link for email %s.", email);
    try {
      Optional<UserRecord> userRecord = AUTH_SERVICE.getUserByEmail(email);
      if (!userRecord.isPresent()) {
        AUTH_SERVICE.createUser(email);
      }
      String link = AUTH_SERVICE.generateEmailLinkForSignIn(email);
      AUTH_SERVICE.sendCustomEmail(email, link);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
