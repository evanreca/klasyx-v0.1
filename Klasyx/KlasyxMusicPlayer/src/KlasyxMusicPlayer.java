/*
KlasyxMusicPlayer.java
Programmer: Evan Reca

This is the main class of the Klasyx Music Player. It launches the application and manages everything except
audio playback, which is handled by AudioPlayer.java

K N O W N   B U G S:

1. Attempting to restart the song when it's paused causes an IllegalArgumentException at line 415 likely because
   the selectSongButton event handler is attempting insert the timestampPane into the activeSongPane a second time
   (using the add method at line 415) after it's already done that once already when the selectSongButton
    was clicked the first time.

    --- Threat: not critical ---

2. TBD

    --- Threat: TBD ---

------------------------------------------------------------------------------------------------------------------------

- GLOSSARY -

Critical == crashes the program

############################################## IMPORTS AND CLASS DEFINITION ############################################
*/

// GUI IMPORTS
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDate;

// DATABASE IMPORTS
import java.sql.*; // Should parse this out into the individual imports that actually get used
import java.util.Timer;
import java.util.TimerTask;

public class KlasyxMusicPlayer extends Application {

	// KLASYX MUSIC PLAYER CLASS VARIABLES

	AudioPlayer audioPlayer;
	String status = "paused"; // either playing or paused

	// 1 = repeat active song continuously and 0 = do not loop active song
	int repeatStatus = 1;

	// displays current timestamp in active song. Example: currentTimeMin + ":" + currentTimeSec = "9:15"
	int currentTimeMin = 0;
	int currentTimeSec = 0;

	long currentMicroPos;

	// STARTS APPLICATION
	public static void main(String[] args) {
		Application.launch(args);
	}

	public void start(Stage primaryStage) throws Exception {

// ################################ DATABASE CONNECTION TEST AND TEST QUERY ############################################


		try{
			// connects to the database
			Connection mySqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/create_klasyx_db",
					                                                "root", "password");
			// creates a statement
			Statement myStatement = mySqlConnection.createStatement();

			// executes a query
			ResultSet myResults = myStatement.executeQuery("SELECT * FROM songs");

			// processes results
			while (myResults.next()) {
				System.out.println("database connection test:\n\n"
						         + "Song ID: " + myResults.getString("song_id") + "\n"
						         + "Title: " + myResults.getString("title") + "\n"
						         + "Release Year: " + myResults.getString("release_year") + "\n"
						 		 + "Song Length: " + myResults.getString("song_length") + "\n"
						         + "Filepath: " + myResults.getString("filepath") + "\n"
							     + "Composer ID: " + myResults.getString("composers_composer_id")
						         + "\n-----------------------------------\n");
			}
		}

		catch(Exception exc){
			exc.printStackTrace();
		}

// ############################################## USER INTERFACE #######################################################

		// GridPanes
		GridPane superMainGridPane = new GridPane();
		GridPane mainGridPane = new GridPane();

		GridPane mainTabsPane = new GridPane();
		GridPane currentTabPane = new GridPane();

		GridPane homePane = new GridPane();

		GridPane songsPane = new GridPane();
		GridPane sortingSongsPane = new GridPane();
		GridPane songsListPane = new GridPane();

		GridPane playlistsPane = new GridPane();
		GridPane historyPane = new GridPane();
		GridPane settingsPane = new GridPane();

		GridPane activeSongPane = new GridPane();
		GridPane timestampPane = new GridPane();

		// Active Song's "progress bar" using a Slider object
		Slider slider = new Slider();
		slider.setPrefWidth(400);
		activeSongPane.add(slider,1,0);
		slider.setMin(0.0);
		slider.setMax(555.0);
		slider.setBlockIncrement(1.0);

		// Panes that go inside other panes + their settings
		superMainGridPane.add(mainGridPane, 0, 1);
		superMainGridPane.setPadding(new Insets(20, 20, 20, 20));
		superMainGridPane.setStyle("-fx-background-color: #BDBDBD;");
//		superMainGridPane.setStyle("-fx-background-color: linear-gradient(from 25px 25px to 100px 100px, #696969, #BDBDBD);");
		currentTabPane.add(homePane,0,0);

		//Title Text
		Text title = new Text();
		title.setText("---- Klasyx Music Player ----");
		title.setFont(Font.font("Vivaldi", FontWeight.BOLD ,50));
		superMainGridPane.add(title, 0, 0);

		// Each GridPane's Location (column and row) + mainGridPane settings
		mainGridPane.setVgap(20);
		mainGridPane.setHgap(50);
		mainGridPane.setLayoutX(10);
		mainGridPane.add(mainTabsPane, 0, 1);
		mainGridPane.add(currentTabPane, 0, 2);
		mainGridPane.add(activeSongPane, 0, 3);
//		mainGridPane.setStyle("-fx-background-color: #C8C8C8;");
		mainGridPane.setStyle("-fx-background-color: #BDBDBD;");

// ---------------------------------------------------------------------------------------------------------------------

