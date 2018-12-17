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
package tavant.twms.rules.model;

import java.io.Serializable;


/**
 * This interface and its implementors are supposed to be used by 
 * the studio.
 * <p>
 * Given a repository name the studio should be able to construct a 
 * complete eclipse project. Hence the fullpath information is maintained
 * @TODO One more thing will be to put both rule, process and the ProjectEntities
 * under the same table and the DAO APIs will look much cleaner
 * 
 * @author kannan.ekanath
 *
 */
public interface ModelObject extends Serializable{

	/**
	 * Get the name of this model object typically the rule name/process name
	 * @return
	 */
	String getName();
	
	/**
	 * The contents as a string
	 * @return
	 */
	String getScript();
	
	/**
	 * The full relative of this resource from the project root.
	 * @return
	 */
	String getPath();
	
	/**
	 * Description/additional properties
	 * @return
	 */
	String getDescription();
	
	/**
	 * Set the file name for this object (used for reconstructing the project)
	 * Note: It will be important to preserve the file name extensions since they 
	 * are being directly picked up by the studio
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * Set a materialised input stream as the contents of this model object
	 * @param inputStream
	 */
	void setScript(String contents);
	
	/**
	 * Set project relative path for this object
	 * @param path
	 */
	void setPath(String path);
	
	/**
	 * Set a description about this model object
	 * @param description
	 */
	void setDescription(String description);
}
