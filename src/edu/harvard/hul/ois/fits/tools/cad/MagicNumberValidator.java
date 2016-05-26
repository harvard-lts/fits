package edu.harvard.hul.ois.fits.tools.cad;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by Isaac Simmons on 8/27/2015.
 */
public class MagicNumberValidator {
    private final byte[] magic;
    private final boolean closeStream;

    private MagicNumberValidator(byte[] magic, boolean closeStream) {
        this.magic = magic;
        this.closeStream = closeStream;
    }

    public static MagicNumberValidator hex(String hex, boolean closeStream) {
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }

        return new MagicNumberValidator(DatatypeConverter.parseHexBinary(hex), closeStream);
    }

    public static MagicNumberValidator hex(String hex) {
        return MagicNumberValidator.hex(hex, true);
    }

    public static MagicNumberValidator string(String s, boolean closeStream) {
        try {
            return MagicNumberValidator.string(s, StandardCharsets.US_ASCII.name(), closeStream);
        } catch(UnsupportedEncodingException ex) {
            throw new RuntimeException("System default charset not found", ex);
        }
    }

    public static MagicNumberValidator string(String s) {
        return MagicNumberValidator.string(s, true);
    }

    public static MagicNumberValidator string(String s, String charset, boolean closeStream) throws UnsupportedEncodingException {
        return new MagicNumberValidator(s.getBytes(charset), closeStream);
    }

    public static MagicNumberValidator string(String s, String charset) throws UnsupportedEncodingException {
        return MagicNumberValidator.string(s, charset, true);
    }

    public static MagicNumberValidator bytes(byte[] bytes, boolean closeStream) {
        return new MagicNumberValidator(bytes, closeStream);
    }

    public static MagicNumberValidator bytes(byte[] bytes) {
        return MagicNumberValidator.bytes(bytes, true);
    }

    public static byte[] readBytes(InputStream in, int length) throws IOException {
        final byte[] buf = new byte[length];

        int offset = 0;

        while(offset < length) {
            final int read = in.read(buf, offset, length - offset);
            if (read == -1) {
                throw new IOException("Reached end of stream before reading expected number of bytes");
            }
            offset += read;
        }
        return buf;
    }

    private boolean isValid(InputStream in) throws IOException {
        //TODO: might I get something with a UTF-8 (or other) BOM?
        try {
            return Arrays.equals(readBytes(in, magic.length), magic);
        } finally {
            if (closeStream) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }
    }

    void validate(InputStream in) throws IOException, ValidationException {
        if(! isValid(in)) {
            throw new ValidationException("Magic number \"0x" + DatatypeConverter.printHexBinary(magic) +
                    "\" not found at start of file");
        }
    }
}
