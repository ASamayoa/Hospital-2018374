/*alter user 'root'@'localhost' identified with
	mysql_native_password by 'admin';
*/
create database DBHospitalInfectologia2018374;
use DBHospitalInfectologia2018374;

-- creacion tablas

create table Medicos (
	codigoMedico int not null auto_increment,
	licenciaMedica integer not null,
	nombres varchar(100) not null,
    apellidos varchar(100) not null,
    horaEntrada Varchar(8) not null,
    horaSalida varchar(8) not null,
    turnoMaximo int not null default 0,
    sexo varchar(15) not null,
    primary key PK_CodigoMedico (codigoMedico)
);

create table Pacientes (
	codigoPaciente int not null auto_increment,
    dpi varchar(20) not null,
    apellidos varchar(100) not null,
    nombres varchar(100) not null,
    fechaDeNacimiento date not null,
    edad int,
    direccion varchar(150) not null,
    ocupacion varchar(50) not null,
    sexo varchar(15) not null,
    primary key PK_CodigoPaciente (codigoPaciente)
);

create table ContactoUrgencia (
	codigoContacto int not null auto_increment,
    codigoPaciente int not null,
    nombre varchar(100) not null,
    apellido varchar(100) not null,
    numeroContacto varchar(100) not null,
    primary key PK_CodigoContacto (codigoContacto),
    constraint FK_CodigoPacienteUrgencia foreign key (codigoPaciente)
    references Pacientes (codigoPaciente)
);

create table Areas (
	codigoArea int not null auto_increment,
    nombreArea varchar(45) not null,
    primary key PK_CodigoArea (codigoArea)
);

create table Cargos (
	codigoCargo int not null auto_increment,
    nombreCargo varchar(45) not null,
    primary key PK_CodigoCargo (codigoCargo)
);

create table Horarios (
	codigoHorario int not null auto_increment,
    horaInicio varchar(8) not null,
    horaSalida varchar(8) not null,
    lunes boolean not null,
    martes boolean not null,
    miercoles boolean not null,
    jueves boolean not null,
    viernes boolean not null,
    primary key PK_CodigoHorario (codigoHorario)
);

create table Especialidad (
	codigoEspecialidad int not null auto_increment,
    nombreEspecialidad varchar(55) not null,
    primary key PK_CodigoEspecialidad (codigoEspecialidad)
);

create table TelefonoMedicos (
	codigoTelefonoMedico int not null auto_increment,
    telefonoPersonal varchar(15) not null,
    telefonoTrabajo varchar(15),
    codigoMedico int not null,
    primary key PK_CodigoTelefonoMedico (codigoTelefonoMedico)
);

create table MedicoEspecialidad (
	codigoMedicoEspecialidad int not null auto_increment,
    codigoMedico int not null,
    codigoEspecialidad int not null,
    codigoHorario int not null,
    primary key PK_CodigoMedicoEspecialidad (codigoMedicoEspecialidad),
    constraint FK_CodigoMedico foreign key (codigoMedico)
		references Medicos (codigoMedico),
	constraint FK_CodigoEspecialidad foreign key (codigoEspecialidad)
		references Especialidad (codigoEspecialidad),
	constraint FK_CodigoHorario foreign key (codigoHorario)
		references Horarios	(codigoHorario)
);

create table ResponsableTurno (
	codigoResponsableTurno int not null auto_increment,
    nombreResponsable varchar(45) not null,
    telefonoResponsable varchar(10) not null,
    codigoArea int not null,
    codigoCargo int not null,
    primary key PK_CodigoResponsableTurno (codigoResponsableTurno),
    constraint FK_CodigoArea foreign key (codigoArea)
		references Areas (codigoArea),
	constraint FK_CodigoCargo foreign key (codigoCargo)
		references Cargos (codigoCargo)
);

