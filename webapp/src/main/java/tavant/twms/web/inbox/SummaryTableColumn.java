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
package tavant.twms.web.inbox;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import org.json.JSONObject;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.web.common.CheckBoxPropertyResolver;
import tavant.twms.web.common.ImgColAwarePropertyResolver;
import static tavant.twms.web.inbox.SummaryTableColumnOptions.BLANK_COL;

/**
 * This class, is meant to be added in the summary table as a column.
 *
 * @author janmejay.singh
 */
public class SummaryTableColumn {
    private String title;
    private final String id;
    private final double widthPercent;
    private String expression;
    private boolean idColumn;
    private boolean labelColumn;
    private boolean hidden;
    private boolean cssColumn;
    private DataRenderer renderer;
    private final String dataType;
    private boolean displayDataFromProperty;
     private SummaryTableColumnOptions columnOptions=new SummaryTableColumnOptions(BLANK_COL);

    public static final String DATE = "Date", NUMBER = "Number", STRING = "String",
            IMAGE = "Image", MONEY = "Money", CHECK_BOX = "CheckBox";
    public static final String LEFT = "left", RIGHT = "right";

    public static interface DataRenderer {

        public Object valueForXLSCell(Object value);

        public Object valueForUiCell(Object value);

    }

    public static final class CalenderDataRenderer implements DataRenderer {

        public Object valueForXLSCell(Object value) {
            return value;
        }

        public Object valueForUiCell(Object value) {
            if (value == null) {
                return "";
            }
            if (value instanceof CalendarDate) {
                String dateFormat = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
                return ((CalendarDate) value).toString(dateFormat);
            }
            return value.toString();
        }

    }
    
    public static final class CheckBoxRenderer implements DataRenderer {

        public Object valueForXLSCell(Object value) {
            return CheckBoxPropertyResolver.getCheckBoxValue((JSONObject)value);
        }

        public Object valueForUiCell(Object value) {
            return value;
        }

    }
    
    public static final class MoneyDataRenderer implements DataRenderer {

        public Object valueForXLSCell(Object value) {
            return valueForUiCell(value);
        }

        public Object valueForUiCell(Object value) {
            if (value == null) {
                return "";
            }
            if (value instanceof Money) {
                return ((Money) value).breachEncapsulationOfCurrency()+" "+((Money) value).breachEncapsulationOfAmount();
            }
            return value.toString();
        }
    }

    public static final class ImageDataRenderer implements DataRenderer {

        public Object valueForXLSCell(Object value) {
            try {
                return ImgColAwarePropertyResolver.getImageTitle((JSONObject) value);
            } catch (Exception e) {
                throw new IllegalStateException("The column value is not in the expected format.");
            }
        }

        public Object valueForUiCell(Object value) {
            return value;
        }
    }

    public static final class DefaultDataRenderer implements DataRenderer {
    	
        public Object valueForXLSCell(Object value) {
            return value;
        }

        public Object valueForUiCell(Object value) {
            return value == null ? "" : renderSpecialChar(value.toString());
        }

		private String renderSpecialChar(String stringToReplace) {
			
			return stringToReplace.replace("<", "&lt;");
		}
    }

