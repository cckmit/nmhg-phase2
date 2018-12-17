package tavant.twms.web.common.barcode;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;

public class BarcodeGeneratorTest extends TestCase {
    
    public void testGenerateBarCode() throws OutputException {
        BarcodeGenerator fixture = new BarcodeGenerator();
        fixture.setData("12345");
        Barcode barCode = fixture.createBarCode();
        ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        fixture.writeBarcodeImage(barCode,out);
        
        // BarcodeImageHandler.saveJPEG(barCode, new File("d:/temp/barcode.jpeg"));
        assertTrue(out.toByteArray().length > 0);
    }

}