create  table Turno (
	codigoTurno int not null auto_increment,
    fechaTurno date not null,
    fechaCita date not null,
    valorCita decimal(10,2) not null,
    codigoMedicoEspecialidad int not null,
    codigoResponsableTurno int not null,
    codigoPaciente int not null,
    primary key PK_CodigoTurno (codigoTurno),
    constraint FK_CodigoMedicoEspecialidad foreign key (codigoMedicoEspecialidad)
		references MedicoEspecialidad (codigoMedicoEspecialidad),
	constraint FK_TurnoResponsable foreign key (codigoResponsableTurno)
		references ResponsableTurno (codigoResponsableTurno),
	constraint FK_CodigoPacienteTurno foreign key (codigoPaciente)
		references Pacientes (codigoPaciente)
);

-- SP's agregar
delimiter $$
create procedure sp_AgregarMedico (in licencia integer, in nombre varchar(100), in apellido varchar(100), in entrada varchar(8), in salida varchar(8), in genero varchar(15))
	begin
		insert into Medicos (licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo)
			values(licencia, nombre, apellido, entrada, salida, genero);
    end$$
delimiter ;


delimiter $$
create procedure sp_AgregarPacientes(in id varchar(20), in nombre varchar(100), in apellido varchar(100), in fecha date, in direccionFis varchar(150), in ocup varchar(50), in genero varchar(15))
	begin
		insert into Pacientes (dpi,nombres,apellidos,fechaDeNacimiento,direccion,ocupacion,sexo)
			values(id,nombre,apellido,fecha,direccionFis,ocup,genero);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarContactoUrgencia(in paciente int,in nombres varchar(100),in apellidos varchar(100),in numero varchar(100))
	begin
		insert into contactourgencia(codigoPaciente,nombre,apellido,numeroContacto)
			values(paciente,nombres,apellidos,numero);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarArea (in nombre varchar(45))
	begin
		insert into Areas(nombreArea)
			values(nombre);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarCargo (in nombre varchar(45))
	begin
		insert into Cargos(nombreCargo)
			values(nombre);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarHorario(in inicio varchar(8), in salida varchar(8), in lun boolean, in mart boolean, in mier boolean, in jue boolean, in vier boolean)
	begin
		insert into horarios(horaInicio,horaSalida,lunes,martes,miercoles,jueves,viernes)
			values(inicio,salida,lun,mart,mier,jue,vier);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarEspecialidad(in nombre varchar(55))
	begin
		insert into especialidad (nombreEspecialidad)
			values(nombre);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarTelefonoMedico (in personal varchar(15), in trabajo varchar(15), in medico int)
	begin
		insert into telefonomedicos(telefonoPersonal,telefonoTrabajo,codigoMedico)
			values(personal,trabajo,medico);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarMedicoEspecialidad (in medico int, in especialidad int, in horario int)
	begin
		insert into MedicoEspecialidad (codigoMedico, codigoEspecialidad, codigoHorario)
			values(medico, especialidad, horario);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarResponsableTurno (in nombre varchar(45),in telefono varchar(10),in area int,in cargo int)
	begin
		insert into ResponsableTurno (nombreResponsable,telefonoResponsable,codigoArea,codigoCargo)
			values(nombre,telefono,area,cargo);
    end$$
delimiter ;

delimiter $$
create procedure sp_AgregarTurno (in fechaT date,in fechaC date,in valorC decimal(10,2),in medicoE int,in responsableT int, in paciente int)
	begin
		insert into turno(fechaTurno,fechaCita,valorCita,codigoMedicoEspecialidad,codigoResponsableTurno,codigoPaciente)
			values(fechaT,fechaC,valorC,medicoE,responsableT,paciente);
    end$$
delimiter ;

-- SP's listar

