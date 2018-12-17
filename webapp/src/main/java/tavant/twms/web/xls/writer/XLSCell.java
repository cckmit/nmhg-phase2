/**
 * 
 */
package tavant.twms.web.xls.writer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

/**
 * @author vineeth.varghese
 * 
 */
public class XLSCell {

    public static final short LEFT_ALIGN = 1;
    public static final short RIGHT_ALIGN = 3;

    private final short alignment;
    private final Object value;
    private final boolean isHeader;

    public XLSCell(Object value, short alignment, boolean isHeader) {
        super();
        if ((alignment != LEFT_ALIGN) && (alignment != RIGHT_ALIGN)) {
            throw new IllegalArgumentException("Cannot understand alignment setting[" + alignment
                    + "]");
        }
        this.alignment = alignment;
        this.value = value;
        this.isHeader = isHeader;
    }

    public XLSCell(Object value, short alignment) {
        this(value, alignment, false);
    }

    public XLSCell(Object value) {
        this(value, LEFT_ALIGN);
    }

    public Object getValue() {
        return this.value;
    }

    public short getAlignment() {
        return this.alignment;
    }

    public boolean isHeader() {
        return this.isHeader;
    }

    void writeToCell(HSSFCell cell) {
        String val = this.value != null ? this.value.toString() : "";
        cell.setCellValue(val);
        setStyle(cell);
    }

    private void setStyle(HSSFCell cell) {
        if (this.alignment == LEFT_ALIGN) {
            cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_LEFT);
        } else {
            cell.getCellStyle().setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        }
    }
}
