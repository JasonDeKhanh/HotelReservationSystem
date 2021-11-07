/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author msipc
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    private String name;
    private String description;
    private Double size;
    private HashMap<String, Integer> beds;
    private Integer capacity;
    private List<String> amenities;
    private static Integer inventory;
    
    
    // one to one, not mandatory, unidirectional
    @OneToOne
    private RoomType nextHigherRoomType;

    public RoomType() {
        this.beds = new HashMap<String,Integer>();
        this.amenities = new ArrayList<>();
    }

    public RoomType(String name, String description, Double size, Integer capacity) {
        this();
        this.name = name;
        this.description = description;
        this.size = size;
        this.capacity = capacity;
        // inventory initialized as 0. Whenver a new rom is created with this room type, increment inventory
        // room is deleted, decrement inventory
        this.inventory = 0;
    }

    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public HashMap<String, Integer> getBeds() {
        return beds;
    }

    public void setBeds(HashMap<String, Integer> beds) {
        this.beds = beds;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public RoomType getNextHigherRoomType() {
        return nextHigherRoomType;
    }

    public void setNextHigherRoomType(RoomType nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
    }
    
}