delimiter $$
create procedure sp_ListarMedico ()
	begin
		select Medicos.codigoMedico,
        Medicos.licenciaMedica,
        Medicos.nombres,
        Medicos.apellidos,
        Medicos.horaEntrada,
        Medicos.horaSalida,
        Medicos.turnoMaximo,
        Medicos.sexo from Medicos;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarPacientes()
	begin
		select Pacientes.codigoPaciente,
        Pacientes.dpi,
        Pacientes.nombres,
        Pacientes.apellidos,
        Pacientes.fechaDeNacimiento,
        Pacientes.edad,
        Pacientes.direccion,
        Pacientes.ocupacion,
        Pacientes.sexo
			from Pacientes;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarContactoUrgencia()
	begin
		select contactourgencia.codigoContacto,
        contactourgencia.codigoPaciente,
        contactourgencia.nombre,
        contactourgencia.apellido,
        contactourgencia.numeroContacto
			from contactourgencia;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarArea ()
	begin
		select Areas.codigoArea,
        Areas.nombreArea
			from Areas;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarCargo ()
	begin
		select Cargos.codigoCargo,
        Cargos.nombreCargo
			from Cargos;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarHorario()
	begin
		select horarios.codigoHorario,
        horarios.horaInicio,
        horarios.horaSalida,
        horarios.lunes,
        horarios.martes,
        horarios.miercoles,
        horarios.jueves,
        horarios.viernes
			from horarios;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarEspecialidad()
	begin
		select especialidad.codigoEspecialidad,
        especialidad.nombreEspecialidad
			from especialidad;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarTelefonoMedico ()
	begin
		select telefonomedicos.codigoTelefonoMedico,
        telefonomedicos.telefonoPersonal,
        telefonomedicos.telefonoTrabajo,
        telefonomedicos.codigoMedico
			from telefonomedicos;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarMedicoEspecialidad ()
	begin
		select MedicoEspecialidad.codigoMedicoEspecialidad,
        MedicoEspecialidad.codigoMedico,
        MedicoEspecialidad.codigoEspecialidad,
        MedicoEspecialidad.codigoHorario
			from MedicoEspecialidad;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarResponsableTurno ()
	begin
		select ResponsableTurno.codigoResponsableTurno, 
        ResponsableTurno.nombreResponsable,
        ResponsableTurno.telefonoResponsable,
        ResponsableTurno.codigoArea,
        ResponsableTurno.codigoCargo
			from ResponsableTurno;
    end$$
delimiter ;

delimiter $$
create procedure sp_ListarTurno ()
	begin
		select turno.codigoTurno,
        turno.fechaTurno,
        turno.fechaCita,
        turno.valorCita,
        turno.codigoMedicoEspecialidad,
        turno.codigoResponsableTurno,
        turno.codigoPaciente
			from turno;
    end$$
delimiter ;
-- SP's buscar

