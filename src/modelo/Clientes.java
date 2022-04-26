package modelo;

import java.sql.*;
import javax.swing.JOptionPane;
import vista.IMenu;
import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Clientes extends Conexiondb{

    String nombres, apellidos, telefono, direccion;
    IMenu menu;
    Connection cn;
    PreparedStatement pst;
    String consulta;
    public Clientes() {
        cn = null;
        pst = null;
        consulta = null;
    }

    public void Guardar(String nombres, String apellidos, String telefono, String direccion) {
        cn = Conexion();
         consulta = "INSERT INTO clientes VALUES(null,?,?,?,?)";
        try {
            pst = cn.prepareStatement(consulta);
            pst.setString(1, nombres);
            pst.setString(2, apellidos);
            pst.setString(3, telefono);
            pst.setString(4, direccion);
            int cont = pst.executeUpdate();
            if (cont > 0) {
                JOptionPane.showMessageDialog(null, "Cliente guardado exitosamente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Actualizar(String id, String nombres, String apellidos, String telefono, String direccion) {
         cn = Conexion();
         consulta = " UPDATE clientes SET nombres = ?, apellidos = ?, telefono = ?, direccion = ? WHERE id=" + id;
        try {
            pst = cn.prepareStatement(consulta);
            pst.setString(1, nombres);
            pst.setString(2, apellidos);
            pst.setString(3, telefono);
            pst.setString(4, direccion);
            int banderin = pst.executeUpdate();
            if (banderin > 0) {
                JOptionPane.showMessageDialog(null, "Dato actualizado correctamente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, id);
        }
    }

    public void Eliminar(String id) {
         cn = Conexion();
         consulta = "DELETE FROM clientes WHERE id=" + id;
        try {
             pst = cn.prepareStatement(consulta);
            int banderin = pst.executeUpdate();
            if (banderin > 0) {
                JOptionPane.showMessageDialog(null, "Dato borrado exitosamente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public DefaultTableModel Consulta(String Buscar) {
         cn = Conexion();
        String Consulta = "SELECT id, nombres, apellidos, telefono, direccion FROM clientes WHERE CONCAT(id,nombres,apellidos) LIKE '%" + Buscar + "%'";
        String[] registro = new String[5];
        String[] titulos = {"Id", "Nombres", "Apellidos", "Teléfono", "Dirección"};
        DefaultTableModel modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(Consulta);
            while (rs.next()) {
                registro[0] = rs.getString("id");
                registro[1] = rs.getString("nombres");
                registro[2] = rs.getString("apellidos");
                registro[3] = rs.getString("telefono");
                registro[4] = rs.getString("direccion");
                modelo.addRow(registro);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return modelo;
    }
    public boolean ValidarClienteCredito(String idCliente)
    {
        boolean SioNo = true;
        String idCredit = "";
        this.consulta = "SELECT creditos.id FROM creditos WHERE creditos.cliente=? AND creditos.estado='Pendiente'";
        this.cn = Conexion();
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, idCliente);
            ResultSet rs = pst.executeQuery();
            while(rs.next())
            {
                idCredit = rs.getString("id");
            }
            //si credito esta vacio no hay credito pendiente
            if(idCredit.equals(""))
            {
                SioNo = false;
            }else
            {
                SioNo = true;
            }
            this.cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion validarClienteCredito en modelo Clientes");
        }
        return SioNo;
    }
}
