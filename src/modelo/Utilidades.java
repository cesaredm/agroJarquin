
package modelo;

import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Utilidades extends Conexiondb{
    Connection cn;
    PreparedStatement pst;
    DefaultTableModel modelo;
    String consulta;
    DecimalFormat formato;
    private float precioDolar;
    int[] enero;
    int[] febrero;
    int[] marzo;
    int[] abril;
    int[] mayo;
    int[] junio;
    int[] julio;
    int[] agosto;
    int[] septiembre;
    int[] octubre;
    int[] noviembre;
    int[] diciembre;
    String[] meses = {"enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"};
    //set en cada array el total vendido y el total de utilidads ejemplo : {25,000.00, 2,536.00}
    public Utilidades(){
        this.consulta = "";
        this.formato = new DecimalFormat("###########0.00");
    }

    public void setPrecioDolar(float precioDolar) {
        this.precioDolar = precioDolar;
    }
    
    public float precioVenta(Date fecha, Date fecha2){
        float total = 0, totalUtilidades = 0;
        String totalString;
        this.cn = Conexion();
        this.consulta = "select sum(f.totalFactura) as total from facturas as f where f.fecha BETWEEN ? AND ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            this.pst.setDate(2, fecha2);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            this.cn.close();
            totalUtilidades = total - (precioCompraDolar(fecha,fecha2)+precioCompraCordobas(fecha,fecha2));
            totalString = this.formato.format(totalUtilidades);
            totalUtilidades = Float.parseFloat(totalString);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioVenta en el modelo de reporte");
        }
        return totalUtilidades;
    }
    public float precioCompraDolar(Date fecha, Date fecha2){
        float total = 0;
        String totalString;
        this.cn = Conexion();
        this.consulta = "SELECT SUM(ROUND((df.cantidadProducto*p.precioCompra)*?,2)) AS total FROM detalleFactura AS df INNER JOIN facturas AS f ON(f.id=df.factura) INNER JOIN productos AS p ON(p.id=df.producto) WHERE p.monedaCompra='Dolar' f.fecha BETWEEN ? AND ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setFloat(1,this.precioDolar);
            this.pst.setDate(2,fecha);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            //System.out.println("total:"+ total);
            //totalString = formato.format(total*this.precioDolar);
            //total = Float.parseFloat(totalString);
            //System.out.println(total);
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioCompraDolar en el modelo de reporte");
        }
        return total;
    }
    
    public float precioCompraCordobas(Date fecha,Date fecha2){
        float total = 0;
        this.cn = Conexion();
        this.consulta = "select sum(df.cantidadProducto*p.precioCompra) as total from detalleFactura as df"
                + " inner join facturas as f on(f.id=df.factura) inner join productos as p on(p.id=df.producto)"
                + " where and p.monedaCompra='Córdobas' f.fecha BETWEEN ? and ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setDate(1,fecha);
            this.pst.setDate(2, fecha2);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                total = rs.getFloat("total");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + "en la funcion precioCompra en el modelo de reporte");
        }
        return total;
    }
}
