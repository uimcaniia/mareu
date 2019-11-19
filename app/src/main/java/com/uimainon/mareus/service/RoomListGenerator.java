package com.uimainon.mareus.service;

import com.uimainon.mareus.R;
import com.uimainon.mareus.model.Participant;
import com.uimainon.mareus.model.Room;

import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RoomListGenerator {

    public static List<Room> DUMMY_ROOMS = Arrays.asList(
            new Room(1, "Réunion A", "#fed1c8"),
            new Room(2, "Réunion B", "#b4cf87"),
            new Room(3, "Réunion C", "#87c0cf"),
            new Room(4, "Réunion D","#7f84cb"),
            new Room(5, "Réunion E", "#be7fcb"),
            new Room(6, "Réunion F", "#cb7f94"),
            new Room(7, "Réunion G", "#f3ef74"),
            new Room(8, "Réunion H", "#eda44c"),
            new Room(9, "Réunion I", "#b4ebe8"),
            new Room(10, "Réunion J", "#c5c5c5")
    );

    public static List<Room> generateAllRooms() {
        return new ArrayList<>(DUMMY_ROOMS);
    }
}
