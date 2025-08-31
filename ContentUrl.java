import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ContentUrl {

    // helper class
    public static class CompanyData {
        public String contentUrl;
        public String symbol;
        public String name;

        public CompanyData(String contentUrl, String symbol, String name) {
            this.contentUrl = contentUrl;
            this.symbol = symbol;
            this.name = name;
        }
    }

    public CompanyData getContentUrl(String cName, String aKey) throws UnsupportedEncodingException {
        FetchAndExtract getFE = new FetchAndExtract();

        String searchUrl = "https://api.twelvedata.com/symbol_search?symbol="
                + URLEncoder.encode(cName, "UTF-8")
                + "&apikey=" + aKey;

        String getData = getFE.fetchData(searchUrl);

        String symbol = getFE.extractData(getData, "\"symbol\"");
        String name = getFE.extractData(getData, "\"instrument_name\"");

        if (symbol == null || symbol.equals("N/A")) {
            System.out.println("Could not find company: " + cName);
            return null;
        }

        String contentUrl = "https://api.twelvedata.com/quote?symbol="
                + URLEncoder.encode(symbol, "UTF-8")
                + "&apikey=" + aKey;

        return new CompanyData(contentUrl, symbol, name);
    }

    public List<CompanyData> searchCompanies(String query, String apiKey) throws UnsupportedEncodingException {
    
    FetchAndExtract getFE = new FetchAndExtract();
    String searchUrl = "https://api.twelvedata.com/symbol_search?symbol="
            + URLEncoder.encode(query, "UTF-8")
            + "&apikey=" + apiKey;

    String getData = getFE.fetchData(searchUrl);

    List<CompanyData> results = new ArrayList<>();

    String[] parts = getData.split("\\{");

    for (String part : parts) {
        if (part.contains("\"symbol\"")) {

            String symbol = getFE.extractData("{" + part, "\"symbol\"");
            String name = getFE.extractData("{" + part, "\"instrument_name\"");

            if (!symbol.equals("N/A")) {
                String contentUrl = "https://api.twelvedata.com/quote?symbol="
                        + URLEncoder.encode(symbol, "UTF-8")
                        + "&apikey=" + apiKey;
    
                results.add(new CompanyData(contentUrl, symbol, name));
            }
        }
    }
    return results;
}

}
