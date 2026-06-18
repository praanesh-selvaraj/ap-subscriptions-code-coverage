package com.kmp.aeroparker.application.form;

import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmendSubscriptionForm {
    // Vehicle details parameters
    private String registration;
    private String make;
    private String model;
    private String colour;

    // Personal details parameters
    private String title;
    private String fname;
    private String lname;
    private String email;
    private String telno;

    // Address details parameters
    private String addr1;
    private String addr2;
    private String town;
    private String county;
    private String postcode;
    private String country;

    /**
     * Converts form data into the CustomerDetails data type.
     * @return A populated CustomerDetails object.
     */
    public CustomerDetails toCustomerDetails() {
        CustomerDetails details = new CustomerDetails();
        details.setTitle(this.title);
        details.setFirstName(this.fname);
        details.setLastName(this.lname);
        details.setEmailAddress(this.email);
        details.setPhoneNumber(this.telno);
        details.setAddressLine1(this.addr1);
        details.setAddressLine2(this.addr2);
        details.setTown(this.town);
        details.setCounty(this.county);
        details.setPostCode(this.postcode);
        details.setCountry(this.country);
        return details;
    }

    /**
     * Converts form data into the VehicleDetails data type.
     * @return A populated VehicleDetails object.
     */
    public VehicleDetails toVehicleDetails() {
        VehicleDetails details = new VehicleDetails();
        details.setLicensePlate(this.registration);
        details.setMake(this.make);
        details.setModel(this.model);
        details.setColour(this.colour);
        return details;
    }
}