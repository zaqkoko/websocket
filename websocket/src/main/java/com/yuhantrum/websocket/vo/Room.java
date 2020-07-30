package com.yuhantrum.websocket.vo;

public class Room {
    int roomNumber; // 방 넘버 변수
    String roomName; // 방 이름 변수

    public int getRoomNumber(){
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber){
        this.roomNumber = roomNumber;
    }

    public String getRoomName(){
        return roomName;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    @Override
    public String toString(){
        return "Room [roomNumber=" + roomNumber + ", roomName=" + roomName + "]";
    }
}
