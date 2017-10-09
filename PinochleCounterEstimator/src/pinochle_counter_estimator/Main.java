package pinochle_counter_estimator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends PinochleStuff {
	/* "stack" everywhere in this program refers to the physical stack of cards
	 * that a player accumulates as he wins tricks (not the data structure) */
	public static final int FITWITDTH_GRID_STATS = 25;
	public static final int FITHIGHT_GRID_STATS = 20;
	public static final int FONTSIZE_GRID_STATS = 20;
	public static final int FITWITDTH_STACKCOUNT = 135;
	public static final int FITLENGTH_STACKCOUNT = 45;
	public static final int FONTSIZE_STACKCOUNT = 55;
	public static final double CARD_HEIGHT = 218;
	public static final double CARD_WIDTH = 150;
	public static final double X_COORD_LEFT_MOST_CARD = 300.0;
	public static final double Y_COORD_LEFT_MOST_CARD = 710.0;
	public static final double Y_COORD_DISCARD_AREA = Y_COORD_LEFT_MOST_CARD + CARD_HEIGHT / 3;
	public static final double Y_COORD_LEGAL_AREA = Y_COORD_LEFT_MOST_CARD - CARD_HEIGHT / 4;
	public static final double CARD_SPACING = 21;
	public static final double DISCARD_TIME = 200; // milliseconds
	public static final double DISCARD_TIME_SUGGESTED_CARDS = 500; // milliseconds
	public static final double TRANSITION_TIME = 200; // milliseconds
	public static final double PAUSE_TIME = 700; // milliseconds
	public static final double LEGAL_CARDS_FORWARD_TIME = 300; // milliseconds
	public static final double LEGAL_CARDS_BACKWARD_TIME = 300; // milliseconds
	public static final double X_COORD_TABLE_MELDSEAT = 600;
	public static final double Y_COORD_TABLE_MELDSEAT = 350;
	public static final double X_COORD_TABLE_BACKSEAT = X_COORD_TABLE_MELDSEAT - CARD_WIDTH / 3;
	public static final double Y_COORD_TABLE_BACKSEAT = Y_COORD_TABLE_MELDSEAT - CARD_WIDTH / 3;
	public static final double X_COORD_TABLE_DRIVERSEAT = X_COORD_TABLE_MELDSEAT + CARD_WIDTH / 3;
	public static final double Y_COORD_TABLE_DRIVERSEAT = Y_COORD_TABLE_MELDSEAT - 2 * CARD_WIDTH / 3;
	public static final double X_COORD_BACKSEAT = -200;
	public static final double Y_COORD_BACKSEAT = -300;
	public static final double X_COORD_DRIVERSEAT = 1500;
	public static final double Y_COORD_DRIVERSEAT = -300;
	public static final double X_COORD_OPPONENT_STACK = X_COORD_TABLE_MELDSEAT;
	public static final double Y_COORD_OPPONENT_STACK = -300;
	public static final double Y_COORD_MELDSEAT_STACK = 1300;
	public static final double X_COORD_MY_STACKCOUNT = 350;
	public static final double Y_COORD_MY_STACKCOUNT = Y_COORD_LEFT_MOST_CARD + CARD_HEIGHT - 90;
	public static final double[] xCoordOfTableCards = { X_COORD_TABLE_MELDSEAT, X_COORD_TABLE_BACKSEAT,
			X_COORD_TABLE_DRIVERSEAT };
	public static final double[] yCoordOfTableCards = { Y_COORD_TABLE_MELDSEAT, Y_COORD_TABLE_BACKSEAT,
			Y_COORD_TABLE_DRIVERSEAT };
	public static final double[] yCoordPlayerHands = { Y_COORD_LEGAL_AREA, Y_COORD_BACKSEAT, Y_COORD_DRIVERSEAT };
	public static final ImageView[] trumpSuitGraphics = { new ImageView("file:spades.png"),
			new ImageView("file:diamonds.png"), new ImageView("file:clubs.png"), new ImageView("file:hearts.png") };

	public static int saveAmountInt;
	public static int discardCount;
	public static int imageTableCount;
	public static int clickIndex;
	public static boolean isPlayModeOn;
	public static boolean isTransitionModeOn;
	public static boolean playWithMySameLastHand;
	public static boolean playWithOpponentsSameLastHands;

	public static int[] meldSeatLastHand;
	public static int[] backSeatLastHand;
	public static int[] throwAwayCards = new int[5];
	public static double[] xCoordPlayerHands = { clickIndex, X_COORD_BACKSEAT, X_COORD_DRIVERSEAT };

	public static String saveAmountString;
	public static Text playACardText;
	public static Text discard5FiveCardsText;
	public static Text backSeatLocationText;
	public static Text driverSeatLocationText;
	public static Text distributionText;
	
	public static Stage window;
	public static Pane pane;
	public static HBox hBox;
	public static StackPane countersCountPane;
	public static StackPane paneForCheckBox;
	public static GridPane grid;
	
	public static Hand myHand;
	public static Hand backSeatHand;
	public static Hand driverSeatHand;
	public static Hand[] cardSets = new Hand[3];

	public static ImageView[] tableImages = new ImageView[3];	
	public static Slider slider;
	public static RadioButton[] trumpSuitButtons = new RadioButton[4];
	public static RadioButton[] AI_buttons = new RadioButton[4];	
	public static Button suggestButton;	
	public static Timer timer = new Timer();
	public static CheckBox checkBox = new CheckBox("Choose Opponent\n    Distribution");	

	public static void main (String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {			
		setUpNewGame(primaryStage);
	}	

	public static void setUpNewGame(Stage primaryStage) {		
		window = primaryStage;
		pane = new Pane();
		pane.setStyle("-fx-background-color: midnightblue");
		deal();
		
		//////////// rectangle //////////////////////////////////////
		Rectangle rectangle = new Rectangle(X_COORD_LEFT_MOST_CARD - CARD_WIDTH / 2 - 10,
				Y_COORD_LEFT_MOST_CARD - CARD_HEIGHT / 2 - 10, 29 * CARD_SPACING + CARD_WIDTH + 20, CARD_HEIGHT + 20);
		rectangle.setFill(Color.TRANSPARENT);
		rectangle.setStroke(Color.GRAY);
		rectangle.setStrokeWidth(2);
		pane.getChildren().add(rectangle);
		
		//////////// hBox and countersCountPane  //////////////////////////////////////
		hBox = new HBox();
		countersCountPane = new StackPane();
		hBox.setVisible(false);
		hBox.setLayoutX(X_COORD_MY_STACKCOUNT);
		hBox.setLayoutY(Y_COORD_MY_STACKCOUNT);
		
		Text countersInStackText = new Text("\n(Current Counters)--> ");
		countersInStackText.setFill(Color.WHITE);
		countersInStackText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
		
		Text predictedCounters = new Text("\n <--(Predicted Counters)");
		predictedCounters.setFill(Color.WHITE);
		predictedCounters.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
		
		hBox.getChildren().addAll(countersInStackText, countersCountPane, predictedCounters);
		pane.getChildren().add(hBox);
		
		/////////// myHand  ///////////////////////////////////////////
		for (int i = 0; i < 30; i++) {
			myHand.getCard(i).getImage().setX(X_COORD_LEFT_MOST_CARD - CARD_WIDTH / 2 + i * CARD_SPACING);
			myHand.getCard(i).getImage().setY(Y_COORD_LEFT_MOST_CARD - CARD_HEIGHT / 2);
			myHand.getCard(i).getImage().setFitHeight(CARD_HEIGHT);
			myHand.getCard(i).getImage().setFitWidth(CARD_WIDTH);
			pane.getChildren().add(myHand.getCard(i).getImage());
		}
		
		myHand.getCard(0).getImage().setOnMouseClicked(e -> handleCardClick(0));
		myHand.getCard(1).getImage().setOnMouseClicked(e -> handleCardClick(1));
		myHand.getCard(2).getImage().setOnMouseClicked(e -> handleCardClick(2));
		myHand.getCard(3).getImage().setOnMouseClicked(e -> handleCardClick(3));
		myHand.getCard(4).getImage().setOnMouseClicked(e -> handleCardClick(4));
		myHand.getCard(5).getImage().setOnMouseClicked(e -> handleCardClick(5));
		myHand.getCard(6).getImage().setOnMouseClicked(e -> handleCardClick(6));
		myHand.getCard(7).getImage().setOnMouseClicked(e -> handleCardClick(7));
		myHand.getCard(8).getImage().setOnMouseClicked(e -> handleCardClick(8));
		myHand.getCard(9).getImage().setOnMouseClicked(e -> handleCardClick(9));
		myHand.getCard(10).getImage().setOnMouseClicked(e -> handleCardClick(10));
		myHand.getCard(11).getImage().setOnMouseClicked(e -> handleCardClick(11));
		myHand.getCard(12).getImage().setOnMouseClicked(e -> handleCardClick(12));
		myHand.getCard(13).getImage().setOnMouseClicked(e -> handleCardClick(13));
		myHand.getCard(14).getImage().setOnMouseClicked(e -> handleCardClick(14));
		myHand.getCard(15).getImage().setOnMouseClicked(e -> handleCardClick(15));
		myHand.getCard(16).getImage().setOnMouseClicked(e -> handleCardClick(16));
		myHand.getCard(17).getImage().setOnMouseClicked(e -> handleCardClick(17));
		myHand.getCard(18).getImage().setOnMouseClicked(e -> handleCardClick(18));
		myHand.getCard(19).getImage().setOnMouseClicked(e -> handleCardClick(19));
		myHand.getCard(20).getImage().setOnMouseClicked(e -> handleCardClick(20));
		myHand.getCard(21).getImage().setOnMouseClicked(e -> handleCardClick(21));
		myHand.getCard(22).getImage().setOnMouseClicked(e -> handleCardClick(22));
		myHand.getCard(23).getImage().setOnMouseClicked(e -> handleCardClick(23));
		myHand.getCard(24).getImage().setOnMouseClicked(e -> handleCardClick(24));
		myHand.getCard(25).getImage().setOnMouseClicked(e -> handleCardClick(25));
		myHand.getCard(26).getImage().setOnMouseClicked(e -> handleCardClick(26));
		myHand.getCard(27).getImage().setOnMouseClicked(e -> handleCardClick(27));
		myHand.getCard(28).getImage().setOnMouseClicked(e -> handleCardClick(28));
		myHand.getCard(29).getImage().setOnMouseClicked(e -> handleCardClick(29));

		//////////// cardSets  /////////////////////////////////////////////
		for (int i = 0; i < 3; i++) {
			cardSets[i] = new Hand();
			cardSets[i].createFullCardSet();
		}
		
		for (int i = 0; i < 60; i++) {
			cardSets[i / 20].getCard(i % 20).getImage().setFitHeight(CARD_HEIGHT);
			cardSets[i / 20].getCard(i % 20).getImage().setFitWidth(CARD_WIDTH);
			cardSets[i / 20].getCard(i % 20).getImage().setVisible(false);
			pane.getChildren().add(cardSets[i / 20].getCard(i % 20).getImage());
		}

		//////////////////// trump radioButtons//////////////////////////////////
		VBox paneForRadioButtons = new VBox(5);
		paneForRadioButtons.setPadding(new Insets(5, 5, 5, 5));
		paneForRadioButtons.setStyle(" -fx-border-color: white;-fx-border-width: 2px");
		paneForRadioButtons.setLayoutY(Y_COORD_LEFT_MOST_CARD - CARD_HEIGHT/2);

		Text trumpText = new Text("Trump");
		trumpText.setFill(Color.WHITE);
		trumpText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
		paneForRadioButtons.getChildren().add(trumpText);

		ToggleGroup group = new ToggleGroup();

		for (int i = 0; i < 4; i++) {
			trumpSuitButtons[i] = new RadioButton();
			paneForRadioButtons.getChildren().add(trumpSuitButtons[i]);
			trumpSuitGraphics[i].setFitHeight(30);
			trumpSuitGraphics[i].setFitWidth(30);
			trumpSuitButtons[i].setGraphic(trumpSuitGraphics[i]);
			trumpSuitButtons[i].setToggleGroup(group);
		}

		pane.getChildren().add(paneForRadioButtons);

		trumpSuitButtons[0].setOnAction(e -> handleTrumpSelectClick(0));
		trumpSuitButtons[1].setOnAction(e -> handleTrumpSelectClick(1));
		trumpSuitButtons[2].setOnAction(e -> handleTrumpSelectClick(2));
		trumpSuitButtons[3].setOnAction(e -> handleTrumpSelectClick(3));

		//////////////////// AI radioButtons////////////////////////////////////////
		VBox paneForAIRadioButtons = new VBox(5);
		paneForAIRadioButtons.setPadding(new Insets(5, 5, 5, 5));
		paneForAIRadioButtons.setStyle(" -fx-border-color: white;-fx-border-width: 2px");

		Text AIText = new Text("   A.I.");
		AIText.setFill(Color.WHITE);
		AIText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
		pane.getChildren().add(AIText);

		paneForAIRadioButtons.getChildren().add(AIText);

		ToggleGroup groupAI = new ToggleGroup();

		AI_buttons[0] = new RadioButton("Max");
		AI_buttons[1] = new RadioButton("Med");
		AI_buttons[2] = new RadioButton("Low");
		AI_buttons[3] = new RadioButton("Off");
		AI_buttons[3 - AILevel].setSelected(true);
		
		for (int i = 0; i < 4; i++) {
			paneForAIRadioButtons.getChildren().add(AI_buttons[i]);
			AI_buttons[i].setToggleGroup(groupAI);
			AI_buttons[i].setTextFill(Paint.valueOf("#ffffff"));
			AI_buttons[i].setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
		}

		pane.getChildren().add(paneForAIRadioButtons);

		AI_buttons[0].setOnAction(e -> AILevel = 3);
		AI_buttons[1].setOnAction(e -> AILevel = 2);
		AI_buttons[2].setOnAction(e -> AILevel = 1);
		AI_buttons[3].setOnAction(e -> AILevel = 0);
		
		//////////////////// gridPane//////////////////////////////////////////
		grid = new GridPane();
		grid.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		grid.setHgap(5.5);
		grid.setVgap(5.5);

		Text meldSeatText = new Text("MeldSeat");
		meldSeatText.setFill(Color.WHITE);
		meldSeatText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));

		Text backSeatText = new Text("BackSeat");
		backSeatText.setFill(Color.WHITE);
		backSeatText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));

		Text driverSeatText = new Text("DriverSeat");
		driverSeatText.setFill(Color.WHITE);
		driverSeatText.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));

		Text trumpTextForGird = new Text("(trump)-->");
		trumpTextForGird.setFill(Color.WHITE);
		trumpTextForGird.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
		GridPane.setHalignment(trumpTextForGird, HPos.RIGHT);

		Text backupTextForGird = new Text("<--(backup)");
		backupTextForGird.setFill(Color.WHITE);
		backupTextForGird.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

		grid.add(meldSeatText, 0, 1);
		grid.add(backSeatText, 0, 2);
		grid.add(driverSeatText, 0, 3);

		grid.add(trumpTextForGird, 0, 0);
		grid.add(backupTextForGird, 3, 0);
		
		for (int i = 1; i <= 2; i++) {
			for (int j = 0; j <= 3; j++) {
				ImageView image = new ImageView("file:girdStatsBackground.png");
				image.setFitWidth(FITWITDTH_GRID_STATS);
				image.setFitHeight(FITHIGHT_GRID_STATS);
				grid.add(image, i, j);
			}
		}
		
		grid.setLayoutY(40);
		grid.setLayoutX(900);
		grid.setVisible(false);
		pane.getChildren().add(grid);
		
		//////////////////// suggestButton ////////////////////////////////////////		
		suggestButton = new Button("Suggest Trump and Discards");
		suggestButton.setLayoutX(X_COORD_TABLE_BACKSEAT - 50);
		suggestButton.setLayoutY(Y_COORD_LEFT_MOST_CARD - 0.75*CARD_HEIGHT);
		pane.getChildren().add(suggestButton);
		suggestButton.setOnAction(e -> handleSuggestButton());
		
		//////////////////// slider and trump/backup distribution %/////////////////////
		distributionText = new Text(Integer.toString(distribution) + "%");
		distributionText.setFill(Color.WHITE);
		distributionText.setFont(Font.font("SansSerif", FontWeight.BOLD, FONTSIZE_GRID_STATS));
		grid.add(distributionText, 0, 5);
		
		slider = new Slider();
		slider.setShowTickMarks(true);
		slider.setValue(distribution/2 + 50);
		slider.valueProperty().addListener(e -> handleSlider());
		grid.add(slider, 0, 4);
		
		GridPane.setHalignment(distributionText, HPos.CENTER);
		GridPane.setValignment(distributionText, VPos.TOP);
		
		//////////// checkBox //////////////////////////////////////		
		paneForCheckBox = new StackPane();
		ImageView image = new ImageView("file:girdStatsBackground.png");
		image.setFitWidth(125);
		image.setFitHeight(35);		
		paneForCheckBox.getChildren().addAll(image, checkBox);
		paneForCheckBox.setLayoutX(1025);
		pane.getChildren().add(paneForCheckBox);
		checkBox.setOnAction(e -> grid.setVisible(checkBox.isSelected() ? true : false));

		//////////////////////////// play a card text/////////////////////////////
		playACardText = new Text("Play a card!");
		playACardText.setFill(Color.WHITE);
		playACardText.setFont(Font.font("SansSerif", FontWeight.BOLD, 30));
		playACardText.setLayoutX(X_COORD_TABLE_BACKSEAT - 50);
		playACardText.setLayoutY(75);
		playACardText.setVisible(false);
		pane.getChildren().add(playACardText);
		
		//////////////////////////// discard 5 cards text////////////////////////
		discard5FiveCardsText = new Text("Please discard 5");
		discard5FiveCardsText.setFill(Color.WHITE);
		discard5FiveCardsText.setFont(Font.font("SansSerif", FontWeight.BOLD, 30));
		discard5FiveCardsText.setLayoutX(X_COORD_TABLE_BACKSEAT - 70);
		discard5FiveCardsText.setLayoutY(Y_COORD_TABLE_BACKSEAT);
		pane.getChildren().add(discard5FiveCardsText);
		
		//////////////////////////// backSeat location text//////////////////////
		backSeatLocationText = new Text("BackSeat");
		backSeatLocationText.setFill(Color.WHITE);
		backSeatLocationText.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
		backSeatLocationText.setLayoutX(120);
		backSeatLocationText.setLayoutY(20);
		backSeatLocationText.setVisible(false);
		pane.getChildren().add(backSeatLocationText);
		
		//////////////////////////// driverSeat location text/////////////////////
		driverSeatLocationText = new Text("DriverSeat");
		driverSeatLocationText.setFill(Color.WHITE);
		driverSeatLocationText.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
		driverSeatLocationText.setLayoutX(1050);
		driverSeatLocationText.setLayoutY(20);
		driverSeatLocationText.setVisible(false);
		pane.getChildren().add(driverSeatLocationText);
		
		////////////////////////////// scene and window////////////////////////////		
		Scene scene = new Scene(pane, 1230, 900);
		window.setTitle("Pinochle Counter Estimator and Explorer"); 
		window.setScene(scene); 
		window.show(); 
	}// end of setUpNewGame	
	
	public static void moveLegalCardImagesForward() {
		playACardText.setVisible(true);
		
		for (int i = 0; i < 30; i++) {
			if (isCardInLegalArray(myHand.getCard(i).getId()) && myHand.getCard(i).getImage().isVisible()
					&& myHand.getCard(i).getImage().getY() != Y_COORD_MELDSEAT_STACK) {
				
				ImageView image = myHand.getCard(i).getImage();
				double xCoord = myHand.getCard(i).getXCoord();
				PathTransition p = new PathTransition(Duration.millis(LEGAL_CARDS_FORWARD_TIME),
						new Line(xCoord, Y_COORD_LEFT_MOST_CARD, xCoord, Y_COORD_LEGAL_AREA), image);
				
				image.setY(Y_COORD_LEGAL_AREA);
				p.play();
			}
		}
	}
	
	public static void moveLegalCardImagesBackward() {
		playACardText.setVisible(false);

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() == Y_COORD_LEGAL_AREA) {
				ImageView image = myHand.getCard(i).getImage();
				double xCoord = myHand.getCard(i).getXCoord();
				PathTransition p = new PathTransition(Duration.millis(LEGAL_CARDS_BACKWARD_TIME),
						new Line(xCoord, Y_COORD_LEGAL_AREA, xCoord, Y_COORD_LEFT_MOST_CARD), image);

				image.setY(Y_COORD_LEFT_MOST_CARD);
				p.play();
			}
		}
	}
		
	public static void handleSuggestButton() {
		ArrayList<Integer> throwArrayBestList = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			throwArrayBestList.add(optimalDiscards[i]);	

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() != Y_COORD_DISCARD_AREA) {

				if (throwArrayBestList.remove((Integer) myHand.getCard(i).getId())) {
					ImageView image = myHand.getCard(i).getImage();
					double xCoord = myHand.getCard(i).getXCoord();
					PathTransition p = new PathTransition(Duration.millis(DISCARD_TIME_SUGGESTED_CARDS),
							new Line(xCoord, Y_COORD_LEFT_MOST_CARD, xCoord, Y_COORD_DISCARD_AREA), image);

					image.setY(Y_COORD_DISCARD_AREA);
					p.play();
				}

			} else if (!throwArrayBestList.remove((Integer) myHand.getCard(i).getId())) {
				ImageView image = myHand.getCard(i).getImage();
				double xCoord = myHand.getCard(i).getXCoord();
				PathTransition p = new PathTransition(Duration.millis(DISCARD_TIME_SUGGESTED_CARDS),
						new Line(xCoord, Y_COORD_DISCARD_AREA, xCoord, Y_COORD_LEFT_MOST_CARD), image);

				image.setY(Y_COORD_LEFT_MOST_CARD);
				p.play();
			}
		}
		
		discardCount = 5;
		trumpCode = trumpOfBestEstimation;
		trumpSuitButtons[trumpCode - 1].setSelected(true);
		handleTrumpSelectClick(trumpCode - 1);
	}

	public static void handleCardClick(int index) {
		if (isTransitionModeOn)
			return;

		clickIndex = index;
		xCoordPlayerHands[0] = (double) myHand.getCard(clickIndex).getXCoord();

		if (isPlayModeOn && isCardPlayLegal(myHand.idArray, 25 - tricksPlayed, myHand.getCard(clickIndex).getId())) {
			moveLegalCardImagesBackward();
			playMyCard();

		} else {
			moveToDiscardArea();

			if (trumpCode != 0 && discardCount < 5)
				setGridSuitGraphic(getBackUpSuitAndTotalCount()[0], 2);
		}
	}

	public static void handleTrumpSelectClick(int index) {
		trumpCode = index + 1;

		setGridSuitGraphic(index + 1, 1);
		setGridSuitGraphic(getBackUpSuitAndTotalCount()[0], 2);

		if (discardCount == 5) 
			showConfirmationBoxForDiscards();		
	}

	public static void handleSlider() {
		if (trumpCode == 0) {
			Alert alert = new Alert(AlertType.WARNING, "Please select a trump suit!", ButtonType.OK);			
			alert.setHeaderText(null);
			alert.show();
			return;
		}

		distribution = 2 * ((int) (slider.getValue()) - 50);
		distributionText.setText(Integer.toString(distribution) + "%");
		updateGridNumbers();
	}
	
	public static boolean isCardInLegalArray(int card) {
		for (int i = 0; i < legalArrayCount; i++){
			if (legalArray[i] == card)
				return true;
		}
		
		return false;		
	}	

	public static void setGridSuitGraphic(int suitCode, int xGridCoord) {
		ImageView trumpGridImage;

		switch (suitCode) {
		case 1:
			trumpGridImage = new ImageView("file:spades.png");
			break;
		case 2:
			trumpGridImage = new ImageView("file:diamonds.png");
			break;
		case 3:
			trumpGridImage = new ImageView("file:clubs.png");
			break;
		default:
			trumpGridImage = new ImageView("file:hearts.png");
		}

		trumpGridImage.setFitHeight(FITHIGHT_GRID_STATS);
		trumpGridImage.setFitWidth(FITWITDTH_GRID_STATS);
		grid.add(trumpGridImage, xGridCoord, 0);
		updateGridNumbers();
	}

	public static void updateGridNumbers() {
		int[] cardDistribution = getCardDistribution(getTrumpHandPlusThrowAwayCount(),
				getbackupHandPlusThrowAwayCount(), distribution);

		int cardDistributionIndex = 0;
		for (int j = 1; j <= 3; j++) {
			for (int i = 1; i <= 2; i++) {
				ImageView image = new ImageView("file:girdStatsBackground.png");
				Text number = new Text(Integer.toString(cardDistribution[cardDistributionIndex]));
				image.setFitWidth(FITWITDTH_GRID_STATS);
				image.setFitHeight(FITHIGHT_GRID_STATS);
				GridPane.setHalignment(number, HPos.CENTER);
				number.setFont(Font.font("SansSerif", FontWeight.BOLD, FONTSIZE_GRID_STATS));
				grid.add(image, i, j);
				grid.add(number, i, j);
				cardDistributionIndex++;
			}
		}
	}

	public static void updateStackCounts() {
		ImageView image = new ImageView("file:girdStatsBackground.png");
		Text myCouters = new Text(Integer.toString(countersInMeldSeatsStack) + "/" + saveAmountString);
		image.setFitWidth(FITWITDTH_STACKCOUNT);
		image.setFitHeight(FITLENGTH_STACKCOUNT);
		myCouters.setFont(Font.font("SansSerif", FontWeight.BOLD, FONTSIZE_STACKCOUNT));
		countersCountPane.getChildren().add(image);
		countersCountPane.getChildren().add(myCouters);
	}

	public static void playMyCard() {		
		isTransitionModeOn = true;
		playACard(myHand.idArray, 25 - tricksPlayed, "meldSeat", myHand.getCard(clickIndex).getId(), 0.11);
		myHand.getCard(clickIndex).getImage().setVisible(false);
		executePathTransition();
		dequeueRecursively();
	}

	public static void dequeueRecursively() {
		if (imageTableCount == 0)
			updateStackCounts();

		// base case
		if (playQueue.size() == 0) {			
			turnTransitionModeOff();
			return;
		}

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					executePathTransition();
					dequeueRecursively();
				});
			}
		}, (long) (TRANSITION_TIME + PAUSE_TIME));
	}
	
	public static void turnTransitionModeOff() {		
		if (tricksPlayed == 25){
			showConfirmationBoxToPlayNewHand();
			return;
		}
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					moveLegalCardImagesForward();

					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							Platform.runLater(() -> isTransitionModeOn = false);
						}
					}, (long) (LEGAL_CARDS_FORWARD_TIME));
				});
			}
		}, (long) (TRANSITION_TIME));
	}
	
	public static void executePathTransition() {
		if (imageTableCount == 3)
			transitionTableCardsToAStack();
		else
			tranistionHandCardToTable();
	}

	public static void transitionTableCardsToAStack() {
		double yCoord = (int) (playQueue.poll() * 10) % 10 == 1 ? Y_COORD_MELDSEAT_STACK : Y_COORD_OPPONENT_STACK;

		PathTransition p = new PathTransition(Duration.millis(TRANSITION_TIME),
				new Line(X_COORD_TABLE_MELDSEAT, Y_COORD_TABLE_MELDSEAT, X_COORD_OPPONENT_STACK, yCoord),
				tableImages[0]);
		PathTransition p2 = new PathTransition(Duration.millis(TRANSITION_TIME),
				new Line(X_COORD_TABLE_BACKSEAT, Y_COORD_TABLE_BACKSEAT, X_COORD_OPPONENT_STACK, yCoord),
				tableImages[1]);
		PathTransition p3 = new PathTransition(Duration.millis(TRANSITION_TIME),
				new Line(X_COORD_TABLE_DRIVERSEAT, Y_COORD_TABLE_DRIVERSEAT, X_COORD_OPPONENT_STACK, yCoord),
				tableImages[2]);
		p.play();
		p2.play();
		p3.play();
		imageTableCount = 0;
	}

	public static void tranistionHandCardToTable() {
		int indexOfPlayer = (int) (playQueue.peek() * 10) % 10 - 1;
		int index = ((int) (double) playQueue.poll() - 1) / 4;
		double xInitial = xCoordPlayerHands[indexOfPlayer];
		double yInitial = yCoordPlayerHands[indexOfPlayer];
		double xFinal = xCoordOfTableCards[indexOfPlayer];
		double yFinal = yCoordOfTableCards[indexOfPlayer];

		tableImages[indexOfPlayer] = cardSets[imageTableCount].getCard(index).getImage();
		tableImages[indexOfPlayer].setVisible(true);
		PathTransition p = new PathTransition(Duration.millis(TRANSITION_TIME),
				new Line(xInitial, yInitial, xFinal, yFinal), tableImages[indexOfPlayer]);
		p.play();
		imageTableCount++;
	}
	
	public static int[] getCardDistribution(int myTotalTrumpCount, int myTotalBackUpCount,
			double percentNeg100ToPos100) {
		// distributions index 0 = meldSeat trump count
		// index 1 = meldSeat backup count
		// index 2 = backSeat trump count
		// index 3 = backSeat backup count
		// index 4 = driverSeat trump count
		// index 5 = driverSeat backup count
		int[] distributions = new int[6];
		int totalTrumpAndBackupOthers = 40 - myTotalTrumpCount - myTotalBackUpCount;
		double percentZeroToPos100 = .5 + percentNeg100ToPos100 / 200;
		double trumpPercentage = (20.0 - myTotalTrumpCount) / totalTrumpAndBackupOthers;
		distributions[0] = getTrumpHandCount();
		distributions[1] = getBackUpSuitAndTotalCount()[1];

		if (percentNeg100ToPos100 >= 0) {
			int driverTotalTrumpAndBackup = (int) Math.round(percentZeroToPos100 * totalTrumpAndBackupOthers);
			if (driverTotalTrumpAndBackup > 25)
				driverTotalTrumpAndBackup = 25;

			distributions[2] = (int) Math.round(trumpPercentage * driverTotalTrumpAndBackup);
			distributions[3] = driverTotalTrumpAndBackup - distributions[2];
			distributions[4] = 20 - myTotalTrumpCount - distributions[2];
			distributions[5] = 20 - myTotalBackUpCount - distributions[3];
		} else {
			int backupTotalTrumpAndBackup = (int) Math.round((1 - percentZeroToPos100) * totalTrumpAndBackupOthers);
			if (backupTotalTrumpAndBackup > 25)
				backupTotalTrumpAndBackup = 25;

			distributions[4] = (int) Math.round(trumpPercentage * backupTotalTrumpAndBackup);
			distributions[5] = backupTotalTrumpAndBackup - distributions[4];
			distributions[2] = 20 - myTotalTrumpCount - distributions[4];
			distributions[3] = 20 - myTotalBackUpCount - distributions[5];
		}

		return distributions;
	}

	public static void moveToDiscardArea() {
		ImageView image = myHand.getCard(clickIndex).getImage();
		double xCoord = myHand.getCard(clickIndex).getXCoord();

		if (myHand.getCard(clickIndex).getImage().getY() != Y_COORD_DISCARD_AREA) {
			if (discardCount == 5)
				return;

			PathTransition p = new PathTransition(Duration.millis(DISCARD_TIME),
					new Line(xCoord, Y_COORD_LEFT_MOST_CARD, xCoord, Y_COORD_DISCARD_AREA), image);
			image.setY(Y_COORD_DISCARD_AREA);
			discardCount++;
			p.play();

			if (discardCount == 5) {
				if (trumpCode != 0) {
					if (trumpCode != 0) {
						setGridSuitGraphic(getBackUpSuitAndTotalCount()[0], 2);
					}
					showConfirmationBoxForDiscards();

				} else {
					Alert alert = new Alert(AlertType.WARNING, "Please select a trump suit!", ButtonType.OK);
					alert.setHeaderText(null);
					alert.show();
				}
			}

		} else {
			PathTransition p = new PathTransition(Duration.millis(DISCARD_TIME),
					new Line(xCoord, Y_COORD_DISCARD_AREA, xCoord, Y_COORD_LEFT_MOST_CARD), image);
			image.setY(Y_COORD_LEFT_MOST_CARD);
			discardCount--;
			p.play();
		}
	}

	public static void showConfirmationBoxForDiscards() {
		int handCount = 0;
		int discardCount = 0;

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() == Y_COORD_DISCARD_AREA) {
				throwAwayCards[discardCount] = myHand.getCard(i).getId();
				discardCount++;
			} else {
				myHand.idArray[handCount] = myHand.getCard(i).getId();
				handCount++;
			}
		}

		for (int i = 25; i < 30; i++)
			myHand.idArray[i] = 0;

		String message = (Integer.toString(losersCounter(myHand.idArray, throwAwayCards))) + " loosers";
		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
		alert.setHeaderText(null);
		alert.showAndWait().ifPresent(e -> {
			if (e == ButtonType.YES)
				initiatePlayMode();
		});
	}
	
	public static void showConfirmationBoxToPlayNewHand() {		
		String message = countersInMeldSeatsStack < saveAmountInt
				? "Damn, you did not win as many counters as expected. "
				: "Nice, you won " + countersInMeldSeatsStack + " counters. ";
		
		message += "Do you want to try playing with the same hand again?";
		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		alert.setHeaderText(null);
		
		alert.showAndWait().ifPresent(e -> {
			if (e == ButtonType.YES){
				playWithMySameLastHand = true;
				alert.setContentText("Do you also want your oppenents hands to remain the same?");
				alert.showAndWait().ifPresent(f -> {
					if (f == ButtonType.YES)					
						playWithOpponentsSameLastHands = true;	
					else
						playWithOpponentsSameLastHands = false;
				});
				
			} else{
				playWithMySameLastHand = false;
				playWithOpponentsSameLastHands = false;
			}
			
			resetSomeGlobalVariables();
			setUpNewGame(window);
		});
	}

	public static void initiatePlayMode() {
		for (int i = 0; i < throwAwayCards.length; i++) {
			if (isThisACounter(throwAwayCards[i]))
				countersInMeldSeatsStack++;
		}

		saveAmountInt = 50 - losersCounter(myHand.idArray, throwAwayCards);
		saveAmountString = Integer.toString(saveAmountInt);
		updateStackCounts();
		pane.getChildren().removeAll(suggestButton, paneForCheckBox);

		for (int i = 0; i < 4; i++) {
			trumpSuitButtons[i].setDisable(true);
			AI_buttons[i].setDisable(true);
		}

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() == Y_COORD_DISCARD_AREA) {
				ImageView image = myHand.getCard(i).getImage();
				double xCoord = myHand.getCard(i).getXCoord();
				PathTransition p = new PathTransition(Duration.millis(1500),
						new Line(xCoord, Y_COORD_DISCARD_AREA, xCoord, Y_COORD_MELDSEAT_STACK), image);
				image.setY(Y_COORD_MELDSEAT_STACK);
				p.play();
			}
		}		

		grid.setVisible(false);
		discard5FiveCardsText.setVisible(false);
		backSeatLocationText.setVisible(true);
		driverSeatLocationText.setVisible(true);
		hBox.setVisible(true);
		distributeOpponentCards();
		calculateLegalArray(myHand.idArray, 25 - tricksPlayed);		
		backSeatLastHand = backSeatHand.idArray.clone();		
		isPlayModeOn = true;
		moveLegalCardImagesForward();
	}

	public static void distributeOpponentCards() {		
		if (playWithOpponentsSameLastHands) {
			backSeatHand = new Hand(backSeatLastHand.clone());
		} else if (checkBox.isSelected()) {
			int[] cardDistribution = getCardDistribution(getTrumpHandPlusThrowAwayCount(),
					getbackupHandPlusThrowAwayCount(), distribution);

			backSeatHand = new Hand(myHand, trumpCode, getBackUpSuitAndTotalCount()[0], cardDistribution[2],
					cardDistribution[3]);
		} else {
			backSeatHand = new Hand(myHand);
		}
		
		driverSeatHand = new Hand(myHand, backSeatHand);

		for (int i = 0; i < 25; i++) {
			relKnowledge.get("driverSeat_unknown").add(backSeatHand.idArray[i]);
			relKnowledge.get("driverSeat_self").add(driverSeatHand.idArray[i]);
			relKnowledge.get("backSeat_unknown").add(driverSeatHand.idArray[i]);
			relKnowledge.get("backSeat_self").add(backSeatHand.idArray[i]);
		}
		
		setBackSeatSuitCountArrays(backSeatHand.idArray);
		setDriverSeatSuitCountArrays(driverSeatHand.idArray);
	}
	
	public static void resetSomeGlobalVariables() {	
		makeBossAndAllTableCardsZero();
		relKnowledge.clear();
		playQueue.clear();
		isPlayModeOn = false;
		isTransitionModeOn = false;
		tricksPlayed = 0;
		trumpCode = 0;
		discardCount = 0;
		imageTableCount = 0;
		countersInMeldSeatsStack = 0;
		suitOfTableCard1 = 0;
		aceComeBackCard = 0;		
		bestEstimation = 0;
		myHandSize = 25;	
		
		for (int i = 0; i < 3; i++){
			trumpingSpades[i] = false;
			trumpingDiamonds[i] = false;
			trumpingClubs[i] = false;
			trumpingHearts[i] = false;
		}
				
		if (playWithOpponentsSameLastHands){
			checkBox.setDisable(true);
		} else{
			checkBox.setDisable(false);
			checkBox.setSelected(false);
		}		
	}
	
	public static void deal() {
		initiateRelativeKnowledges();
		myHand = playWithMySameLastHand ? new Hand(meldSeatLastHand.clone(), X_COORD_LEFT_MOST_CARD, CARD_SPACING)
				: new Hand(X_COORD_LEFT_MOST_CARD, CARD_SPACING);

		for (int i = 0; i < 30; i++) {
			relKnowledge.get("driverSeat_unknown").add(myHand.idArray[i]);
			relKnowledge.get("backSeat_unknown").add(myHand.idArray[i]);
		}

		for (int i = 1; i <= 4; i++) {
			trumpCode = i;
			findOptimalDiscards(myHand.idArray, getHandAfterTakingOutAllTrumpAndAces(myHand.idArray), 0, 0);
		}

		trumpCode = 0;
		bestEstimation /= 10000;
		meldSeatLastHand = myHand.idArray.clone();
	}
	
	public static void initiateRelativeKnowledges (){
		relKnowledge.put("driverSeat_self", new ArrayList<>());
		relKnowledge.put("driverSeat_unknown", new ArrayList<>());
		relKnowledge.put("driverSeat_discards", new ArrayList<>());
		relKnowledge.put("driverSeat_meldSeatOrDiscards", new ArrayList<>());
		relKnowledge.put("driverSeat_backSeatOrDiscards", new ArrayList<>());		
		relKnowledge.put("backSeat_self", new ArrayList<>());
		relKnowledge.put("backSeat_unknown", new ArrayList<>());
		relKnowledge.put("backSeat_discards", new ArrayList<>());
		relKnowledge.put("backSeat_meldSeatOrDiscards", new ArrayList<>());
		relKnowledge.put("backSeat_driverSeatOrDiscards", new ArrayList<>());			
		relKnowledge.put("nonMelders_playedCards", new ArrayList<>());	
	}

	public static int[] getBackUpSuitAndTotalCount() {
		int[] suitCounts = new int[4];
		int[] backUpSuitAndCount = new int[2];
		int backUpSuit = 0;
		int maxCount = 0;

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() != Y_COORD_DISCARD_AREA
					&& myHand.getCard(i).getImage().getY() != Y_COORD_MELDSEAT_STACK) {

				if (trumpCode != SPADES && myHand.getCard(i).getSuitCode() == SPADES)
					suitCounts[0]++;
				else if (trumpCode != DIAMONDS && myHand.getCard(i).getSuitCode() == DIAMONDS)
					suitCounts[1]++;
				else if (trumpCode != CLUBS && myHand.getCard(i).getSuitCode() == CLUBS)
					suitCounts[2]++;
				else if (trumpCode != HEARTS && myHand.getCard(i).getSuitCode() == HEARTS)
					suitCounts[3]++;
			}
		}

		for (int i = 0; i < 4; i++) {
			if (suitCounts[i] > maxCount) {
				maxCount = suitCounts[i];
				backUpSuit = i;
			}
		}

		backUpSuitAndCount[0] = backUpSuit + 1;
		backUpSuitAndCount[1] = maxCount;
		return backUpSuitAndCount;
	}

	public static int getTrumpHandCount() {
		int trumpCount = 0;

		for (int i = 0; i < 30; i++) {
			if (myHand.getCard(i).getImage().getY() != Y_COORD_DISCARD_AREA
					&& myHand.getCard(i).getImage().getY() != Y_COORD_MELDSEAT_STACK) {
				if (trumpCode == myHand.getCard(i).getSuitCode())
					trumpCount++;
			}
		}

		return trumpCount;
	}

	public static int getTrumpHandPlusThrowAwayCount() {
		int trumpCount = 0;

		for (int i = 0; i < 30; i++) {
			if (trumpCode == myHand.getCard(i).getSuitCode())
				trumpCount++;
		}

		return trumpCount;
	}

	public static int getbackupHandPlusThrowAwayCount() {
		int backupCount = 0;
		int backupSuit = getBackUpSuitAndTotalCount()[0];

		for (int i = 0; i < 30; i++) {
			if (backupSuit == myHand.getCard(i).getSuitCode())
				backupCount++;
		}

		return backupCount;
	}
		
	static void playACard(int array[], int n, String player, int cardId, double signiture) {	
		int playCard;
		
		if (player.equals("meldSeat")) {	
			playCard = cardId;

		} else {
			calculateLegalArray(array, n);
			playCard = player.equals("backSeat") ? backSeat(legalArray, legalArrayCount, player)
					: driverSeat(legalArray, legalArrayCount, player);
		}
		
		updateAIKnowledge(playCard, player);
		playQueue.add(playCard + signiture);
		removeThisCardFromHandArray(array, n, playCard);
		placeCardOnTable(playCard + signiture);
		updateBossCard(playCard + signiture);
				
		if (tableCount == 2) {
			tricksPlayed++;			
			playQueue.add(bossCardDouble);

			updateStack();			
			makeBossAndAllTableCardsZero();	

			if (tricksPlayed == 25) 
				return;
						
			if ((int) (bossCardDouble * 10) % 10 == 1)
				calculateLegalArray(myHand.idArray, 25 - tricksPlayed);
			else if ((int) (bossCardDouble * 10) % 10 == 2)
				playACard(backSeatHand.idArray, 25 - tricksPlayed, "backSeat", 0, 0.22);
			else
				playACard(driverSeatHand.idArray, 25 - tricksPlayed, "driverSeat", 0, 0.33);

		} else {
			tableCount++;
			
			if (player.equals("meldSeat"))
				playACard(backSeatHand.idArray, n, "backSeat", 0, 0.22);		
			else if (player.equals("backSeat"))
				playACard(driverSeatHand.idArray, n, "driverSeat", 0, 0.33);
			else	
				calculateLegalArray(myHand.idArray, n);
		}
	}		
}// end of class
