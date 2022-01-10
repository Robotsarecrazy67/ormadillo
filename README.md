# Ormadillo

## Project Description
A java based ORM for simplifying connecting to and from an SQL database without the need for SQL or connection management. 

## Technologies Used

* PostgreSQL - version 42.2.12  
* Java - version 8.0  
* Apache commons - version 2.1  
* JUnit

## Features

List of features ready and TODOs for future development  
* Easy to use and straightforward user API.  
* No need for SQL, HQL, or any databse specific language.  
* Straightforward and simple Annotation based for ease of use. 
* etc...

To-do list: [`for future iterations`]
* Mapping of join columns inside of entities.    
* Implement of aggregate functions.  
* Add Custom Exceptions 
* etc...

## Getting Started  
Currently project must be included as local dependency. to do so:
```shell
  git clone https://github.com/Robotsarecrazy67/ormadillo.git
  cd ormadillo
  mvn install
```
Next, place the following inside your project pom.xml file:
```XML
  <dependencies>
    <dependency>
	  <groupId>com.revature</groupId>
	  <artifactId>ormadillo</artifactId>
	  <version>0.0.1-SNAPSHOT</version>
 	</dependency>
  </dependencies>

```

Inside your project structure you need a application.proprties file. 
 (typically located src/main/resources/)
 ``` 
  url=path/to/database
  username=username/of/database
  password=password/of/database 
  driver=org.postgresql.Driver
  packageName=package containing annotated classes
  autoBuildTables=true/false (builds the tables dynamically, set to false once tables are built)
  maxConnections=number of maximum connections in pool
  ```
You will also need to copy the allTypes.csv file to  
## Usage  
  ### Annotating classes  
  All classes which represent objects in database must be annotated.
   - #### @Table(name = "table_name)  
      - Indicates that this class is associated with table 'table_name'  
   - #### @Column(name = "column_name)  
      - Indicates that the Annotated field is a column in the table with the name 'column_name'  
   - #### @Id(name = "column_name") 
      - Indicates that the annotated field is the primary key for the table.
   - #### @JoinColumn(name = "column_name") 
      - Indicates that the annotated field is a serial key.

  ### User API  
  
  - #### `public static DataSource getConnection()`  
     - returns the singleton instance of the class. It is the starting point to calling any of the below methods.  
  - #### `public HashMap<Class<?>, HashSet<Object>> getCache()`  
     - returns the cache as a HashMap.  
  - #### `public boolean addClass(final Class<?> clazz)`  
     - Adds a class to the ORM. This is the method to use to declare a Class is an object inside of the database.  
  - #### `public boolean UpdateObjectInDB(final Object obj,final String update_columns)`  
     - Updates the given object in the databse. Update columns is a comma seperated lsit fo all columns in the onject which need to be updated  
  - #### `public boolean removeObjectFromDB(final Object obj)`  
     - Removes the given object from the database.  
  - #### `public boolean addObjectToDB(final Object obj)`  
     - Adds the given object to the database.  
  - #### `public Optional<List<Object>> getListObjectFromDB(final Class <?> clazz, final String columns, final String conditions)`
     - Finds all Objects in the Database by the given class
  - #### `public Optional<List<Object>> getObjectFromDBById(final Class <?> clazz, int id)`
     - Finds an Object in the Database by the given id  
  - #### `public void commit()`  
     - begin databse commit.  
  - #### `public void rollback()`  
     - Rollback to previous commit.  
  - #### `public void rollback(final String name)`  
     - Rollback to previous commit with given name.  
  - #### `public void setSavepoint(final String name)`  
     - Set a savepoint with the given name.  
  - #### `public void releaseSavepoint(final String name)`  
     - Release the savepoint with the given name.  
  - #### `public void enableAutoCommit()`  
     - Enable auto commits on the database.  
  - #### `public void setTransaction()`  
     - Start a transaction block.  
  - #### `public void addAllFromDBToCache(final Class<?> clazz)`  
     - Adds all objects currently in the databse of the given clas type to the cache.  
