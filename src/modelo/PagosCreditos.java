/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class PagosCreditos extends Conexiondb {

    DefaultTableModel modelo;
    Connection cn;
    PreparedStatement pst;
    Statement st;
    String consulta;
    String[] resgistros;
    public DefaultComboBoxModel combo;
    int banderin;

    private int credito, formaPago;
    private float monto,utilidadPago;
    private String moneda,anotacion;
    private Date fecha;

    public PagosCreditos() {
        this.cn = null;
        this.consulta = null;
        this.pst = null;
        this.banderin = 0;
    }

	public int getCredito() {
		return credito;
	}

	public void setCredito(int credito) {
		this.credito = credito;
	}

	public int getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}

	public float getMonto() {
		return monto;
	}

	public void setMonto(float monto) {
		this.monto = monto;
	}

	public float getUtilidadPago() {
		return utilidadPago;
	}

	public void setUtilidadPago(float utilidadPago) {
		this.utilidadPago = utilidadPago;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getAnotacion() {
		return anotacion;
	}

	public void setAnotacion(String anotacion) {
		this.anotacion = anotacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
    
    //metodo para Guardar pagos
    public void Guardar(int credito, float monto, Date fecha, int formaPago, String anotacion, float utilidadPago, String moneda) {
        this.consulta = "INSERT INTO pagoscreditos(credito,monto,fecha,formaPago,anotacion,utilidad,moneda) VALUES(?,?,?,?,?,?,?)";
        cn = Conexion();
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, credito);
            pst.setFloat(2, monto);
            pst.setDate(3, fecha);
            pst.setInt(4, formaPago);
	    pst.setString(5, anotacion);
	    pst.setFloat(6, utilidadPago);
	    pst.setString(7, moneda);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Pago guardado exitosamete", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }finally{
		try {
			cn.close();
		} catch (SQLException ex) {
			Logger.getLogger(PagosCreditos.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
    }

	public void editar(int id){
		this.cn = Conexion();
		this.consulta = "SELECT * FROM creditos WHERE id = ?";
		try {
			this.pst = this.cn.prepareStatement(this.consulta);
			this.pst.setInt(1,id);
			ResultSet rs = this.pst.executeQuery();
			while(rs.next()){
				this.credito = rs.getInt("credito");
				this.monto = rs.getFloat("monto");
				this.anotacion = rs.getString("anotacion");
				this.fecha = rs.getDate("fecha");
				this.formaPago = rs.getInt("formaPago");
				this.moneda = rs.getString("moneda");
				this.utilidadPago = rs.getFloat("utilidad");		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				this.cn.close();
			} catch (SQLException ex) {
				Logger.getLogger(PagosCreditos.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
    
    //metodo para eliminar pago
    public void Eliminar(int id) {
        this.consulta = "DELETE FROM pagoscreditos WHERE id = ?";
        cn = Conexion();
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Pago eliminado exitosamete", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    //metodo para Actualizar Pagos
    public void Actualizar(String id, int credito, float monto, Date fecha, int formaPago, String anotaciones, float utilidadPago) {
        this.consulta = "UPDATE pagoscreditos SET credito = ?, monto = ?, fecha = ?, formaPago = ?, anotaciones = ?, utilidad = ? WHERE id = ?";
        cn = Conexion();
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, credito);
            pst.setFloat(2, monto);
            pst.setDate(3, fecha);
            pst.setInt(4, formaPago);
	    pst.setString(5, anotaciones);
	    pst.setFloat(6, utilidadPago);
            pst.setString(7, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Pago Actualizado Exitosamete", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    //metodo para mostrar todos los pagos
    public DefaultTableModel Mostrar(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT pagoscreditos.id AS idPago, monto as montoPago, moneda, credito, pagoscreditos.fecha, clientes.nombres,apellidos,"
		+ " formapago.tipoVenta, pagoscreditos.anotacion FROM pagoscreditos LEFT JOIN creditos ON(pagoscreditos.credito = creditos.id)"
		+ " LEFT JOIN formapago"
		+ " ON(formapago.id=pagoscreditos.formaPago) LEFT JOIN clientes ON(creditos.cliente = clientes.id) WHERE"
		+ " CONCAT(pagoscreditos.id, pagoscreditos.credito, pagoscreditos.fecha, clientes.nombres, clientes.apellidos)"
		+ " LIKE '%" + buscar + "%'";
        this.resgistros = new String[9];
        String[] titulos = {
		"Id Pago",
		"Monto de Pago",
		"Moneda",
		"Al Crédito",
		"Fecha De Pago",
		"Nombres Cliente",
		"Apellidos Cliente",
		"Forma Pago",
		"Anotaciones"
	};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                this.resgistros[0] = rs.getString("idPago");
                this.resgistros[1] = rs.getString("montoPago");
		this.resgistros[2] = rs.getString("moneda");
                this.resgistros[3] = rs.getString("credito");
                this.resgistros[4] = rs.getString("fecha");
                this.resgistros[5] = rs.getString("nombres");
                this.resgistros[6] = rs.getString("apellidos");
                this.resgistros[7] = rs.getString("tipoVenta");
		this.resgistros[8] = rs.getString("anotacion");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return this.modelo;
    }
    //metodo para obtener los tipos de pago

    public DefaultComboBoxModel FormasPago() {
        cn = Conexion();
        this.consulta = "SELECT * FROM formapago";
        combo = new DefaultComboBoxModel();
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(this.consulta);
            while (rs.next()) {
                combo.addElement(rs.getString("tipoVenta"));
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return combo;
    }
    //metodo para obtener el id de la forma de pago segun el metodo de pago que recibe
    public String ObtenerFormaPago(String pago) {
        cn = Conexion();
        this.consulta = "SELECT id FROM formapago WHERE tipoVenta = '" + pago + "'";
        String id = "";
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(this.consulta);
            while (rs.next()) {
                id = rs.getString("id");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return id;
    }
    //funcion que me obtiene el total de pagos que tiene el cliete
    public float PagosCliente(String id) {
        cn = Conexion();
        float credito = 0;
        this.consulta = "SELECT SUM(pagoscreditos.monto) AS pago FROM pagoscreditos INNER JOIN creditos ON(pagoscreditos.credito=creditos.id) INNER JOIN clientes ON(creditos.cliente = clientes.id) WHERE clientes.id = ?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                credito = rs.getFloat("pago");//total de pagos de cliente
            }
            cn.close();
        } catch (SQLException e) {
        }
        return credito;
    }

    public String cliente(String id)
    {
        this.consulta = "select c.nombres, c.apellidos from clientes as c inner join creditos on(c.id = creditos.cliente) where creditos.id = ?";
        this.cn = Conexion();
        String nombre = "";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                nombre = rs.getString("nombres")+" "+rs.getString("apellidos");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion cliente en modelo pagodCreditos");
        }
        return nombre;
    }
    public float PagosSegunCredito(String id)
    {
        this.consulta = "SELECT SUM(p.monto) AS pagos FROM pagoscreditos AS p INNER JOIN creditos AS c ON(c.id = p.credito) WHERE c.id = ?";
        this.cn = Conexion();
        float monto = 0;
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                monto = rs.getFloat("pagos");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion saldo en modelo pagodCreditos");
        }
        return monto;
    }
    public float deuda(String id)
    {
        this.consulta = "SELECT SUM(f.totalFactura) AS deuda FROM facturas AS f INNER JOIN creditos AS c ON(f.credito=c.id) WHERE c.id= ?";
        this.cn = Conexion();
        float monto = 0;
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                monto = rs.getFloat("deuda");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion saldo en modelo pagodCreditos");
        }
        return monto;
    }

    public float utilidadXfactura(int id, float precioDolar, float pago){
	float utilidadFactura = 0,
		utilidadPago = 0,
		dato = 0,
		cordobas = 0,
		dolar = 0,
		ventas = 0;
	this.consulta = "CALL utilidadFactura(?)";
	this.cn = Conexion();
	    try {
		   this.pst = this.cn.prepareStatement(this.consulta);
		   this.pst.setInt(1, id);
		   ResultSet rs = this.pst.executeQuery();
		   while(rs.next())
		   {
			   ventas = rs.getFloat("ventas");
			   dolar = rs.getFloat("dolar");
			   cordobas = rs.getFloat("cordobas");
		   }
		   utilidadFactura = ventas - (dolar + cordobas);
		   dato = ventas / pago;
		   utilidadPago = utilidadFactura / dato;
		   this.cn.close();
	    } catch (SQLException e) {
		    JOptionPane.showMessageDialog(null, e);
	    }
	return utilidadPago;
    }

    public int obtenerUltimoPago() {
        int id = 0;
        this.cn = Conexion();
        this.consulta = "SELECT MAX(id) AS id FROM pagoscreditos";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion obtenerUltimoPago en el modelo Creditos.. ");
        }
        return id + 1;
    }

    public void getFormasPago(){
	    this.cn = Conexion();
	    this.consulta = "SELECT * FROM formaPago";
	    this.combo = new DefaultComboBoxModel();
	    try {
		   this.st = this.cn.createStatement();
		   ResultSet rs = this.st.executeQuery(this.consulta);
		    while (rs.next()) {			    
			   this.combo.addElement(new CmbFormaPago(rs.getInt("id"), rs.getString("tipoVenta")));
		    }
	    } catch (Exception e) {
		    e.printStackTrace();
	    }finally{
		    try {
			    this.cn.close();
		    } catch (SQLException ex) {
			    Logger.getLogger(PagosCreditos.class.getName()).log(Level.SEVERE, null, ex);
		    }
	    }
    }
}
