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

import java.io.Serializable;
import java.text.MessageFormat;

import javax.persistence.Embeddable;

/**
 * @author radhakrishnan.j
 * 
 */
@Embeddable
@SuppressWarnings("serial")
public class TreeNodeInfo implements Serializable{
    private long treeId = 0;
    
    private int lft = 1;

    private int rgt = 2;
    
    private int depth = 1;

    public TreeNodeInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public TreeNodeInfo(long treeId,int depth,int left,int right) {
        this.treeId = treeId;
        this.depth = depth;
        this.lft = left;
        this.rgt = right;
    }
    
    
    /**
     * @return the left
     */
    public int getLft() {
        return lft;
    }

    /**
     * @param left
     *            the left to set
     */
    public void setLft(int left) {
        this.lft = left;
    }

    /**
     * @return the right
     */
    public int getRgt() {
        return rgt;
    }
    
    

    @Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + depth;
		result = PRIME * result + lft;
		result = PRIME * result + rgt;
		result = PRIME * result + (int) (treeId ^ (treeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TreeNodeInfo other = (TreeNodeInfo) obj;
		if (depth != other.depth)
			return false;
		if (lft != other.lft)
			return false;
		if (rgt != other.rgt)
			return false;
		if (treeId != other.treeId)
			return false;
		return true;
	}

	/**
     * @return the treeId
     */
    public long getTreeId() {
        return treeId;
    }

    /**
     * @param treeId the treeId to set
     */
    public void setTreeId(long treeId) {
        this.treeId = treeId;
    }

    /**
     * @param right
     *            the right to set
     */
    public void setRgt(int right) {
        this.rgt = right;
    }
    
    /**
	 * @param depth the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	public boolean isDescendentOf(TreeNodeInfo treeNodeInfo) {
        return treeId==treeNodeInfo.treeId && treeNodeInfo.lft < lft && rgt < treeNodeInfo.rgt;
    }
    
    public boolean isAncestorOf(TreeNodeInfo treeNodeInfo) {
        return treeId==treeNodeInfo.treeId && isAncestorOfWithoutTreeConsideration(treeNodeInfo);
    }

    public boolean isAncestorOfWithoutTreeConsideration(TreeNodeInfo treeNodeInfo) {
        return lft < treeNodeInfo.lft && treeNodeInfo.rgt < rgt;
    }
    
    public boolean isLeaf() {
    	return rgt== lft + 1;
    }

    public boolean isTreeSame(TreeNode treeNode) {
    	return getTreeId() == treeNode.getNodeInfo().getTreeId();
    }

    public int getInsertionDeletionOffset() {
    	return 2 * ((int)Math.floor( (rgt - lft)/2 ) + 1 );    	
    }
    
    private static MessageFormat format = new MessageFormat("(treeId={0}, depth={1}, lft={2}, rgt={3}))");
    
    @Override
    public String toString() {
        return format.format(new Object[]{treeId,depth,lft,rgt});
    }
}
