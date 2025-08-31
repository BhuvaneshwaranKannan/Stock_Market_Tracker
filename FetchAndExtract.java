import java.net.URL;
import java.util.Scanner;

public class FetchAndExtract{

    private static String fetch(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        StringBuilder data;
        try (Scanner sc = new Scanner(url.openStream())) {
            data = new StringBuilder();
            while (sc.hasNext()) {
                data.append(sc.nextLine());
            }
        }
        return data.toString();
    }

    private static String extract(String content, String field) {
        int idx = content.indexOf(field);
        if (idx == -1) return "N/A";

        int start = content.indexOf(":", idx) + 1;
        int endComma = content.indexOf(",", start);
        int endBrace = content.indexOf("}", start);

        int end = (endComma == -1) ? endBrace : Math.min(endComma, endBrace);

        return content.substring(start, end).replace("\"", "").trim();
    }

    public String fetchData(String url){
        try {
            return fetch(url);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractData(String content1, String field1){
            return extract(content1, field1);
    }
}
