
/*
package data;
//orig
import entities.*;

import java.sql.*;
import java.util.ArrayList;

public class DataUsuario {
	
	public ArrayList<Usuario> getAll(){
		Statement stmt=null;
		ResultSet rs=null;
		ArrayList<Usuario> users= new ArrayList<>();
		
		try {
			stmt= FactoryConexion.getInstancia().getConn().createStatement();
			rs= stmt.executeQuery("select * from usuario");
			if(rs!=null) {
				while(rs.next()) {
					Usuario u=new Usuario();
					//p.setDocumento(new Documento());
					u.setId(rs.getInt("idusuario"));
					u.setNombre(rs.getString("nombre"));
					users.add(u);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if(rs!=null) {rs.close();}
				if(stmt!=null) {stmt.close();}
				FactoryConexion.getInstancia().releaseConn();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return users;
	}

	public static Usuario login(Usuario user) {
		Usuario u=null;
		PreparedStatement stmt=null;
		ResultSet rs=null;
		String username = user.getUsername();    
		String password = user.getPassword();   
		String searchQuery = "select * from users where username=? AND password=?";

		
		//Impresión por consola para verificación
		System.out.println("Your user name is " + username);          
		System.out.println("Your password is " + password);
		System.out.println("Query: "+searchQuery);
	    
     try 
     {
		stmt=FactoryConexion.getInstancia().getConn().prepareStatement("select * from users where username=? AND password=?");
		(stmt).setString(1, user.getUsername());
		(stmt).setString(2, user.getPassword());
		rs=stmt.executeQuery();
		boolean more = (rs!=null && rs.next());
		// Si el usuario no existe, seteo variable "isValid" en false
		if (!more){
			System.out.println("Usted no está registrado");
			user.setValid(false);
		} 
		// Si el usuario existe, seteo variable "isValid" en true
		else if (more) 
		{
			u = new Usuario();
			u.setId(rs.getInt("id"));
			u.setNombre(rs.getString("nombre"));
			u.setApellido(rs.getString("apellido"));
			u.setUsername(rs.getString("username"));
			u.setValid(true);
			System.out.println("¡Bienvenido, " + rs.getString("username") + "!");
		}
    } catch (SQLException e) {
    	e.printStackTrace();
    } finally {
    	try {
    		if(rs!=null) {rs.close();}
    		if(stmt!=null) {stmt.close();}
    		FactoryConexion.getInstancia().releaseConn();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    return u;
	}	
	
	
	
	
	/*
	public Persona getByDocumento(Persona per) {
		Persona p=null;
		PreparedStatement stmt=null;
		ResultSet rs=null;
		try {
			stmt=FactoryConexion.getInstancia().getConn().prepareStatement(
					"select * from persona where tipo_doc=? and nro_doc=?"
					);
			stmt.setString(1, per.getDocumento().getTipo());
			stmt.setString(2, per.getDocumento().getNro());
			rs=stmt.executeQuery();
			if(rs!=null && rs.next()) {
				p=new Persona();
				p.setDocumento(new Documento());
				p.setId(rs.getInt("id"));
				p.setNombre(rs.getString("nombre"));
				p.setApellido(rs.getString("apellido"));
				p.getDocumento().setTipo(rs.getString("tipo_doc"));
				p.getDocumento().setNro(rs.getString("nro_doc"));
				p.setEmail(rs.getString("email"));
				p.setTel(rs.getString("tel"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs!=null) {rs.close();}
				if(stmt!=null) {stmt.close();}
				FactoryConexion.getInstancia().releaseConn();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return p;
	}
	
	public void add(Persona p) {
		PreparedStatement stmt= null;
		ResultSet keyResultSet=null;
		try {
			stmt=FactoryConexion.getInstancia().getConn().
					prepareStatement(
							"insert into persona(nombre, apellido, tipo_doc, nro_doc, email, tel) values(?,?,?,?,?,?)",
							PreparedStatement.RETURN_GENERATED_KEYS
							);
			stmt.setString(1, p.getNombre());
			stmt.setString(2, p.getApellido());
			stmt.setString(3, p.getDocumento().getTipo());
			stmt.setString(4, p.getDocumento().getNro());
			stmt.setString(5, p.getEmail());
			stmt.setString(6, p.getTel());
			stmt.executeUpdate();
			
			keyResultSet=stmt.getGeneratedKeys();
            if(keyResultSet!=null && keyResultSet.next()){
                p.setId(keyResultSet.getInt(1));
            }

			
		}  catch (SQLException e) {
            e.printStackTrace();
		} finally {
            try {
                if(keyResultSet!=null)keyResultSet.close();
                if(stmt!=null)stmt.close();
                FactoryConexion.getInstancia().releaseConn();
            } catch (SQLException e) {
            	e.printStackTrace();
            }
		}
    }

}

*/
