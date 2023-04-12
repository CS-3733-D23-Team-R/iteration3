package edu.wpi.teamR.controllers;

import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

import java.io.IOException;

public class RootController {
  @FXML VBox homeButton;
  @FXML VBox profileButton;
  @FXML VBox newRequestButton;
  @FXML VBox pendingRequestButton;
  @FXML VBox pathfindingButton;
  @FXML VBox helpButton;
  @FXML VBox logoutButton;
  @FXML Text flowerDelivery;
  @FXML Text furnitureDelivery;
  @FXML Text mealDelivery;

  @FXML
  public void initialize() {
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    profileButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ADMINHOME));
    newRequestButton.setOnMouseClicked(event -> openRequest());
    pendingRequestButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    pathfindingButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    logoutButton.setOnMouseClicked(event -> Platform.exit());
    furnitureDelivery.setOnMouseClicked(event -> Navigation.navigate(Screen.FurnitureDelivery));
    mealDelivery.setOnMouseClicked(event -> Navigation.navigate(Screen.MealDelivery));

    helpButton.setOnMouseClicked(
            event -> {
              try {
                help();
              } catch (IOException e) {
                e.printStackTrace();
              }
            });
  }

  @FXML
  private void help() throws IOException {
    PopOver helpPopup = new PopOver();
    final FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/Help.fxml"));
    Parent help = loader.load();
    helpPopup.setContentNode(help);
    helpPopup.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    helpPopup.setAutoHide(true);
    helpPopup.show(helpButton);
  }

  @FXML
  private void openRequest(){


  }
}
