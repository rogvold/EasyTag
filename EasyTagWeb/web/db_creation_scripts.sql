CREATE ROLE easytag LOGIN ENCRYPTED PASSWORD 'md51ddd3c9ee2606101941055cf5b2df0ce'
   VALID UNTIL 'infinity';

CREATE DATABASE easytag
  WITH ENCODING='UTF8'
       OWNER=easytag
       CONNECTION LIMIT=-1;
