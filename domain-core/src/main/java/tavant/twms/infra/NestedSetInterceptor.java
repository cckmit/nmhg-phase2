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
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.security.model.OrgAwareUserDetails;

/**
 * @author radhakrishnan.j
 * 
 */
@SuppressWarnings( { "serial", "unused" })
public class NestedSetInterceptor extends EmptyInterceptor implements BeanFactoryAware {
    private static Logger logger = LogManager.getLogger(NestedSetInterceptor.class);

    private static TreeNodes nodesInserted = new TreeNodes();

    private static TreeNodes nodesDeleted = new TreeNodes();

    private static ThreadLocal<Boolean> clearSession = new ThreadLocal<Boolean>();

    private BeanFactory beanFactory;

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * clears the current hibernate session (first level cache) if any TreeNodes
     * have been modified in the current transaction. This is done to ensure
     * that no TreeNode with stale lft and rgt values are returned in a
     * subsequent transaction within the same hibernate session. One way this
     * might happen is if spring's OpenSessionInViewFilter is used such that in
     * the same HTTP request, two business transactions happen and tree
     * modifications happen in both the transactions.
     */
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        if (clearSession.get() != null && clearSession.get()) {
            Session currentSession = ((SessionFactory) this.beanFactory.getBean("sessionFactory"))
                    .getCurrentSession();
            if (currentSession != null)
                currentSession.clear();
            clearSession.set(false);
        }
    }

    @Override
    public int[] findDirty(Object entity, Serializable id, Object[] currentState,
            Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof TreeNode && ((TreeNode) entity).getNodeInfo().getTreeId() != 0) {
            TreeNode treeNode = (TreeNode) entity;

            String parentPropertyName = getParentProperty(treeNode);
            int indexOfParentProperty = indexOfProperty(propertyNames, parentPropertyName);
            if (previousState == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" Node [" + treeNode + "] is a new node with no previous state ");
                }
                nodesInserted.add(treeNode);
            } else {
                TreeNode oldParent = (TreeNode) previousState[indexOfParentProperty];

                // FIX-ME : Getting the new parent from the list of
                // 'currentStates'
                // does not return the new parent for nodes that get shuffled
                // around
                // in a tree :-?
                TreeNode newParent = treeNode.getParent();
                TreeNode newParentAsKnownByHibernate = (TreeNode) currentState[indexOfParentProperty];
                if (newParent != null && !newParent.equals(newParentAsKnownByHibernate)) {
                    logger
                            .error("New parent as known by hibernate and as determined thru API call are not the same");
                }

                // Move the node if parent changed
                if (oldParent != null && !oldParent.equals(newParent)) {
                    // Delete the node from its current position (possibly also
                    // its children)
                    nodesDeleted.add(treeNode);
                    if (newParent != null) {
                        // Place it in new position
                        nodesInserted.add(treeNode);
                    }
                } else if (oldParent == null && newParent != null) {
                    nodesInserted.add(treeNode);
                }
            }
        }
        return null;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames,
            Type[] types) {
        if (entity instanceof TreeNode) {
            TreeNode treeNode = (TreeNode) entity;
            TreeNodeInfo nodeInfo = treeNode.getNodeInfo();
            if (nodeInfo.getTreeId() != 0) {
                if (nodesDeleted.add(treeNode) && logger.isDebugEnabled()) {
                    logger.debug(" Adjustments will be performed for deletion of node [" + treeNode
                            + "]");
                }
            }
        }
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames,
            Type[] types) {
        if (entity instanceof TreeNode) {
            TreeNode treeNode = (TreeNode) entity;
            TreeNodeInfo nodeInfo = treeNode.getNodeInfo();
            if (nodeInfo.getTreeId() == 0) {
                boolean insertedNewNode = nodesInserted.add(treeNode);
                if (insertedNewNode && logger.isDebugEnabled()) {
                    logger.debug(" Adjustments will be performed for insertion of node ["
                            + treeNode + "]");
                }
            }
        }        
        
        if (entity instanceof BusinessUnitAware) {
			BusinessUnitAware buAuditable = (BusinessUnitAware) entity;
			OrgAwareUserDetails orgAwareUserDetails = (OrgAwareUserDetails) SecurityContextHolder
					.getContext().getAuthentication().getPrincipal();
			if(orgAwareUserDetails.getDefaultBusinessUnit() != null)
			{
				//If the user is a single BU user(by default his single BU) or an admin (who chooses a BU from UI), set the chosen one
				buAuditable.getBusinessUnitInfo().setName(
						orgAwareUserDetails.getDefaultBusinessUnit().getName());
			}	
			else
			{
				//else, which means the user is a multi bu user and he has chosen  a BU from UI and set it from thread local
				String threadLocalBuName = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
				threadLocalBuName = threadLocalBuName == null ? orgAwareUserDetails.getWarrantyAdminSelectedBusinessUnit() : threadLocalBuName;
				buAuditable.getBusinessUnitInfo().setName(threadLocalBuName);
			}			
		}
        return true;
    }

    @Override
    public void postFlush(Iterator entities) {

        if (nodesDeleted.isEmpty() && nodesInserted.isEmpty()) {
            return;
        }

        Session currentSession = ((SessionFactory) this.beanFactory.getBean("sessionFactory"))
                .getCurrentSession();
        SessionFactory hbmSessionFactory = currentSession.getSessionFactory();
        Connection jdbcConnection = currentSession.connection();

        Session tmpSession = hbmSessionFactory.openSession(jdbcConnection);

        TreeNodes nodesInContext = new TreeNodes();
        while (entities.hasNext()) {
            Object entity = entities.next();
            if (entity instanceof TreeNode) {
                nodesInContext.add((TreeNode) entity);
            }
        }
        try {
            // Handle deletes first.
            Set<String> forests = nodesDeleted.get().keySet();
            for (String forest : forests) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" Adjustments for deletion of nodes in forest [" + forest + "]");
                }
                Set<TreeNode> nodesDeletedInForest = nodesDeleted.getNodesInForest(forest);
                for (TreeNode node : nodesDeletedInForest) {
                    handleDeletion(hbmSessionFactory, tmpSession, node, nodesInContext);
                }
            }

            forests = nodesInserted.get().keySet();
            for (String forest : forests) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" Adjustments for insertion of nodes in forest [" + forest + "]");
                }
                Set<TreeNode> nodesInsertedInForest = nodesInserted.getNodesInForest(forest);
                Set<TreeNode> insertionsHandled = new HashSet<TreeNode>();
                for (TreeNode node : nodesInsertedInForest) {
                    handleInsertion(hbmSessionFactory, tmpSession, node, insertionsHandled,
                                    nodesInContext);
                }
            }
        } finally {
            if (tmpSession != null) {
                tmpSession.close();
            }
            clearSession.set(true);
            nodesDeleted.remove();
            nodesInserted.remove();
        }
    }

    protected void handleInsertion(SessionFactory hbmSessionFactory, Session tmpSession,
            TreeNode node, Set<TreeNode> insertionsHandled, TreeNodes nodesInContext) {
        if (insertionsHandled.contains(node)) {
            return;
        }
        insertionsHandled.add(node);

        TreeNodeInfo nodeInfo = node.getNodeInfo();
        String entityName = entityName(hbmSessionFactory, node);
        String nodeInfoPropertyName = nodeInfoPropertyName(node);

        TreeNode parent = node.getParent();
        if (parent == null) {
            // Root node.
            nodeInfo.setTreeId(node.getId());
            MessageFormat updateRootNode = new MessageFormat(
                    "update {0} n set n.{1}.treeId=n.id where n.id=:id");
            String query = updateRootNode.format(new Object[] { entityName, nodeInfoPropertyName });
            Query hqlQuery = tmpSession.createQuery(query);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", node.getId());
            hqlQuery.setProperties(params);
            int numRowsUpdated = hqlQuery.executeUpdate();

            if (logger.isDebugEnabled()) {
                logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                        + numRowsUpdated + "] rows");
            }
            return;
        } else if (nodesInserted.isPresent(parent)) {
            handleInsertion(hbmSessionFactory, tmpSession, parent, insertionsHandled,
                            nodesInContext);
        }
        TreeNodeInfo parentNodeInfo = parent.getNodeInfo();

        long parentNodeTreeId = parentNodeInfo.getTreeId();
        int insertedNodeLft = nodeInfo.getLft();
        int insertedNodeRgt = nodeInfo.getRgt();
        int insertOffset = nodeInfo.getInsertionDeletionOffset();

        // Get the nodes' parent.

        MessageFormat updateNonAncestorsOnNodeInsertion = new MessageFormat(
                "update {0} n set n.{1}.lft = n.{1}.lft + :insertOffset, n.{1}.rgt = n.{1}.rgt + :insertOffset where n.{1}.treeId=:parentNodeTreeId and n.{1}.lft > :parentRgt ");
        String query = updateNonAncestorsOnNodeInsertion.format(new Object[] { entityName,
                nodeInfoPropertyName });

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentNodeTreeId", parentNodeTreeId);
        params.put("insertOffset", insertOffset);
        params.put("parentRgt", parentNodeInfo.getRgt());

        Query hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        int numRowsUpdated = hqlQuery.executeUpdate();

        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }

        Collection<TreeNode> nodesInForestInSession = nodesInContext.getNodesInForest(node
                .getForestName());
        for (TreeNode _thisNode : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = _thisNode.getNodeInfo();
            if (_thisNodeInfo.isTreeSame(parent)
                    && _thisNodeInfo.getLft() > parentNodeInfo.getRgt()) {
                _thisNodeInfo.setLft(_thisNodeInfo.getLft() + insertOffset);
                _thisNodeInfo.setRgt(_thisNodeInfo.getRgt() + insertOffset);
            }
        }

        MessageFormat updateAncestorsOnNodeInsertion = new MessageFormat(
                "update {0} n set n.{1}.rgt = n.{1}.rgt + :insertOffset where n.{1}.treeId=:parentNodeTreeId and n.{1}.lft < :parentLft and n.{1}.rgt > :parentRgt ");
        query = updateAncestorsOnNodeInsertion.format(new Object[] { entityName,
                nodeInfoPropertyName });

        params = new HashMap<String, Object>();
        params.put("parentNodeTreeId", parentNodeTreeId);
        params.put("insertOffset", insertOffset);
        params.put("parentLft", parentNodeInfo.getLft());
        params.put("parentRgt", parentNodeInfo.getRgt());

        hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        numRowsUpdated = hqlQuery.executeUpdate();

        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }

        for (TreeNode _thisNode : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = _thisNode.getNodeInfo();
            if (_thisNodeInfo.isAncestorOf(parentNodeInfo)) {
                _thisNodeInfo.setRgt(_thisNodeInfo.getRgt() + insertOffset);
            }
        }

        // Copy the parent node info before updating the parent.
        int parentRgtBeforeInsertion = parentNodeInfo.getRgt();
        MessageFormat updateParent = new MessageFormat(
                "update {0} n set n.{1}.rgt = n.{1}.rgt + :offset where n.id=:id ");
        query = updateParent.format(new Object[] { entityName, nodeInfoPropertyName });

        params = new HashMap<String, Object>();
        params.put("id", parent.getId());
        params.put("offset", insertOffset);

        hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        numRowsUpdated = hqlQuery.executeUpdate();

        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }
        parentNodeInfo.setRgt(parentRgtBeforeInsertion + insertOffset);

        // The sub-tree that is being inserted, update its nodes.
        int parentDepth = parentNodeInfo.getDepth();
        MessageFormat subTreeWithInsertedNodeAsRoot = new MessageFormat(
                "update {0} n set n.{1}.treeId = :newTreeId, n.{1}.depth = n.{1}.depth + :parentDepth, n.{1}.lft = n.{1}.lft + :offset,n.{1}.rgt = n.{1}.rgt + :offset where n.{1}.treeId=:treeId and ( n.id=:insertedNodeId or ( :insertedNodeLft < n.{1}.lft and n.{1}.rgt < :insertedNodeRgt ) ) ");
        query = subTreeWithInsertedNodeAsRoot.format(new Object[] { entityName,
                nodeInfoPropertyName });

        params = new HashMap<String, Object>();
        Long insertedNodeId = node.getId();
        params.put("treeId", nodeInfo.getTreeId());
        params.put("newTreeId", parentNodeInfo.getTreeId());
        params.put("insertedNodeId", insertedNodeId);
        params.put("insertedNodeLft", insertedNodeLft);
        params.put("insertedNodeRgt", insertedNodeRgt);
        params.put("parentDepth", parentDepth);

        int subTreeChangeOffset = parentRgtBeforeInsertion - nodeInfo.getLft();
        params.put("offset", subTreeChangeOffset);

        hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);

        if (logger.isDebugEnabled()) {
            logger.debug(" About to execute query [" + query + "] with parameters " + params);
        }

        numRowsUpdated = hqlQuery.executeUpdate();

        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }

        // in-memory.
        // the sub-tree nodes other than the root itself.
        for (TreeNode _thisNode : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = _thisNode.getNodeInfo();
            if (_thisNodeInfo.isDescendentOf(nodeInfo)) {
                _thisNodeInfo.setLft(_thisNodeInfo.getLft() + subTreeChangeOffset);
                _thisNodeInfo.setRgt(_thisNodeInfo.getRgt() + subTreeChangeOffset);
                _thisNodeInfo.setDepth(_thisNodeInfo.getDepth() + parentDepth);
                _thisNodeInfo.setTreeId(parentNodeTreeId);
            }
        }

        // the sub-tree root.
        nodeInfo.setLft(nodeInfo.getLft() + subTreeChangeOffset);
        nodeInfo.setRgt(nodeInfo.getRgt() + subTreeChangeOffset);
        nodeInfo.setDepth(nodeInfo.getDepth() + parentDepth);
        nodeInfo.setTreeId(parentNodeTreeId);
    }

    protected void handleDeletion(SessionFactory hbmSessionFactory, Session tmpSession,
            TreeNode node, TreeNodes nodesInContext) {
        String entityName = entityName(hbmSessionFactory, node);
        String nodeInfoPropertyName = nodeInfoPropertyName(node);
        TreeNodeInfo nodeInfo = node.getNodeInfo();
        long deletedFromTreeId = nodeInfo.getTreeId();
        int deletedNodeRgt = nodeInfo.getRgt();
        int deletedNodeLft = nodeInfo.getLft();
        int deleteOffset = nodeInfo.getInsertionDeletionOffset();

        // Update sub-tree first and
        // Make it a sub-tree with this node as the root node.
        int depth = nodeInfo.getDepth();
        int nodesBubbleUpBy = depth - 1;
        int offset = nodeInfo.getLft() - 1;
        MessageFormat subTreeWithDeletedNodeAsRoot = new MessageFormat(
                "update {0} n set n.{1}.treeId = :deletedNodeId, n.{1}.depth = n.{1}.depth - :bubbleUpBy, n.{1}.lft = n.{1}.lft - :offset, n.{1}.rgt = n.{1}.rgt - :offset where n.{1}.treeId=:treeId and ( n.id=:deletedNodeId or ( :deletedNodeLft < n.{1}.lft and n.{1}.rgt < :deletedNodeRgt ) ) ");
        String query = subTreeWithDeletedNodeAsRoot.format(new Object[] { entityName,
                nodeInfoPropertyName });

        Map<String, Object> params = new HashMap<String, Object>();
        Long deletedNodeId = node.getId();
        params.put("treeId", deletedFromTreeId);
        params.put("deletedNodeId", deletedNodeId);
        params.put("deletedNodeLft", deletedNodeLft);
        params.put("deletedNodeRgt", deletedNodeRgt);
        params.put("bubbleUpBy", nodesBubbleUpBy);
        params.put("offset", offset);

        Query hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        int numRowsUpdated = hqlQuery.executeUpdate();

        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }

        // In-memory.
        Collection<TreeNode> nodesInForestInSession = nodesInContext.getNodesInForest(node
                .getForestName());
        for (TreeNode nodeInSession : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = nodeInSession.getNodeInfo();
            if (_thisNodeInfo.isDescendentOf(nodeInfo)) {
                _thisNodeInfo.setTreeId(deletedNodeId);
                _thisNodeInfo.setDepth(_thisNodeInfo.getDepth() - nodesBubbleUpBy);
                _thisNodeInfo.setLft(_thisNodeInfo.getLft() - offset);
                _thisNodeInfo.setRgt(_thisNodeInfo.getRgt() - offset);
            }
        }

        MessageFormat updateNonAncestorsOnNodeDeletion = new MessageFormat(
                "update {0} n set n.{1}.lft = n.{1}.lft - :deleteOffset, n.{1}.rgt = n.{1}.rgt - :deleteOffset where n.{1}.treeId=:treeId and n.{1}.lft > :deletedNodeRgt ");
        query = updateNonAncestorsOnNodeDeletion.format(new Object[] { entityName,
                nodeInfoPropertyName });
        params = new HashMap<String, Object>();
        params.put("treeId", deletedFromTreeId);
        params.put("deleteOffset", deleteOffset);
        params.put("deletedNodeRgt", deletedNodeRgt);
        hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        numRowsUpdated = hqlQuery.executeUpdate();
        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }
        for (TreeNode nodeInSession : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = nodeInSession.getNodeInfo();
            int currentLft = _thisNodeInfo.getLft();
            int currentRgt = _thisNodeInfo.getRgt();
            if (_thisNodeInfo.getTreeId() == deletedFromTreeId && currentLft > deletedNodeRgt) {
                _thisNodeInfo.setLft(currentLft - deleteOffset);
                _thisNodeInfo.setRgt(currentRgt - deleteOffset);
            }
        }
        MessageFormat updateAncestorsOnNodeDeletion = new MessageFormat(
                "update {0} n set n.{1}.rgt = n.{1}.rgt - :deleteOffset where n.{1}.treeId=:treeId and n.{1}.rgt > :deletedNodeRgt and n.{1}.lft < :deletedNodeLft ");
        query = updateAncestorsOnNodeDeletion.format(new Object[] { entityName,
                nodeInfoPropertyName });
        params = new HashMap<String, Object>();
        params.put("treeId", deletedFromTreeId);
        params.put("deleteOffset", deleteOffset);
        params.put("deletedNodeRgt", deletedNodeRgt);
        params.put("deletedNodeLft", deletedNodeLft);
        hqlQuery = tmpSession.createQuery(query);
        hqlQuery.setProperties(params);
        numRowsUpdated = hqlQuery.executeUpdate();
        if (logger.isDebugEnabled()) {
            logger.debug(" Query [" + query + "] with parameters " + params + " updated ["
                    + numRowsUpdated + "] rows");
        }
        for (TreeNode nodeInSession : nodesInForestInSession) {
            TreeNodeInfo _thisNodeInfo = nodeInSession.getNodeInfo();
            int currentRgt = _thisNodeInfo.getRgt();
            if (_thisNodeInfo.getTreeId() == deletedFromTreeId
                    && _thisNodeInfo.isAncestorOfWithoutTreeConsideration(nodeInfo)) {
                _thisNodeInfo.setRgt(currentRgt - deleteOffset);
            }
        }

        nodeInfo.setTreeId(deletedNodeId);
        nodeInfo.setDepth(nodeInfo.getDepth() - nodesBubbleUpBy);
        nodeInfo.setLft(nodeInfo.getLft() - offset);
        nodeInfo.setRgt(nodeInfo.getRgt() - offset);
    }

    protected String entityName(SessionFactory hbmSessionFactory, TreeNode node) {
        Class<? extends TreeNode> klass = node.getClass();
        ClassMetadata classMetadata = hbmSessionFactory.getClassMetadata(klass);
        return classMetadata.getEntityName();
    }

    protected String nodeInfoPropertyName(TreeNode node) {
        String nodeInfoPropertyName = "nodeInfo";
        if (node.getClass().isAnnotationPresent(TreeStructuredData.class)) {
            final TreeStructuredData annotation = node.getClass()
                    .getAnnotation(TreeStructuredData.class);
            nodeInfoPropertyName = annotation.nodeInfoProperty();
        }
        return nodeInfoPropertyName;
    }

    protected int indexOfProperty(String[] propertyNames,
            final String propertyNameThatRefersToParent) {
        int indexOfParentProperty = 0;
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName.equals(propertyNameThatRefersToParent)) {
                indexOfParentProperty = i;
                break;
            }
        }
        return indexOfParentProperty;
    }

    protected String getParentProperty(TreeNode treeNode) {
        String parentPropertyName = "parent";
        if (treeNode.getClass().isAnnotationPresent(TreeStructuredData.class)) {
            parentPropertyName = treeNode.getClass().getAnnotation(TreeStructuredData.class)
                    .parentProperty();
        }
        return parentPropertyName;
    }

    static class TreeNodes extends ThreadLocal<SortedMapOfOrderedSets> {

        @Override
        public SortedMapOfOrderedSets get() {
            SortedMapOfOrderedSets sortedMapOfOrderedSets = super.get();
            if (sortedMapOfOrderedSets == null) {
                sortedMapOfOrderedSets = new SortedMapOfOrderedSets();
                set(sortedMapOfOrderedSets);
            }
            return sortedMapOfOrderedSets;
        }

        public boolean add(TreeNode treeNode) {
            return get().addNode(treeNode);
        }

        public boolean remove(TreeNode treeNode) {
            return get().removeNode(treeNode);
        }

        public boolean isPresent(TreeNode treeNode) {
            return get().get(treeNode.getForestName()).contains(treeNode);
        }

        public boolean isEmpty() {
            return get().isEmpty();
        }

        public Set<TreeNode> getNodesInForest(String forest) {
            return get().get(forest);
        }
    }

    static class IdAwareLinkedHashSet extends LinkedHashSet<TreeNode> {
        @Override
        public boolean add(TreeNode o) {
            // Check if a tree node with this id already exists.
            Long id = o.getId();
            if (id != null) {
                Iterator<TreeNode> iterator = iterator();
                while (iterator.hasNext()) {
                    if (id.equals(iterator.next().getId())) {
                        iterator.remove();
                        break;
                    }
                }

            }
            return super.add(o);
        }
    }

    static class SortedMapOfOrderedSets extends TreeMap<String, Set<TreeNode>> {

        @Override
        public Set<TreeNode> get(Object key) {
            Set<TreeNode> linkedHashSet = super.get(key);
            if (linkedHashSet == null) {
                linkedHashSet = new IdAwareLinkedHashSet();
                put((String) key, linkedHashSet);
            }
            return linkedHashSet;
        }

        public boolean addNode(TreeNode treeNode) {
            String forestName = treeNode.getForestName();
            return get(forestName).add(treeNode);
        }

        public boolean removeNode(TreeNode treeNode) {
            String forestName = treeNode.getForestName();
            return get(forestName).remove(treeNode);
        }
    }
}
