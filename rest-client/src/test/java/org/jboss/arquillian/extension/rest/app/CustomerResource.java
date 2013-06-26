package org.jboss.arquillian.extension.rest.app;

import org.jboss.arquillian.extension.rest.app.model.Customer;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/customer")
public interface CustomerResource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    List<Customer> getAllCustomers();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("/{id:[1-9][0-9]*}")
    Customer getCustomerById(@PathParam("id") long id);

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/")
    Customer createCustomer(Customer customer);
}
