package com.kmp.aeroparker.application.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;

public class AmendSubscriptionFormTest
{
	@Test
    public void testToCustomerDetails() {
        AmendSubscriptionForm form = new AmendSubscriptionForm();
        form.setTitle("Mr");
        form.setFname("John");
        form.setLname("Smith");
        form.setEmail("john.smith@example.com");
        form.setTelno("01234567890");
        form.setAddr1("123 Main Street");
        form.setAddr2("Apartment 4B");
        form.setTown("Manchester");
        form.setCounty("Greater Manchester");
        form.setPostcode("M1 1AA");
        form.setCountry("UK");
        
        CustomerDetails customerDetails = form.toCustomerDetails();
        
        assertNotNull(customerDetails);
        assertEquals("Mr", customerDetails.getTitle());
        assertEquals("John", customerDetails.getFirstName());
        assertEquals("Smith", customerDetails.getLastName());
        assertEquals("john.smith@example.com", customerDetails.getEmailAddress());
        assertEquals("01234567890", customerDetails.getPhoneNumber());
        assertEquals("123 Main Street", customerDetails.getAddressLine1());
        assertEquals("Apartment 4B", customerDetails.getAddressLine2());
        assertEquals("Manchester", customerDetails.getTown());
        assertEquals("Greater Manchester", customerDetails.getCounty());
        assertEquals("M1 1AA", customerDetails.getPostCode());
        assertEquals("UK", customerDetails.getCountry());
    }

    @Test
    public void testToVehicleDetails() {
        AmendSubscriptionForm form = new AmendSubscriptionForm();
        form.setRegistration("AB12 CDE");
        form.setMake("Ford");
        form.setModel("Focus");
        form.setColour("Blue");
        
        VehicleDetails vehicleDetails = form.toVehicleDetails();
        
        assertNotNull(vehicleDetails);
        assertEquals("AB12 CDE", vehicleDetails.getLicensePlate());
        assertEquals("Ford", vehicleDetails.getMake());
        assertEquals("Focus", vehicleDetails.getModel());
        assertEquals("Blue", vehicleDetails.getColour());
    }
}
