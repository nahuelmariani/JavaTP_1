package data;
import entities.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class DataCuota {
	public void add(Cuota c) {
		PreparedStatement stmt= null;
		ResultSet keyResultSet=null;
		try {
			stmt=FactoryConexion.getInstancia().getConn().
					prepareStatement(
							"insert into cuota(mes, anio, importe, idPersona) values(?,?,?,?)",
							PreparedStatement.RETURN_GENERATED_KEYS
							);
			
			stmt.setInt(1, c.getMes());
			stmt.setInt(2,  c.getAnio());
			stmt.setDouble(3,  c.getImporte());
		    stmt.setInt(4,  c.getP().getId());
			stmt.executeUpdate();
			
			keyResultSet=stmt.getGeneratedKeys();
          if(keyResultSet!=null && keyResultSet.next()){
              c.setId_cuota(keyResultSet.getInt(1));
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

	public void actualizar(Cuota cuota, Persona soc) {
		// TODO Auto-generated method stub
		PreparedStatement stmt= null;
		Date objDate = new Date();	
			try {
				stmt=FactoryConexion.getInstancia().getConn().
						prepareStatement("update cuota set fecha_pago=? where idPersona=? and mes=? and anio=?");
				
				stmt.setTimestamp(1, new java.sql.Timestamp(objDate.getTime()));
				stmt.setInt(2, soc.getId());
				stmt.setInt(3, cuota.getMes());
				stmt.setInt(4, cuota.getAnio());
				
				stmt.executeUpdate();
				
			}  catch (SQLException e) {
	          e.printStackTrace();
			} finally {
	          try {
	              if(stmt!=null)stmt.close();
	              FactoryConexion.getInstancia().releaseConn();
	          } catch (SQLException e) {
	          	e.printStackTrace();
	          }
			}
	  }
	
	public Cuota buscarCuota(Cuota cuota, Persona soc) {
		 Cuota c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt=FactoryConexion.getInstancia().getConn().prepareStatement(
					"select idcuota, mes, anio, importe, fecha_pago idPersona from cuota where fecha_pago is not NULL and mes=? and anio=? and idPersona=?"
					);
			stmt.setInt(1, cuota.getMes());
			stmt.setInt(2, cuota.getAnio());
			stmt.setInt(3, soc.getId());
			
			rs=stmt.executeQuery();
			if(rs!=null && rs.next()) {
				Persona p = new Persona();
				c=new Cuota();
				c.setId_cuota(rs.getInt("id_cuota"));
				c.setMes(rs.getInt("mes"));
				c.setAnio(rs.getInt("anio"));
				c.setImporte(rs.getDouble("importe"));
				
				c.setP(p);
				
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
		
		return c;
	}
	
	
	}
