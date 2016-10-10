# upload-lob
Upload LOB

Some DB tools make it really difficult to upload files to clob or blob fields (especially anything Oracle related).
This is a simple utility to upload a file to a clob or blob field in an existing record or existing records.

The DB connection settings are stored in the file **loadlob.properties**:

connectionstring=jdbc:oracle:thin:@localhost:1521/XE
user=testu
pass=password
driver=com.mysql.jdbc.Driver

The command line takes the following parameters in this exact order:

  1. table name
  1. lob field name
  1. fieldtype (blob|clob) 
  1. whereclause  - where condition excluding the "where" keyword. All matching records will be updated.
  1. filename
  
For example:

mytable imagefield blob "id in (1,2)" ../myfilename.jpg


