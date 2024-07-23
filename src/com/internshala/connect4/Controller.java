package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int columns = 7;
	private static final int row = 6;
	private static final int circled = 80;
	private static String p1="Player 1";
	private static String p2="Player 2";
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAABB";
	private boolean isp1 = true;
	private Disc[][] insertDiscArray = new Disc[row][columns];
	private boolean isAllow = true;

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label pName;

	@FXML
	public TextField p1n;

	@FXML
	public TextField p2n;

	@FXML
	public Button setN;


	public void createPlayGround() {
		setN.setOnAction(event -> {
			p1=p1n.getText();
			p2=p2n.getText();
			pName.setText(p1);
		});

		Shape rectwithholes = pggrid();
		rootGridPane.add(rectwithholes, 0, 1);
		List<Rectangle> r = clickableRectColumns();
		for (Rectangle re : r) {
			rootGridPane.add(re, 0, 1);
		}
	}

	public Shape pggrid() {
		Shape rectwithholes = new Rectangle((columns + 1) * circled, (row + 1) * circled);

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < columns; j++) {

				Circle circle = new Circle();

				circle.setRadius(circled / 2);
				circle.setCenterX(circled / 2);
				circle.setCenterY(circled / 2);
				circle.setTranslateX(j * (circled + 5) + circled / 4);
				circle.setTranslateY(i * (circled + 5) + circled / 4);
				circle.setSmooth(true);

				rectwithholes = Shape.subtract(rectwithholes, circle);
			}
		}
		rectwithholes.setFill(Color.WHITE);
		return rectwithholes;
	}

	private List<Rectangle> clickableRectColumns() {
		List<Rectangle> rectList = new ArrayList<>();
		for (int j = 0; j < columns; j++) {
			Rectangle rect = new Rectangle(circled, (row + 1) * circled);
			rect.setTranslateX(circled / 4);
			rect.setFill(Color.TRANSPARENT);

			rect.setOnMouseEntered(event -> rect.setFill(Paint.valueOf("#eeeeee55")));
			rect.setOnMouseExited(event -> rect.setFill(Color.TRANSPARENT));

			int jj = j;
			rect.setOnMouseClicked(event -> {
				if (isAllow) {
					isAllow = false;
					insertDisc(new Disc(isp1), jj);
				}
			});
			rectList.add(rect);
			rect.setTranslateX(j * (circled + 5) + circled / 4);
		}
		return rectList;
	}

	private void insertDisc(Disc disc, int j) {

		int r = row - 1;

		while (r >= 0) {
			if (ifDiscPresent(r, j) == null) {
				break;
			}
			r--;
		}
		if (r < 0) {
			return;
		}

		insertDiscArray[r][j] = disc;
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX(j * (circled + 5) + circled / 4);

		int currentRow = r;

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(r * (circled + 5) + circled / 4);

		translateTransition.setOnFinished(event -> {
			if (gameEnded(currentRow, j)) {
				gameOver();
				return;
			}
			isAllow = true;
			isp1 = !isp1;
			pName.setText(isp1 ? p1 : p2);
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = isp1 ? p1 : p2;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect 4");
		alert.setHeaderText("The Winner is " + winner);
		alert.setContentText("Want to Play Again ? ");
		ButtonType y = new ButtonType("Yes");
		ButtonType n = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(y, n);

		Platform.runLater(() -> {
			Optional<ButtonType> opt = alert.showAndWait();
			if ((opt.isPresent() && (opt.get()) == y)) {
				resetGame();
			} else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		isAllow=true;
		insertedDiscsPane.getChildren().clear();
		for (int i = 0; i < insertDiscArray.length; i++) {
			for (int j = 0; j < insertDiscArray[i].length; j++) {
				insertDiscArray[i][j] = null;
			}
		}
		isp1 = true;
		pName.setText(p1);
		createPlayGround();
	}

	private boolean gameEnded(int i, int j) {

		List<Point2D> verticalPoints = IntStream.rangeClosed(i - 3, i + 3).mapToObj(r -> new Point2D(r, j)).collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(j - 3, j + 3).mapToObj(c -> new Point2D(i, c)).collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(i - 3, j + 3);

		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6).mapToObj(a -> startPoint1.add(a, -a)).collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(i - 3, j - 3);

		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(a -> startPoint2.add(a, a)).collect(Collectors.toList());

		boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints) || checkCombination(diagonal1Points) || checkCombination(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombination(List<Point2D> points) {
		int chain = 0;

		for (Point2D point : points) {
			int i = (int) point.getX();
			int j = (int) point.getY();
			Disc disc = ifDiscPresent(i, j);
			if (disc != null && disc.isPlayer == isp1) {
				chain++;
				if (chain == 4) {
					return true;
				}
			}
				else {
					chain = 0;
				}
			}
		return false;
	}

	private Disc ifDiscPresent(int i, int j) {
		if (i >= row || i < 0 || j >= columns || j < 0) {
			return null;
		}
		return insertDiscArray[i][j];
	}

	private static class Disc extends Circle {

		private boolean isPlayer;

		public Disc(boolean isPlayer) {
			this.isPlayer = isPlayer;
			setRadius(circled / 2);
			setFill(isPlayer ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(circled / 2);
			setCenterY(circled / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
