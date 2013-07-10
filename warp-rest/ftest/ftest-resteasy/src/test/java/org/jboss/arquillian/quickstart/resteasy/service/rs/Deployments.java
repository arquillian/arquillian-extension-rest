package org.jboss.arquillian.quickstart.resteasy.service.rs;

import org.jboss.arquillian.quickstart.resteasy.application.StockApplication;
import org.jboss.arquillian.quickstart.resteasy.model.Stock;
import org.jboss.arquillian.quickstart.resteasy.service.StockService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import java.io.File;

/**
 * An utility class that creates the test deployments.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
final class Deployments {

    /**
     * Creates test deployment.
     *
     * @return the test deployment
     */
    public static Archive createDeployment() {
        File[] libs = loadLibraries();

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("WEB-INF/web.xml")
                .addAsWebResource("restclient.jsp")
                .addAsWebResource("js/jquery-1.8.2.min.js", "js/jquery-1.8.2.min.js")
                .addAsLibraries(libs);
    }

    /**
     * Loads all required dependencies needed to run the application in the application server.
     *
     * @return the loaded dependencies
     */
    private static File[] loadLibraries() {
        return DependencyResolvers.use(MavenDependencyResolver.class)
                .artifacts("org.easytesting:fest-assert:1.4").resolveAsFiles();
    }
}
