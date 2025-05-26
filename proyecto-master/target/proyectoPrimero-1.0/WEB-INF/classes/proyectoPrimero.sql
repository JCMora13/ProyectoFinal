create database  ProyectoPrimero;
use ProyectoPrimero;

create table Usuario (
  id_usuario int auto_increment,
  nombre varchar(25),
  apellidos varchar(25),
  email varchar(25),
  contraseña varchar(12),
  primary key(id_usuario)
);

create table  Libro (
  id_libro int auto_increment,
  titulo varchar(15),
  autor varchar(25),
  tipo char(1),
  constraint estado check (tipo = 'P' OR tipo = 'D'),
  primary key (id_libro)
);

create table  Prestamo (
  id_prestamo int auto_increment,
  id_usuario int,
  id_libro int,
  fecha_prestamo date,
  fecha_devolucion date,
  primary key (id_prestamo),
  foreign key (id_usuario) references Usuario(id_usuario),
  foreign key  (id_libro) references Libro(id_libro)
);