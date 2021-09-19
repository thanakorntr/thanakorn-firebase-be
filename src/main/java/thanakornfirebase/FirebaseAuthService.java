package thanakornfirebase;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.com.google.common.flogger.FluentLogger;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class FirebaseAuthService {
  private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

  // For App Engine, these won't be used.
  private static final String SENDER_EMAIL = "YOUR_SENDER_EMAIL_FOR_LOCAL_RUN";
  private static final String SENDER_EMAIL_PASSWORD = "YOUR_SENDER_EMAIL_PASSWORD_FOR_LOCAL_RUN";

  private static final String BE_SERVICE_ACCOUNT_FILE_NAME
      = "thanakorn-firebase-be-firebase-adminsdk-hkyjz-8b36476567.json";

  public static void main(String[] args) throws Exception {
    FirebaseAuthService authService = new FirebaseAuthService();

    safePublishInternal(authService);
    // verifyTokenId(authService);
  }

  private static void safePublishInternal(FirebaseAuthService authService) throws Exception {
    String recipientUserEmail = "thanakorn.amp10@gmail.com";
    Optional<UserRecord> userRecord = authService.getUserByEmail(recipientUserEmail);
    if (!userRecord.isPresent()) {
      authService.createUser(recipientUserEmail);
    }
    String link = authService.generateEmailLinkForSignIn(recipientUserEmail);
    authService.sendCustomEmail(recipientUserEmail, link);
  }

  private static void verifyTokenId(FirebaseAuthService authService, String tokenId) throws Exception {
    FirebaseToken token = authService.verifyTokenId(tokenId);
    System.out.println(token.getUid());
  }

  FirebaseAuthService() {
    try {
      initializeFirebaseApp();
    } catch (IOException e) {
      LOGGER.atSevere().withCause(e).log("Error initializing Firebase app.");
      throw new RuntimeException(e);
    }
  }

  // https://firebase.google.com/docs/auth/admin/email-action-links#generate_email_link_for_sign-in
  String generateEmailLinkForSignIn(String recipientEmail) throws FirebaseAuthException {
    String url;
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      url = "https://thanakorn-firebase-fe.uk.r.appspot.com/";
    } else {
      url = "http://localhost:4200/";
    }
    ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
        .setUrl(url)
        .build();
    return FirebaseAuth.getInstance().generateSignInWithEmailLink(
        recipientEmail, actionCodeSettings);
  }

  Optional<UserRecord> getUserByEmail(String email) {
    try {
      UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
      return Optional.of(userRecord);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  UserRecord createUser(String email)
      throws InterruptedException, ExecutionException {
    CreateRequest request = new CreateRequest()
        .setEmail(email)
        .setDisabled(false);
    return FirebaseAuth.getInstance().createUserAsync(request).get();
  }

  void sendCustomEmail(String emailAddress, String link) {
    if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
      AppEngineMail.sendEmail(emailAddress, link);
    } else {
      Email email = EmailBuilder.startingBlank()
          .from(SENDER_EMAIL)
          .to(emailAddress)
          .withSubject("Firebase Login Link")
          .withPlainText(link)
          .buildEmail();
      MailerBuilder
          .withSMTPServer("smtp.gmail.com", 25, SENDER_EMAIL, SENDER_EMAIL_PASSWORD)
          .withTransportStrategy(TransportStrategy.SMTP_TLS)
          .buildMailer()
          .sendMail(email);
    }
  }

  UserRecord getUserById(String uid)
      throws InterruptedException, ExecutionException {
    return FirebaseAuth.getInstance().getUserAsync(uid).get();
  }

  FirebaseToken verifyTokenId(String tokenId) throws ExecutionException, InterruptedException {
    return FirebaseAuth.getInstance().verifyIdTokenAsync(tokenId).get();
  }

  private static FirebaseApp initializeFirebaseApp() throws IOException {
    InputStream is = FirebaseAuthService.class.getClassLoader()
        .getResourceAsStream(BE_SERVICE_ACCOUNT_FILE_NAME);
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(is))
        .setProjectId("thanakorn-firebase-be")
        .setServiceAccountId("112670844804760466853")
        .build();
    return FirebaseApp.initializeApp(options);
  }
}
