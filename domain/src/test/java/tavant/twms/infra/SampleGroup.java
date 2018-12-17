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
package tavant.twms.infra;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@TreeStructuredData(parentProperty="myParent",nodeInfoProperty="treeNodeInfo")
public class SampleGroup implements TreeNode {

    @Id
	@GeneratedValue(generator = "ModelRuleEntity")
	@GenericGenerator(name = "ModelRuleEntity", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MODEL_RULE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
    
    @ManyToOne(optional=true)
    private SampleGroup myParent;
    
    @OneToMany(mappedBy="myParent")
    @Cascade({CascadeType.ALL})
    private List<SampleGroup> includesGroups = new ArrayList<SampleGroup>();

    @Embedded
    private TreeNodeInfo treeNodeInfo = new TreeNodeInfo();

    private String groupName;
    
    @Transient
    private String forestName = " Grouping Example ";
    
    public SampleGroup() {
        super();
    }

    public TreeNodeInfo getNodeInfo() {
        return treeNodeInfo;
    }

    public TreeNode getParent() {
        return myParent;
    }

    public String getForestName() {
        return forestName;
    }

    
    /**
     * @return the myParent
     */
    public SampleGroup getMyParent() {
        return myParent;
    }

    /**
     * @param myParent the myParent to set
     */
    public void setMyParent(SampleGroup myParent) {
        this.myParent = myParent;
    }

    /**
     * @return the treeNodeInfo
     */
    public TreeNodeInfo getTreeNodeInfo() {
        return treeNodeInfo;
    }

    /**
     * @param treeNodeInfo the treeNodeInfo to set
     */
    public void setTreeNodeInfo(TreeNodeInfo treeNodeInfo) {
        this.treeNodeInfo = treeNodeInfo;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    
    
    /**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public void includeGroup(SampleGroup group) {
        group.setMyParent(this);
        includesGroups.add(group);
    }
    
    public void excludeGroup(SampleGroup group) {
        if (this.equals(group.getMyParent())) {
            group.setMyParent(null);
            includesGroups.remove(group);
        }        
    }
    
    public boolean includesGroup(SampleGroup group) {
        return includesGroups.contains(group);
    }
    
    private static MessageFormat messageFormat = new MessageFormat("Forest ''{0}'', group=''{1}'', id={2}, {3} ");
    
    
    @Override
    public String toString() {
        return messageFormat.format(new Object[]{forestName,groupName,id,treeNodeInfo});
    }
}