		// PLACES THE 5 MAIN TABS - BUTTON CONTROL OBJECTS

		// 5 Main Tabs - creates each button
		Button homeButton = new Button();
		Button songsButton = new Button();
		Button playlistsButton = new Button();
		Button historyButton = new Button();
		Button settingsButton = new Button();

		// Home Button
		mainTabsPane.add(homeButton, 0,0);
		mainTabsPane.setVgap(5);
		homeButton.setText("Home");
		homeButton.setMinWidth(100);
		homeButton.setMinHeight(85);

		// Songs Button
		mainTabsPane.add(songsButton, 1,0);
		songsButton.setText("Songs");
		songsButton.setMinWidth(100);
		songsButton.setMinHeight(85);

		// Playlists Button
		mainTabsPane.add(playlistsButton, 2,0);
		playlistsButton.setText("Playlists");
		playlistsButton.setMinWidth(100);
		playlistsButton.setMinHeight(85);

		// History Button
		mainTabsPane.add(historyButton, 3,0);
		historyButton.setText("History");
		historyButton.setMinWidth(100);
		historyButton.setMinHeight(85);

		// Settings Button
		mainTabsPane.add(settingsButton, 4,0);
		settingsButton.setText("Settings");
		settingsButton.setMinWidth(100);
		settingsButton.setMinHeight(85);

// ---------------------------------------------------------------------------------------------------------------------

		// EACH MAIN TAB'S GRID PANE CONTENTS AND SETTINGS

		// Contents inside the Home Pane
		Text homeWelcomeText = new Text();
		homeWelcomeText.setText("  Welcome, User. Please enjoy today's Pick of the Day: ");
		homeWelcomeText.setFont(Font.font("default", FontWeight.BOLD ,20));

		Text homeWelcomeText2 = new Text();
		homeWelcomeText2.setText("                Introduction and Rondo Cappricioso");
		homeWelcomeText2.setFont(Font.font("default", FontWeight.BOLD ,20));

		Text homeWelcomeText3 = new Text();
		LocalDate today = LocalDate.now();
		homeWelcomeText3.setText("                                   " + today.toString());
		homeWelcomeText3.setFont(Font.font("default", FontWeight.BOLD ,20));

		homePane.add(homeWelcomeText, 0, 0);
		homePane.add(homeWelcomeText2, 0, 1);
		homePane.add(homeWelcomeText3, 0, 2);

		// Contents inside the Song Pane
		Text fillerTextSongPane = new Text();  // fillerTexts only exist to properly pad out pane spacing
		Text fillerTextSongPane1 = new Text(); // because of the way the grid is designed. If you don't add
		Text fillerTextSongPane2 = new Text(); // content to empty grid locations then it improperly
		Text fillerTextSongPane3 = new Text(); // changes how the panes are laid out. So I've added dummy text objects
		Text fillerTextSongPane4 = new Text(); // with no actual text assigned to them to circumvent this issue.
		Text fillerTextSongPane5 = new Text();
		Text fillerTextSongPane6 = new Text();

//		fillerTextSongPane6.setText("      "); // pads out selectSongButton on the left hand side

		Text songPaneTitleText = new Text();
		songPaneTitleText.setText("Klasyx: Available Library Contents");
		songPaneTitleText.setFont(Font.font("default", FontWeight.BOLD ,22));

		RadioButton sortByComposerRB = new RadioButton();
		RadioButton sortByTitleRB = new RadioButton();
		sortByTitleRB.setSelected(true); // defaults to sort by title name alphabetically
		ToggleGroup sortOptionsTG = new ToggleGroup();
		sortByComposerRB.setToggleGroup(sortOptionsTG);
		sortByTitleRB.setToggleGroup(sortOptionsTG);

		Text sortText = new Text();
		sortText.setText("Sort By: ");
		sortText.setFont(Font.font("default", FontWeight.BOLD ,18));

		sortByTitleRB.setText("Title");
		sortByTitleRB.setFont(Font.font("default", FontWeight.BOLD ,18));
		sortByComposerRB.setText("Composer");
		sortByComposerRB.setFont(Font.font("default", FontWeight.BOLD ,18));

		Button selectSongButton = new Button();
		selectSongButton.setText("Introduction and Rondo Capriccioso ---------------- Saint-Saëns ----------------");
		selectSongButton.setFont(Font.font("default", FontWeight.BOLD ,16));
		selectSongButton.setWrapText(true);
		selectSongButton.setMinHeight(55);
		selectSongButton.setMaxWidth(305);

		songsPane.setVgap(15);
		songsPane.setHgap(75);

