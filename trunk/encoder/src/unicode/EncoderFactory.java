package unicode;

import unicode.exception.NotSupportedEncdoingException;
import unicode.utf16.UTF16Encoder;
import unicode.utf8.UTF8Encoder;

public class EncoderFactory {

	public static Encoder getEncoder (String charset) throws NotSupportedEncdoingException {

		String charsetName = charset.toLowerCase();

		if (charsetName.compareTo("utf-8") == 0) {

			return new UTF8Encoder(ByteOrderMark.EMPTY);

		} else if (charsetName.compareTo("utf-8bom") == 0) {

			return new UTF8Encoder(ByteOrderMark.UTF8);

		} else if (charsetName.compareTo("utf-16be") == 0
					|| charsetName.compareTo("utf-16") == 0) {

			return new UTF16Encoder(ByteOrderMark.UTF16BE);

		} else if (charsetName.compareTo("utf-16le") == 0) {

			return new UTF16Encoder(ByteOrderMark.UTF16LE);

		}

		throw new NotSupportedEncdoingException();

	}

}
