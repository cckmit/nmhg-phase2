package tavant.twms.domain.partreturn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 16/12/12
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public enum LoadType {
    PALLET("pallet", "label.loadType.pallet"),
    BOX("Box","label.loadType.box"),
    CRATE("Crate", "label.loadType.Crate"),
    ENVELOPE("Envelope","label.loadType.Envelope"),
    PARCEL("Parcel","label.loadType.Parcel"),
    FORKLIFT("Forklift","label.loadType.Forklift");


    private String loadType;
    private String i18Value;

    private LoadType(String loadType, String i18Value){
        this.loadType = loadType;
        this.i18Value = i18Value;
    }

    public static List<String> getAllLoadType(){
        List<String> types = new ArrayList<String>();
        for(LoadType type : LoadType.values()){
            types.add(type.i18Value);
        }
        return types;
    }

    @Override
    public String toString() {
        return this.loadType;
    }

    public static LoadType typeFor(String type) {
        if (PALLET.loadType.equalsIgnoreCase(type)) {
            return PALLET;
        } else if (BOX.loadType.equalsIgnoreCase(type)) {
            return BOX;
        }else if (CRATE.loadType.equalsIgnoreCase(type)) {
            return CRATE;
        }else if (ENVELOPE.loadType.equalsIgnoreCase(type)) {
            return ENVELOPE;
        }else if (PARCEL.loadType.equalsIgnoreCase(type)) {
            return PARCEL;
        }else if (FORKLIFT.loadType.equalsIgnoreCase(type)) {
            return FORKLIFT;
        }else {
            throw new IllegalArgumentException("Cannot understand the Load Type");
        }
    }
}
