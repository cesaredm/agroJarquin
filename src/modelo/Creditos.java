/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Creditos extends Conexiondb {

    DefaultTableModel modelo;
    Connection cn;
    String consulta;
    String[] resgistros;
    PagosCreditos pagos;
    PreparedStatement pst;
    private float creditoCordobas,
	    creditoDolar,pagosDolar,
	    pagosCordobas,
	    saldoCordobas,
	    saldoDolar;
    int banderin;

    public Creditos() {
        this.cn = null;
        this.pst = null;
        this.consulta = null;
        this.banderin = 0;
        this.pagos = new PagosCreditos();
    }

	public PagosCreditos getPagos() {
		return pagos;
	}

	public void setPagos(PagosCreditos pagos) {
		this.pagos = pagos;
	}

	public float getCreditoDolar() {
		return creditoDolar;
	}

	public void setCreditoDolar(float creditoDolar) {
		this.creditoDolar = creditoDolar;
	}

	public float getPagosDolar() {
		return pagosDolar;
	}

	public void setPagosDolar(float pagosDolar) {
		this.pagosDolar = pagosDolar;
	}

	public float getPagosCordobas() {
		return pagosCordobas;
	}

	public void setPagosCordobas(float pagosCordobas) {
		this.pagosCordobas = pagosCordobas;
	}

	public float getCreditoCordobas() {
		return creditoCordobas;
	}

	public void setCreditoCordobas(float creditoCordobas) {
		this.creditoCordobas = creditoCordobas;
	}

	public float getSaldoCordobas() {
		return saldoCordobas;
	}

	public void setSaldoCordobas(float saldoCordobas) {
		this.saldoCordobas = saldoCordobas;
	}

	public float getSaldoDolar() {
		return saldoDolar;
	}

	public void setSaldoDolar(float saldoDolar) {
		this.saldoDolar = saldoDolar;
	}

    //Funcion para guardar los creditos
    public void GuardarCredito(int cliente, Date fecha, String estado, float limite) {
        cn = Conexion();
        //consulta sql para guardar creditos
        this.consulta = "INSERT INTO creditos(cliente, estado, fecha, limite) VALUES(?,?,?,?)";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, cliente);
            pst.setString(2, estado);
            pst.setDate(3, fecha);
            pst.setFloat(4, limite);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Credito Agregado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Funcion para actualizar los creditos
    public void Actualizar(int id, int cliente, Date fecha, String estado, float limite) {
        cn = Conexion();
        this.consulta = "UPDATE creditos SET cliente = ?, estado = ?, fecha = ?, limite = ? WHERE id=?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, cliente);
            pst.setString(2, estado);
            pst.setDate(3, fecha);
            pst.setFloat(4, limite);
            pst.setInt(5, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Credito " + id + " Actualizado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //funcion para eliminar creditos
    public void Eliminar(int id) {
        cn = Conexion();
        this.consulta = "DELETE FROM creditos WHERE id = ?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Credito " + id + " Borrado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void saldoYpagos(int id){
	this.cn = Conexion();
	this.consulta = "CALL saldos(?)";
	    try {
		    this.pst = this.cn.prepareStatement(this.consulta);
		    this.pst.setInt(1,id);
		    ResultSet rs = this.pst.executeQuery();
		    while (rs.next()) {			    
			   this.creditoDolar = rs.getFloat("creditoDolar");
			   this.creditoCordobas = rs.getFloat("creditoCordobas");
			   this.pagosDolar = rs.getFloat("pagosDolar");
			   this.pagosCordobas = rs.getFloat("pagosCordobas");
		    }
	    } catch (Exception e) {
		    e.printStackTrace();
	    }finally{
		try {
			this.cn.close();
		} catch (SQLException ex) {
			Logger.getLogger(Creditos.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
    }

    //funcion de consulta de datos de creditos y retornar una tabla con los creditos para mostrarla en interfaz
    public DefaultTableModel Mostrar(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT c.id,SUM(f.totalFactura) AS totalCredito, c.limite ,cl.id as idCliente,nombres,apellidos, c.estado FROM creditos AS c INNER JOIN clientes AS cl ON(c.cliente = cl.id) INNER JOIN facturas AS f ON(f.credito = c.id) WHERE CONCAT(c.id, cl.nombres, cl.apellidos) LIKE '%" + buscar + "%' AND c.estado = 'Pendiente' GROUP BY cl.id";
        String[] titulos = {"N° Crédito", "Saldo C$", "Saldo $", "Límite", "Id Cliente", "Nombres", "Apellidos", "Estado"};
        this.resgistros = new String[8];
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        DecimalFormat formato = new DecimalFormat("#############.##");
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //en la variable monto obtengo el total de pagos de el cliente
//                monto = this.pagos.PagosCliente(rs.getString("idCliente"));
                //en la variable saldo obtengo lo que queda de la resta de lo que debe el cliente menos total de pagos que ha hecho
		this.saldoYpagos(rs.getInt("id"));
                this.saldoCordobas = this.creditoCordobas - this.pagosCordobas;
		this.saldoDolar = this.creditoDolar - this.pagosDolar;
                this.resgistros[0] = rs.getString("id");
                this.resgistros[1] = formato.format(this.saldoCordobas);
		this.resgistros[2] = formato.format(this.saldoDolar);
                this.resgistros[3] = rs.getString("limite");
                this.resgistros[4] = rs.getString("idCliente");
                this.resgistros[5] = rs.getString("nombres");
                this.resgistros[6] = rs.getString("apellidos");
                this.resgistros[7] = rs.getString("estado");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " mostrar creditos");
        }
        return this.modelo;
    }

    //funcion para mostrar Los creditos abiertos o creados
    public DefaultTableModel MostrarCreditosCreados(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT creditos.id as idCredito,cliente, clientes.nombres, apellidos, creditos.fecha, limite, estado FROM creditos INNER JOIN clientes ON(clientes.id = creditos.cliente) WHERE CONCAT(creditos.id, clientes.nombres, clientes.apellidos) LIKE '%" + buscar + "%'";
        String[] titulos = {"N° Crédito", "Id Cliente", "Nombres", "Apellidos", "fecha", "Límite", "Estado"};
        this.resgistros = new String[7];
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                this.resgistros[0] = rs.getString("idCredito");
                this.resgistros[1] = rs.getString("cliente");
                this.resgistros[2] = rs.getString("nombres");
                this.resgistros[3] = rs.getString("apellidos");
                this.resgistros[4] = rs.getString("fecha");
                this.resgistros[5] = rs.getString("limite");
                this.resgistros[6] = rs.getString("estado");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " mostrar creditos");
        }
        return this.modelo;
    }

    //mostrar solo los creditos abiertos y pendientes menos los cancelados 
    public DefaultTableModel MostrarCreditosAddFactura(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT creditos.id as idCredito,cliente, clientes.nombres, apellidos, creditos.fecha, estado FROM creditos INNER JOIN clientes ON(clientes.id = creditos.cliente) WHERE CONCAT(creditos.id, clientes.nombres, clientes.apellidos) LIKE '%" + buscar + "%' AND creditos.estado!='Cancelado'";
        String[] titulos = {"N° Credito", "Id Cliente", "Nombres", "Apellidos", "fecha", "Estado"};
        this.resgistros = new String[6];
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                this.resgistros[0] = rs.getString("idCredito");
                this.resgistros[1] = rs.getString("cliente");
                this.resgistros[2] = rs.getString("nombres");
                this.resgistros[3] = rs.getString("apellidos");
                this.resgistros[4] = rs.getString("fecha");
                this.resgistros[5] = rs.getString("estado");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " mostrar creditos");
        }
        return this.modelo;
    }

    //funcion para actualizar el estado del credito segun lo que debe o a ha pagado el cliente
    public void ActualizarEstadoCredito(int id, String estado) {
        cn = Conexion();
        this.consulta = "UPDATE creditos SET estado=? WHERE id=?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, estado);
            pst.setInt(2, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                //JOptionPane.showMessageDialog(null,"Credito "+id+" Cancelado");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " Actualizar Estado de Credito");
        }
    }

    //funcion que que me obtiene el total de credito que debe el cliente
    public float TotalCreditoCliente(int id) {
        cn = Conexion();
        float creditoCliente = 0;
        int idCredito = 0;
        String estado = "";
        this.consulta = "SELECT creditos.id,SUM(facturas.totalFactura) AS totalCredito, clientes.id as idCliente,nombres,apellidos, creditos.estado FROM creditos INNER JOIN clientes ON(creditos.cliente = clientes.id) INNER JOIN facturas ON(facturas.credito = creditos.id) WHERE clientes.id = ? AND creditos.estado = 'Abierto'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                creditoCliente = rs.getFloat("totalCredito");
                idCredito = rs.getInt("id");
                estado = rs.getString("estado");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " total credito cliente");
        }
        return creditoCliente;
    }

    //
    public DefaultTableModel MostrarProductosCreditoDolar(int id) {
        this.cn = Conexion();
        String[] titulos = {"Fecha", "Nombre", "Cantidad", "Precio", "", "Total C$"};
        resgistros = new String[6];
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.consulta = "SELECT f.fecha, p.nombre, df.cantidadProducto, precioProducto, totalVenta AS totalImporte FROM facturas AS f "
                + "INNER JOIN creditos AS c ON(f.credito=c.id) INNER JOIN detalleFactura AS df ON(f.id = df.factura) INNER JOIN productos AS p ON(df.producto=p.id)"
                + " WHERE c.id = ? AND p.monedaVenta = 'Dolar' AND df.cantidadProducto > 0 ORDER BY f.id DESC";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resgistros[0] = rs.getString("fecha");
                resgistros[1] = rs.getString("nombre");
                resgistros[2] = rs.getString("cantidadProducto");
                resgistros[3] = rs.getString("precioProducto");
                resgistros[4] = "$";
                resgistros[5] = rs.getString("totalImporte");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return this.modelo;
    }

    public DefaultTableModel MostrarProductosCreditoCordobas(int id) {
        this.cn = Conexion();
        String[] titulos = {"Fecha", "Nombre", "Cantidad", "Precio", "", "Total C$"};
        resgistros = new String[6];
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.consulta = "SELECT f.fecha, p.nombre, df.cantidadProducto, precioProducto, totalVenta AS totalImporte FROM facturas AS f "
                + "INNER JOIN creditos AS c ON(f.credito=c.id) INNER JOIN detalleFactura AS df ON(f.id = df.factura) INNER JOIN productos AS p"
		+ " ON(df.producto=p.id) WHERE c.id = ? AND p.monedaVenta = 'Cordobas' AND df.cantidadProducto > 0 ORDER BY f.id DESC";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                resgistros[0] = rs.getString("fecha");
                resgistros[1] = rs.getString("nombre");
                resgistros[2] = rs.getString("cantidadProducto");
                resgistros[3] = rs.getString("precioProducto");
                resgistros[4] = "C$";
                resgistros[5] = rs.getString("totalImporte");
                this.modelo.addRow(resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return this.modelo;
    }

    public DefaultTableModel MostrarAbonosCliente(int id) {
        this.cn = Conexion();
        this.consulta = "SELECT p.id,p.fecha AS f,p.monto FROM pagoscreditos AS p INNER JOIN creditos AS c ON(p.credito=c.id) WHERE c.id = ?";
        String[] titulos = {"Id Pago", "Fecha", "Monto"};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.resgistros = new String[3];

        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setInt(1, id);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                this.resgistros[0] = rs.getString("id");
                this.resgistros[1] = rs.getString("f");
                this.resgistros[2] = rs.getString("monto");
                this.modelo.addRow(this.resgistros);
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en funcion MostrarAbonosCliente en modelo creditos");
        }

        return this.modelo;
    }

    //
    public void ActualizarCreditoFactura(int idFactura, int idCredito) {
        this.cn = Conexion();
        this.consulta = "UPDATE facturas SET credito = ? FROM id = ?";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setInt(1, idCredito);
            pst.setInt(2, idFactura);
            pst.execute();
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion ActualizarCreditoFactura en modelo creditos");
        }
    }

    public boolean VerificarExistenciaDeCredito(String id) {
        this.cn = Conexion();
        String valor = "";
        boolean validar = true;
        this.consulta = "SELECT clientes.id FROM clientes INNER JOIN creditos ON(clientes.id=creditos.cliente) WHERE clientes.id = ?";
        try {
            pst = cn.prepareStatement(this.consulta);
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                valor = rs.getString("id");
            }
            if (valor.equals("")) {
                validar = false;
            } else {
                validar = true;
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion VerificarExistenciaDeCredito");
        }
        return validar;
    }

    //metodo para obtener el nombre de cliente que tiene el credito
    public String NombreCliente(String idCredito) {
        String nombre = "";
        this.cn = Conexion();
        this.consulta = "SELECT c.nombres, c.apellidos FROM clientes AS c INNER JOIN creditos ON(c.id = creditos.cliente) WHERE creditos.id = ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, idCredito);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                nombre = rs.getString("nombres") + " " + rs.getString("apellidos");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "funcion NombreCliente en el modelo credito");
        }
        return nombre;
    }

    public float creditoPorCliente(String id) {
        cn = Conexion();
        this.consulta = "SELECT SUM(facturas.totalFactura) AS totalCredito, clientes.id as idCliente FROM creditos INNER JOIN clientes ON(creditos.cliente = clientes.id) INNER JOIN facturas ON(facturas.credito = creditos.id) WHERE creditos.estado = 'Pendiente' AND creditos.id = ?";
        float saldo = 0, monto = 0;
        DecimalFormat formato = new DecimalFormat("#############.##");
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //en la variable monto obtengo el total de pagos de el cliente
                monto = this.pagos.PagosCliente(rs.getString("idCliente"));
                //en la variable saldo obtengo lo que queda de la resta de lo que debe el cliente menos total de pagos que ha hecho
                saldo = rs.getFloat("totalCredito") - monto;
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " mostrar creditos");
        }
        return saldo;
    }

    public float limiteCredito(String id) {
        this.cn = Conexion();
        float limite = 0;
        this.consulta = "SELECT limite FROM creditos WHERE id = ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                limite = rs.getFloat("limite");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion limiteCredito en modelo creditos");
        }
        return limite;
    }

    public float creditoGlobalCliente(int id) {
        this.consulta = "SELECT SUM(facturas.totalFactura) AS totalCredito FROM creditos INNER JOIN clientes ON(creditos.cliente = clientes.id) "
                + "INNER JOIN facturas ON(facturas.credito = creditos.id) WHERE creditos.id = ?";
        this.cn = Conexion();
        float total = 0;
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setInt(1, id);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("totalCredito");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion creditoGlobalCliente en modelo creditos");
        }
        return total;
    }

    public float AbonoGlobalCliente(int id) {
        this.consulta = "SELECT SUM(p.monto) AS totalAbonos FROM pagoscreditos AS p "
                + "INNER JOIN creditos AS c ON(p.credito = c.id) INNER JOIN clientes ON(c.cliente=clientes.id) WHERE c.id = ?";
        this.cn = Conexion();
        float total = 0;
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setInt(1, id);
            ResultSet rs = this.pst.executeQuery();
            while (rs.next()) {
                total = rs.getFloat("totalAbonos");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion AbonosGlobalCliente en modelo creditos");
        }
        return total;
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
}
