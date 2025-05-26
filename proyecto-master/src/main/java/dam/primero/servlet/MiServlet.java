package dam.primero.servlet;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dam.primero.dao.BibliotecaDao;
import dam.primero.modelos.Libro;
import dam.primero.modelos.Prestamos;
import dam.primero.modelos.Usuario;

public class MiServlet extends HttpServlet {

    private static final long serialVersionUID = 2051990309999713971L;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        IServletWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        response.setContentType("text/html;charset=UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.trim().isEmpty() || pathInfo.equals("/")) {
            templateEngine.process("login", context, response.getWriter());
            return;
        }

        String accion = pathInfo.substring(1);
        if (accion.endsWith(".html")) {
            accion = accion.substring(0, accion.length() - 5);
        }

        switch (accion) {
            case "index":
            case "login":
            case "menu":
            case "registrar":
            case "registro":
            case "devolucion":
            case "historial":
                templateEngine.process(accion, context, response.getWriter());
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + pathInfo);
        }
    }

    private boolean validaUsuarioYClave(HttpServletRequest request) {
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");
        boolean correcto = false;

        try {
            BibliotecaDao dao = new BibliotecaDao();
            correcto = dao.iniciarSesion(usuario, clave);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return correcto;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        IServletWebExchange webExchange = application.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "";

        try {
            BibliotecaDao dao = new BibliotecaDao();

            switch (pathInfo) {
                case "/validaUsuario": {
                    boolean correcto = validaUsuarioYClave(request);
                    if (correcto) {
                        HttpSession session = request.getSession();
                        session.setAttribute("usuarioLogueado", request.getParameter("usuario"));
                        response.sendRedirect(request.getContextPath() + "/app/menu");
                    } else {
                        context.setVariable("error", "Usuario o clave incorrectos");
                        templateEngine.process("login", context, response.getWriter());
                    }
                    break;
                }

                case "/filtrarHistorial": {
                    HttpSession session = request.getSession(false);
                    if (session == null || session.getAttribute("usuarioLogueado") == null) {
                        response.sendRedirect(request.getContextPath() + "/app/login");
                        return;
                    }

                    String nombreUsuario = (String) session.getAttribute("usuarioLogueado");
                    String filtroTitulo = request.getParameter("filtroTitulo"); // o el nombre que uses en el form

                    try {
                        int idUsuario = dao.obtenerIdUsuarioPorNombre(nombreUsuario);
                        List<Prestamos> prestamosFiltrados = dao.obtenerPrestamosFiltrados(idUsuario, filtroTitulo);

                        context.setVariable("prestamos", prestamosFiltrados);
                        templateEngine.process("historial", context, response.getWriter());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        context.setVariable("error", "Error al filtrar el historial.");
                        templateEngine.process("historial", context, response.getWriter());
                    }
                    break;
                }



                case "/registroUsuario": {
                    String nombreUsuario = request.getParameter("usuario");
                    String clave = request.getParameter("password");

                    Usuario usuario = new Usuario();
                    usuario.setNombreUsuario(nombreUsuario);
                    usuario.setClave(clave);

                    try {
                        boolean registrado = dao.altaUsuario(usuario);
                        if (registrado) {
                            response.sendRedirect(request.getContextPath() + "/app/login");
                        } else {
                            response.sendRedirect(
                                    request.getContextPath() + "/app/registro?error=No se pudo registrar el usuario");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        response.sendRedirect(
                                request.getContextPath() + "/app/registro?error=Error interno en el servidor");
                    }
                    break;
                }

                case "/registrarPrestamo": {
                    String prestamoTitulo = request.getParameter("titulo");
                    String prestamoAutor = request.getParameter("autor");
                    String fechaStr = request.getParameter("fecha");

                    HttpSession sesionPrestamo = request.getSession(false);
                    if (sesionPrestamo == null || sesionPrestamo.getAttribute("usuarioLogueado") == null) {
                        response.sendRedirect(request.getContextPath() + "/app/login");
                        return;
                    }

                    String nombreUsuarioPrestamo = (String) sesionPrestamo.getAttribute("usuarioLogueado");
                    int idUsuarioPrestamo = dao.obtenerIdUsuarioPorNombre(nombreUsuarioPrestamo);

                    Usuario usuarioPrestamo = new Usuario();
                    usuarioPrestamo.setId(idUsuarioPrestamo);

                    Libro libroPrestamo = dao.obtenerLibroPorTituloYAutor(prestamoTitulo, prestamoAutor);
                    if (libroPrestamo == null) {
                        context.setVariable("error", "No se encontró un libro con ese título y autor.");
                        templateEngine.process("registrar", context, response.getWriter());
                        return;
                    }

                    Prestamos prestamo = new Prestamos();
                    prestamo.setLibro(libroPrestamo);
                    prestamo.setUsuario(usuarioPrestamo);

                    if (fechaStr == null || fechaStr.isEmpty()) {
                        context.setVariable("error", "La fecha del préstamo no puede estar vacía.");
                        templateEngine.process("registrar", context, response.getWriter());
                        return;
                    }

                    LocalDate fechaPrestamo = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_DATE);
                    prestamo.setFechaPrestamo(fechaPrestamo);

                    if (dao.estaDisponible(libroPrestamo.getId_libro())) {
                        boolean prestado = dao.registrarPrestamo(prestamo);
                        if (prestado) {
                            response.sendRedirect(request.getContextPath() + "/app/menu");
                        } else {
                            context.setVariable("error", "No se pudo registrar el préstamo.");
                            templateEngine.process("registrar", context, response.getWriter());
                        }
                    } else {
                        context.setVariable("error", "El libro no está disponible para préstamo.");
                        templateEngine.process("registrar", context, response.getWriter());
                    }
                    break;
                }

                case "/devolverLibro": {
                    String devolucionTitulo = request.getParameter("titulo");
                    String devolucionAutor = request.getParameter("autor");
                    String fechaDevolucionStr = request.getParameter("fecha");

                    HttpSession sesionDevolucion = request.getSession(false);
                    if (sesionDevolucion == null || sesionDevolucion.getAttribute("usuarioLogueado") == null) {
                        response.sendRedirect(request.getContextPath() + "/app/login");
                        return;
                    }

                    if (devolucionTitulo == null || devolucionTitulo.isEmpty() || devolucionAutor == null
                            || devolucionAutor.isEmpty() || fechaDevolucionStr == null
                            || fechaDevolucionStr.isEmpty()) {
                        context.setVariable("error", "Debe completar todos los campos: título, autor y fecha.");
                        templateEngine.process("devolucion", context, response.getWriter());
                        return;
                    }

                    String nombreUsuarioDevolucion = (String) sesionDevolucion.getAttribute("usuarioLogueado");
                    int idUsuarioDevolucion = dao.obtenerIdUsuarioPorNombre(nombreUsuarioDevolucion);

                    Libro libroDevolucion = dao.obtenerLibroPorTituloYAutor(devolucionTitulo, devolucionAutor);
                    if (libroDevolucion == null) {
                        context.setVariable("error", "No se encontró un libro con ese título y autor.");
                        templateEngine.process("devolucion", context, response.getWriter());
                        return;
                    }

                    LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionStr, DateTimeFormatter.ISO_DATE);

                    try {
                        int idPrestamo = dao.obtenerIdPrestamoActivo(libroDevolucion.getId_libro(), idUsuarioDevolucion);
                        boolean devuelto = dao.devolverLibro(idPrestamo, java.sql.Date.valueOf(fechaDevolucion));
                        if (devuelto) {
                            response.sendRedirect(request.getContextPath() + "/app/menu");
                        } else {
                            context.setVariable("error", "No se pudo registrar la devolución.");
                            templateEngine.process("devolucion", context, response.getWriter());
                        }
                    } catch (SQLException e) {
                        context.setVariable("error", "No hay préstamo activo para ese libro y usuario.");
                        templateEngine.process("devolucion", context, response.getWriter());
                    }
                    break;
                }

                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida: " + pathInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor.");
        }

    }

}

