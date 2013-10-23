Arquillian REST Client Extension
===
Arquillian REST Client Extension allows you to test your RESTful applications on the client side.

Sometimes you need to test your REST app as a black box. You know the interface (the contract), you have some input and know what results you expect.
For that purpose Arquillian REST Client Extension is your friend.

But wait! Why do I need extension for that? Can't I simply deploy test app with Arquillian Core and simply use HttpClient?
Yes you can, but don't you want to make your code shorter?

##Let's see how short it can be.
Here is fragment from functional tests of our extension:

    @RunWith(Arquillian.class)
    public class RestClientTestCase {

        @ArquillianResource
        private URL deploymentURL;

        @Deployment(testable = false)
        public static WebArchive create()
        {
            return ShrinkWrap.create(WebArchive.class)
                .addPackage(Customer.class.getPackage())
                .addClasses(CustomerResource.class, CustomerResourceImpl.class, JaxRsActivator.class);
        }

        /**
         * Arquillian calculates resource path by using deployment URL+ArquillianResteasyResource.value which is by default "rest".
         * If your API is located under different root i.e. "api_v2" then use @ArquillianResteasyResource("api_v2")
         *
         * @param customerResource configured resource ready for use, injected by Arquillian
         */
        @Test
        public void getCustomerById(@ArquillianResteasyResource CustomerResource customerResource)
        {
        //        Given
            final String name = "Acme Corporation";
            final long customerId = 1L;

        //        When
            final Customer result = customerResource.getCustomerById(customerId);

        //        Then
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(name, result.getName());
        }
    }
Look at line 7. Note that after Arquillian deploys test archive to container (i.e. AS7) our tests should run from client JVM (outside of container).
Now our test method accepts single param annotated with @ArquillianResteasyResource. That annotation tells REST extension that is should create some REST client artifact (in this case it's a client proxy to CustomerResource).
That's it. No opening HTTP connections, no JSON/XML/etc. And still under the hood there are HTTP requests flying back and forth between our test and server.

##Ok, but what the hell is CustomerResource?
It's interface properly decorated with JAX-RS annotations.


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
It might be a nice way to extract your app's REST resources interfaces into separate module so that it can be used later on in tests and clients.

##What if I want to be naughty?
Let's suppose you want to test what will happen if you make GET request to "/rest/customer" but tell server you accept only YAML. Nothing simpler. Just put @Consumes annotation on your test method. It will overwrite annotations from CustomerResource interface.

    @Test
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public void createCustomer(@ArquillianResteasyResource CustomerResource customerResource) {
    //      ...
    }
Actually all annotations on test method will be copied to CustomerResource, except for @Test.

##Nice, but what if I need a bit more control
The objects we're passing (i.e. Customer) are being marshaled by JAXB, Jackson, or whatever you configure. But what if you want to send some additional headers, tell server about accepted encoding or post manually created and invalid JSON object? For that we need to inject different resource.

    /**
     * We can inject either proxy or a ResteasyWebTarget for low level manipulations and assertions.
     *
     * @param webTarget configured resource ready for use, injected by Arquillian
     */
    @Test
    public void createPackageBareRsource(@ArquillianResteasyResource("rest/customer") ResteasyWebTarget webTarget)
    {
        //        Given
        final Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.acceptEncoding("UTF-8");
        invocationBuilder.accept(MediaType.APPLICATION_ATOM_XML_TYPE);
        invocationBuilder.header("Authorization","Basic sialala");
        final Invocation invocation = invocationBuilder.buildPost(Entity.entity("{\"biskupa\":\"?upa\"}", MediaType.APPLICATION_JSON_TYPE));

        //        When
        final Response response = invocation.invoke();

        //        Then
        assertEquals(deploymentURL + "rest/customer", webTarget.getUri().toASCIIString());
        assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

In the example above we want to do POST against "rest/customer", send customer in JSON fromat but add some field "biskupa" that actually does not exist in Customer class. What's more we want to specify authorization header to mimic Basic Authorization and instruct serve to return response in ATOM format.
Of course this test will not pass (we told server to respond in ATOM format and then we do assertion for JSON), but it's purpose here is to show you what cool customization we can do.

Notice that org.jboss.resteasy.client.jaxrs.ResteasyWebTarget is available only if you use arquillian-rest-client-impl-3x. If you use arquillian-rest-client-impl-2x then you should inject org.jboss.resteasy.client.ClientRequest. More details at the end.

##So how does this extension know the URL of my API?
Well, Arquillian knows very well where it has deployed the archive. The path to API is taken from @ArquillianResteasyResource which by default is "rest". If your API is located somewhere else then simply specify it i.e.: @ArquillianResteasyResource("api/v2"). Note lack of preceeding and trailing slashes.

##So what do I need in my POM?
This depends. The extension leverages RestEasy client, but since there are major differences between RestEasy 2.x and 3.x the extension ships with two different implementations, one for each RestEasy version.

Here are dependencies for Arquillian REST Client Extension if you choose RestEasy 3.x:

    <dependency>
        <groupId>org.jboss.arquillian.extension</groupId>
        <artifactId>arquillian-rest-client-api</artifactId>
        <version>1.0.0.Final-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.jboss.arquillian.extension</groupId>
        <artifactId>arquillian-rest-client-impl-3x</artifactId>
        <version>1.0.0.Final-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

Depending on data formats you want to use to communicate with your REST app you need to also add appropriate marshallers.
JSON:

    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-jackson-provider</artifactId>
        <version>${version.resteasy}</version>
        <scope>test</scope>
    </dependency>
XML:

    <dependency>
        <groupId>org.jboss.resteasy</groupId>
        <artifactId>resteasy-jaxb-provider</artifactId>
        <version>${version.resteasy}</version>
        <scope>test</scope>
    </dependency>
