create table gruppen_entity
(
    id          serial,
    titel       varchar(50) NOT NULL,
    geschlossen bool,
    PRIMARY KEY (id)
);

create table gruppen_teilnehmer_entity
(
    gruppen_entity int,
    username       varchar(39),
    CONSTRAINT gruppen_fk FOREIGN KEY (gruppen_entity) REFERENCES gruppen_entity (id)
);

create table ausgaben_entity
(
    id             serial,
    gruppen_entity int,
    titel          varchar(50) NOT NULL,
    cents          int         NOT NULL,
    glaeubiger     varchar(39) NOT NULL,
    datetime       timestamp   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT gruppen_entity_fk FOREIGN KEY (gruppen_entity) REFERENCES gruppen_entity (id)
);

create table ausgaben_teilnehmer_entity
(
    ausgaben_entity int,
    username        varchar(39),
    CONSTRAINT ausgaben_fk FOREIGN KEY (ausgaben_entity) REFERENCES ausgaben_entity (id)
);