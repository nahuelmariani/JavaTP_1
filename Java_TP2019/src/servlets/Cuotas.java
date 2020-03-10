package servlets;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import entities.*;
import logic.*;
import java.util.Date;

@WebServlet("/Cuotas")
public class Cuotas extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Cuotas() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		switch (request.getParameter("action")) {
		case "agregar":
			this.agregar(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "agregarASoc":
			this.setearImporte(request,response);//metodo para guardar el importe de la cuota que se recibe del jsp
			request.getRequestDispatcher("/WEB-INF/genCuoSocio.jsp").forward(request, response);	
			break;
		case "nuevaCuota":
			request.getRequestDispatcher("/WEB-INF/generarCuota.jsp").forward(request, response);	
			break;
		case "genCuotaSoc":
			this.generarASoc(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "gestionCuota":
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "homeUser":
			request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);	
			break;
		case "gestionCobro":
		//	this.listarSocios(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);	
			break;
		case "nuevoCobro":
			this.confirmarCobro(request,response);
			request.getRequestDispatcher("/WEB-INF/confirmarCobro.jsp").forward(request, response);
			break;
		case "registrarCobro":
			this.registrarCobro(request, response);
			//request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);
			break;
		
		default:
			System.out.println("Error: opcion no disponible");
			break;
		}
	}
	
	
	

	private void setearImporte(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Cuota c = new Cuota();
		
		c.setImporte(Double.parseDouble(request.getParameter("importe")));
		request.getSession().setAttribute("Cuota", c);	
	}

	private void generarASoc(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Cuota c = new Cuota();
		CuotaControler cuotaCtrl = new CuotaControler();
		PersonaControler perCtrl = new PersonaControler();
		Persona soc = new Persona();
		Documento doc = new Documento();
		
		// recupero tipo y nro de doc y obtengo el socio. Lo seteo en la sesion
		doc.setNro(request.getParameter("dni"));
		doc.setTipo(request.getParameter("tipoDNI"));
		
				
		soc.setDocumento(doc);
		soc = perCtrl.buscarPersonaPorDNI(soc);
				
		//recupero cuota de la sesi�n con el importe seteado
		Cuota cuota = (Cuota) request.getSession().getAttribute("Cuota");	
		
		//Seteo socio a la cuota
		cuota.setP(soc);
		
		Calendar c1 = Calendar.getInstance();
		System.out.println(c1.get(Calendar.YEAR));
		int anio = c1.get(Calendar.YEAR);
		int mes = c1.get(Calendar.MONTH);
	
		// el mes enero lo toma como cero por lo tanto todos van a ser un mes mes por eso pongo mes+1
		for(int i = mes+1; i < 13; i++) {
			cuota.setMes(i);
			cuota.setAnio(anio);
		    cuotaCtrl.agregar(cuota);
		}
		
		
	}

	private void confirmarCobro(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		PersonaControler perCtrl = new PersonaControler();
		Persona soc = new Persona();
		Documento doc = new Documento();
		Cuota cuota = new Cuota();
		
		// recupero tipo y nro de doc y obtengo el socio. Lo seteo en la sesion
		doc.setNro(request.getParameter("dni"));
		doc.setTipo(request.getParameter("tipoDNI"));
		
		soc.setDocumento(doc);
		soc = perCtrl.buscarPersonaPorDNI(soc);
		request.getSession().setAttribute("Soc", soc);
		
		// recupero el mes ingresado, lo seteo en cuota 
		int mes = Integer.parseInt(request.getParameter("mes"));
		cuota.setMes(mes);

		// recupero el anio ingresado, lo seteo en cuota  
		int anio = Integer.parseInt(request.getParameter("anio"));
		cuota.setAnio(anio);
		//guardo la cuota en la sesion
		request.getSession().setAttribute("Cuota", cuota);

	}

	private void registrarCobro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		CuotaControler cuotaCtrl = new CuotaControler();
		int existe;
		// recupero cuota de la sesion para mandarsela al controlador
		Cuota cuota = (Cuota) request.getSession().getAttribute("Cuota");
			
		// recupero socio de la sesion para actualizar su cuota
		Persona soc = (Persona) request.getSession().getAttribute("Soc");
		
		existe = cuotaCtrl.cobrar(cuota, soc);
		if (existe == 0) {
			request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);
		}
		else {
			request.getRequestDispatcher("/WEB-INF/cuotaPaga.jsp").forward(request, response);
		}
	}

	

	private void listarSocios(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		PersonaControler perCtrl = new PersonaControler();
		ArrayList<Persona> socios = new ArrayList<Persona>();
	
		socios = perCtrl.getSocios();
		request.getSession().setAttribute("listaSocios", socios);		
		
	}
	private void agregar(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		Cuota c = new Cuota();
		CuotaControler cuotaCtrl = new CuotaControler();
		PersonaControler perCtrl = new PersonaControler();
		ArrayList<Persona> usuarios = new ArrayList<>();
		
		usuarios = perCtrl.getSocios();
		Calendar c1 = Calendar.getInstance();
		System.out.println(c1.get(Calendar.YEAR));
		int anio = c1.get(Calendar.YEAR);
		for (Persona per: usuarios) {
			for(int i = 1; i < 13; i++) {
				c.setMes(i);
				c.setP(per);
				c.setAnio(anio);
				c.setImporte(Double.parseDouble(request.getParameter("importe")));
				cuotaCtrl.agregar(c);
			}
		}
		
		
		
	}
}