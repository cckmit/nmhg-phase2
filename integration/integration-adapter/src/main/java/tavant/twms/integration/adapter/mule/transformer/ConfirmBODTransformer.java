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

package tavant.twms.integration.adapter.mule.transformer;

import org.apache.xmlbeans.XmlException;
import org.mule.transformers.xml.AbstractXmlTransformer;
import org.mule.umo.transformer.TransformerException;
import org.w3c.dom.Document;
import tavant.oagis.BODDTO;
import tavant.oagis.ConfirmBODDataAreaDTO;
import tavant.oagis.ConfirmBODDocumentDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfirmBODTransformer extends AbstractXmlTransformer {

    @Override
    protected Object doTransform(Object src, String encoding) throws TransformerException {
        ConfirmBODDocumentDTO dto;
        try {
            if (src instanceof byte[]) {
                dto = ConfirmBODDocumentDTO.Factory.parse(new ByteArrayInputStream((byte[]) src));
            } else if (src instanceof String) {
                dto = ConfirmBODDocumentDTO.Factory.parse((String) src);
            } else if (src instanceof Document) {
                dto = ConfirmBODDocumentDTO.Factory.parse((Document) src);
            } else {
                throw new RuntimeException("Unexpected source type " + src.getClass());
            }
        } catch (XmlException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getBODDTOs(dto.getConfirmBOD().getDataArea());
    }

    private List<BODDTO> getBODDTOs(ConfirmBODDataAreaDTO dataArea) {
        return Arrays.asList(dataArea.getBODArray());
    }
}
