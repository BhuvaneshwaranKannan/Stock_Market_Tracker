import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    static List<ContentUrl.CompanyData> watchList = new ArrayList<>();

    public static void main(String[] args) throws UnsupportedEncodingException {
        try (Scanner input = new Scanner(System.in)) {

            System.out.println(Colors.yellow + "<---------------Stock Market Tracker--------------->" + Colors.reset);
            boolean running = true;
            while (running) {
                System.out.println(
                        "\n1 - Company Data \n2 - Get Suggestion \n3 - Add Company to WatchList  \n4 - See Your WatchList \n5 - Remove Company from your WatchList \n6 - Exit");

                System.out.print(Colors.blue + "\nEnter the Option : " + Colors.reset);

                int option = input.nextInt();
                input.nextLine();

                ApiKey akey = new ApiKey();
                String apiKey = akey.getApiKey();

                FetchAndExtract getFE = new FetchAndExtract();

                ContentUrl data = new ContentUrl();
                switch (option) {
                    // <--------------------------- Company Data ---------------------------->
                    case 1 -> {
                        System.out.print(Colors.blue + "\nEnter the Company Name : " + Colors.reset);
                        String companyName = input.nextLine();

                        ContentUrl.CompanyData company = data.getContentUrl(companyName, apiKey);

                        String content = getFE.fetchData(company.contentUrl);

                        if (content == null) {
                            System.out.println(Colors.red + "Failed to fetch data." + Colors.reset);
                            return;
                        }

                        if (content.contains("\"code\"")) {
                            System.out.println(Colors.red + "API Error: " + getFE.extractData(content, "\"message\"")
                                    + Colors.reset);
                            return;
                        }

                        System.out.println(Colors.blue + "\nFor " + Colors.reset + Colors.yellow + company.name + "("
                                + company.symbol + ")" + Colors.reset + Colors.blue + " What do you want?"
                                + Colors.reset);

                        boolean y = true;
                        System.out.println(
                                "\n1  - Price \n2  - Currency \n3  - Open \n4  - High \n5  - Low \n6  - Change \n7  - Percentage Change \n8  - Is Market Open? \n9  - Exit");
                        while (y) {
                            System.out.print(Colors.blue + "\nEnter the Option : " + Colors.reset);
                            int option2 = input.nextInt();
                            switch (option2) {
                                case 1 -> {
                                    System.out.println("Price : " + Colors.green
                                            + getFE.extractData(content, "\"close\"") + Colors.reset);
                                }

                                case 2 -> {
                                    System.out.println("Currency : " + Colors.green
                                            + getFE.extractData(content, "\"currency\"") + Colors.reset);
                                }

                                case 3 -> {
                                    System.out.println("Open : " + Colors.green + getFE.extractData(content, "\"open\"")
                                            + Colors.reset);
                                }

                                case 4 -> {
                                    System.out.println("High : " + Colors.green + getFE.extractData(content, "\"high\"")
                                            + Colors.reset);
                                }

                                case 5 -> {
                                    System.out.println("Low : " + Colors.red + getFE.extractData(content, "\"low\"")
                                            + Colors.reset);
                                }

                                case 6 -> {
                                    System.out.println("Change : " + Colors.green
                                            + getFE.extractData(content, "\"change\"") + Colors.reset);
                                }

                                case 7 -> {
                                    System.out.println(
                                            "Percentage of Change : "
                                                    + Colors.green + getFE.extractData(content, "\"percent_change\"")
                                                    + "%" + Colors.reset);
                                }

                                case 8 -> {
                                    String marketOpen = getFE.extractData(content, "\"is_market_open\"");
                                    System.out.println("Market Open : "
                                            + (marketOpen.equals("true") ? Colors.green + "Yes" + Colors.red : "No")
                                            + Colors.reset);
                                }

                                case 9 -> {
                                    System.out.println(Colors.red + "Menu Exited !" + Colors.reset);
                                    y = false;
                                }

                                default -> System.out.println(Colors.red + "Invalid option!" + Colors.reset);
                            }
                        }
                    }
                    // <--------------------------- Get Suggestion ---------------------------->
                    case 2 -> {
                        System.out.print(Colors.blue + "Enter part of company name or symbol: " + Colors.reset);
                        String guess = input.nextLine();

                        List<ContentUrl.CompanyData> suggestions = data.searchCompanies(guess, apiKey);

                        if (suggestions.isEmpty()) {
                            System.out.println("No results found!");
                        } else {
                            System.out.println("\nSuggestions:");
                            for (int i = 0; i < suggestions.size(); i++) {
                                System.out.println((i + 1) + ". " + suggestions.get(i).name
                                        + " (" + suggestions.get(i).symbol + ")");
                            }
                        }
                        System.out.println(Colors.yellow + "Use this symbol to search for Company" + Colors.reset);
                    }

                    // <--------------------------- Add company to
                    // Watchlist---------------------------->
                    case 3 -> {
                        boolean z = true;
                        while (z) {
                            System.out.print(Colors.blue + "\nEnter the Company to add : " + Colors.reset);
                            String companyName = input.nextLine();

                            ContentUrl.CompanyData company = data.getContentUrl(companyName, apiKey);

                            if (company == null) {
                                System.out.println(Colors.red + "There is no company like that!" + Colors.reset);
                            } else {
                                boolean alreadyExist = false;

                                for (ContentUrl.CompanyData i : watchList) {
                                    if (i.symbol.equalsIgnoreCase(company.symbol)) {
                                        alreadyExist = true;
                                        break;
                                    }
                                }

                                if (!alreadyExist) {
                                    watchList.add(company);
                                    System.out.println("\n" + Colors.yellow + company.name + "(" + company.symbol + ")"
                                            + Colors.reset + Colors.green + " added to your watchlist!" + Colors.reset);
                                } else {
                                    System.out.println(Colors.red + "Company name already exist in your watchList!"
                                            + Colors.reset);
                                }
                            }

                            System.out.print(Colors.blue + "\nDo you want to add again [Y/N] : " + Colors.reset);
                            String YorN = input.nextLine().trim();
                            z = YorN.equalsIgnoreCase("y");
                            if (z == false)
                                System.out.println(Colors.red + "!" + Colors.reset);
                        }
                    }
                    // <--------------------------- See Your Watchlist ---------------------------->
                    case 4 -> {
                        if (!watchList.isEmpty()) {
                            System.out.println("\nWatch List : \n");
                            for (ContentUrl.CompanyData i : watchList) {
                                String content = getFE.fetchData(i.contentUrl);
                                if (content != null) {
                                    System.out.println(i.name + " (" + i.symbol + ") | Price : "
                                            + getFE.extractData(content, "\"close\""));
                                } else {
                                    System.out.println(i.name + " (" + i.symbol + ") |" + Colors.red
                                            + "Price : N/A (API Error)" + Colors.reset);
                                }
                            }
                        } else {
                            System.out.println(Colors.red + "Your Watchlist is Empty!" + Colors.reset);
                        }
                    }
                    // <--------------------------- Remove Your Watchlist
                    // ---------------------------->
                    case 5 -> {
                        boolean z = true;
                        while (z) {
                            System.out.print(Colors.blue + "\nEnter the Company to remove : " + Colors.reset);
                            String companyName = input.nextLine();

                            ContentUrl.CompanyData rcompany = data.getContentUrl(companyName, apiKey);

                            if (rcompany == null) {
                                System.out.println(Colors.red + "There is no company like that!" + Colors.reset);
                            } else {
                                ContentUrl.CompanyData toRemove = null;

                                for (ContentUrl.CompanyData i : watchList) {
                                    if (i.symbol.equalsIgnoreCase(rcompany.symbol)) {
                                        toRemove = i;
                                        break;
                                    }
                                }

                                if (toRemove != null) {
                                    watchList.remove(toRemove);
                                    System.out.println("\n" + Colors.yellow + rcompany.name + "(" + rcompany.symbol
                                            + ")" + Colors.reset + Colors.red + " remove from your watchlist!"
                                            + Colors.reset);
                                } else {
                                    System.out.println(Colors.red + "Company name already not exist in your watchList!"
                                            + Colors.reset);
                                }
                            }

                            System.out.print(Colors.blue + "\nDo you want to remove again [Y/N] : " + Colors.reset);
                            String YorN = input.nextLine().trim();

                            z = YorN.equalsIgnoreCase("y");
                            if (z == false)
                                System.out.println("!");
                        }
                    }
                    // <--------------------------- Exit ---------------------------->
                    case 6 -> {
                        running = false;
                        System.out.println(Colors.red + "! Exit !" + Colors.reset);
                    }
                }
            }
        }
    }
}
