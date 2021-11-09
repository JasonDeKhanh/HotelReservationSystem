/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String name;
    @Column()
    @Size(min = 1, max = 200) // precision something
    private String description;
    @Column(nullable = false, precision = 6, scale = 3)
    @NotNull
    @DecimalMin("0.000")
    @Digits(integer = 3, fraction = 3)
    private Double size;
    private Integer beds;
    private Integer capacity;
    private String amenities;
    private Integer inventory;
    private Boolean enabled;
    
    
    // one to one, not mandatory, unidirectional
    @OneToOne
    private RoomType nextHigherRoomType;
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
    
    public RoomType() {
        this.inventory = 0;
        this.enabled = true;
    }

    public RoomType(String name, String description, Double size, Integer beds, Integer capacity, String amenities) {
        this();
        
        this.name = name;
        this.description = description;
        this.size = size;
        this.beds = beds;
        this.capacity = capacity;
        this.amenities = amenities;
        this.inventory = 0;
        this.enabled = true;
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

    public Integer getBeds() {
        return beds;
    }

    public void setBeds(Integer beds) {
        this.beds = beds;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setNextHigherRoomType(RoomType nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
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
