/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import util.enumeration.ReservationType;

/**
 *
 * @author xqy11
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ReservationType type;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date checkinDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date checkoutDate;
    
    @ManyToMany
    private List<Room> rooms;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = true)
    private Partner partner;
    
    @OneToOne
    private RoomAllocationExceptionReport roomAllocationExceptionReport;

    public Reservation() {
        rooms = new ArrayList<Room>();
    }

    public Reservation(ReservationType type, Date checkinDate, Date checkoutDate) {
        this();
        this.type = type;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationEntity[ id=" + reservationId + " ]";
    }

    /**
     * @return the type
     */
    public ReservationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ReservationType type) {
        this.type = type;
    }

    /**
     * @return the checkinDate
     */
    public Date getCheckinDate() {
        return checkinDate;
    }

    /**
     * @param checkinDate the checkinDate to set
     */
    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    /**
     * @return the checkoutDate
     */
    public Date getCheckoutDate() {
        return checkoutDate;
    }

    /**
     * @param checkoutDate the checkoutDate to set
     */
    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the roomAllocationExceptionReport
     */
    public RoomAllocationExceptionReport getRoomAllocationExceptionReport() {
        return roomAllocationExceptionReport;
    }

    /**
     * @param roomAllocationExceptionReport the roomAllocationExceptionReport to set
     */
    public void setRoomAllocationExceptionReport(RoomAllocationExceptionReport roomAllocationExceptionReport) {
        this.roomAllocationExceptionReport = roomAllocationExceptionReport;
    }

    /**
     * @return the partner
     */
    public Partner getPartner() {
        return partner;
    }

    /**
     * @param partner the partner to set
     */
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    
}
