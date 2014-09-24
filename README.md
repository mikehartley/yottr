yottr
=====

Prerequisites:
Must have a running Postgres server with two databases: 
  yottr and yottrtest 
  (username and password as in Persistence config)
  (must have createdb role attribute)

To build:
  mvn clean install [-DskipITs]

To deploy (assuming Tomcat):
  cp target/yottr.war $TOMCAT_HOME/webapps/
  
URL (by default):
  http://localhost:8080/yottr
