-- Indexes for primary keys have been explicitly created.

-- ---------- Table for validation queries from the connection pool. ----------

DROP TABLE PingTable;
CREATE TABLE PingTable (foo CHAR(1));

-- ------------------------------ UserProfile ----------------------------------

Drop table Apuesta;
Drop table Opcion;
Drop table TipoApuesta;
Drop table Evento;
Drop table Categoria;
DROP TABLE UserProfile;


CREATE TABLE UserProfile (
    usrId BIGINT NOT NULL AUTO_INCREMENT,
    loginName VARCHAR(30) COLLATE latin1_bin NOT NULL,
    enPassword VARCHAR(13) NOT NULL, 
    firstName VARCHAR(30) NOT NULL,
    lastName VARCHAR(40) NOT NULL, email VARCHAR(60) NOT NULL,
    CONSTRAINT UserProfile_PK PRIMARY KEY (usrId),
    CONSTRAINT LoginNameUniqueKey UNIQUE (loginName)) 
    ENGINE = InnoDB;

CREATE INDEX UserProfileIndexByLoginName ON UserProfile (loginName);

create table Categoria(
	idCategoria BIGINT NOT NULL AUTO_INCREMENT,
	nombreCategoria VARCHAR(20),
	CONSTRAINT idCategoria_pk PRIMARY KEY(idCategoria)
)ENGINE = InnoDB; 

create index categoria_index on Categoria(idCategoria);

create table Evento (

	idEvento BIGINT NOT NULL AUTO_INCREMENT,
	nombreEvento VARCHAR(255),
	fecha TIMESTAMP,
	categoria BIGINT NOT NULL,
	CONSTRAINT IdEvento_pk PRIMARY KEY (idEvento),
	CONSTRAINT Categoria_fk FOREIGN KEY (categoria) REFERENCES Categoria(idCategoria))
	ENGINE = InnoDB; 

create index evento_index on Evento(idEvento);


create table TipoApuesta(
	idTipo BIGINT NOT NULL AUTO_INCREMENT,
	pregunta VARCHAR(255) NOT NULL,
	multiple BOOLEAN NOT NULL,
	evento BIGINT NOT NULL,
	CONSTRAINT evento_fk FOREIGN KEY (evento) REFERENCES Evento(idEvento),
	CONSTRAINT idTipo_pk PRIMARY KEY(idTipo)
)ENGINE = InnoDB; 

create index tipoApuesta_index on TipoApuesta(idTipo);

create table Opcion (
	idOpcion BIGINT NOT NULL AUTO_INCREMENT,
	nombreOpcion VARCHAR(255) NOT NULL,
	cuota DOUBLE NOT NULL,
	estado BOOLEAN,
	tipoApuesta BIGINT NOT NULL,
	version BIGINT DEFAULT 1 NOT NULL,
	CONSTRAINT tipoApuesta_fk FOREIGN KEY (tipoApuesta) REFERENCES TipoApuesta(idTipo),
	CONSTRAINT idOpcion_pk PRIMARY KEY(idOpcion)
)ENGINE = InnoDB; 

create index opcion_index on Opcion(idOpcion);

create table Apuesta(
	idApuesta BIGINT NOT NULL AUTO_INCREMENT,
	fecha TIMESTAMP NOT NULL,
	apostado DOUBLE NOT NULL,
	opcionElegida BIGINT NOT NULL,
	usuario BIGINT NOT NULL,
	CONSTRAINT usuario_fk foreign key (usuario) REFERENCES UserProfile(usrId),
	CONSTRAINT opcionElegida_fk FOREIGN KEY (opcionElegida) REFERENCES Opcion(idOpcion),
	CONSTRAINT idApuesta_pk PRIMARY KEY(idApuesta)
)ENGINE = InnoDB; 

create index apuesta_index on Apuesta(idApuesta);