		sortingSongsPane.setHgap(10);
		sortingSongsPane.setStyle("-fx-padding: 0 0 0 35;");
		
		songsPane.add(fillerTextSongPane,0,0);
		songsPane.add(songPaneTitleText,1,0);
		songsPane.add(fillerTextSongPane1,2,0);

		songsPane.add(fillerTextSongPane2,0,1);
		songsPane.add(sortingSongsPane,1,1);
		songsPane.add(fillerTextSongPane3,2,1);

		sortingSongsPane.add(sortText,0,0);
		sortingSongsPane.add(sortByTitleRB,1,0);
		sortingSongsPane.add(sortByComposerRB,2,0);

		songsPane.add(fillerTextSongPane4,0,2);
		songsPane.add(songsListPane,1,2);
		songsPane.add(fillerTextSongPane5,2,2);

		songsListPane.add(fillerTextSongPane6, 0, 0);
		songsListPane.add(selectSongButton, 1, 0); // only the row number should increase with each
		                                                             // new song button added
		songsListPane.setPadding(new Insets(0, 0, 0, 30));

// #################################### EVENT HANDLERS FOR THE 5 MAIN TABS #############################################

		// EVENT HANDLERS FOR THE 5 MAIN TABS

		// Click Home Button - Event Handler
		EventHandler<ActionEvent> homeMainTabEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
			currentTabPane.getChildren().remove(0);
			currentTabPane.add(homePane, 0, 0);
            }
        };
        homeButton.setOnAction(homeMainTabEvent);

        // Click Song Button - Event Handler
		EventHandler<ActionEvent> songMainTabEvent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
				currentTabPane.getChildren().remove(0);
				currentTabPane.add(songsPane, 0, 0);
				// clicking song tab should make the window taller
				// to correctly fit all the song buttons
            }
		};
		songsButton.setOnAction(songMainTabEvent);

		// Click Playlist Button - Event Handler
		EventHandler<ActionEvent> playlistMainTabEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// playlist event handler contents goes here
			}
		};
		playlistsButton.setOnAction(playlistMainTabEvent);

		// Click History Button - Event Handler
		EventHandler<ActionEvent> historyMainTabEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// history event handler contents goes here
			}
		};
		historyButton.setOnAction(historyMainTabEvent);

		// Click Settings Button - Event Handler
		EventHandler<ActionEvent> settingsMainTabEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// settings event handler contents goes here
			}
		};
		settingsButton.setOnAction(settingsMainTabEvent);

