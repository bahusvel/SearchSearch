package scrapper.ptp;

import java.util.Optional;

/**
 * Created by denislavrov on 3/5/15.
 */
public class CompoundContext extends Context{
    final Context[] slices;
    public CompoundContext(Context... contexts){
        Optional<Context> optContext = parentContext(contexts);
        if (optContext.isPresent()) {
            parentContext = optContext.get();
            slices = contexts;
            makeRawTerm();
            position = -1;
        }
        else throw new IllegalArgumentException("Contexts must have a common ancestor");
    }



    private void makeRawTerm(){
        StringBuilder sb = new StringBuilder();
        for (Context slice : slices) {
            sb.append(slice.rawContext);
        }
        rawContext = sb.toString();
    }
}
