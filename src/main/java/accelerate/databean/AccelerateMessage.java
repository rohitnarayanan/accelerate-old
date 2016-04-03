package accelerate.databean;

import static accelerate.util.AppUtil.compare;

import java.io.Serializable;

/**
 * Class holding message to be passed between components or to Client Layer
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Feb 1, 2014
 */
public class AccelerateMessage implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link MessageType}
	 */
	private MessageType messageType = null;

	/**
	 * AccelerateMessage Code
	 */
	private String messageCode = null;

	/**
	 * AccelerateMessage Text
	 */
	private String messageText = null;

	/**
	 * @param aMessageType
	 * @param aMessageCode
	 * @param aMessageText
	 */
	public AccelerateMessage(MessageType aMessageType, String aMessageCode, String aMessageText) {
		setMessageType(aMessageType);
		setMessageCode(aMessageCode);
		setMessageText(aMessageText);
	}

	/**
	 * @param aMessageType
	 * @param aMessageCode
	 */
	public AccelerateMessage(MessageType aMessageType, String aMessageCode) {
		setMessageType(aMessageType);
		setMessageCode(aMessageCode);
	}

	/**
	 * Overloaded Setter method for "messageType" property accepting argument
	 * orf type {@link String}
	 *
	 * @param aMessageType
	 */
	public void setMessageType(String aMessageType) {
		this.messageType = MessageType.getMessageType(aMessageType);
	}

	/**
	 * Getter method for "messageType" property
	 *
	 * @return messageType
	 */
	public MessageType getMessageType() {
		return this.messageType;
	}

	/**
	 * Setter method for "messageType" property
	 *
	 * @param aMessageType
	 */
	public void setMessageType(MessageType aMessageType) {
		this.messageType = aMessageType;
	}

	/**
	 * Getter method for "messageCode" property
	 *
	 * @return messageCode
	 */
	public String getMessageCode() {
		return this.messageCode;
	}

	/**
	 * Setter method for "messageCode" property
	 *
	 * @param aMessageCode
	 */
	public void setMessageCode(String aMessageCode) {
		this.messageCode = aMessageCode;
	}

	/**
	 * Getter method for "messageText" property
	 *
	 * @return messageText
	 */
	public String getMessageText() {
		return this.messageText;
	}

	/**
	 * Setter method for "messageText" property
	 *
	 * @param aMessageText
	 */
	public void setMessageText(String aMessageText) {
		this.messageText = aMessageText;
	}

	/**
	 * {@link Enum} to define the type of message
	 *
	 * @author Rohit Narayanan
	 * @version 1.0 Initial Version
	 * @since Feb 1, 2014
	 */
	public static enum MessageType {
		/**
		 *
		 */
		INFO,

		/**
		 *
		 */
		SUCCESS,

		/**
		 *
		 */
		ERROR;

		/**
		 * This method returns the {@link MessageType} instance mapped to the
		 * given string
		 *
		 * @param aMessageType
		 * @return {@link MessageType}
		 */
		public static MessageType getMessageType(String aMessageType) {
			for (MessageType messageType : values()) {
				if (compare(messageType.name(), aMessageType)) {
					return messageType;
				}
			}

			return null;
		}
	}
}
