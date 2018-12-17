/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web.imputils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

public class ExcelToXMLConverterTest extends TestCase {

    public void testExtractHeader() {
        ExcelToXMLConverter fixture = new ExcelToXMLConverter();
        HSSFWorkbook wb = fixture.open(getResourceAsStream("claims.xls"));
        assertNotNull(wb);
        assertEquals(1, wb.getNumberOfSheets());
        HSSFSheet sheet = wb.getSheetAt(0);
        assertNotNull(sheet);
        Header header = fixture.extractHeader(sheet);
        assertNotNull(header);
        assertEquals(4, header.getNumberOfRowsOccupied());
        Field field_claim = header.getRoot();
        assertEquals(10, field_claim.getSubFields().size());
        assertEquals("Claim No",field_claim.getSubFields().get(0).getName());
        assertEquals("Claim Type",field_claim.getSubFields().get(1).getName());
        assertEquals("Date of failure",field_claim.getSubFields().get(2).getName());
        assertEquals("Date of repair",field_claim.getSubFields().get(3).getName());
        assertEquals("Serial Number",field_claim.getSubFields().get(4).getName());
        assertEquals("Item Number",field_claim.getSubFields().get(5).getName());
        assertEquals("Hours on Machine",field_claim.getSubFields().get(6).getName());
        assertEquals("Date of installation",field_claim.getSubFields().get(7).getName());
        assertEquals("Comment",field_claim.getSubFields().get(8).getName());
        assertEquals("Service Information",field_claim.getSubFields().get(9).getName());
        
        Field field_serviceInformation = field_claim.getSubFields().get(9);
        assertEquals("Service Information",field_serviceInformation.getName());
        assertEquals(10,field_serviceInformation.getSubFields().size());
        assertEquals("Fault Code",field_serviceInformation.getSubFields().get(0).getName());
        assertEquals("Causal Part",field_serviceInformation.getSubFields().get(1).getName());
        assertEquals("Fault found",field_serviceInformation.getSubFields().get(2).getName());
        assertEquals("Caused by",field_serviceInformation.getSubFields().get(3).getName());
        assertEquals("oem_part_replaced",field_serviceInformation.getSubFields().get(4).getName());
        assertEquals("non_oem_part_replaced",field_serviceInformation.getSubFields().get(5).getName());
        assertEquals("labor_detail",field_serviceInformation.getSubFields().get(6).getName());
        assertEquals("travel_detail",field_serviceInformation.getSubFields().get(7).getName());
        assertEquals("meals_expense",field_serviceInformation.getSubFields().get(8).getName());
        assertEquals("item_frieght_and_duty",field_serviceInformation.getSubFields().get(9).getName());

        
        
        Field field_oemPartReplaced = field_serviceInformation.getSubFields().get(4);
        assertEquals(3,field_oemPartReplaced.getSubFields().size());
        assertEquals("item_number",field_oemPartReplaced.getSubFields().get(0).getName());
        assertEquals("serial_number",field_oemPartReplaced.getSubFields().get(1).getName());
        assertEquals("quantity",field_oemPartReplaced.getSubFields().get(2).getName());
        
        
        Field field_nonOemPartReplaced = field_serviceInformation.getSubFields().get(5);
        assertEquals(4,field_nonOemPartReplaced.getSubFields().size());
        assertEquals("item_number",field_nonOemPartReplaced.getSubFields().get(0).getName());
        assertEquals("description",field_nonOemPartReplaced.getSubFields().get(1).getName());
        assertEquals("quantity",field_nonOemPartReplaced.getSubFields().get(2).getName());
        assertEquals("price",field_nonOemPartReplaced.getSubFields().get(3).getName());        
        
        
        Field field_laborDetail = field_serviceInformation.getSubFields().get(6);
        assertEquals(2,field_laborDetail.getSubFields().size());
        assertEquals("job_code",field_laborDetail.getSubFields().get(0).getName());
        assertEquals("hours",field_laborDetail.getSubFields().get(1).getName());
        
        
        Field field_travelDetail = field_serviceInformation.getSubFields().get(7);
        assertEquals(4,field_travelDetail.getSubFields().size());
        assertEquals("location",field_travelDetail.getSubFields().get(0).getName());
        assertEquals("trips",field_travelDetail.getSubFields().get(1).getName());
        assertEquals("hours",field_travelDetail.getSubFields().get(2).getName());
        assertEquals("miles",field_travelDetail.getSubFields().get(3).getName());        
    }

    public void testConvert() throws IOException, SAXException, ParserConfigurationException {
        ExcelToXMLConverter fixture = new ExcelToXMLConverter();
        
        HSSFWorkbook wb = fixture.open(getResourceAsStream("claim-with-error.xls"));
        assertNotNull(wb);
        assertEquals(1, wb.getNumberOfSheets());
        HSSFSheet sheet = wb.getSheetAt(0);
        assertNotNull(sheet);
        Header header = fixture.extractHeader(sheet);
        assertNotNull(header);
        assertEquals(4, header.getNumberOfRowsOccupied());
        Field field_claim = header.getRoot();
        
        StringWriter output = new StringWriter();
        fixture.convert(getResourceAsStream("claim-with-error.xls"), output);
        String xml = output.toString();
        System.out.println(xml);
        Diff diff = XMLUnit.compareXML(new InputStreamReader(getResourceAsStream("claims.xml")), xml);
        assertTrue(diff.identical());
    }

    private InputStream getResourceAsStream(String path) {
        try {
            ClassPathResource cpr = new ClassPathResource(path,getClass());
            return cpr.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
