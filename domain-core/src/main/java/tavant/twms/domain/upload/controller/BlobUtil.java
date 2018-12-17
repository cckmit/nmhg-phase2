/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.domain.upload.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * @author jhulfikar.ali
 *
 */
public class BlobUtil {

	// API to convert a file into Blob to store it in Oracle. 
	public void writeToBlob(InputStream inputFileInputStream, oracle.sql.BLOB blob) 
	throws SQLException, IOException {

		OutputStream os = blob.getBinaryOutputStream();

		int chunkSize = blob.getChunkSize();
		byte[] binaryBuffer = new byte[chunkSize];

		int position = 1;
		int bytesRead = 0;
		int bytesWritten = 0;
		int totbytesRead = 0;
		int totbytesWritten = 0;
		// FileInputStream inputFileInputStream = new FileInputStream(file);
		while ((bytesRead = inputFileInputStream.read(binaryBuffer)) != -1) {
			bytesWritten = blob.putBytes(position, binaryBuffer, bytesRead);
			position += bytesRead;
			totbytesRead += bytesRead;
			totbytesWritten += bytesWritten;
		}
		inputFileInputStream.close();
	}
	
	// API to convert a file into Blob to store it in Oracle. 
	public void writeToBlob(InputStream fileInputStream, java.sql.Blob blob)  
		throws SQLException, IOException {
		OutputStream os = blob.setBinaryStream(1);
		byte[] data = new byte[1];
		int i;       
		while ((i = fileInputStream.read(data)) != -1) 
		{         
			os.write(data, 0, i);       
		}       
		os.close();
	}
	
}
