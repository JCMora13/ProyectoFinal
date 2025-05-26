	package dam.primero.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dam.primero.modelos.Libro;
import dam.primero.modelos.Prestamos;
import dam.primero.modelos.Usuario;
import dam.primero.modelos.Estado;

public class BibliotecaDao extends JdbcDao {

    public BibliotecaDao() throws Exception {
        super();
    }

    public List<Libro> obtenerLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT * FROM Libro";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId_libro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setEstado(Estado.valueOf(rs.getString("estado")));
                libros.add(libro);
            }
        }

        return libros;
    }


    public boolean registrarPrestamo(Prestamos p) throws SQLException {
        String query = "INSERT INTO Prestamo (id_libro, id_usuario, fecha_prestamo) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, p.getLibro().getId_libro());
            stmt.setInt(2, p.getUsuario().getId());
            stmt.setDate(3, java.sql.Date.valueOf(p.getFechaPrestamo()));

            int filas = stmt.executeUpdate();
            return filas > 0;
        }
    }


    public boolean devolverLibro(int idPrestamo, java.util.Date fechaDevolucion) throws SQLException {
        String query = "UPDATE Prestamo SET fecha_devolucion = ? WHERE id_prestamo = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, new java.sql.Date(fechaDevolucion.getTime()));
            stmt.setInt(2, idPrestamo);

            int filas = stmt.executeUpdate();
            return filas > 0;
        }
    }

    public List<Prestamos> obtenerHistorial() throws SQLException {
        List<Prestamos> prestamos = new ArrayList<>();
        String query = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, " +
                       "l.id_libro, l.titulo, l.autor, l.estado, " +
                       "u.id_usuario, u.usuario, u.clave " +
                       "FROM Prestamo p " +
                       "JOIN Libro l ON p.id_libro = l.id_libro " +
                       "JOIN Usuario u ON p.id_usuario = u.id_usuario";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Prestamos p = new Prestamos();
                p.setId_prestamo(rs.getInt("id_prestamo"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());

                Date fechaDev = rs.getDate("fecha_devolucion");
                p.setFechadevolucion(fechaDev != null ? fechaDev.toLocalDate() : null);

                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("usuario"));
                u.setClave(rs.getString("clave"));
                p.setUsuario(u);

                Libro libro = new Libro();
                libro.setId_libro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setEstado(Estado.valueOf(rs.getString("estado")));
                p.setLibro(libro);

                prestamos.add(p);
            }
        }

        return prestamos;
    }


    public boolean estaDisponible(int idLibro) throws SQLException {
        String query = "SELECT COUNT(*) FROM Prestamo WHERE id_libro = ? AND fecha_devolucion IS NULL";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int contador = rs.getInt(1);
                return contador == 0; 
            }
        }

        return false;
    }


    public boolean altaUsuario(Usuario usuario) throws SQLException {
    	String sql = "INSERT INTO Usuario (usuario, clave) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getClave());
            int filasInsertadas = ps.executeUpdate();
            return filasInsertadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            throw e;
        }
    }

    



    public boolean iniciarSesion(String nombreUsuario, String clave) throws SQLException {
        String query = "SELECT clave FROM Usuario WHERE usuario = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String claveBD = rs.getString("clave");
                return claveBD.equals(clave);
            }
        }

        return false;
    }
    public int obtenerIdUsuarioPorNombre(String nombreUsuario) throws SQLException {
        String query = "SELECT id_usuario FROM Usuario WHERE usuario = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_usuario");
            } else {
                throw new SQLException("Usuario no encontrado: " + nombreUsuario);
            }
        }
    }
    public Libro obtenerLibroPorTituloYAutor(String titulo, String autor) throws SQLException {
        String query = "SELECT * FROM Libro WHERE titulo = ? AND autor = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, titulo);
            stmt.setString(2, autor);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId_libro(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setEstado(Estado.valueOf(rs.getString("estado")));
                    return libro;
                } else {
                    return null;
                }
            }
        }
    }
    public int obtenerIdPrestamoActivo(int idLibro, int idUsuario) throws SQLException {
        String query = "SELECT id_prestamo FROM Prestamo WHERE id_libro = ? AND id_usuario = ? AND fecha_devolucion IS NULL";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_prestamo");
            } else {
                throw new SQLException("No hay préstamo activo para ese libro y usuario.");
            }
        }
    }
    public List<Prestamos> obtenerPrestamosPorUsuario(int idUsuario) throws SQLException {
        List<Prestamos> prestamos = new ArrayList<>();
        String query = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, " +
                       "l.id_libro, l.titulo, l.autor, l.estado " +
                       "FROM Prestamo p " +
                       "JOIN Libro l ON p.id_libro = l.id_libro " +
                       "WHERE p.id_usuario = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamos p = new Prestamos();
                    p.setId_prestamo(rs.getInt("id_prestamo"));
                    p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());

                    Date fechaDev = rs.getDate("fecha_devolucion");
                    p.setFechadevolucion(fechaDev != null ? fechaDev.toLocalDate() : null);

                    Libro libro = new Libro();
                    libro.setId_libro(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setEstado(Estado.valueOf(rs.getString("estado")));
                    p.setLibro(libro);

         
                    prestamos.add(p);
                }
            }
        }

        return prestamos;
    }
    public List<Prestamos> obtenerPrestamosFiltrados(int idUsuario, String filtroTitulo) throws SQLException {
        List<Prestamos> prestamos = new ArrayList<>();
        String query = "SELECT p.id_prestamo, p.fecha_prestamo, p.fecha_devolucion, " +
                       "l.id_libro, l.titulo, l.autor, l.estado " +
                       "FROM Prestamo p " +
                       "JOIN Libro l ON p.id_libro = l.id_libro " +
                       "WHERE p.id_usuario = ? AND l.titulo LIKE ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            stmt.setString(2, "%" + filtroTitulo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prestamos p = new Prestamos();
                    p.setId_prestamo(rs.getInt("id_prestamo"));
                    p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());

                    Date fechaDev = rs.getDate("fecha_devolucion");
                    p.setFechadevolucion(fechaDev != null ? fechaDev.toLocalDate() : null);

                    Libro libro = new Libro();
                    libro.setId_libro(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setEstado(Estado.valueOf(rs.getString("estado")));
                    p.setLibro(libro);

                    prestamos.add(p);
                }
            }
        }

        return prestamos;
    }


}
