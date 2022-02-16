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
import com.google.firebase.auth.SessionCookieOptions;
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
      = "thanakorn-firebase-be3-firebase-adminsdk-pu8qn-46c9e75cc8.json";

  public static void main(String[] args) throws Exception {
    FirebaseAuthService authService = new FirebaseAuthService();

    // safePublishInternal(authService);
    authService.verifyTokenId("eyJhbGciOiJSUzI1NiIsImtpZCI6ImFlNTJiOGQ4NTk4N2U1OWRjYWM2MmJlNzg2YzcwZTAyMDcxN2I0MTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vdGhhbmFrb3JuLWZpcmViYXNlLWJlIiwiYXVkIjoidGhhbmFrb3JuLWZpcmViYXNlLWJlIiwiYXV0aF90aW1lIjoxNjMyMTgxODQ2LCJ1c2VyX2lkIjoiT2o3U3FydzdWWlUyWUVhWmtLUVdNU3dibVhKMiIsInN1YiI6Ik9qN1Nxcnc3VlpVMllFYVprS1FXTVN3Ym1YSjIiLCJpYXQiOjE2MzIxODE4NDcsImV4cCI6MTYzMjE4NTQ0NywiZW1haWwiOiJ0aGFuYWtvcm4uYW1wMTBAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsidGhhbmFrb3JuLmFtcDEwQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19.uqX1AUb10X6EwLtRguUXK3pQBcsXrueW9haUbjGH6Xj4YB1PaftJnqdFNg6CNnS9hore0-L1KQZbhPEbvsog3vJbxKmrlFniP7lWF2L4yyrPaNL_ox8bRCWWrdDTjn26yj6mF2T56WKPHkdOln-Mx_NGmGVnTQryQUOt3qn0t0n3_NLhI64htJ4WKtMghTmMU8n48xpb77xqDae4ozvaFgVCUaCCyA_vYEt3NdGFjIwD4knj_Dtu4bktMx9cn3EpzPhyIKJHM6iCYe1X74zvTVGHtaYWdyu4-rHxlAz9ph7HxA38jh33Gt9dSdNbXrroFmfH3cdMQ2jaXYx2sXnubA");
    createSessionCookie(
        "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFlNTJiOGQ4NTk4N2U1OWRjYWM2MmJlNzg2YzcwZTAyMDcxN2I0MTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vdGhhbmFrb3JuLWZpcmViYXNlLWJlIiwiYXVkIjoidGhhbmFrb3JuLWZpcmViYXNlLWJlIiwiYXV0aF90aW1lIjoxNjMyMTgxODQ2LCJ1c2VyX2lkIjoiT2o3U3FydzdWWlUyWUVhWmtLUVdNU3dibVhKMiIsInN1YiI6Ik9qN1Nxcnc3VlpVMllFYVprS1FXTVN3Ym1YSjIiLCJpYXQiOjE2MzIxODE4NDcsImV4cCI6MTYzMjE4NTQ0NywiZW1haWwiOiJ0aGFuYWtvcm4uYW1wMTBAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsidGhhbmFrb3JuLmFtcDEwQGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19.uqX1AUb10X6EwLtRguUXK3pQBcsXrueW9haUbjGH6Xj4YB1PaftJnqdFNg6CNnS9hore0-L1KQZbhPEbvsog3vJbxKmrlFniP7lWF2L4yyrPaNL_ox8bRCWWrdDTjn26yj6mF2T56WKPHkdOln-Mx_NGmGVnTQryQUOt3qn0t0n3_NLhI64htJ4WKtMghTmMU8n48xpb77xqDae4ozvaFgVCUaCCyA_vYEt3NdGFjIwD4knj_Dtu4bktMx9cn3EpzPhyIKJHM6iCYe1X74zvTVGHtaYWdyu4-rHxlAz9ph7HxA38jh33Gt9dSdNbXrroFmfH3cdMQ2jaXYx2sXnubA");
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

  private static void verifyTokenId(FirebaseAuthService authService, String tokenId)
      throws Exception {
    FirebaseToken token = authService.verifyTokenId(tokenId);
    System.out.println(token.getUid());
  }

  private static String createSessionCookie(String tokenId) throws Exception {
    SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(360000L).build();
    String cookie = FirebaseAuth.getInstance().createSessionCookie(tokenId, options);
    System.out.println("Cookie: " + cookie);
    return cookie;
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
      url = "https://thanakorn-firebase-fe3.ue.r.appspot.com/";
    } else {
      url = "http://localhost:4200/";
      url = "https://thanakorn-firebase-fe3.ue.r.appspot.com/";
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
        .setProjectId("thanakorn-firebase-be3")
        .setServiceAccountId("117041882942638156691")
        .build();
    return FirebaseApp.initializeApp(options);
  }
}
