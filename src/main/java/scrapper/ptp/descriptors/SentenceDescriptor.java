package scrapper.ptp.descriptors;

import scrapper.ptp.AbstractTerm.TERM_TYPE;
import scrapper.ptp.Context;
import scrapper.ptp.ContextTerm;
import scrapper.ptp.TermDescriptor;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by denislavrov on 2/17/15.
 */
public class SentenceDescriptor extends TermDescriptor {
    public static SentenceDescriptor INSTANCE = new SentenceDescriptor();

    private final BreakIterator iterator = BreakIterator.getSentenceInstance();
    public SentenceDescriptor() {
        super(TERM_TYPE.SENTENCE);
    }

    @Override
    public Collection<ContextTerm> findAllTerms(Context context) {
        ArrayList<ContextTerm> contextTerms = new ArrayList<>();
        String source = context.getRawContext();
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
             contextTerms.add(new ContextTerm(this, context, start, source.substring(start, end)));
        }
        return contextTerms;
    }
}
