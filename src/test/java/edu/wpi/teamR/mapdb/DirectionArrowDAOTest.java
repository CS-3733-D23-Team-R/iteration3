package edu.wpi.teamR.mapdb;

import edu.wpi.teamR.Configuration;
import edu.wpi.teamR.ItemNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DirectionArrowDAOTest {
    private static Connection connection;
    private static LocationNameDAO locationNameDAO;
    private static DirectionArrowDAO directionArrowDAO;

    @BeforeAll
    static void startup() throws SQLException, ClassNotFoundException {
        Configuration.changeSchemaToTest();
        connection = Configuration.getConnection();
        locationNameDAO = new LocationNameDAO();
        directionArrowDAO = new DirectionArrowDAO();
    }
    @AfterAll
    static void end() throws SQLException, ClassNotFoundException {
        directionArrowDAO.deleteAllDirectionArrows();
        locationNameDAO.deleteAllLocationNames();
    }
    @BeforeEach
    void reset() throws SQLException, ClassNotFoundException {
        directionArrowDAO.deleteAllDirectionArrows();
        locationNameDAO.deleteAllLocationNames();
    }
    @Test
    void addDirectionArrow() throws SQLException, ClassNotFoundException {
        ArrayList<DirectionArrow> directionArrows; // create empty ArrayList
        locationNameDAO.addLocationName("test1", "", ""); // creates a location for any direction arrows
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 0 items
        assertEquals(directionArrows.size(), 0); // should be true
        directionArrowDAO.addDirectionArrow("test1",0, Direction.Up); // creates a directionArrow
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 1 item
        assertEquals(directionArrows.size(), 1); // should be true
        DirectionArrow directionArrow = directionArrows.get(0);
        assertEquals(directionArrow.getLongname(), "test1"); // checks that longName is the same
        assertEquals(directionArrow.getKioskID(), 0); // checks that kioskID is the same
        assertEquals(directionArrow.getDirection(), Direction.Up); // checks that the direction is the same
    }

    @Test
    void deleteDirectionArrowByLongname() throws SQLException, ItemNotFoundException, ClassNotFoundException {
        // creates an empty list
        ArrayList<DirectionArrow> directionArrows;
        locationNameDAO.addLocationName("test1", "", "");
        locationNameDAO.addLocationName("test2", "", "");
        // creates two directionArrows
        directionArrowDAO.addDirectionArrow("test1",0, Direction.Up);
        directionArrowDAO.addDirectionArrow("test2",0, Direction.Up);
        // gets directionArrow(s) in directionArrows
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 2 items
        DirectionArrow directionArrow = directionArrows.get(0);
        assertEquals(directionArrows.size(), 2); // should be true
        // uses delete function on one matching specific longName
        directionArrowDAO.deleteDirectionArrowByLongname("test1");
        // checks that the list only contains the other one
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 1 items
        directionArrow = directionArrows.get(0);
        assertEquals(directionArrows.size(), 1); // should be true
        assertEquals(directionArrow.getLongname(), "test2");
        assertEquals(directionArrow.getKioskID(), 0);
        assertEquals(directionArrow.getDirection(), Direction.Up);
        // deletes remaining directionArrow by its longName
        directionArrowDAO.deleteDirectionArrowByLongname("test2");
        // check that list is now empty
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 0 items
        assertEquals(directionArrows.size(), 0); // should be true
    }

    @Test
    void deleteDirectionArrowsByKiosk() throws SQLException, ItemNotFoundException, ClassNotFoundException {
        // creates an empty list
        ArrayList<DirectionArrow> directionArrows;
        locationNameDAO.addLocationName("test1", "", "");
        locationNameDAO.addLocationName("test2", "", "");
        // creates two directionArrows
        directionArrowDAO.addDirectionArrow("test1",1, Direction.Up);
        directionArrowDAO.addDirectionArrow("test2",2, Direction.Up);
        // gets directionArrow(s) in directionArrows
        // uses delete function on one matching specific longName
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 1 item
        assertEquals(directionArrows.size(), 2); // should be true
        directionArrowDAO.deleteDirectionArrowsByKiosk(1);
        // checks that the list only contains the other one
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 1 item
        DirectionArrow directionArrow = directionArrows.get(0);
        assertEquals(directionArrows.size(), 1); // should be true
        assertEquals(directionArrow.getLongname(), "test2");
        assertEquals(directionArrow.getKioskID(), 2);
        assertEquals(directionArrow.getDirection(), Direction.Up);
        // deletes remaining directionArrow by its longName
        directionArrowDAO.deleteDirectionArrowsByKiosk(2);
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 0 item
        // check that list is now empty
        assertEquals(directionArrows.size(), 0); // should be true
    }

    @Test
    void deleteAllDirectionArrows() throws SQLException, ClassNotFoundException {
        ArrayList<DirectionArrow> directionArrows;
        locationNameDAO.addLocationName("test1", "", "");
        locationNameDAO.addLocationName("test2", "", "");
        directionArrowDAO.addDirectionArrow("test1",1, Direction.Up);
        directionArrowDAO.addDirectionArrow("test2",2, Direction.Up);
        directionArrowDAO.deleteAllDirectionArrows();
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 0 items
        assertEquals(directionArrows.size(), 0); // should be true

    }

    // @Test
    // void getDirectionArrows() {
    // }

    @Test
    void getDirectionArrowsByKiosk() throws SQLException, ClassNotFoundException {
        // creates an empty list
        ArrayList<DirectionArrow> directionArrows;
        locationNameDAO.addLocationName("test1", "", "");
        locationNameDAO.addLocationName("test2", "", "");
        // creates two directionArrows
        directionArrowDAO.addDirectionArrow("test1",1, Direction.Up);
        directionArrowDAO.addDirectionArrow("test2",2, Direction.Up);
        // gets directionArrow(s) in directionArrows
        directionArrows = directionArrowDAO.getDirectionArrows(); // should return list w/ 2 items
        DirectionArrow directionArrow = directionArrows.get(0);
        directionArrowDAO.getDirectionArrowsByKiosk(1);
        assertEquals(directionArrow.getLongname(), "test1");
        assertEquals(directionArrow.getDirection(), Direction.Up);
        directionArrows = directionArrowDAO.getDirectionArrowsByKiosk(2);
        directionArrow = directionArrows.get(0);
        assertEquals(directionArrow.getLongname(), "test2");
        assertEquals(directionArrow.getDirection(), Direction.Up);

    }
}