package clockshop.extras;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Image;
import com.itextpdf.text.DocumentException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class QrCodeService {

	/**
	 * Generates a QR code image from the provided text using the ZXing library.
	 * The generated QR code image is then converted into an iText Image object.
	 *
	 * @param text The text to be encoded into the QR code.
	 * @return An iText Image object representing the generated QR code.
	 * @throws WriterException   If there is an error generating the QR code using ZXing.
	 * @throws IOException       If there is an error reading or writing the QR code image.
	 * @throws DocumentException If there is an error creating an iText Image instance.
	 */
    public static Image generateQrCode(String text) throws WriterException, IOException, DocumentException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 70, 70);

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "PNG", baos);
        byte[] imageInByte = baos.toByteArray();

        return Image.getInstance(imageInByte);
    }
}