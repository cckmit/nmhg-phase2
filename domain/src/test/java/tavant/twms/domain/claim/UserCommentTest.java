package tavant.twms.domain.claim;

import java.util.Iterator;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.TimePoint;

public class UserCommentTest extends DomainRepositoryTestCase {

    OrgService orgService;

    User user;
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        user = orgService.findUserByName("dealer");
    }

    public void testSimpleCommentSave() {
        UserComment comment = new UserComment(user, "some comment");
        getSession().save(comment);
        assertNotNull(comment.getId());
        assertNotNull(comment.getMadeOn());        
    }
    
    public void testComparison() {
        UserComment comment1 = new UserComment();
        UserComment comment2 = new UserComment();
        UserComment comment3 = new UserComment();
        comment1.setMadeOn(TimePoint.atGMT(2006, 9, 13, 8, 12, 25));
        comment2.setMadeOn(TimePoint.atGMT(2006, 9, 13, 8, 15, 30));
        comment3.setMadeOn(TimePoint.atGMT(2006, 9, 13, 8, 14, 30));
        Claim claim = new MachineClaim();
        claim.setProcessComments(comment1);
        claim.setProcessComments(comment2);
        claim.setProcessComments(comment3);
        Iterator<UserComment> it = claim.getUserProcessComments().iterator();
        //comment2 which was added later should come up first
        assertEquals(comment2, it.next());
        assertEquals(comment3, it.next());
        assertEquals(comment1, it.next());
    }

    
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}
