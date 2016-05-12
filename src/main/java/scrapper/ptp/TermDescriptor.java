package scrapper.ptp;

import scrapper.ptp.AbstractTerm.TERM_TYPE;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by denislavrov on 2/17/15.
 */
public abstract class TermDescriptor {
    protected final TERM_TYPE descriptorType;

    public TermDescriptor(TERM_TYPE descriptorType) {
        this.descriptorType = descriptorType;
    }

    public abstract Collection<ContextTerm> findAllTerms(Context context);
    public ContextTerm findFirst(Context context){
        return findAllTerms(context).iterator().next();
    }
    public ContextTerm fintLast(Context context) {
        Iterator<ContextTerm> iterator = findAllTerms(context).iterator();
        ContextTerm ret = null;
        while (iterator.hasNext()){
            ret = iterator.next();
        }
        return ret;
    }
}
