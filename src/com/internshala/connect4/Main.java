package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
     FXMLLoader loader =new FXMLLoader(getClass().getResource("game.fxml"));

        GridPane rootGridPane=loader.load();

        controller= loader.getController();
        controller.createPlayGround();

        MenuBar menuBar=createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane myPane= (Pane) rootGridPane.getChildren().get(0);

        myPane.getChildren().add(menuBar);

        Scene scene= new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private MenuBar createMenu(){

        SeparatorMenuItem s=new SeparatorMenuItem();

        Menu file=new Menu("File");
        MenuItem newGameMenu =new MenuItem("New Game");
        MenuItem resetGame=new MenuItem("Reset Game");
        MenuItem exitGame=new MenuItem("Exit");

       newGameMenu.setOnAction(event->controller.resetGame());
       resetGame.setOnAction(event->controller.resetGame());
       exitGame.setOnAction(event -> {
       	Platform.exit();
        System.exit(0);
       });
       file.getItems().addAll(newGameMenu,resetGame,s,exitGame);
        
        
        Menu help=new Menu("Help");

        MenuItem aboutMe=new MenuItem("About Developer");
        MenuItem aboutGame=new MenuItem("About Connect 4");
        

        aboutMe.setOnAction(event->getAbout());
        aboutGame.setOnAction(event -> getAboutGame());

	    help.getItems().addAll(aboutMe,aboutGame);
        MenuBar menubar=new MenuBar();

        menubar.getMenus().addAll(file,help);

        return menubar;
    }

	private void getAboutGame() {

		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About The Game");
		alert.setContentText("Connect Four Game Description:\n" +
				"\n" +
				"Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
		alert.show();
	}

	private void getAbout() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About The Developer");
		alert.setHeaderText("Prithvik");
		alert.setContentText("An aspiring developer learning to code,practice and develop applications for the people to make lives easier and fun. ");
		alert.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
}
