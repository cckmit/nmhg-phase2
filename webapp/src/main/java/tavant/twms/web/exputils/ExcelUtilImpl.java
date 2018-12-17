package tavant.twms.web.exputils;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import tavant.twms.common.TWMSException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ExcelUtilImpl implements ExcelUtil {

	public void export(InputStream template, Map beans, OutputStream output) {
		XLSTransformer transformer = new XLSTransformer();
		HSSFWorkbook result = transformer.transformXLS(template, beans);
		try {
			result.write(output);
		} catch (IOException e) {
			throw new TWMSException("Export to excel failed",e);
		}
	}

}