delimiter $$
create procedure sp_BuscarMedico (in codigo int)
	begin
		select Medicos.codigoMedico,
        Medicos.licenciaMedica,
        Medicos.nombres,
        Medicos.apellidos,
        Medicos.horaEntrada,
        Medicos.horaSalida,
        Medicos.turnoMaximo,
        Medicos.sexo from Medicos
			where codigoMedico = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarPacientes(in codigo int)
	begin
		select Pacientes.codigoPaciente,
        Pacientes.dpi,
        Pacientes.nombres,
        Pacientes.apellidos,
        Pacientes.fechaDeNacimiento,
        Pacientes.edad,
        Pacientes.direccion,
        Pacientes.ocupacion,
        Pacientes.sexo
			from Pacientes
            where codigoPaciente = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarContactoUrgencia(in codigo int)
	begin
		select contactourgencia.codigoContacto,
        contactourgencia.codigoPaciente,
        contactourgencia.nombre,
        contactourgencia.apellido,
        contactourgencia.numeroContacto
			from contactourgencia
            where codigoContacto = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarArea (in codigo int)
	begin
		select Areas.codigoArea,
        Areas.nombreArea
			from Areas
            where codigoArea = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarCargo (in codigo int)
	begin
		select Cargos.codigoCargo,
        Cargos.nombreCargo
			from cargos
            where codigoCargo = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarHorario(in codigo int)
	begin
		select horarios.codigoHorario,
        horarios.horaInicio,
        horarios.horaSalida,
        horarios.lunes,
        horarios.martes,
        horarios.miercoles,
        horarios.jueves,
        horarios.viernes,
        horarios.sabado,
        horarios.domingo
			from horarios
            where codigoHorario = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarEspecialidad(in codigo int)
	begin
		select especialidad.codigoEspecialidad,
        especialidad.nombreEspecialidad
			from especialidad
            where codigoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarTelefonoMedico (in codigo int)
	begin
		select telefonomedicos.codigoTelefonoMedico,
        telefonomedicos.telefonoPersonal,
        telefonomedicos.telefonoTrabajo,
        telefonomedicos.codigoMedico
			from telefonomedicos
            where codigoTelefonoMedico = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarMedicoEspecialidad (in codigo int)
	begin
		select MedicoEspecialidad.codigoMedicoEspecialidad,
        MedicoEspecialidad.codigoMedico,
        MedicoEspecialidad.codigoEspecialidad,
        MedicoEspecialidad.codigoHorario
			from MedicoEspecialidad
            where codigoMedicoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarResponsableTurno (in codigo int)
	begin
		select ResponsableTurno.codigoResponsableTurno, 
        ResponsableTurno.nombreResponsable,
        ResponsableTurno.telefonoResponsable,
        ResponsableTurno.codigoArea,
        ResponsableTurno.codigoCargo
			from ResponsableTurno
            where codigoResponsableTurno = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_BuscarTurno (in codigo int)
	begin
		select turno.codigoTurno,
        turno.fechaTurno,
        turno.fechaCita,
        turno.valorCita,
        turno.codigoMedicoEspecialidad,
        turno.codigoResponsableTurno,
        turno.codigoPaciente
			from turno
			where codigoTurno = codigo;
    end$$
delimiter ;
-- SP's editar

delimiter $$
create procedure sp_EditarMedico (in codigo int, in licencia integer, in nombre varchar(100), in apellido varchar(100), in entrada varchar(8), in salida varchar(8), in genero varchar(15))
	begin 
		update Medicos set licenciaMedica = licencia, 
        nombres = nombre, 
        apellidos = apellido, 
        horaEntrada = entrada, 
        horaSalida = salida, 
        sexo = genero 
			where codigo = codigoMedico;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarPacientes(in codigo int, in id varchar(20), in nombre varchar(100), in apellido varchar(100), in fecha date,in direccionFis varchar(150), in ocup varchar(50), in genero varchar(15))
	begin
		update Pacientes set dpi=id,nombres=nombre,apellidos=apellido,fechaDeNacimiento=fecha,direccion=direccionFis,ocupacion=ocup,sexo=genero
        where codigoPaciente = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarContactoUrgencia(in codigo int, in paciente int,in nombres varchar(100),in apellidos varchar(100),in numero varchar(100))
	begin
		update contactourgencia set codigoPaciente=paciente,nombre=nombres,apellido=apellidos,numeroContacto=numero
			where codigoContacto = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarArea (in codigo int, in nombre varchar(45))
	begin
		update Areas set nombreArea=nombre
			where codigoArea  = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarCargo (in codigo int, in nombre varchar(45))
	begin
		update Cargos set nombreCargo=nombre
			where codigo = codigoCargo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarHorario(in codigo int, in inicio varchar(8), in salida varchar(8), in lun boolean, in mart boolean, in mier boolean, in jue boolean, in vier boolean)
	begin
		update horarios set horaInicio=inicio,horaSalida=salida,lunes=lun,martes=mart,miercoles=mier,jueves=jue,viernes=vier
			where codigoHorario = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarEspecialidad(in codigo int, in nombre varchar(55))
	begin
		update especialidad set nombreEspecialidad=nombre
			where codigoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarTelefonoMedico (in codigo int, in personal varchar(15), in trabajo varchar(15), in medico int)
	begin
		update telefonomedicos set telefonoPersonal=personal,telefonoTrabajo=trabajo,codigoMedico=medico
			where codigoTelefonoMedico = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarMedicoEspecialidad (in codigo int, in medico int, in especialidad int, in horario int)
	begin
		update MedicoEspecialidad set codigoMedico=medico, codigoEspecialidad=especialidad, codigoHorario=horario
			where codigoMedicoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarResponsableTurno (in codigo int, in nombre varchar(45),in telefono varchar(10),in area int,in cargo int)
	begin
		update ResponsableTurno set nombreResponsable=nombre,telefonoResponsable=telefono,codigoArea=area,codigoCargo=cargo
			where codigoResponsableTurno = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EditarTurno (in codigo int, in fechaT date,in fechaC date,in valorC decimal(10,2),in medicoE int,in responsableT int, in paciente int)
	begin
		update turno set fechaTurno=fechaT,fechaCita=fechaC,valorCita=valorC,codigoMedicoEspecialidad=medicoE,codigoResponsableTurno=responsableT,codigoPaciente=paciente
			where codigoTurno = codigo;
    end$$
