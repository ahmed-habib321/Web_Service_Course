1. After finishing making your service 
2. start your service with GroupPublisher
3. generate client stubs using wsimport
4. `wsimport -keep -p org.example.client http://localhost:8888/myserv?wsdl`
5. wsimport has been deleted from modern java versions so you have to add it manually
6. https://repo1.maven.org/maven2/com/sun/xml/ws/jaxws-ri/2.3.7/jaxws-ri-2.3.7.zip
7. add it to system path then use the command in cmd 
8. This will generate client classes in the `org.example.client` package.
9. put it in the project (can be other project but the GroupPublisher must be still running)
10. use `GroupServiceClient.java` to access the service and use it
11. Deploy your JSP to your web server (Tomcat, etc.)
12. DONE


