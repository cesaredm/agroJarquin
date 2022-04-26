package modelo;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Transacciones extends Conexiondb {

    DefaultTableModel modelo;
    Connection cn;
    PreparedStatement pst;
    String consulta;
    String[] resgistros;
    DefaultComboBoxModel combo;
    int banderin;

    public Transacciones() {
        this.cn = null;
        this.pst = null;
        this.consulta = "";
        this.banderin = 0;
    }

    public void Guardar(float monto, Date fecha, String Descripcion, String TipoTrans, int idCaja) {
        cn = Conexion();
        this.consulta = "INSERT INTO transaccion(monto, fecha, descripcion, tipoTransaccion, caja) VALUES(?,?,?,?,?)";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setFloat(1, monto);
            pst.setDate(2, fecha);
            pst.setString(3, Descripcion);
            pst.setString(4, TipoTrans);
            pst.setInt(5, idCaja);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Gasto guardado exitosamente", "Infromacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void Actualizar(int id, float monto, Date fecha, String Descripcion, String tipoTrans, int caja) {
        cn = Conexion();
        this.consulta = "UPDATE transaccion SET tipoTransaccion = ?, monto = ?, caja = ?, fecha = ?, descripcion = ? WHERE id = ?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, tipoTrans);
            pst.setFloat(2, monto);
            pst.setInt(3, caja);
            pst.setDate(4, fecha);
            pst.setString(5, Descripcion);
            pst.setInt(6, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Gasto actualizado exitosamente", "Infromacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void Eliminar(int id) {
        cn = Conexion();
        this.consulta = "DELETE FROM transaccion WHERE id = ?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setInt(1, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Gasto borrado exitosamente", "Infromacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public DefaultTableModel Mostrar(String Buscar) {
        cn = Conexion();
        this.resgistros = new String[6];
        String[] titulos = {"Id Transac.", "Tipo Transac", "Caja", "Monto", "Fecha", "Descripción"};
        this.modelo = new DefaultTableModel(null, titulos) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.consulta = "SELECT transaccion.id,transaccion.tipoTransaccion,cajas.caja,transaccion.monto,transaccion.fecha,transaccion.descripcion FROM transaccion INNER JOIN cajas ON(transaccion.caja=cajas.id) WHERE CONCAT(transaccion.id, transaccion.monto, transaccion.fecha, cajas.caja, transaccion.tipoTransaccion) LIKE '%" + Buscar + "%'";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                this.resgistros[0] = rs.getString("id");
                this.resgistros[1] = rs.getString("tipoTransaccion");
                this.resgistros[2] = rs.getString("caja");
                this.resgistros[3] = rs.getString("Monto");
                this.resgistros[4] = rs.getString("Fecha");
                this.resgistros[5] = rs.getString("descripcion");
                this.modelo.addRow(this.resgistros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return this.modelo;
    }
    public int IdCaja(String caja)
    {
        int id = 0;
        cn = Conexion();
        this.consulta = "SELECT id FROM cajas WHERE caja = ?";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, caja);
            ResultSet rs = pst.executeQuery();
            while(rs.next())
            {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion idCaja");
        }
        return id;
    }
    public DefaultComboBoxModel mostrarCajas()
    {
        this.combo = new DefaultComboBoxModel();
        cn = Conexion();
        this.consulta = "SELECT caja FROM cajas";
        try {
            pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while(rs.next())
            {
                this.combo.addElement(rs.getString("caja"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion idCaja");
        }
        return this.combo;
    }
}
