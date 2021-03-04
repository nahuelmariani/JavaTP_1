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
		case "agregarATodos": //Botón GENERAR A TODOS de generarCuotas.jsp
			this.agregarATodos(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "agregarASocio": //Botón GENERAR A SOCIO de generarCuotas.jsp
			//this.setearImporte(request,response);//metodo para guardar el importe de la cuota que se recibe del jsp
			request.getRequestDispatcher("/WEB-INF/genCuoSocio.jsp").forward(request, response);	
			break;
		case "generarCuotas": //Botón GENERAR CUOTAS de gestionCuotas.jsp
			request.getRequestDispatcher("/WEB-INF/generarCuota.jsp").forward(request, response);	
			break;
		case "generarCuotaSocio": //Botón ACEPTAR de genCuoSocio.jsp
			this.generarASocio(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "gestionCuota":
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "homeUser": //Botón VOLVER de gestionCuotas.jsp
			request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);	
			break;
		case "nuevoValorCuota": //Botón ACTUALIZAR PRECIO CUOTAS de gestionCuotas.jsp
			this.listarValoresCuotas(request, response);
			request.getRequestDispatcher("/WEB-INF/nuevoValorCuota.jsp").forward(request, response);	
			break;
		case "agregarNuevoValorCuota":
			try {
				this.nuevoValorCuota(request,response);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			request.getRequestDispatcher("/WEB-INF/gestionCuota.jsp").forward(request, response);	
			break;
		case "gestionCobro":
		//	this.listarSocios(request,response);
			request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);	
			break;
		case "nuevoCobro":
			this.confirmarCobro(request,response);
			//request.getRequestDispatcher("/WEB-INF/confirmarCobro.jsp").forward(request, response);
			break;
		case "registrarCobro":
			this.registrarCobro(request, response);
			//request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);
			break;
		//REQUERIMIENTO: ESTADO CUOTAS
		case "estadoCuotas":
			this.estadoCuotasSocio(request,response);
			request.getRequestDispatcher("/WEB-INF/estadoCuotas.jsp").forward(request, response);
			break;
		case "estadoCuotasSocio":
			this.estadoCuotasSocio(request,response);
			request.getRequestDispatcher("/WEB-INF/listadoEstadoCuotas.jsp").forward(request, response);
			break;
		case "estadoCuotasTodos":
			request.getRequestDispatcher("/WEB-INF/listadoEstadoCuotas.jsp").forward(request, response);
			break;

		default:
			System.out.println("Error: opcion no disponible");
			break;
		}
	}
	
	
	
	private void nuevoValorCuota(HttpServletRequest request, HttpServletResponse response)  throws IOException, ServletException, ParseException{
		CuotaControler cuotaCtrl = new CuotaControler();
		Valores_Cuota vc = new Valores_Cuota();
		
		

			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Date fecha = sdf.parse(request.getParameter("fecha_desde"));
			Double valor = Double.parseDouble(request.getParameter("valor"));
			
			vc.setFecha(fecha);
			vc.setValor(valor);
			
			cuotaCtrl.agregarValorCuota(vc);
			
			this.listarValoresCuotas(request, response);
		
		
	}
	
	private void listarValoresCuotas(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		CuotaControler cuotaCtrl = new CuotaControler();
		ArrayList<Valores_Cuota> vcs = new ArrayList<>();
		
		vcs = cuotaCtrl.getAll();
		request.getSession().setAttribute("listaValoresCuotas", vcs);
	}

	/*
	private void setearImporte(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Cuota c = new Cuota();
		
		c.setImporte(Double.parseDouble(request.getParameter("importe")));
		request.getSession().setAttribute("Cuota", c);	
	}*/

	private void generarASocio(HttpServletRequest request, HttpServletResponse response) {
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
		soc=perCtrl.buscarPersonaPorDNI(soc);
				
		//recupero cuota de la sesi�n con el importe seteado
		//Cuota cuota = (Cuota) request.getSession().getAttribute("Cuota");	
				
		//Seteo socio a la cuota
		//cuota.setP(soc);
		c.setPer(soc);
		c.setImporte(cuotaCtrl.valorCuota());
		
		Calendar c1 = Calendar.getInstance();
		System.out.println(c1.get(Calendar.YEAR));
		int anio = c1.get(Calendar.YEAR);
		int mes = c1.get(Calendar.MONTH);
	
		// el mes enero lo toma como cero por lo tanto todos van a ser un mes mes por eso pongo mes+1
		for(int i = mes+1; i < 13; i++) {
			c.setMes(i);
			c.setAnio(anio);
		    cuotaCtrl.agregarCuota(c);
		}
		
		
	}

	private void confirmarCobro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PersonaControler perCtrl = new PersonaControler();
		InscripcionControler insCtrl = new InscripcionControler();
		CuotaControler cuoCtrl = new CuotaControler();
		Persona s = new Persona();
		Documento d = new Documento();
		Cuota c = new Cuota();
		ArrayList<Actividad> actividades = new ArrayList<Actividad>();
		
		int mes = Integer.parseInt(request.getParameter("mes"));
		int anio = Integer.parseInt(request.getParameter("anio"));
		d.setNro(request.getParameter("dni"));
		d.setTipo(request.getParameter("tipoDNI"));
				
		try {
			s.setDocumento(d);
			s = perCtrl.buscarPersonaPorDNI(s);
			if (s==null) {
				throw new Exception("No existe el socio."); 
			}
			request.getSession().setAttribute("socio", s);
			try {
				c.setMes(mes);
				c.setAnio(anio);
				c = cuoCtrl.buscarCuota(c,s);
				if (c==null) {
					throw new Exception("No existe la cuota."); 
				}
				c.setImporte(cuoCtrl.valorCuota());
				request.getSession().setAttribute("cuota", c);

				actividades = insCtrl.getAll(s.getId());
				double importe = insCtrl.costoAct(actividades) + c.getImporte();
				request.getSession().setAttribute("importe", importe);
				request.getSession().setAttribute("actividades", actividades);
				request.getRequestDispatcher("/WEB-INF/confirmarCobro.jsp").forward(request, response);
			} catch (Exception e) {
				System.out.println("No se puede cobrar la cuota " + mes + "/" + anio + " para el socio "+ d.getTipo() + " " + d.getNro() + ".");
				request.getSession().setAttribute("message","No se puede cobrar la cuota " + mes + "/" + anio + " para el socio "+ d.getTipo() + " " + d.getNro() + ".");
				request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);
			}
		} catch (Exception e) {
			System.out.println("No existe el socio "+ d.getTipo() + " " + d.getNro());
			request.getSession().setAttribute("message", "No existe el socio "+ d.getTipo() + " " + d.getNro());
			request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);
		}

	}

	private void registrarCobro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CuotaControler cuotaCtrl = new CuotaControler();
		Cuota c = (Cuota) request.getSession().getAttribute("cuota");
		Persona s = (Persona) request.getSession().getAttribute("socio");
		try {
			cuotaCtrl.cobrar(c,s);
			System.out.println("Cobro realizado exitosamente");
			request.getSession().setAttribute("message", "Cobro realizado exitosamente");
			request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);
		} catch (Exception e) {
			System.out.println("No se realizó el cobro. Ocurrió un error inesperado");
			request.getSession().setAttribute("message", "No se realizó el cobro. Ocurrió un error inesperado");
			request.getRequestDispatcher("/WEB-INF/gestionCobro.jsp").forward(request, response);
		}
		
		
		
		/*CuotaControler cuotaCtrl = new CuotaControler();
		int existe;
		
		Cuota cuota = (Cuota) request.getSession().getAttribute("cuota");
		Persona soc = (Persona) request.getSession().getAttribute("socio");
		
		existe = cuotaCtrl.cobrar(cuota, soc);
		if (existe == 0) {
			request.getRequestDispatcher("/WEB-INF/homeUser.jsp").forward(request, response);
		}
		else {
			request.getRequestDispatcher("/WEB-INF/cuotaPaga.jsp").forward(request, response);
		}*/
	}

	private void estadoCuotasSocio(HttpServletRequest request, HttpServletResponse response) {
		if (request.getParameter("anio")!=null) {
			CuotaControler cuoCtrl = new CuotaControler();
			PersonaControler perCtrl = new PersonaControler();
			ArrayList<Cuota> cuotas = new ArrayList<Cuota>();
			ArrayList<Persona> socios = new ArrayList<Persona>();
			ArrayList<String> cuo;
			ArrayList<ArrayList<String>> tabla = new ArrayList<ArrayList<String>>();
			int anio = Integer.parseInt((request.getParameter("anio")));
			if (request.getParameterValues("todos")!=null) {
				socios = perCtrl.getSocios();
				for (Persona s : socios) {
					cuotas = cuoCtrl.getByAnioPer(anio,s);
					cuo = cuoCtrl.obtenerCuotas(cuotas,s);
					tabla.add(cuo);
				}
			} else {
				Persona p = new Persona();
				Documento d = new Documento();
				d.setTipo(request.getParameter("tipoDNI"));
				d.setNro(request.getParameter("dni"));
				p.setDocumento(d);
				try {
					p=perCtrl.buscarPersonaPorDNI(p);
					cuotas = cuoCtrl.getByAnioPer(anio,p);
					cuo = cuoCtrl.obtenerCuotas(cuotas,p);
					tabla.add(cuo);
				} catch (Exception e) {
					System.out.println("No existe el socio "+ d.getTipo() + " " + d.getNro());
					request.getSession().setAttribute("message", "No existe el socio "+ d.getTipo() + " " + d.getNro());
				}

			}
			if (!tabla.isEmpty()) {
				System.out.println("Cuotas para el año " + anio);
				request.getSession().setAttribute("cuotasPorAnio", tabla);
				request.getSession().setAttribute("anio2", String.valueOf(anio));
			}
		}
	}

	private void listarSocios(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		PersonaControler perCtrl = new PersonaControler();
		ArrayList<Persona> socios = new ArrayList<Persona>();
	
		socios = perCtrl.getSocios();
		request.getSession().setAttribute("listaSocios", socios);		
		
	}
	private void agregarATodos(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
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
				c.setPer(per);
				c.setAnio(anio);
				c.setImporte(cuotaCtrl.valorCuota()); //En mi código lo tenía comentado
				cuotaCtrl.agregarCuota(c);
			}
		}
	}
}