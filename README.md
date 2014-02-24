Houston
==========================

Houston Application is the Mission Control Center for Cloudezz. This application is based on the boiler plate code generated using a [Yeoman](http://yeoman.io/) Generator called [JHipster](http://jhipster.github.io/) . JHipster is a Yeoman generator, used to create a Yeoman + Maven + Spring + AngularJS project.


### Pre-requisites ###

1. Install JDK and other J2EE components
2. Install MySQL
2. Install NodeJS
2. Install [Yeoman](http://yeoman.io/) by running the command `npm install -g yo`. This will internally install Grunt and Bower for you.


### Getting Started in Development mode with JHipster/Houston ###

Once the code is downloaded run the following commands from the root project folder to build the application:

    bower install
	npm install 
	mvn clean install -DskipTests=true

Then to run the application you can choose either of the following options

1. Run from eclipse IDE:

	On the Eclipse IDE run the class `Application.java` using the option Run As Java Application. The application will be running on `http://localhost:8090`

2. Run using Spring Boot using Maven command: 

	On the command prompt run `mvn` task `mvn spring-boot:run` . The application will be running on `http://localhost:8090`

3. Run using Grunt & Java Server together:

	Run the application in any of the form given above. We use this only as a backend Restful service.

	You can now run Grunt to work on the client-side JavaScript application:

	On the command prompt run `grunt server` . This should open up your Web browser, with live reload enabled, on `http://localhost:9000.` Any changes to HTML, JS files would be auto deployed. This is the best way to run the application if you are developing frontend.

	This Grunt server has a proxy to the REST endpoints on the Java server which we just launched, so it should be able to do live REST requests to the Java back-end.

	If you want more information on using Grunt, please go to [http://gruntjs.com](http://gruntjs.com).

### Login Credentials

    Email: admin@cloudezz.com
	Password: admin
	Role: ROLE_ADMIN

	Email: user@cloudezz.com
	Password: user
	Role: ROLE_USER 

### Dependency Updates ###

#### Maven/Java ####

Update the `pom.xml`.

#### Javascript ####
You can use bower normally to update your JavaScript dependencies:

    bower update

Or if you want to install a new JavaScript dependency:
    
    bower install <package>

Your JavaScript dependencies will be stored in your src/main/webapp/bower_components folder.

If you want more information on using Bower, please go to [http://bower.io](http://bower.io).

#### Database ####

If you add or modify a JPA entity, you will need to update your database schema.

Houston uses Liquibase and stores its configuration in /src/main/resources/config/liquibase/, so your development process should be:

- Adding, modifying or removing a JPA entity
- Adding a new "changeSet" in your db-changelog.xml file to reflect this change
- Starting up your application


When you startup your application, the Spring Boot will update your database schema automatically using Liquibase.

If you want more information on using Liquibase, please go to [http://www.liquibase.org](http://www.liquibase.org).


### Advanced usage: using hot reloading ###

JHipster comes with full hot reloading, both on the client-side (JavaScript) and on the server-side (Java/Spring).

#### On the client side (JavaScript)

Run `grunt server`

This will launch a Web browser, on [http://localhost:9000](http://localhost:9000), which will have live reload of your HTML/CSS/JavaScript code.

#### On the server side (Java/Spring)

Hot reloading is enabled thanks to Spring Loaded, which is still an experimental technology.

Spring Loaded works for the following scenarios:

- Replacing a "normal" Java class. You can add/modify/delete code (methods and fields) on any class, and they will be automatically reloaded.
- Replacing a Spring bean: field-based injection and PostContruct hooks work, adding REST endpoints to your controllers work.
- Modifying a Jackson object: both JPA entities and REST DTOs, when they are changed, trigger a flush of the Jackson caches.


To use Spring Loaded, add to your JVM command line the following arguments: -

	javaagent:spring_loaded/springloaded-1.1.5-dev.jar -noverify

Now, once your application is launched and you compile a class, it will be automatically reloaded, without restarting your server:

- Your server will be available on [http://localhost:8090](http://localhost:8090)
- If you use `grunt server` on the client side, you will be able to access all dynamic requests on `http://localhost:9000/app`, as it is a proxy to `http://localhost:8090/ap`p thanks to [grunt-connect-proxy](https://npmjs.org/package/grunt-connect-proxy).


 
#### Putting it all together

Run the "Application" class with the `-javaagent:spring_loaded/springloaded-1.1.5-dev.jar -noverify` arguments.

Then run `grunt server`

And you should have live reload of both your HTML/JS/CSS files and your Java classes.