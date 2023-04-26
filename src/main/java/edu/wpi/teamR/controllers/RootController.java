package edu.wpi.teamR.controllers;

import edu.wpi.teamR.App;
import edu.wpi.teamR.ItemNotFoundException;
import edu.wpi.teamR.datahandling.ShoppingCart;
import edu.wpi.teamR.login.AccessLevel;
import edu.wpi.teamR.navigation.Navigation;
import edu.wpi.teamR.navigation.Screen;
import edu.wpi.teamR.userData.CurrentUser;
import edu.wpi.teamR.userData.UserData;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.teamR.navigation.Navigation.navigate;

/*TODO
    add signage page in place of sort orders
      if admin is signage config
      find icon
    add way to access conference room request
 */
public class RootController {
  @FXML VBox bwhHome;
  @FXML VBox profileButton;
  @FXML VBox newRequestButton;
  @FXML VBox signagePageButton;
  @FXML VBox pathfindingButton;
  @FXML VBox helpButton;
  @FXML VBox logoutButton;
  @FXML VBox exitButton;
  @FXML Text flowerDelivery;
  @FXML Text furnitureDelivery;
  @FXML Text mealDelivery;

  @FXML VBox sidebarVBox;
  @FXML
  HBox rootHbox;

  @FXML
  ImageView profileIcon;
  @FXML
  Circle circle;

  @FXML Text signageText;

  PauseTransition transition;

  private static RootController instance;

  EventHandler<InputEvent> ssevent = evt -> transition.playFromStart();

  @FXML
  public void initialize() {
    instance = this;
    bwhHome.setOnMouseClicked(event -> navigate(Screen.HOME));
    profileButton.setOnMouseClicked(event -> {
      try {
        openProfile();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      } catch (ItemNotFoundException e) {
        throw new RuntimeException(e);
      }
    });
    logoutButton.setOnMouseClicked(event -> logout());
    newRequestButton.setOnMouseClicked(event -> navigate(Screen.ITEMREQUEST));
    pathfindingButton.setOnMouseClicked(event -> navigate(Screen.MAP));
    signagePageButton.setOnMouseClicked(event -> navigate(Screen.SIGNAGE));

    exitButton.setOnMouseClicked(event -> Platform.exit());

    helpButton.setOnMouseClicked(
            event -> {
              try {
                help();
              } catch (IOException e) {
                e.printStackTrace();
              }
            });

    sidebarVBox.setVisible(false);
    sidebarVBox.setManaged(false);

    Duration delay = Duration.seconds(120);
    transition = new PauseTransition(delay);
    transition.setOnFinished(evt -> timeout());

    // restart transition on user interaction
    App.getPrimaryStage().addEventFilter(InputEvent.ANY, ssevent);

    transition.play();
  }

  private void openProfile() throws SQLException, ClassNotFoundException, ItemNotFoundException {
    UserData thisUserData = UserData.getInstance();
    if (!thisUserData.isLoggedIn()){
      return;
    }
    CurrentUser user = thisUserData.getLoggedIn();
    if(user.getAccessLevel().equals(AccessLevel.Admin)){
      Navigation.navigate(Screen.ADMINPROFILEPAGE);
    } else if(user.getAccessLevel().equals(AccessLevel.Staff)){
      Navigation.navigate(Screen.STAFFPROFILEPAGE);
    }
  }

  @FXML
  private void help() throws IOException {
    PopOver helpPopup = new PopOver();
    final FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/edu/wpi/teamR/views/Help.fxml"));
    Parent help = loader.load();
    helpPopup.setContentNode(help);
    helpPopup.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
    helpPopup.setAutoHide(true);
    helpPopup.show(helpButton);
  }
//  @FXML
//  private void openRequest(){
//    if (sidebarVBox.isVisible()) {
//      sidebarVBox.setVisible(false);
//      sidebarVBox.setManaged(false);
//    }
//    else {
//      sidebarVBox.setVisible(true);
//      sidebarVBox.setManaged(true);
//      flowerDelivery.setOnMouseClicked(event -> flowerRequest());
//      furnitureDelivery.setOnMouseClicked(event -> furnitureRequest());
//      mealDelivery.setOnMouseClicked(event -> mealRequest());
//    }
//  }
////
//  @FXML private void mealRequest() {
//    RequestController.requestType = new RequestTypeMeal();
//    navigate(Screen.MEAL_REQUEST);
//  }
//
//  @FXML private void flowerRequest() {
//    RequestController.requestType = new RequestTypeFlower();
//    navigate(Screen.FLOWER_REQUEST);
//  }
//
//  @FXML private void furnitureRequest() {
//    RequestController.requestType = new RequestTypeFurniture();
//    navigate(Screen.FURNITURE_REQUEST);
//  }

  /* This is a little buggy and could be worked on more. */
  private void timeout() {
      rootHbox.setVisible(false);
      rootHbox.setManaged(false);
      Navigation.navigate(Screen.SCREENSAVER);
      App.getPrimaryStage().removeEventFilter(InputEvent.ANY, ssevent);
  }

  public static RootController getInstance() {
    return instance;
  }

  private void navigate(Screen screen) {
    sidebarVBox.setVisible(false);
    sidebarVBox.setManaged(false);
    Navigation.navigate(screen);
  }

  public void showSidebar() {
    rootHbox.setVisible(true);
    rootHbox.setManaged(true);
    App.getPrimaryStage().addEventFilter(InputEvent.ANY, ssevent);
  }

  @FXML private void logout(){
    if (UserData.getInstance().isLoggedIn()) {
      UserData.getInstance().logout();
      ShoppingCart.getInstance().clearCart();
      Navigation.navigate(Screen.HOME);
    }
  }
  public void setSignagePage(){
    //signagePageButton.setOnMouseClicked(event -> navigate(Screen.SIGNAGECONFIGURATION));
    //signageText.setText("  Edit\nSignage");
    signagePageButton.setOnMouseClicked(event -> navigate(Screen.SIGNAGE));
  }
}
