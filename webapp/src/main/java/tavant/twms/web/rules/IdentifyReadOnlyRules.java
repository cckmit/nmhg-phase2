/**
 * 
 */
package tavant.twms.web.rules;

import tavant.twms.domain.rules.*;

/**
 * @author radhakrishnan.j
 *
 */
public class IdentifyReadOnlyRules extends OGNLExpressionGenerator {
    private boolean readOnly = false;
    
    @Override
    public void visit(ForAnyNOf visitable) {
        super.visit(visitable);
        readOnly = true;
    }

    @Override
    public void visit(ForAnyOf visitable) {
        super.visit(visitable);
//        readOnly = true;
    }

    @Override
    public void visit(ForEachOf visitable) {
        super.visit(visitable);
//        readOnly = true;
    }
    
    public void visit(DomainPredicate visitable) {
        // Ignoring, a refernce to nested expression
        // is editable even though the expression might
        // not be editable.
    }

    public boolean isReadOnly() {
        return readOnly;
    }
    
    
}

