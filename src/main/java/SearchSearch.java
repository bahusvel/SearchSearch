import crawler.Crawler;
import search.BingSearchService;

import java.util.ArrayList;

/**
 * Created by denislavrov on 12/05/16.
 */
public class SearchSearch {

    public static class CrawledResult {
        BingSearchService.PrimaryResult result;
        String html;

        public CrawledResult(BingSearchService.PrimaryResult result, String html) {
            this.result = result;
            this.html = html;
        }
    }

    public class SSResult {
    }

    public static void main(String[] args) {
        BingSearchService service = new BingSearchService();
        Crawler crawler = new Crawler();

        ArrayList<BingSearchService.PrimaryResult> results = service.searchBing("memory encryption");

        ArrayList<CrawledResult> crawledResults = new ArrayList<>();
        for (BingSearchService.PrimaryResult result : results) {
            String html = crawler.crawlURL(result.url);
            System.out.println(html);
            crawledResults.add(new CrawledResult(result, html));
        }
    }
}