// #####################################################################################################################
// ########## ACTIVE SONG GRID PANE (OBJECTS ON THE BOTTOM OF THE WINDOW - PLAY/PAUSE, REPEAT, SLIDER, ETC.) ###########
// #####################################################################################################################

		activeSongPane.setHgap(40);
		activeSongPane.setVgap(-5);

		// creates playPauseButton and settings for it
		Button playPauseButton = new Button();
		activeSongPane.add(playPauseButton, 0, 0);
		playPauseButton.setStyle("-fx-background-image: url('paused.png')");
		playPauseButton.setMinWidth(50);
		playPauseButton.setMinHeight(50);

		// creates repeat button and settings for it
		Button repeatOnOff = new Button();
		activeSongPane.add(repeatOnOff, 2, 0);
		repeatOnOff.setStyle("-fx-background-image: url('repeatOn.png')");
		repeatOnOff.setMinWidth(50);
		repeatOnOff.setMinHeight(50);

		// timestampCurrent text - text that displays song's current position in time
		Text timestampCurrent = new Text();
		timestampCurrent.setText(currentTimeMin + ":" + currentTimeSec);
		timestampCurrent.setFont(Font.font("default", FontWeight.BOLD ,18));

		// EVENT HANDLERS FOR THE OBJECTS INSIDE THE ACTIVE SONG PANE

		// Click playPauseButton - Event Handler
		EventHandler<ActionEvent> playPauseButtonEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				// if a song is playing, pause it and change the button's background image accordingly
				if (audioPlayer == null) {
					return;
				}

				// if the song is currently paused, unpause it
				if(status == "paused" ) {
					try {
						audioPlayer.resumeAudio();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					playPauseButton.setStyle("-fx-background-image: url('playing.png')");
					audioPlayer.setStatus("playing");
					status = audioPlayer.getStatus();
				}

				// if the song is currently playing, pause it
				else {
					playPauseButton.setStyle("-fx-background-image: url('paused.png')");
					try {
						audioPlayer.pause();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					audioPlayer.setStatus("paused");
					status = audioPlayer.getStatus();
				}
			}
		};
		playPauseButton.setOnAction(playPauseButtonEvent);

		// Click repeatOnOffButton - Event Handler: default setting is setRepeatOn
		EventHandler<ActionEvent> repeatOnOffButtonEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				// if a song is playing, it defaults to
				if (audioPlayer == null) {
					return;
				}

				// if the repeat setting is on, turn it off
				if(repeatStatus == 1) {
					try {
						audioPlayer.setRepeatOff();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					repeatOnOff.setStyle("-fx-background-image: url('repeatOff.png')");
					repeatStatus = 0;
				}

				// if the song is not set to repeat, then set it to repeat
				else {
					try {
						audioPlayer.setRepeatOn();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					repeatOnOff.setStyle("-fx-background-image: url('repeatOn.png')");
					repeatStatus = 1;
				}
			}
		};
		repeatOnOff.setOnAction(repeatOnOffButtonEvent);

		// Click selectSongButton - Event Handler
		EventHandler<ActionEvent> demoSongButtonEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				// start playing a song - initial state is set to "paused" when program launches
				if(status == "paused") {
					try {
						if (audioPlayer != null) {
							audioPlayer.stop();
							audioPlayer = null;
						}
						audioPlayer = new AudioPlayer();
					}
						catch (Exception ex) {
						ex.printStackTrace();
						}
					playPauseButton.setStyle("-fx-background-image: url('playing.png')");
					audioPlayer.setStatus("playing");
					status = audioPlayer.getStatus();

					//tests getSongLength Method
					Long songLength2 = audioPlayer.getSongLength();
//					System.out.println("Song length in microseconds: " + songLength2);

					// grid pane that contains text timestamps beneath slider - varies by song
					activeSongPane.add(timestampPane, 1, 1);
					timestampPane.setHgap(125);

					// places the timestamp into the timestamp pane
					timestampPane.add(timestampCurrent, 0, 0);

					// timestampEnd text - text that displays active song's total length
					Text timestampEnd = new Text();

					String songLength = "";
					try{
						Connection mySqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/create_klasyx_db",
								"root", "password");
						Statement myStatement = mySqlConnection.createStatement();
						ResultSet myResults = myStatement.executeQuery("SELECT song_length FROM songs WHERE song_id = 1");
						myResults.next();
						songLength = myResults.getString("song_length");
					}

					catch(Exception exc){
						exc.printStackTrace();
					}

					timestampEnd.setText(songLength);
					timestampEnd.setFont(Font.font("default", FontWeight.BOLD ,18));
					timestampPane.add(timestampEnd, 2, 0);

						try {
							Timer timer = new Timer();
							TimerTask timerTask = new TimerTask() {
								@Override
								public void run() {
									updateTime(status, audioPlayer, slider, timestampCurrent);
								}
							};
							timer.scheduleAtFixedRate(timerTask, 0, 1000);
						}
						catch (Exception e2) {
							e2.printStackTrace();
						}
				}

				// clicking the selectSongButton after the song's already started playing will restart the song
				else {
					try {
						audioPlayer.restart();
						currentTimeSec = 0;
						currentTimeMin = 0;
						slider.setValue(0.0);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		selectSongButton.setOnAction(demoSongButtonEvent);

// ---------------------------------------------------------------------------------------------------------------------

		// Drag Slider - Event Handler
		EventHandler<MouseEvent> dragSliderEvent = e -> {
			try {
				audioPlayer.jump((long)(slider.getValue() * 1_000_000));
				if (status == "playing") {
					audioPlayer.resumeAudio();
				}
			}
			catch (Exception exeption) {
				exeption.printStackTrace();
			}
		};
		slider.setOnMouseReleased(dragSliderEvent);

// ################################### SETS SCENE ######################################################################

		Scene scene = new Scene(superMainGridPane,540,420); // original dimensions 540 x 425
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Klasyx Music Player");
		Image icon = new Image("Eighth_Note_Ver2.png");
		primaryStage.getIcons().add(icon);
		primaryStage.show();
		}

// ############################## TIMER/SLIDER UPDATE METHOD ##################################################################

	public void updateTime(String status, AudioPlayer audioPlayer, Slider slider
					    , Text timestampCurrent) {
		// SLIDER AND TIMER
		currentMicroPos = audioPlayer.getMicrosecondPosition() % audioPlayer.getSongLength();
		int timeSeconds = (int)(currentMicroPos / 1_000_000);
		currentTimeSec = timeSeconds % 60;

		if (timeSeconds <= 555) {
			//Update current count of seconds
			String secondsPad;

			// pads zeros for the timer
			if (currentTimeSec < 10) {
				secondsPad = "0";
			}
			else {
				secondsPad = "";
			}
			slider.increment();

			if (slider.getValue() == slider.getMax()) {
				slider.setValue(0.0);
			}

			//Update the current count of minutes
			currentTimeMin = (int)(timeSeconds / 60);

			//Update the timer display
			timestampCurrent.setText(currentTimeMin + ":" + secondsPad + currentTimeSec);
		}
	}
}
