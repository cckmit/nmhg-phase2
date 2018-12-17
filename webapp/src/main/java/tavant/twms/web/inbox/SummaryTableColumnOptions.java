package tavant.twms.web.inbox;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 16 Apr, 2008
 * Time: 5:02:19 PM
 */
public class SummaryTableColumnOptions {
    public static final int IMAGE_COL = 0;  // bit mask 000000
    public static final int BLANK_COL = 0;  // bit mask 000000
    public static final int LABEL_COL = 1;  // bit mask 000001
    public static final int ID_COL = 2;     // bit mask 000010
    public static final int HIDDEN_COL = 4; // bit mask 000100
    public static final int CSS_COL = 8;    // bit mask 001000
    public static final int NO_SORT = 16;   // bit mask 010000
    public static final int NO_FILTER = 32; // bit mask 100000

    // Compound Options
    public static final int HIDDEN_ID_COL = ID_COL | HIDDEN_COL;
    public static final int HIDDEN_LABEL_COL = LABEL_COL | HIDDEN_COL;
    public static final int LABEL_ID_COL = ID_COL | LABEL_COL;
    public static final int HIDDEN_LABEL_ID_COL = HIDDEN_ID_COL | LABEL_COL;
    public static final int NO_SORT_NO_FILTER_COL = NO_SORT | NO_FILTER;
    public static final int NO_SORT_LABEL_COL = NO_SORT | LABEL_COL;
    public static final int NO_FILTER_LABEL_COL = NO_FILTER | LABEL_COL;
    public static final int NO_SORT_NO_FILTER_LABEL_COL = NO_SORT | NO_FILTER | LABEL_COL;
    public static final int NO_FILTER_ID_COL = NO_FILTER | ID_COL;
    public static final int NO_SORT_ID_COL = NO_SORT | ID_COL;
    public static final int NO_SORT_NO_FILTER_ID_COL = NO_FILTER | NO_SORT | ID_COL;
    public static final int NO_SORT_NO_FILTER_ID_LABEL_COL = NO_SORT_NO_FILTER_ID_COL | LABEL_COL;

    private int bitMask;

    public SummaryTableColumnOptions(int bitMask) {
        this.bitMask = bitMask;
    }

    public boolean isLabelColSet() {
        return isBitSet(LABEL_COL);
    }

    public boolean isIdColSet() {
        return isBitSet(ID_COL);
    }

    public boolean isHiddenColSet() {
        return isBitSet(HIDDEN_COL);
    }

    public boolean isCssColSet() {
        return isBitSet(CSS_COL);
    }

    public boolean isDisableSortingSet() {
        return isBitSet(NO_SORT);
    }

    public boolean isDisableFilteringSet() {
        return isBitSet(NO_FILTER);
    }

    private boolean isBitSet(int bit) {
        return (bitMask & bit) == bit;
    }

	public int getBitMask() {
		return bitMask;
	}

	public void setBitMask(int bitMask) {
		this.bitMask = bitMask;
	}
}
