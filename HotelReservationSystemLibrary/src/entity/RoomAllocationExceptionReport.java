/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.RoomAllocationExceptionType;

/**
 *
 * @author xqy11
 */
@Entity
public class RoomAllocationExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomAllocationExceptionReportId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RoomAllocationExceptionType type;
    @Column(nullable = false, length = 250)
    @NotNull
    @Size(min = 1, max = 250)
    private String reason;
    
    @OneToOne(mappedBy = "roomAllocationExceptionReport")
    private Reservation reservation;

    public RoomAllocationExceptionReport() {
    }

    public RoomAllocationExceptionReport(RoomAllocationExceptionType type, String reason) {
        this();
        this.type = type;
        this.reason = reason;
    }

    public Long getRoomAllocationExceptionReportId() {
        return roomAllocationExceptionReportId;
    }

    public void setRoomAllocationExceptionReportId(Long roomAllocationExceptionReportId) {
        this.roomAllocationExceptionReportId = roomAllocationExceptionReportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomAllocationExceptionReportId != null ? roomAllocationExceptionReportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomAllocationExceptionReportId fields are not set
        if (!(object instanceof RoomAllocationExceptionReport)) {
            return false;
        }
        RoomAllocationExceptionReport other = (RoomAllocationExceptionReport) object;
        if ((this.roomAllocationExceptionReportId == null && other.roomAllocationExceptionReportId != null) || (this.roomAllocationExceptionReportId != null && !this.roomAllocationExceptionReportId.equals(other.roomAllocationExceptionReportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomAllocationExceptionReport[ id=" + roomAllocationExceptionReportId + " ]";
    }

    /**
     * @return the type
     */
    public RoomAllocationExceptionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(RoomAllocationExceptionType type) {
        this.type = type;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
}
