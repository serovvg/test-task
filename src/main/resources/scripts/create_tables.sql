CREATE TABLE if NOT EXISTS doctors
(
    id bigint IDENTITY PRIMARY KEY,
    last_name varchar(50) NOT NULL,
    first_name varchar(50) NOT NULL,
    patronymic varchar(50),
    specialization varchar(50) NOT NULL
);

CREATE TABLE if NOT EXISTS patients
(
    id bigint IDENTITY PRIMARY KEY,
    last_name varchar(50) NOT NULL,
    first_name varchar(50) NOT NULL,
    patronymic varchar(50),
    phone_number varchar(15)
);

CREATE TABLE if NOT EXISTS recipes
(
    id bigint IDENTITY PRIMARY KEY,
    description varchar(1000) NOT NULL,
    patient_id bigint NOT NULL references patients,
    doctor_id bigint NOT NULL references doctors,
    creation_date date NOT NULL,
    validity smallint NOT NULL,
    priority varchar(20) NOT NULL
);