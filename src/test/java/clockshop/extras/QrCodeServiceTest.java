package clockshop.extras;


import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.itextpdf.text.Image;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTest {

    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QrCodeService();
    }

    @Test
    void testGenerateQrCode() {
        String text = "Test";
        try {
            Image qrCode = qrCodeService.generateQrCode(text);
            assertNotNull(qrCode);
        } catch (WriterException | IOException | DocumentException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testGenerateQrCodeWithEmptyString() {
        String text = "";
        assertThrows(IllegalArgumentException.class, () -> qrCodeService.generateQrCode(text));
    }

    @Test
    void testGenerateQrCodeWithNull() {
        assertThrows(NullPointerException.class, () -> qrCodeService.generateQrCode(null));
    }
}