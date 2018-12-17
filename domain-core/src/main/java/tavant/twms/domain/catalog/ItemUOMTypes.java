package tavant.twms.domain.catalog;

public enum ItemUOMTypes {
	
	ACTIVE("ACTIVE"),
	BX("BX"),
	CS("CS"),
	DRUM("DRUM"),
	EACH("EACH"),
	FEET("FEET"),
	GALLON("GALLON"),
	GRAM("GRAM"),
	INCH("INCH"),
	KILOGRAM("KILOGRAM"),
	KT("KT"),
	LITRE("LITRE"),
	LT("LT"),
	METER("METER"),
	MILLILITRE("MILLILITRE"),
	MILLIMETER("MILLIMETER"),	
	PACK_OF_10("PACK OF 10"),
	PACK_OF_12("PACK OF 12"),
	PACK_OF_2("PACK OF 2"),
	PACK_OF_25("PACK OF 25"),
	PACK_OF_4("PACK OF 4"),
	PACK_OF_5("PACK OF 5"),
	PACK_OF_50("PACK OF 50"),
	PACK_OF_6("PACK OF 6"),
	PACK_OF_8("PACK OF 8"),
	PAIL("PAIL"),
	PALLET("PALLET"),
	PC("PC"),
	PK("PK"),
	POUND("POUND"),
	QUART("QUART"),
	SQUARE_FEET("SQUARE FEET"),
	SQUARE_METER("SQUARE METER"),
	ST("ST"),
	SI("SI"),
	OZ("OZ"),
	KIT("KIT"),
	PR("PR"),
	BF("BF"),
	NU("NU"),
	MEA("MEA"),
	KG("KG"),
	HR("HR"),
	M3("M3"),
	LB("LB"),
	GL("GL"),
	PT("PT"),
	RL("RL"),
	CCF("CCF"),
	CEA("CEA"),
	SET("SET"),
	M("M"),
	EA("EA"),
	OZT("OZT"),
	SH("SH"),
	FT("FT"),
	SF("SF"),
	IN("IN"),
	QT("QT"),
	SM("SM"),
	CWT("CWT"),	
	L("L"),
	BOT("BOT"),
	FL("FL"),
	A("A"),
	NA("NA"),
	CM("CM"),
	CENTIMETER("CENTIMETER"),
	MM("MM"),
	US_GALLON("US GALLON"),
	BARREL("BARREL"),
	CUBIC_CENTIMETER("CUBIC CENTIMETER"),
	FEET_FOOT("FEET/FOOT"),
	MILLILITER ("MILLILITER"),
	OUNCE ("OUNCE"),
	PINT ("PINT"),
	AS_REQUIRED ("AS REQUIRED"),
	BOX("BOX"),
	CASE("CASE"),
	CUBIC_METER("CUBIC METER"),
	PACK("PACK"),
	PAIR("PAIR"),
	ROLL("ROLL"),
	METRIC_TONNE("METRIC TONNE"),
	GA("GA");
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName()
	{
		return this.name();
	}
	
	private ItemUOMTypes(String type) {
        this.type = type;
    }
	
	@Override
	public String toString() {
		 return this.type;
	}
	
}
