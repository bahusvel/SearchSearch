package scrapper.ptp;

import scrapper.ptp.descriptors.LineDescriptor;

import java.util.Collection;

/**
 * Created by denislavrov on 2/17/15.
 */
public class ContextTerm extends AbstractTerm{
    final Context context;
    int position = 0;

    public ContextTerm(TermDescriptor descriptor, Context context, int position, String rawTerm) {
        super(descriptor.descriptorType, descriptor);
        this.context = context;
        this.position = position;
        super.rawTerm = rawTerm;
    }


    public ResultContext after(){
        return ResultContext.from(context, position + rawTerm.length(), false);
    }
    public ResultContext before(){
        return ResultContext.from(context, 0, position, true);
    }


    public boolean contains(ContextTerm term){
        return position <= term.position && position + rawTerm.length() >= term.position + term.rawTerm.length();
    }

    public int distanceTo(ContextTerm another, TermDescriptor unit){
        ResultContext betweenContext = between(another);
        return unit.findAllTerms(betweenContext).size();
    }

    public ContextTerm getParentByDescriptor(TermDescriptor parent){
        Collection<ContextTerm> parentTerms = context.tokenizeBy(parent);
        for (ContextTerm parentTerm : parentTerms) {
            if (parentTerm.contains(this)){
                return parentTerm;
            }
        }
        return null;
    }

    public ResultContext right(){
        ContextTerm lineContext = getParentByDescriptor(LineDescriptor.INSTANCE);
        return ResultContext.from(context, position + rawTerm.length(), lineContext.position + lineContext.rawTerm.length(), false);
    }
    public ResultContext left(){
        ContextTerm lineContext = getParentByDescriptor(LineDescriptor.INSTANCE);
        return ResultContext.from(context, lineContext.position, position, true);
    }
    public ResultContext above(){
        ContextTerm lineContext = getParentByDescriptor(LineDescriptor.INSTANCE);
        return ResultContext.from(context, 0, lineContext.position, true);
    }
    public ResultContext below(){
        ContextTerm lineContext = getParentByDescriptor(LineDescriptor.INSTANCE);
        return ResultContext.from(context, lineContext.position + lineContext.rawTerm.length(), false);
    }
    public ResultContext between(ContextTerm another){
        int begin, end;
        if (position <= another.position){
            begin = position + rawTerm.length();
            end = another.position;
        }else{
            begin = another.position + another.rawTerm.length();
            end = position;
        }
        return ResultContext.from(context, begin, end, false);
    }
}
