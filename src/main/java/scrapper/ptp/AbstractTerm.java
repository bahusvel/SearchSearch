package scrapper.ptp;

import scrapper.ptp.descriptors.RawDescriptor;

/**
 * Created by denislavrov on 2/17/15.
 */
public class AbstractTerm {
    public enum TERM_TYPE {LINE, WORD, SENTENCE, CHAR, PHRASE}
    TERM_TYPE termType;
    String rawTerm;
    TermDescriptor descriptor;
    ContextTerm obtainContext(Context context){return null;}

    public AbstractTerm(TERM_TYPE termType, TermDescriptor descriptor) {
        this.termType = termType;
        this.descriptor = descriptor;
    }

    public AbstractTerm(TERM_TYPE termType, String rawTerm) {
        this.termType = termType;
        this.rawTerm = rawTerm;
        descriptor = new RawDescriptor(termType, rawTerm);
    }

    public String getRawTerm() {
        return rawTerm;
    }

    @Override
    public String toString() {
        return "AbstractTerm{" +
                "termType=" + termType +
                ", rawTerm='" + rawTerm + '\'' +
                ", descriptor=" + descriptor +
                '}';
    }
}
