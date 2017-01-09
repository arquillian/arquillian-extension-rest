Arquillian Warp REST Extension
===
Arquillian Warp REST Extension allows you to test your RESTful applications on the server side.

This extension brings tools for intercepting the state of the executing service and exercise it through in container
tests that can be executed directly before or after the service is being invoked. It supports JAX-RS major versions
including 1.1 and 2.0 and the most popular implementations.

### Supported versions:
* JAX-RS 1.1 - since API didn't provide any common functionality for intercepting the requests, the support has been
               implemented through implementation specific (RESTEasy, Jersey, CXF) hooks
* JAX-RS 2.0 - generic support based on the request interceptors and filters

### API
We introduce an artificial RestContext that allows to verify the state of the executing service, including the
incoming request, response, status code, headers and i.e. returned entity.

### Setup:

In order to setup the project one need only to add one of the REST implementations for the targeted JAX-RS version.

```xml
   <dependency>
       <groupId>org.jboss.arquillian.extension</groupId>
       <artifactId>arquillian-rest-warp-impl-jaxrs-2.0</artifactId>
   </dependency>
```

In case your are using JAX-RS 1.1 you need to use one of the artifacts that targets the specific JAX-RS implementation:
* arquillian-rest-warp-impl-resteasy
* arquillian-rest-warp-impl-jersey
* arquillian-rest-warp-impl-cxf

### Container
The JAX-RS needs to be aware of the Warp REST interceptors. If you are using the automatic discovery of the resources
and provider through classpath scanning, you will not to do anything since the environment will pick up the extension
automatically. In order cases the proper interceptor will have to be properly registered.

### Example test:

```java
   @WarpTest
   @RunWith(Arquillian.class)
   public class StockServiceResourceTestCase {

       @Deployment
       @OverProtocol("Servlet 3.0")
       public static Archive createTestArchive() {

           return Deployments.createDeployment();
       }

       @ArquillianResource
       private URL contextPath;

       /**
        * The service client.
        */
       private StockService stockService;

       @BeforeClass
       public static void setUpClass() {

           // initializes the rest easy client framework
           RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
       }
       @Before
       public void setUp() {

           ResteasyClient client = new ResteasyClientBuilder().build();
           ResteasyWebTarget  target = client.target(contextPath + "rest");

           stockService = target.proxy(StockService.class);
       }

       @Test
       @RunAsClient
       public void testStockGetWarp() {

           final Stock stock = createStock();
           Response response = stockService.createStock(stock);
           assertEquals("The service returned incorrect status code.", 201, response.getStatus());
           response.close();

           Warp.initiate(new Activity() {
               @Override
               public void perform() {

                   Stock result = stockService.getStock(2L);

                   assertEquals("Stock has invalid name.", stock.getName(), result.getName());
                   assertEquals("Stock has invalid code.", stock.getCode(), result.getCode());
                   assertEquals("Stock has invalid value.", stock.getValue(), result.getValue());
               }
           }).inspect(new Inspection() {

               private static final long serialVersionUID = 1L;

               @ArquillianResource
               private RestContext restContext;

               @AfterServlet
               public void testGetStock() {

                   assertEquals(HttpMethod.GET, restContext.getHttpRequest().getMethod());
                   assertEquals(200, restContext.getHttpResponse().getStatusCode());
                   assertEquals("application/json", restContext.getHttpResponse().getContentType());
                   assertNotNull(restContext.getHttpResponse().getEntity());
               }
           });
       }
   }
```