    public SummaryTableColumn(String title, String id, double widthPercent) {
        this(title, id, widthPercent, STRING);
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType) {
        this.title = title;
        this.id = id;
        if (widthPercent > 100) {
            throw new IllegalArgumentException("The share must be less than or equal to 100.");
        }
        this.widthPercent = widthPercent;
        this.dataType = dataType;
        this.expression = id;
        if (this.dataType.equalsIgnoreCase(DATE)) {
            this.renderer = new CalenderDataRenderer();
        } else if (this.dataType.equalsIgnoreCase(IMAGE)) {
            this.renderer = new ImageDataRenderer();
        } else if (this.dataType.equalsIgnoreCase(MONEY)) {
            this.renderer = new MoneyDataRenderer();
        } else if(CHECK_BOX.equals(this.dataType)) {
            this.renderer = new CheckBoxRenderer();
        }
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType, boolean displayDataFromProperty) {
        this.title = title;
        this.id = id;
        if (widthPercent > 100) {
            throw new IllegalArgumentException("The share must be less than or equal to 100.");
        }
        this.widthPercent = widthPercent;
        this.displayDataFromProperty = displayDataFromProperty;
        this.dataType = dataType;
        this.expression = id;
        if (this.dataType.equalsIgnoreCase(DATE)) {
            this.renderer = new CalenderDataRenderer();
        } else if (this.dataType.equalsIgnoreCase(IMAGE)) {
            this.renderer = new ImageDataRenderer();
        } else if (this.dataType.equalsIgnoreCase(MONEY)) {
            this.renderer = new MoneyDataRenderer();
        } else if(CHECK_BOX.equals(this.dataType)) {
            this.renderer = new CheckBoxRenderer();
        }
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            String expression) {
        this(title, id, widthPercent, dataType);
        this.expression = expression;
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            String expression, boolean isLabelColumn, boolean isIdColumn, boolean isHidden,
            boolean isCssColumn) {
        this(title, id, widthPercent, dataType, expression);
        this.idColumn = isIdColumn;
        this.labelColumn = isLabelColumn;
        this.hidden = isHidden;
        this.cssColumn = isCssColumn;
    }
    
    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            String expression, boolean isLabelColumn, boolean isIdColumn, boolean isHidden,
            boolean isCssColumn,int options) {
        this(title, id, widthPercent, dataType, expression);
        this.idColumn = isIdColumn;
        this.labelColumn = isLabelColumn;
        this.hidden = isHidden;
        this.cssColumn = isCssColumn;
        this.columnOptions = new SummaryTableColumnOptions(options);
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            String expression, boolean isLabelColumn, boolean isIdColumn, boolean isHidden,
            boolean isCssColumn, DataRenderer renderer) {
        this(title, id, widthPercent, dataType, expression, isLabelColumn, isIdColumn, isHidden,
                isCssColumn);
        this.renderer = renderer;
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType, int options) {
	        this(title, id, widthPercent, dataType, id, options);
	}

	public SummaryTableColumn(String title, String id, double widthPercent, int options) {
	        this(title, id, widthPercent, STRING, id, options);
    }

      public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
	            String expression, int options) {
	        this(title, id, widthPercent, dataType, expression);
	        this.columnOptions = new SummaryTableColumnOptions(options);
    }

    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            boolean isLabelColumn, boolean isIdColumn, boolean isHidden, boolean isCssColumn) {
        this(title, id, widthPercent, dataType, id, isLabelColumn, isIdColumn, isHidden,
                isCssColumn);
    }
    
    public SummaryTableColumn(String title, String id, double widthPercent, String dataType,
            boolean isLabelColumn, boolean isIdColumn, boolean isHidden, boolean isCssColumn,int options) {
        this(title, id, widthPercent, dataType, id, isLabelColumn, isIdColumn, isHidden,
                isCssColumn);
        this.columnOptions = new SummaryTableColumnOptions(options);
    }

    public SummaryTableColumn(String title, String id, double widthPercent, boolean isLabelColumn,
            boolean isIdColumn, boolean isHidden, boolean isCssColumn) {
        this(title, id, widthPercent, STRING, id, isLabelColumn, isIdColumn, isHidden, isCssColumn);
    }

    public String getExpression() {
        return this.expression;
    }

    public String getTitle() {
        return this.title;
    }

    public double getWidthPercent() {
        return this.widthPercent;
    }

    public String getAlignment() {
        if (this.dataType.equalsIgnoreCase(DATE) || this.dataType.equalsIgnoreCase(NUMBER)
        		|| this.dataType.equalsIgnoreCase(MONEY)) {
            return RIGHT;
        }
        return LEFT;
    }

    public String getId() {
        return this.id;
    }

    public boolean isIdColumn() {
        return this.idColumn;
    }

    public boolean isLabelColumn() {
        return this.labelColumn;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isCssColumn() {
        return this.cssColumn;
    }

    public DataRenderer getRenderer() {
        this.renderer = this.renderer != null ? this.renderer : new DefaultDataRenderer();
        return this.renderer;
    }

    public String getDataType() {
        return this.dataType;
    }

    public boolean isImageColumn() {
        return this.dataType.equalsIgnoreCase(IMAGE);
    }
    
    public boolean isCheckBoxColumn() {
        return this.dataType.equalsIgnoreCase(CHECK_BOX);
    }

    public void setTitle(String text) {
        this.title = text;
    }

    public boolean isDisableSorting() {
        return columnOptions.isDisableSortingSet();
    }

    public boolean isDisableFiltering() {
        return columnOptions.isDisableFilteringSet();
    }

	public SummaryTableColumnOptions getColumnOptions() {
		return columnOptions;
	}

	public void setColumnOptions(SummaryTableColumnOptions columnOptions) {
		this.columnOptions = columnOptions;
	}

    public boolean isDisplayDataFromProperty() {
        return displayDataFromProperty;
    }

    public void setDisplayDataFromProperty(boolean displayDataFromProperty) {
        this.displayDataFromProperty = displayDataFromProperty;
    }
}