delimiter ;

-- SP's eliminar

delimiter $$
create procedure sp_EliminarMedico (in codigo int)
	begin
		delete from Medicos
			where codigoMedico = codigo; 
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarPacientes(in codigo int)
	begin
		delete from pacientes 
            where codigoPaciente = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarContactoUrgencia(in codigo int)
	begin
		delete from contactourgencia
            where codigoContacto = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarArea (in codigo int)
	begin
		delete from Areas
            where codigoArea = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarCargo (in codigo int)
	begin
		delete from cargos
            where codigoCargo = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarHorario(in codigo int)
	begin
		delete from horarios
            where codigoHorario = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarEspecialidad(in codigo int)
	begin
		delete from especialidad
            where codigoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarTelefonoMedico (in codigo int)
	begin
		delete from telefonomedicos
            where codigoTelefonoMedico = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarMedicoEspecialidad (in codigo int)
	begin
		delete from MedicoEspecialidad
            where codigoMedicoEspecialidad = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarResponsableTurno (in codigo int)
	begin
		delete from ResponsableTurno
            where codigoResponsableTurno = codigo;
    end$$
delimiter ;

delimiter $$
create procedure sp_EliminarTurno (in codigo int)
	begin
		delete from turno
			where codigoTurno = codigo;
    end$$
delimiter ;
-- trigget
delimiter $$
create trigger tr_Pacientes
	before insert
    on pacientes for each row
    begin
		set new.edad = timestampdiff(year,new.fechaDeNacimiento,now());
    end$$
delimiter ;

delimiter $$
create trigger tr_Medicos
	before delete
    on medicos for each row
    begin
		delete from telefonomedicos 
			where telefonomedicos.codigoMedico = old.codigoMedico;
    end$$
delimiter ;

delimiter $$
create trigger tr_Pacientes_BeforeDelete
	before delete
    on pacientes for each row
    begin
		delete from contactoUrgencia
			where contactoUrgencia.codigoPaciente = old.codigoPaciente;
    end$$
delimiter ;

delimiter $$
create trigger tr_MedicoEspecialidad_BeforeInsert
	before insert
    on MedicoEspecialidad for each row
    begin
		declare turnos int;
		select horarios.lunes+horarios.martes+horarios.miercoles+horarios.jueves+horarios.viernes
				into turnos from horarios where horarios.codigoHorario = new.codigoHorario;
		update medicos set TurnoMaximo = turnos 
			where medicos.codigoMedico = new.codigoMedico;
    end$$
delimiter ;
/*
alter table contactourgencia change column nombres nombre varchar(100);

alter table contactourgencia change column apellidos apellido varchar(100);*/