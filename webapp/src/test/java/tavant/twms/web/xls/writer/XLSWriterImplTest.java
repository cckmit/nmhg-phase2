package tavant.twms.web.xls.writer;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class XLSWriterImplTest extends TestCase {

    XLSWriter write;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.write = new XLSWriterImpl();
    }

    public void testWriter() throws Exception {
        List<List<XLSCell>> tableData = new ArrayList<List<XLSCell>>();
        List<XLSCell> rowData = new ArrayList<XLSCell>();
        rowData.add(new XLSCell("First Name", XLSCell.LEFT_ALIGN, true));
        rowData.add(new XLSCell("Last Name", XLSCell.LEFT_ALIGN, true));
        rowData.add(new XLSCell("Age", XLSCell.LEFT_ALIGN, true));
        tableData.add(rowData);

        rowData = new ArrayList<XLSCell>();
        rowData.add(new XLSCell("George"));
        rowData.add(new XLSCell("Clooney"));
        rowData.add(new XLSCell(45));
        tableData.add(rowData);

        rowData = new ArrayList<XLSCell>();
        rowData.add(new XLSCell("Brad"));
        rowData.add(new XLSCell("Pitt"));
        rowData.add(new XLSCell(45));
        tableData.add(rowData);

        String tempDir = System.getProperty("java.io.tmpdir");
        FileOutputStream outputStream = new FileOutputStream(tempDir + "/Test.xls");
        this.write.write(tableData, outputStream);
        outputStream.flush();
        outputStream.close();

    }

}
