package dam.primero.modelos;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String clave;

    public Usuario() {}

    public Usuario(int id, String nombreUsuario, String clave) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombreUsuario=" + nombreUsuario + "]";
    }
}

