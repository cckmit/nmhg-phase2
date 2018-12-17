/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.upload;

import tavant.twms.domain.upload.history.UploadHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kaustubhshobhan.b
 *
 */
public class DownloadUtil {
    /**
     *
     * @param uploadedClaimHistory
     * @param pagesize
     *
     * This function takes the list to be reversed and the pagesize and reverses the list
     * @return
     */
    public List<UploadHistory> reverseClaimHistoryOrder(List<UploadHistory>
                    uploadedClaimHistory, int number){
        List<UploadHistory> reverseOrder=new ArrayList<UploadHistory>();
        int listSize=uploadedClaimHistory.size();
        if(listSize<number){
            for(listSize--;listSize>=0;listSize--){
                UploadHistory generatedClaimHistory=uploadedClaimHistory.get(listSize);
                reverseOrder.add(generatedClaimHistory);
            }
        }else{
            for(listSize--;number>0;number--){
                UploadHistory generatedClaimHistory=uploadedClaimHistory.get(listSize);
                reverseOrder.add(generatedClaimHistory);
            listSize--;
            }
        }
        return reverseOrder;
    }
    /**
     *
     * @param uploadedClaimHistory
     * @return
     *
     * This takes the entire list and reverses the list.
     *
     */
    public List<UploadHistory> reverseClaimHistoryOrder(List<UploadHistory> uploadedClaimHistory){
        List<UploadHistory> reverseOrder = new ArrayList<UploadHistory>();
        int listSize=uploadedClaimHistory.size();
        for(listSize--;listSize>=0;listSize--){
            reverseOrder.add(uploadedClaimHistory.get(listSize));
        }
        return reverseOrder;
    }
}
