## Subtitle Downloader
Subtitle Downloader is a Java Command Line tool to download subtitles for Movies in any language especially for Movies downloaded from Yify/YTS.

Download link: [subtitle-downloader](https://github.com/syedalisait/subtitle-downloader/releases/tag/v1.0)

Refer [Downloading Subtitles](#downloading-subtitles) section for help

## Table of Contents
  * [Overview](#overview)
  * [Pre-requisites](#pre-requisites)
  * [Verification](#verification)
    + [Windows](#windows)
    + [MAC/Linux](#mac-and-linux)
  * [Installation](#installation)
  * [Downloading Subtitles](#downloading-subtitles)
    + [Downloading Subtitles with Movie Name](#downloading-subtitles-with-movie-name)
    + [Downloading Subtitles with Movie File Path](#downloading-subtitles-with-movie-file-path)
    + [Downloading Subtitles with Movie Directory in Bulk](#downloading-subtitles-with-movie-directory-in-bulk)
    + [Downloading Subtitles with specific Language](#downloading-subtitles-with-specific-language)
  * [Working of Subtitle Downloader](#working-of-subtitle-downloader)
    + [Given a Movie Name](#given-a-movie-name)
    + [Given a File Path](#given-a-file-path)
    + [Given a Directory](#given-a-directory)
  * [Development Components](#development-components)
  * [Development Setup](#development-setup)
  * [Contribution](#contribution)
  * [Future Ideas](#future-ideas)


### Overview
Downloading subtitles for movies is a repetitive task which involves the following steps
1. Search for the subtitles Online
2. Download the highest rated subtitle
3. Extract the downloaded zip/rar file
4. Open the Movie in a Media player and adding the Subtitle track

The above process is a bit tedious.

Subtitle Downloader solves this problem. All you have to do is give **one of the three options** below
- Movie Name
- Movie File Path/Location
- Movies Directory Path where Multiple Movies are present inside the Directory

You can also download subtitles for Movies in a specific language. By default downloads Subtitles in English

### Pre-requisites
For this to work you need to install Java in your system
Follow this [tutorial](https://www.guru99.com/install-java.html), it hardly takes 5 mins to set up and install Java

### Verification
Once Java installation is complete, please do verify whether the installation is successful

#### Windows
- Press Windows + R button to open the Run window. Type cmd to open the Command-Line for Windows 
- Type `java -version` command to verify Java installation and its okay if the version number is different from what is shown below
```
C:\Users\admin> java -version
java version "1.8.0_191"
Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
```

- If there is an error like `java is not recognized as an internal or external command`, I would suggest you to set Environment Variable for `java` or give the entire path in cmd
```
C:\Program Files\Java\jdk1.8.0_121\bin\java -version
```

#### MAC and Linux
- Open Terminal and type the command to verify
```
$ java -version
java version "1.8.0_191"
Java(TM) SE Runtime Environment (build 1.8.0_191-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.191-b12, mixed mode)
```

### Installation
- Download the subtitle-downloader from [here](https://github.com/syedalisait/subtitle-downloader/releases/tag/v1.0)
- Move the downloaded file to your preferred location where it is easy to access frequently

### Downloading Subtitles

- Open Terminal/Cmd based on your Operating System (MAC/Windows/Unix)
- Move to the location of the downloaded(`subtitle-downloader.jar`) jar file. This can usually be done using the `cd` command in most of the Operating System
    - Example: `cd C:/Users/Downloads` if this is the location where the `subtitle-downloader.jar` file was downloaded 
- Enter the command `java -jar subtitle-downloader.jar -help` to know how to download subtitles with one of the three options

<br>

#### Downloading Subtitles with Movie Name

- Open Terminal/Cmd and Move to the location where you need the subtitles for the Movie to be downloaded
- Command:
```
java -jar <JAR_FILE_PATH> -m <MOVIE NAME>
```

**Example:**
- I need the subtitles downloaded inside directory `C:/Downloads/Movies/Inception`

```
C:/Users/admin> cd C:/Downloads/Movies/Inception
C:/Downloads/Movies/Inception> java -jar "C:/Users/downloads/subtitle-downloader.jar" -m "Inception"
```

<br>

#### Downloading Subtitles with Movie File Path
Command:
```
java -jar <JAR_FILE_PATH> -mP <MOVIE_FILE_PATH>
```
**Note: This will work fine only if the Movie Name has year appended to it**

**Example:**
- I need subtitles for the movie located here C:/Downloads/Movies/Prestige/Prestige.2010.bluray.mp4 (Year is present in the Movie Name)

```
java -jar "C:/Users/downloads/subtitle-downloader.jar" -mP "C:/Downloads/Movies/Prestige/Prestige.2010.bluray.mp4"
```

<br>

#### Downloading Subtitles with Movie Directory in Bulk
Command:
```
java -jar <JAR_FILE_PATH> -mD <MOVIES_FOLDER_PATH>
```

**Note:**
- **This will work fine only if the Movie Name has year appended to it**
- **Each Movie(.mp4 file) should have its own folder/directory. This folder/directory should be inside a common directory which will be parsed (Refer Example to understand better)**

**Example:**
- I have a folder `C:/Downloads/Movies` where inside the directory there are multiple directories and each directory has a movie
- Something like:
    - C:/Downloads/Movies/Inception/Inception.2010.bluray.mp4
    - C:/Downloads/Movies/Final Destination (2014)/Final Destination (2014) x264 Bluray.mp4
    - C:/Downloads/Movies/Prestige (2006)/Prestige.2006.x264.Bluray.[720p].mp4
- To download subtitle for all these Movies:
```
java -jar "C:/Users/downloads/subtitle-downloader.jar" -mD "C:/Downloads/Movies"
```

#### Downloading Subtitles with specific Language
- Can be used with other above commands with an extra parameter
Command:
```
java -jar <JAR_FILE_PATH> -m <MOVIES_NAME> -lang "French"
```

### Working of Subtitle Downloader

#### Given a Movie Name
- Searches for the movie in yifysubtitles.com
- If the movie is found or if there are multiple movies found, returns the list and asks the user to select one of the movie
- When the user selects the movie, downloads the highest rated subtitle for the movie in yifysubtitles.com
- If there is a specific language that the user has provided, then searches for the highest rated subtitle for the specific language and downloads the movie

#### Given a File Path
- Parse the Movie File Path to guess the right movie name
- Searches yifysubtitles.com to find out if any movie exists
- If none were found, then hits Omdb Api to figure out the movie name
- Still if it cannot find the results, asks the user to input the correct movie name
- Then same logic as [above](#given-a-movie-name)

#### Given a Directory
- Performs the same logic as [Given a File Path](#given-a-file-path) for all the movies
- Checks if the movie already has .srt file
- If yes Skips downloading the Subtitle for that Movie

### Development Components
- **Language:** Core Java
- **Source of Subtitles:** Downloads Subtitles from yifysubtitles.com
- **Movie Name:** [OmdbApi](https://www.omdbapi.com/) to get the correct Movie Name
- **HTML Parser:** Using [Jsoup](https://jsoup.org/) to parse the contents of html
- **JSON Parser:** Using [Gson](https://github.com/google/gson) to parse the Json
- **Maven:** To manage dependencies (Gson and Jsoup)

### Development Setup
- Fork the repository
- Then Clone the repository which has been created in your github account from local
```
git clone https://github.com/{USERNAME}/subtitle-downloader.git
```
- Open the project in any IDE (My preference is IntelliJ) by selecting `File -> New -> Project From Existing Sources`
- Select the **subtitle-downloader**
- On next window select "Maven" and then complete the next next windows with default settings and Finish
- Running the program needs 'Command Line Arguments' as parameters. So, do a `Run -> Edit with Configuration` and add appropriate 'Command Line Arguments' (Example: -m "Inception")
- Right click SubtitleDownloader.java and Run the program as Java Application
- Output will be displayed in the Console

### Contribution
- Contributions are welcome even if its relatively simple in terms of Exception handling, Conversion of Java 7 to Java 8, improvisation of Logic and any other improvements that can be made to the tool

### Future Ideas
- Searching Multiple sites for subtitles if its not present in yifysubtitles (Subscene, Opensubtitles etc.)
- Transition from Command Line to GUI with JavaFX or Swing
