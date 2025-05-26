package dam.primero.modelos;

import java.util.Objects;
import java.util.Set;

public class Libro {
    private int id_libro;
    private String titulo;
    private String autor;
    private Estado estado;

    public Libro() {}

    public Libro(int id_libro, String titulo, String autor, Estado estado) {
        this.id_libro = id_libro;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado;
    }

    public int getId_libro() {
        return id_libro;
    }

    public void setId_libro(int id_libro) {
        this.id_libro = id_libro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_libro);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro other = (Libro) obj;
        return id_libro == other.id_libro;
    }

    @Override
    public String toString() {
        return "Libro [id_libro=" + id_libro + ", titulo=" + titulo + ", autor=" + autor + ", estado=" + estado + "]";
    }

    public boolean estaDisponible(Set<Prestamos> listaPrestamos) {
        for (Prestamos prestamo : listaPrestamos) {
            if (this.equals(prestamo.getLibro()) && prestamo.getFechadevolucion() == null) {
                return false;
            }
        }
        return true;
    }
}