```
----------------------------------------------------------------------------------------------------/
-----------------------------_-------------_--------------------------------------------------------/
----------------------------/%\-----------/%\-------------------------------------------------------/
---------------------------/%``\---------/%``\------------------------------------------------------/
--------------------------/%````|-------/%````|-----------------------------------------------------/
-------------------------/%`````/------/%`````/------------------/%\--------------------------------/
------------------------/%``````/-----/%``````/-----------------/%--\-------------------------------/
-----------------------/%``````/-----/%``````/-----------------/%----|------------------------------/
----------------------/%``````/-----/%%`````/-----------------/%-----/------------------------------/
---------------------/%%`````/-_-_-|%%%````/-----------------/%------/--____------------------------/
--------------------|%%%```%/==============\----------------/%------/--/%----\--____----------------/
---------------------\%%%%==================\--------------/%------/--/%------\/%----\--------------/
---------------------/``````=================\------------/%-------|-/%-------/%------\-------------/
--------------------/``````````####===========\----------|%--------|/%-------/%-------/___----------/
-------------------/`````````##````============\---------|%---------%-------/%-------/%----\--------/
---------%%%%%%%%%|`````````````$$O`============\--------|%--------\%------/%-------/%------\-------/
------%%%%%%%%%%%%|````````````$$$$`=============\-------|%---------\______%-------/%-------/__-----/
----%%%%%%%%%%%%%%|`````````````$$`===============\------|%----------\%%%%%\------/%-------/%---\---/
--%%%%%%%%%%%%%%%%|`````````````````===============\-----|%-----------\----%\_____%-------/%----/---/
$$$%%%%%%%%%%%%%%%%\``````````````````==============\----\%------------|----%%%%%%\-------%----/----/
$$$$$%%%%%%%%%%%%%%%\```````````````````=============\----\%-----------|----------%\___--%----/-----/
$$$$$$$%%%%%%%%%%%%%%\_``````````````````\-===o====o==|----\%---------/------------%%%%\%----/------/
$$$$$$$$%%%%%%%%%%%%%%%\_`````````````````\---========|-----\%-------------------------%\___/-------/
%$$$$$$$$%%%%%%%%%%%%%%%%\_``````````______\__________/------\%-------------------------%/----------/
%%$$$$$$$%%%%%%%%%%%%%%%%%%%-------------%%%%%%--------------/%%------------------------/-----------/
%%%$$$$$$%%%%%%%%%%%%%%%%%%%%-------------%%%%%%%%----------/%%---------------------___/------------/
%%%%$$$$$%%%%%%%%%%%%%%%%%%%%%-------------%%%%%%%%%-------/%----------------------/----------------/
%%%%%$$$%%%%%%%%%%%%%%%%%%%%%%%-------------%%%%%%%%%-----/%----------------------/-----------------/
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%-------------%%%%%%%%----/%----------------------/------------------/
--________________________________________________________________________________________________--/
-|                                                                                                |-/
-|----$$$$$$$$$$-$$$$--$$$$-----$$$$$$$-----$$$$-----$$$$-$$$$---$$$$$---$$$$$$$$$$---$$$$$$$$----|-/
-|----$$$$$$$$$$-$$$$--$$$$-----$$$$$$$-----$$$$$----$$$$-$$$$--$$$$$---$$$$$$$$$$$$--$$$$$$$$----|-/
-|----$$$$$$$$$$-$$$$--$$$$----$$$$-$$$$----$$$$$$---$$$$-$$$$-$$$$$---$$$$$$---$$$$$--$$$$$$-----|-/
-|-------$$$$----$$$$--$$$$----$$$$-$$$$----$$$$$$$--$$$$-$$$$$$$$$$---$$$$$-----------$$$$$$-----|-/
-|-------$$$$----$$$$$$$$$$---$$$$$$$$$$$---$$$$$$$$-$$$$-$$$$$$$$$----$$$$$$$$---------$$$$------|-/
-|-------$$$$----$$$$$$$$$$---$$$$$$$$$$$---$$$$$$$$$$$$$-$$$$$$$$$------$$$$$$$$$------$$$$------|-/
-|-------$$$$----$$$$--$$$$--$$$$----$$$$$--$$$$-$$$$$$$$-$$$$-$$$$$---------$$$$$$$$-------------|-/
-|-------$$$$----$$$$--$$$$--$$$$-----$$$$--$$$$--$$$$$$$-$$$$--$$$$$--$$$$$---$$$$$$--$$$$$$-----|-/
-|-------$$$$----$$$$--$$$$-$$$$-------$$$$-$$$$---$$$$$$-$$$$---$$$$$--$$$$$$$$$$$$--$$$$$$$$----|-/
-|-------$$$$----$$$$--$$$$-$$$$-------$$$$-$$$$----$$$$$-$$$$----$$$$$---$$$$$$$$-----$$$$$$-----|-/
-|________________________________________________________________________________________________|-/
----------------------------------------------------------------------------------------------------/
```

## License

This project uses the following license: [GNU Public License 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).