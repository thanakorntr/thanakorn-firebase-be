package thanakornfirebase;

import com.google.appengine.repackaged.com.google.common.flogger.FluentLogger;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AppEngineMail {
  private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

  private static final String SENDER_EMAIL = "no-reply@thanakorn-firebase-be2.appspotmail.com";

  static void sendEmail(String recipientAddress, String link) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(SENDER_EMAIL));
      msg.addRecipient(Message.RecipientType.TO,
          new InternetAddress(recipientAddress));
      msg.setSubject("Firebase Login Link");
      msg.setText(link);
      Transport.send(msg);
    } catch (MessagingException e) {
      LOGGER.atWarning().withCause(e).log();
    }
  }
}
