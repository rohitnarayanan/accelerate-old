package accelerate.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * PUT DESCRIPTION HERE
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Sep 14, 2009
 */

public class AccelerateEmail extends JavaMailSenderImpl {
	/**
	 *
	 */
	private MimeMessage mimeMessage = null;

	/**
	 *
	 */
	private MimeMessageHelper mimeMessageHelper = null;

	/**
	 * @throws MessagingException
	 */
	public AccelerateEmail() throws MessagingException {
		this.mimeMessage = createMimeMessage();
		this.mimeMessageHelper = new MimeMessageHelper(this.mimeMessage, true);
	}

	/**
	 * @param aAttachName
	 * @param aAttachFiles
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void addAttachments(String aAttachName, File... aAttachFiles) throws IOException, MessagingException {
		File targetFile = FileUtil.zipFiles(aAttachName, aAttachFiles);
		this.mimeMessageHelper.addAttachment(aAttachName, targetFile);
	}

	/**
	 * @param aAttachName
	 * @param aAttachFile
	 * @throws MessagingException
	 */
	public void addAttachment(String aAttachName, File aAttachFile) throws MessagingException {
		this.mimeMessageHelper.addAttachment(aAttachName, aAttachFile);
	}

	/**
	 */
	public void useSSL() {
		getJavaMailProperties().put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		getJavaMailProperties().put("mail.smtp.auth", "true");
		getJavaMailProperties().put("mail.smtp.port", "465");
		getJavaMailProperties().put("mail.smtp.socketFactory.port", "465");
		getJavaMailProperties().put("mail.smtp.socketFactory.fallback", "false");
		getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
	}

	/**
	 * @param aFrom
	 * @throws MessagingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#setFrom(java.lang.String)
	 */
	public void setFrom(String aFrom) throws MessagingException {
		this.mimeMessageHelper.setFrom(aFrom);
	}

	/**
	 * @param aFrom
	 * @param aPersonal
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#setFrom(java.lang.String,
	 *      java.lang.String)
	 */
	public void setFrom(String aFrom, String aPersonal) throws MessagingException, UnsupportedEncodingException {
		this.mimeMessageHelper.setFrom(aFrom, aPersonal);
	}

	/**
	 * @param aTo
	 * @throws MessagingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addTo(java.lang.String)
	 */
	public void addTo(String aTo) throws MessagingException {
		this.mimeMessageHelper.addTo(aTo);
	}

	/**
	 * @param aTo
	 * @param aPersonal
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addTo(java.lang.String,
	 *      java.lang.String)
	 */
	public void addTo(String aTo, String aPersonal) throws MessagingException, UnsupportedEncodingException {
		this.mimeMessageHelper.addTo(aTo, aPersonal);
	}

	/**
	 * @param aCc
	 * @throws MessagingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addCc(java.lang.String)
	 */
	public void addCc(String aCc) throws MessagingException {
		this.mimeMessageHelper.addCc(aCc);
	}

	/**
	 * @param aCc
	 * @param aPersonal
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addCc(java.lang.String,
	 *      java.lang.String)
	 */
	public void addCc(String aCc, String aPersonal) throws MessagingException, UnsupportedEncodingException {
		this.mimeMessageHelper.addCc(aCc, aPersonal);
	}

	/**
	 * @param aBcc
	 * @throws MessagingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addBcc(java.lang.String)
	 */
	public void addBcc(String aBcc) throws MessagingException {
		this.mimeMessageHelper.addBcc(aBcc);
	}

	/**
	 * @param aBcc
	 * @param aPersonal
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#addBcc(java.lang.String,
	 *      java.lang.String)
	 */
	public void addBcc(String aBcc, String aPersonal) throws MessagingException, UnsupportedEncodingException {
		this.mimeMessageHelper.addBcc(aBcc, aPersonal);
	}

	/**
	 * @param aSubject
	 * @throws MessagingException
	 * @see javax.mail.internet.MimeMessage#setSubject(java.lang.String,
	 *      java.lang.String)
	 */
	public void setSubject(String aSubject) throws MessagingException {
		this.mimeMessage.setSubject(aSubject);
	}

	/**
	 * @param aHtmlMsg
	 * @throws MessagingException
	 * @see org.springframework.mail.javamail.MimeMessageHelper#setText(java.lang.String,
	 *      boolean)
	 */
	public void setHtmlMsg(String aHtmlMsg) throws MessagingException {
		this.mimeMessageHelper.setText(aHtmlMsg, true);
	}

	/**
	 *
	 */
	public void send() {
		this.send(this.mimeMessage);
	}

	/**
	 * Getter method for "mimeMessage" property
	 *
	 * @return mimeMessage
	 */
	public MimeMessage getMimeMessage() {
		return this.mimeMessage;
	}

	/**
	 * Setter method for "mimeMessage" property
	 *
	 * @param aMimeMessage
	 */
	public void setMimeMessage(MimeMessage aMimeMessage) {
		this.mimeMessage = aMimeMessage;
	}

	/**
	 * Getter method for "mimeMessageHelper" property
	 *
	 * @return mimeMessageHelper
	 */
	public MimeMessageHelper getMimeMessageHelper() {
		return this.mimeMessageHelper;
	}

	/**
	 * Setter method for "mimeMessageHelper" property
	 *
	 * @param aMimeMessageHelper
	 */
	public void setMimeMessageHelper(MimeMessageHelper aMimeMessageHelper) {
		this.mimeMessageHelper = aMimeMessageHelper;
	}
}