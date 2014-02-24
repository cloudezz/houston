Houston
==========================

Houston Application is the Mission Control Center for Cloudezz


### Pre-requisites ###

1. Install JDK and other J2EE components
2. Install MySQL
2. Install NodeJS
2. Install Yeoman by running the command `npm install -g yo`. This will internally install Grunt and Bower for you.


### Getting Started ###

Once the code is downloaded run the following commands from the root project folder to build the application:

    bower install
	npm install 
	mvn clean install -DskipTests=true

Then to run the application you can choose either of the following options

1. Run from eclipse IDE -

	On the Eclipse IDE run the class Application.java using the option Run As Java Application. The application will be running on `http://localhost:8090`

2. Run using Spring Boot

	On the command prompt run `mvn` task `mvn spring-boot:run` . The application will be running on `http://localhost:8090`

3. Run using Grunt

	On the command prompt run `grunt server` . This should open up your Web browser, with live reload enabled, on `http://localhost:9000.` Any changes to HTML, JS files would be auto deployed. This is the best way to run the application if you are developing frontend.

