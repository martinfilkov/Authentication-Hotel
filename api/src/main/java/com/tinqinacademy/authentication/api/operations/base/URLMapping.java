package com.tinqinacademy.authentication.api.operations.base;

public class URLMapping {
    //Hotel
    public final static String GET_IDS = "/api/hotel/rooms";
    public final static String GET_ROOM = "/api/hotel/{roomId}";
    public final static String BOOK_ROOM = "/api/hotel/{roomId}";
    public final static String UNBOOK_ROOM = "/api/hotel/{bookingId}";

    //System
    public final static String REGISTER_VISITOR = "/api/system/register";
    public final static String INFO_REGISTRY = "/api/system/register";
    public final static String CREATE_ROOM = "/api/system/room";
    public final static String UPDATE_ROOM = "/api/system/room/{roomId}";
    public final static String PARTIAL_UPDATE_ROOM = "/api/system/room/{roomId}";
    public final static String DELETE_ROOM = "/api/system/room/{roomId}";
}
