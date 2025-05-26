package dam.primero.modelos;

import java.time.LocalDate;
import java.util.Objects;

public class Prestamos {
    private int id_prestamo;
    private Usuario usuario;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechadevolucion;

    public Prestamos() {}

    public Prestamos(int id_prestamo, Usuario usuario, Libro libro, LocalDate fechaPrestamo, LocalDate fechadevolucion) {
        this.id_prestamo = id_prestamo;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechadevolucion = fechadevolucion;
    }

    public int getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(int id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechadevolucion() {
        return fechadevolucion;
    }

    public void setFechadevolucion(LocalDate fechadevolucion) {
        this.fechadevolucion = fechadevolucion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_prestamo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prestamos other = (Prestamos) obj;
        return id_prestamo == other.id_prestamo;
    }

    @Override
    public String toString() {
        return "Prestamo [id_prestamo=" + id_prestamo + ", usuario=" + usuario +
                ", libro=" + libro + ", fechaPrestamo=" + fechaPrestamo +
                ", fechadevolucion=" + fechadevolucion + "]";
    }
}
