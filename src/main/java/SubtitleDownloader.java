import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SubtitleDownloader {
    private static String movieFolderPath = null;
    private static String movieFilePath = null;
    private static String language = "English";
    private static String movieName = null;

    private static void Usage() {
        System.out.println("\nUsage: ");
        System.out.println("------");
        System.out.println("\nCommand to get Subtitle with Movie Name: ");
        System.out.println("\tjava -jar <JAR_FILE_PATH> -m <MOVIE NAME>");
        System.out.println("\nExample:\n\tjava -jar \"C:/Users/admin/downloads/subtitle-downloader.jar\" -m \"Inception\"");
        System.out.println("\n\t\tOR");
        System.out.println("\nCommand to get Subtitles with Movie File Path: ");
        System.out.println("\tjava -jar <JAR_FILE_PATH> -mP <MOVIE_FILE_PATH>");
        System.out.println("\nExample:\n\tjava -jar \"C:/Users/admin/downloads/subtitle-downloader.jar\" -mP \"E:/Movies/Final Destination/Final.Destination.2009.mp4\"");
        System.out.println("\n\t\tOR");
        System.out.println("\nCommand to get Subtitles for all the Movies in separate folders present inside a Folder:");
        System.out.println("\tjava -jar <JAR_FILE_PATH> -mD <MOVIES_FOLDER_PATH>");
        System.out.println("\nExample:\n\tjava -jar \"C:/Users/admin/downloads/subtitle-downloader.jar\" -mD \"E:/Movies\"");
        System.out.println("\nOptional Parameters:");
        System.out.println("\t-lang Language of the Subtitle. By Default set to English");
    }

    private static void parseArguments(String[] arguments) {
        CommandOptions cmd = new CommandOptions(arguments);
        // Show Usage() if the user wants to know how to execute the program
        if (cmd.hasOption("-help")) {
            Usage();
            System.exit(0);
        }

        if (cmd.hasOptionAndValue("-m")) {
            movieName = cmd.valueOf("-m");
        }
        // If the user needs subtitle for a different language other than English
        if (cmd.hasOptionAndValue("-lang")) {
            language = cmd.valueOf("-lang");
        }

        // Get the movieFilePath or movieFolderPath to Download subtitles
        if (cmd.hasOptionAndValue("-mP")) {
            movieFilePath = cmd.valueOf("-mP");
        }

        if (cmd.hasOptionAndValue("-mD")) {
            movieFolderPath = cmd.valueOf("-mD");
        }
    }

    public static void main(String[] args) {
        // Parse Arguments of the Program
        parseArguments(args);
        if (movieName != null) {
            if (!getSubtitles(movieName, null)) {
                System.out.println("\nEXITING THE PROGRAM!!!");
                System.exit(1);
            }
        }
        else if (movieFilePath != null) {
            // Get MovieName using Full Path of Movie File Location
            System.out.println("Movie File Path:\n\t" + movieFilePath);
            String movieName = getMovieName(movieFilePath);
            // Call get Subtitles to download the subtitles to the movieFilePath location
            if(!getSubtitles(movieName, movieFilePath)) {
                System.out.println("\nEXITING THE PROGRAM!!!");
                System.exit(1);
            }
        }
        else if (movieFolderPath != null) {
            /*
             * Expectation:
             *       movieFolderPath is something like E:/Movies or E:/Downloads/Movies where all the movies are present inside
             *       separate folders
             * Example:
             *       E:/Downloads/Movies/Thelma/Thelma (2017).mp4
             *       E:/Downloads/Elle/Elle (2016).mp4
             * Logic:
             *       1) Parse through the directories inside "movieFolderPath"
             *       2) If there are any Movie Folders with ".srt" file present, Ignore them
             *       3) Else, download the subtitle for the movie
             */

            // Lambda Expression for FileNameFilter
            String[] files = new File(movieFolderPath).list((directory, name) -> getSubtitlesForAllMovies(directory, name));
        }
        else {
            System.out.println("Please provide one of the following: \n1) Movie Name\n2) Movie File Path\n3) Movie Folder Path");
            Usage();
        }
    }

    /**
     *****************************************  Util Functions *****************************************************************************
     * getSubtitleForAllMovies - Loops through the Movies Directory and calls getSubtitles function to Download the subtitles for the movies
     * getSubtitles - Given a movieName, Downloads the subtitle for the movie
     * getMovieName - Given the Full Path of Movie File location returns the Movie Name
     * getSearchResults - Given a Movie Name, returns the search results in Yify Subtitle
     * getMovieURL - Given a html content of "div class=media-body", gives the link to Movie URL page
     * getMovieAndYear - Given a html content of the Movie, parses the movieName and Year of the Movie
     * getSubtitleURL - Given a movieURL retrieves the Subtitle URL for the movie
     * getMovieFolder - Given a full path of the movie, returns the path of the parent folder
     * getHTMLContent - Given a URL, returns the Document which has the html content which can be used to parsed
     * downloadSubtitle - Given a downloadSubtitleURL and PathtoDownload, downloads the zip file, extracts the contents and
     saves the files in the given Path to Download
     * checkExtension - Given a File object and list of Extension, checks whether the File contains any of the extensions and returns a boolean
     * getExtensions - Returns the list of video file extensions
     * displayMovieAndYear - Given a html element and count variable -> Parses the movie name/year from the Html content and prints to console
     *****************************************************************************************************************************************
     **/

    private static boolean getSubtitlesForAllMovies(File directory, String name) {
        try {
            File temp = new File(directory, name);
            if (temp.isDirectory() && temp.listFiles() != null) {
                // Check if ".srt" file is already not present for the Movie
                if (Arrays.stream(temp.listFiles()).noneMatch(f -> f.toString().endsWith(".srt"))) {
                    // Check and get the Movie File with one of the video format extension from the Movie Folder
                    Optional<File> file = Arrays.stream(temp.listFiles()).filter(f -> checkExtension(f, getFileExtensions())).findFirst();
                    if (file.isPresent()) {
                        String movieFilePath = file.get().toString();
                        System.out.println("\nMovie File Path:\n\t" + movieFilePath);
                        String movieName = getMovieName(movieFilePath);
                        if(!getSubtitles(movieName, movieFilePath)) {
                            System.out.println("\nSkipping Downloading Subtitles for the Current Movie");
                            System.out.println("---------------------------------------------------------------" +
                                    "---------------------------------------------------\n");
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e);
        }
        return false;
    }

    private static boolean getSubtitles(String movieName, String movieFilePath) {
        Scanner s = new Scanner(System.in);
        String movieUrl = null;
        boolean flag = true;

        if (movieName == null) {
            System.out.println("\nMovie doesn't exist in yifysubtitles.com\n\t\t( OR )\n\"Movie Name\" " +
                    "which was parsed from Movie filepath gives empty result when searched\n" +
                    "\nPlease enter a keyword/part of the Movie Name as in \"Harry\" in \"Harry Potter\" to search once more" +
                    "\nThis can provide appropriate results" +
                    "\nMovie name ( Press ENTER To SKIP ): ");
            movieName = s.nextLine().trim();
            // Sometimes User might want to skip entering the Movie Name and move on to Next Movie
            // So Pressing Enter should skip Downloading Subtitles for the Movie
            if (movieName.equals("")) {
                return false;
            }
            flag = false;
        }
        System.out.println("\nMovie: " + movieName);

        // HTML Content of all Movies matching the movie name
        Elements elements = getSearchResults(movieName);

        // If the result is empty, Ask the User to input the Movie name
        if (elements == null || elements.isEmpty()) {
            System.out.println("\nNo results were found for Movie \"" + movieName + "\" in yifysubtitles.com");
            // If the User was given an option to type the movie name, Don't prompt the user to Enter the Movie Name again
            if (!flag) {
                return false;
            }
            System.out.print("\nPlease enter a keyword/part of the Movie Name as in \"Harry\" in \"Harry Potter 3\" to search again" +
                    "\nMovie name ( Press ENTER To SKIP ): ");
            movieName = s.nextLine().trim();
            if (movieName.equals("")) {
                return false;
            }
            elements = getSearchResults(movieName);
            if (elements == null || elements.isEmpty()) {
                System.out.println("Movie: " + movieName + " does not exists in yifysubtitles.com");
                return false;
            }
        }

        if(elements.size() == 1) {
            movieUrl = getMovieURL(elements.get(0));
        }
        else {
            System.out.println("\nMore than one result found for: " + movieName);
            AtomicInteger i = new AtomicInteger(0);
            elements.forEach(e -> displayMovieAndYear(e, i.incrementAndGet()));
            System.out.print("Please enter the number to select the movie to download subtitles ( PRESS 0 TO SKIP ): ");
            int option = s.nextInt();
            // Skip Processing the Current Movie
            if (option == 0) {
                return false;
            }
            if (option < 0 ||option > elements.size()) {
                System.out.println("\n[ERROR] - INVALID OPTION SELECTED: " + option);
                return false;
            }
            movieUrl = getMovieURL(elements.get(option - 1));

            // Update the Movie Name with the Option Selected
            movieName = getMovieAndYear(elements.get(option - 1));
        }

        String subtitleUrl = getSubtitleURL(movieUrl, language);
        if (subtitleUrl == null) {
            System.out.println("\nSubtitle doesn't exist for the Movie \"" + movieName + "\" in language \"" + language  + "\" in yifysubtitles.com");
            System.out.println("URL: " + movieUrl);
            return false;
        }
        System.out.println("Movie URL: " + movieUrl);
        System.out.println("Subtitle URL: " + subtitleUrl);

        // Download subtitle URL is same as Subtitle URL except /subtitles changes to /subtitle with ".zip" appended
        String downloadSubtitleUrl = subtitleUrl.replace("/subtitles", "/subtitle") + ".zip";

        String movieFolder;
        // If there is no movieFilePath given, then take the Current Directory
        // as the place to download subtitles
        if (movieFilePath != null) {
            movieFolder = getMovieFolder(movieFilePath);
        }
        else {
            movieFolder = System.getProperty("user.dir");
        }

        if (movieFolder == null) {
            System.out.println("Something went wrong while trying to get Movie folder for Movie File Path:\n\t" + movieFilePath + "\n");
            return false;
        }

        // Download and Extract the zip to get the Subtitle
        downloadSubtitle(downloadSubtitleUrl, movieFolder);
        System.out.println("\nSuccessfully downloaded Subtitle for Movie: " + movieName + "\n");
        System.out.println("---------------------------------------------------------------" +
                "---------------------------------------------------\n");
        return true;
    }

    private static String getMovieName(String movieFilePath) {
        /*
         * Please use Regexr.com to if you like to understand the below regex precisely
         * This is one of the common regex to filter out "Movie name" from other contents present in the Name of the Movie
         * Example: E:/Movies/Final Destination/Final.Destination.2013.[720p].[AAA].Watever.mp4
         * This regex trims the name and gives "Final.Destination" as output
         * Important Assumption here is that every movie name has "Year" appended to it separated
         * either by a "." or "[" or " "(space,tabs), otherwise this regex won't work to get the correct movie name
         */
        Pattern movieFilePathPattern = Pattern.compile("(?:.*[\\\\\\/])?(.*)(?:[\\s\\.\\(\\[]\\d{4}[\\.\\)\\]\\s]).*");
        Matcher movieNameMatcher = movieFilePathPattern.matcher(movieFilePath);
        if (!movieNameMatcher.find()) {
            return null;
        }
        // Replace '.' with space in the Control group which was selected
        String movieName = movieNameMatcher.group(1).replace(".", " ");
        // YIFY Subtitles Website URL
        String yifySubtitleUrl = "https://www.yifysubtitles.com/search?q=" + movieName.replace(" ", "+");

        try {
            // Check if the Yify subtitle site gives any results for the movie search
            // Or else hit OMDB API to retrieve the correct Movie Name
            Document document = Jsoup.connect(yifySubtitleUrl).get();
            Element element = document.select("div.container > div.row > div > div[style=\"text-align:center;\"]").first();

            // If the Yify subtitle site returns no results, hit the OMDB API to retrieve the movie name
            if (element != null && "no results".equals(element.text())) {
                // Hit OMDB API to retrieve the Movie name
                String OmdbUrl = "http://www.omdbapi.com/?apikey=d345b81e&t=" + movieName.replace(" ", "+");
                String omdbJsonData = Jsoup.connect(OmdbUrl).ignoreContentType(true).execute().body();
                JsonObject jsonObject = new Gson().fromJson(omdbJsonData, JsonObject.class);
                if (jsonObject == null || jsonObject.get("Title") == null) {
                    return null;
                }
                System.out.println("\nNo results in yifysubtitles.com for Movie: " + movieName + "\n" + "Closest matching ");
                movieName = jsonObject.get("Title").getAsString();
            }
        }
        catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        return movieName;
    }

    private static Elements getSearchResults(String movieName) {
        final String yifySubtitleUrl = "https://www.yifysubtitles.com/search?q=" + movieName.replace(" ", "+");
        Document document = getHTMLContent(yifySubtitleUrl);
        if (document == null) {
            return null;
        }
        return document.select("div.media-body");
    }

    private static String getMovieURL(Element element) {
        String href = element.select("a").first().attr("href");
        return "https://www.yifysubtitles.com" + href;
    }

    private static String getMovieAndYear(Element element) {
        String movieName = element.select("h3.media-heading").text();
        String year = element.select("span.movinfo-section").first().text().substring(0, 4);
        return movieName + " " + year;
    }

    private static String getSubtitleURL(String movieUrl, String language) {
        // Get the HTML content of the page with list of subtitles in various languages
        Document document = getHTMLContent(movieUrl);
        if (document == null) {
            return null;
        }
        // Select all the subtitles in the page
        Elements elements = document.select("tr[data-id]");

        if (elements == null || elements.isEmpty()) {
            return null;
        }

        String subtitleUrl = null;
        /*
         * Loop through the tr[data-id] and check for the language
         * The first tr you find with language should be the highest rated subtitle for that language
         * as the tr displayed in the website is grouped by language and ordered by rating
         */
        for (Element element : elements) {
            if (element.select("td.flag-cell > span.sub-lang").text().contains(language)) {
                // Break after you find the highest rated subtitle
                subtitleUrl = element.select("td.download-cell > a").attr("href");
                break;
            }
        }
        subtitleUrl = (subtitleUrl == null) ? null : "https://www.yifysubtitles.com" + subtitleUrl;
        return subtitleUrl;
    }

    private static String getMovieFolder(String path) {
        if (path.lastIndexOf("/") != -1) {
            return path.substring(0, path.lastIndexOf("/"));
        }
        else if (path.lastIndexOf("\\") != -1) {
            return path.substring(0, path.lastIndexOf("\\"));
        }
        return null;
    }

    private static Document getHTMLContent(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        }
        catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        return document;
    }

    private static void downloadSubtitle(String downloadSubtitleUrl, String movieFolder) {
        try {
            URL u = new URL(downloadSubtitleUrl);
            // Open Connection and get the contents of the zip file
            URLConnection connection = u.openConnection();
            InputStream in = connection.getInputStream();
            // Create a temporary zip file to where contents will be written
            File zipfile = File.createTempFile("temp", ".zip", new File(movieFolder));
            FileOutputStream fileOutputStream = new FileOutputStream(zipfile);
            byte[] bytes = new byte[1024];
            int count;
            // Write contents to temporary Zip file
            while ((count = in.read(bytes)) >= 0) {
                fileOutputStream.write(bytes, 0, count);
            }
            // Flush the contents from output stream
            fileOutputStream.flush();

            File dir = new File(movieFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Create a Input Stream to read contents of the temporary zip file
            FileInputStream fileInputStream = new FileInputStream(zipfile);
            ZipInputStream zis = new ZipInputStream(fileInputStream);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String filename = ze.getName();
                // Check for .srt file and ignore other files
                if (!filename.endsWith(".srt")) {
                    ze = zis.getNextEntry();
                    continue;
                }
                // Create a new file for the .srt file
                File newFile = new File(movieFolder, filename);
                //Create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                // Write each zip file contents to the newly created file
                while ((count = zis.read(bytes)) >= 0) {
                    fos.write(bytes, 0, count);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            fileOutputStream.close();
            fileInputStream.close();
            in.close();
            // Delete the zip file
            zipfile.delete();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private static boolean checkExtension(File file, List<String> fileExtensions) {
        int index = file.toString().lastIndexOf(".");
        // If its a directory and there is no extension
        if (index == -1) {
            return false;
        }
        String format = file.toString().substring(file.toString().lastIndexOf("."));
        return fileExtensions.contains(format);
    }

    private static List<String> getFileExtensions() {
        return Arrays.asList(".avi", ".mp4", ".mkv", ".mpg", ".mpeg", ".mov", ".rm", ".vob", ".wmv", ".flv", ".3gp");
    }

    private static void displayMovieAndYear(Element element, int n) {
        String movieName = element.select("h3.media-heading").text();
        String year = element.select("span.movinfo-section").first().text().substring(0, 4);
        System.out.println(n + ". " + movieName + " " + year);
    }


}

class CommandOptions {
    private List arguments;

    // Constructor
    CommandOptions(String[] args) {
        arguments = Arrays.asList(args);
    }

    // Check whether the Option Exists
    boolean hasOption (String option) {
        return arguments.contains(option);
    }

    // Check whether the Option and Value Exists
    boolean hasOptionAndValue(String option) {
        return arguments.contains(option) && arguments.indexOf(option) + 1 < arguments.size();
    }

    // Return the Option's value if it exists
    String valueOf(String option) {
        if (arguments.indexOf(option) + 1 < arguments.size()) {
            return (String)arguments.get(arguments.indexOf(option) + 1);
        }
        else {
            return null;
        }
    }
}