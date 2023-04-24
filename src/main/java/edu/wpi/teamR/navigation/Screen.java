package edu.wpi.teamR.navigation;

public enum Screen {
  ROOT("views/Root.fxml"),
  HOME("views/Home.fxml"),
  SIGNAGE("views/Signage.fxml"),
  MEAL_REQUEST("views/DeliveryRequest.fxml"),
  FURNITURE_REQUEST("views/DeliveryRequest.fxml"),
  FLOWER_REQUEST("views/DeliveryRequest.fxml"),
  ROOM_REQUEST("views/ConfrenceRoom.fxml"),
  MAP("views/Map.fxml"),
  LOGIN("views/Login.fxml"),
  MAP_EDITOR("views/MapEditor.fxml"),
  SORT_ORDERS("views/SortOrders.fxml"),
  SCREENSAVER("views/Screensaver.fxml"),
  ADMINPROFILEPAGE("views/AdminProfilePage.fxml"),
  STAFFPROFILEPAGE("views/StaffProfilePage.fxml"),
  EMPLOYEEMANAGER("views/EmployeeManager.fxml"),
  ITEMREQUEST("views/ItemRequest.fxml"),
  ADDEMPLOYEE("views/AddEmployee.fxml"),
  SIGNAGECONFIGURATION("views/SignageConfiguration.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
