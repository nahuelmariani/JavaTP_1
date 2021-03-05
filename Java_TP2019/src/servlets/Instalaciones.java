package servlets;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import entities.*;
import logic.*;
import java.util.Date;
import java.io.File;
import java.io.FileNotFoundException;

import static logic.Constants.UPLOAD_DIRECTORY;

@WebServlet("/Instalaciones")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class Instalaciones extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Instalaciones() {
        super();
    }
    
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename"))
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
        return Constants.DEFAULT_FILENAME;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		switch (request.getParameter("action")) {
		case "agregar":
			this.agregar(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionInstalacion.jsp").forward(request, response);	
			break;
		case "actualizar":
			this.actualizar(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionInstalacion.jsp").forward(request, response);	
			break;
		case "eliminar":
			this.eliminar(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionInstalacion.jsp").forward(request, response);	
			break;
		case "gestionInstalacion":
			this.listar(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionInstalacion.jsp").forward(request, response);	
			break;
		case "reservaInstalacion":
			this.listar(request,response);
			request.getRequestDispatcher("/WEB-INF/reservaInstalacion.jsp").forward(request, response);
			break;
		case "misReservas":
			this.listarResUsuario(request,response);
			request.getRequestDispatcher("/WEB-INF/listadoReservas.jsp").forward(request, response);
			break;
		case "obtenerReservas":
			this.listarResTodas(request,response);
			request.getRequestDispatcher("/WEB-INF/listadoTotalReservas.jsp").forward(request, response);
			break;
		case "nuevaReserva":
			this.buscarPorId(request, response);
			this.validarCuota(request,response);
			break;
		case "filtrarInstalacion":
			this.filtrarInstalacion(request, response);
			request.getRequestDispatcher("/WEB-INF/listadoTotalReservas.jsp").forward(request, response);
			break;
		case "nuevaInstalacion":
			request.getRequestDispatcher("/WEB-INF/nuevaInstalacion.jsp").forward(request, response);
			break;
		case "confirmarReserva":
			request.getRequestDispatcher("/WEB-INF/confirmarReserva.jsp").forward(request, response);
			break;
		case "modificarInstalacion":
			this.buscarPorId(request,response);
			request.getRequestDispatcher("/WEB-INF/modificarInstalacion.jsp").forward(request, response);
			break;
		case "cancelarReserva":
			this.cancelarReserva(request,response);
			
			break;
		case "preReservar":
			try {
				this.preReservar(request,response);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			break;
		case "reservar":
			this.reservar(request,response);
			request.getRequestDispatcher("/WEB-INF/reservaInstalacion.jsp").forward(request, response);
			break;
		case "borrarPreReserva":
			this.borrarPreReserva(request, response);
			request.getRequestDispatcher("/WEB-INF/reservaInstalacion.jsp").forward(request, response);
			break;
		case "homeUser":
			request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);
			break;
		default:
			System.out.println("Error: opcion no disponible");
			break;
		}
	}
	
	private void validarCuota(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Persona p = new Persona();
		CuotaControler cuoCtrl = new CuotaControler();
		
		//recupero el usuario del login 
		p = (Persona) request.getSession().getAttribute("usuario");
		int debe = cuoCtrl.validarCuota(p);
		
		if (debe == 1) {
			request.getRequestDispatcher("/WEB-INF/nuevaReserva.jsp").forward(request, response);
		} else 
		{
			request.getSession().setAttribute("message", "Para poder reservar debe estar al dia con la cuota");
			this.listar(request, response);
			request.getRequestDispatcher("/WEB-INF/reservaInstalacion.jsp").forward(request, response);
		}
		
			
	}

	private void filtrarInstalacion(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		ReservaControler resCtrl = new ReservaControler();
		ArrayList<Reserva> reservas = new ArrayList<Reserva>();
		
		
		
		reservas = resCtrl.getByInstalacion(Integer.parseInt((request.getParameter("instalacion"))));
		if (reservas.isEmpty()) {
			System.out.println("no hay reservas");
			request.getSession().setAttribute("message", "No hay reservas realizadas");
			request.getSession().setAttribute("totalReservas", null);
		} else {
			System.out.println("hay reservas");
			request.getSession().setAttribute("totalReservas", reservas);
			
		}
		
	}

	private void listarResTodas(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		ReservaControler resCtrl = new ReservaControler();
		InstalacionControler ic = new InstalacionControler();
		ArrayList<Reserva> reservas = new ArrayList<Reserva>();
		ArrayList<Instalacion> instalaciones = new ArrayList<Instalacion>();
		
		
		reservas = resCtrl.getAll();
		//uso empty porque si uso null no anda porque esta en null yo necesito saber si no tiene nada
		if (reservas.isEmpty()) {
			System.out.println("no hay reservas");
			request.getSession().setAttribute("message", "No hay reservas realizadas");
		} else {
			System.out.println("hay reservas");
			instalaciones = ic.getAll();
			request.getSession().setAttribute("listaInstalaciones", instalaciones);
			request.getSession().setAttribute("totalReservas", reservas);
			
		}
	
	}

	private void borrarPreReserva(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Reserva r = new Reserva(); 
		ReservaControler rc = new ReservaControler();
		
		r = (Reserva) request.getSession().getAttribute("reserva");
			
		rc.bajaPreReserva(r);

		this.listar(request, response);
	}

	//INSTALACIONES
	
	private void agregar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Instalacion i = new Instalacion();
		InstalacionControler instCtrl = new InstalacionControler();

		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY; // Ruta para guardar el archivo en la carpeta "upload_directory", declarada en Constant.java
		File uploadDir = new File(uploadPath);
        
		if (!uploadDir.exists()) { // Si no existe la carpeta, la crea
            uploadDir.mkdir();
        }
        
        try {
        	Part filePart = request.getPart("imagen");
        	String fileName = getFileName(filePart);
        	filePart.write(uploadPath + File.separator + fileName);
        	i.setImagen(fileName);
            //request.setAttribute("message", "File " + fileName + " has uploaded successfully!");
        } catch (FileNotFoundException fne) {
            //request.setAttribute("message", "There was an error: " + fne.getMessage());
        }

		i.setNom_instalacion(request.getParameter("nom_instalacion"));
		i.setDesc_instalacion(request.getParameter("desc_instalacion"));
		i.setImporte(Double.parseDouble(request.getParameter("importe")));
		
		instCtrl.altaInstalacion(i);
		
		this.listar(request, response);
	}
	
	// WORKSPACE\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\UploadServletApp\ upload
	
	private void buscarPorId(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Instalacion i = new Instalacion();
		InstalacionControler instCtrl = new InstalacionControler();
		int idInstalacion = Integer.parseInt(request.getParameter("idInstalacion"));
		
		i = instCtrl.buscarInstalacionPorId(idInstalacion);
		request.getSession().setAttribute("instalacion", i);
	}
	
	private void listar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		InstalacionControler instCtrl = new InstalacionControler();
		ArrayList<Instalacion> instalaciones = new ArrayList<Instalacion>();
		
		instalaciones = instCtrl.getAll();
		request.getSession().setAttribute("listaInstalaciones", instalaciones);
	}
	
	private void actualizar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Instalacion i = new Instalacion();
		InstalacionControler instCtrl = new InstalacionControler();
		
		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY; // Ruta para guardar el archivo en la carpeta "upload_directory", declarada en Constant.java
		File uploadDir = new File(uploadPath);
        
		if (!uploadDir.exists()) { // Si no existe la carpeta, la crea
            uploadDir.mkdir();
        }
        
        try {
        	Part filePart = request.getPart("imagen");
        	String fileName = getFileName(filePart);
        	filePart.write(uploadPath + File.separator + fileName);
        	i.setImagen(fileName);
            //request.setAttribute("message", "File " + fileName + " has uploaded successfully!");
        } catch (FileNotFoundException fne) {
            //request.setAttribute("message", "There was an error: " + fne.getMessage());
        }
		
		int idInstalacion = Integer.parseInt(request.getParameter("idInstalacion"));
		i.setNom_instalacion(request.getParameter("nom_instalacion"));
		i.setDesc_instalacion(request.getParameter("desc_instalacion"));
		i.setImporte(Double.parseDouble(request.getParameter("importe")));
		
		instCtrl.modificarInstalacion(i, idInstalacion);
		
		this.listar(request, response);
	}
	
	private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		int idInstalacion = Integer.parseInt(request.getParameter("idInstalacion"));
		InstalacionControler instCtrl = new InstalacionControler();
		
		instCtrl.bajaInstalacion(idInstalacion);
		this.listar(request, response);
	}
	
	//RESERVAS
	
	private void preReservar (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ParseException{
		Reserva r = new Reserva();
		Instalacion i = new Instalacion();
		ReservaControler rc = new ReservaControler();
		Persona p = new Persona();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");


		Date fecha_hora_desde = sdf.parse(request.getParameter("fecha_hora_desde"));
		r.setFecha_hora_desde(fecha_hora_desde);

		Date fecha_hora_hasta = sdf.parse(request.getParameter("fecha_hora_hasta"));
		r.setFecha_hora_hasta(fecha_hora_hasta);
		
		System.out.println("Fecha: " + fecha_hora_desde);
		
		i = (Instalacion) request.getSession().getAttribute("instalacion");
		r.setInst(i);
		
		if (rc.validarDisp(r)) {
			
			p = (Persona) request.getSession().getAttribute("usuario");
			r.setPer(p);
					
			r.setConfirmada(false);
			
			request.getSession().setAttribute("reserva", r);
			//guardo la reserva con confirmada en false para guardar el lugar 
			rc.altaPreReserva(r);
			
			request.getRequestDispatcher("/WEB-INF/confirmarReserva.jsp").forward(request, response);
			
		} else
		{
			request.getSession().setAttribute("message", "No hay disponibilidad para las fechas solicitadas");
			this.listar(request, response);
			request.getRequestDispatcher("/WEB-INF/reservaInstalacion.jsp").forward(request, response);
		}
		
		
		//El alta a la reserva lo hacemos en otro paso
		//resCtrl.altaReserva(r);
		//this.listar(request, response);
	}
	
	private void reservar (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Reserva r = new Reserva();
		ReservaControler resCtrl = new ReservaControler();
		
		r = (Reserva) request.getSession().getAttribute("reserva");
		
		resCtrl.confirmarReserva(r);
		this.listar(request, response);
	} 
	
	private void listarResUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		ReservaControler resCtrl = new ReservaControler();
		ArrayList<Reserva> reservas = new ArrayList<Reserva>();
		Persona p = new Persona();
		p = (Persona) request.getSession().getAttribute("usuario");
		
		reservas = resCtrl.getByUsuario(p.getId());
		//uso empty porque si uso null no anda porque esta en null yo necesito saber si no tiene nada
		if (reservas.isEmpty()) {
			System.out.println("no hay reservas");
			request.getSession().setAttribute("message", "No hay reservas realizadas");
		} else {
			System.out.println("hay reservas");
			request.getSession().setAttribute("listaReservas", reservas);
			
		}
	
	}

	private void cancelarReserva(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Persona p = new Persona();
		int idReserva = Integer.parseInt(request.getParameter("idReserva"));
		ReservaControler resCtrl = new ReservaControler();
		
		resCtrl.cancelarRes(idReserva);
		
		p = (Persona) request.getSession().getAttribute("usuario");
		if (p.getRol().equals("Socio")) {
			this.listarResUsuario(request,response);
			request.getRequestDispatcher("/WEB-INF/listadoReservas.jsp").forward(request, response);
		} else {
			this.listarResTodas(request,response);
			request.getRequestDispatcher("/WEB-INF/listadoTotalReservas.jsp").forward(request, response);
		}
		
	}

}

