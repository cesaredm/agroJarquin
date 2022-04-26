package modelo;

import controlador.CtrlProducto;
import java.awt.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class Productos extends Conexiondb {

    DefaultTableModel modelo;
    DefaultComboBoxModel combo;
    ArrayList lista;
    DefaultListModel modeloLista;
    Connection cn;
    PreparedStatement pst;
    String consulta;
    int banderin;
    private boolean existe = true;
    private float precioMinimo;
    
    public Productos() {
        this.lista = new ArrayList();//instancia de un nuevo array list
        this.modeloLista = new DefaultListModel();//modelo para el Jlist
        this.cn = null;
        this.combo = new DefaultComboBoxModel();
        this.pst = null;
    }

    public boolean isExiste() {
        return existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }
    
    public float getPrecioMinimo(){
        return this.precioMinimo;
    }
    
    public void Guardar(
	    String codigoBarra,
	    String nombre,
	    String precioCompra,
	    String monedaCompra,
	    String precioVenta,
	    String monedaVenta,
	    String precioMinimo,
	    Date fechaVencimiento,
	    String stock,
	    String categoria,
	    String laboratorio,
	    String ubicacion,
	    String descripcion,
	    float utilidad,
	    String composicion,
	    float precioCompraDolar
    ) {
        cn = Conexion();
        this.consulta = "INSERT INTO productos(codigoBarra, nombre, precioCompra, monedaCompra, precioVenta,"
		+ " precioMinimo, monedaVenta, fechaVencimiento,"
		+ " stock, categoria, marca, ubicacion, descripcion, utilidad,composicion,preciocompradolar)"
		+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        float compra = Float.parseFloat(precioCompra),
		venta = Float.parseFloat(precioVenta),
		cantidad = Float.parseFloat(stock),
		ventaMin = Float.parseFloat(precioMinimo);
        int Idcategoria = Integer.parseInt(categoria),
		Idlaboratorio = Integer.parseInt(laboratorio);
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, codigoBarra);
            pst.setString(2, nombre);
            pst.setFloat(3, compra);
            pst.setString(4, monedaCompra);
            pst.setFloat(5, venta);
            pst.setFloat(6,ventaMin);
            pst.setString(7, monedaVenta);
            pst.setDate(8, fechaVencimiento);
            pst.setFloat(9, cantidad);
            pst.setInt(10, Idcategoria);
            pst.setInt(11, Idlaboratorio);
            pst.setString(12, ubicacion);
            pst.setString(13, descripcion);
            pst.setFloat(14, utilidad);
	    pst.setString(15, composicion);
	    pst.setFloat(16, precioCompraDolar);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Producto guardado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void guardarKardexIncial(int producto, String user, float cantidad, String anotacion ){
        this.cn = Conexion();
        this.consulta = "INSERT INTO kardexentradas(producto,usuario,cantidad,anotacion) VALUES(?,?,?,?)";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setInt(1, producto);
            this.pst.setString(2, user);
            this.pst.setFloat(3, cantidad);
            this.pst.setString(4, anotacion);
            this.banderin = this.pst.executeUpdate();
            if(this.banderin > 0){
                //JOptionPane.showMessageDialog(null, "Kardex Actualizado exitosamente");
            }else{
                JOptionPane.showMessageDialog(null, "Error al actualizar kardex");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en la funcion guardarKardex en modelo producto.");
        }
    }
    
    public void Actualizar(
	    String id,
	    String codigoBarra,
	    String nombre,
	    String precioCompra,
	    String monedaCompra,
	    String precioVenta,
	    String monedaVenta ,
	    String precioMinimo ,
	    Date fechaVencimiento,
	    String stock,
	    String categoria,
	    String laboratorio,
	    String ubicacion,
	    String descripcion,
	    float utilidad,
	    String composicion,
	    float precioCompraDolar
    ) {
        cn = Conexion();
        this.consulta = "UPDATE productos SET codigoBarra=?, nombre=?, precioCompra=?, monedaCompra=?, precioVenta=?, precioMinimo=?,"
		+ " monedaVenta=?, fechaVencimiento=?, stock=?, categoria=?, marca=?, ubicacion=?, descripcion=?, utilidad=?, composicion = ?, preciocompradolar = ?"
		+ " WHERE id = ?";
        
        float compra = Float.parseFloat(precioCompra),
		venta = Float.parseFloat(precioVenta),
		cantidad = Float.parseFloat(stock),
		ventaMin = Float.parseFloat(precioMinimo);
        int Idcategoria = Integer.parseInt(categoria),
		Idlaboratorio = Integer.parseInt(laboratorio);
        try {
            pst = this.cn.prepareStatement(this.consulta);
            pst.setString(1, codigoBarra);
            pst.setString(2, nombre);
            pst.setFloat(3, compra);
            pst.setString(4, monedaCompra);
            pst.setFloat(5, venta);
            pst.setFloat(6, ventaMin);
            pst.setString(7, monedaVenta);
            pst.setDate(8, fechaVencimiento);
            pst.setFloat(9, cantidad);
            pst.setInt(10, Idcategoria);
            pst.setInt(11, Idlaboratorio);
            pst.setString(12, ubicacion);
            pst.setString(13, descripcion);
            pst.setFloat(14, utilidad);
	    pst.setString(15, composicion);
	    pst.setFloat(16, precioCompraDolar);
            pst.setString(17, id);
            this.banderin = pst.executeUpdate();
            if (this.banderin > 0) {
                JOptionPane.showMessageDialog(null, "Producto actualizado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void Eliminar(String id) {
        cn = Conexion();
        this.consulta = "DELETE FROM productos WHERE id=" + id;
        try {
            pst = this.cn.prepareStatement(consulta);
            this.banderin = pst.executeUpdate();
            if (banderin > 0) {
                JOptionPane.showMessageDialog(null, "Dato borrado exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public DefaultTableModel ConsultaAdmin(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT productos.id, productos.codigoBarra, productos.nombre AS nombreProducto, precioCompra, monedaCompra, preciocompradolar, precioVenta,"
		+ " monedaVenta, precioMinimo,fechaVencimiento, stock, ubicacion, productos.descripcion, categorias.nombre AS nombreCategoria,"
		+ " marca.nombre as nombreMarca, productos.utilidad, productos.composicion FROM productos LEFT JOIN categorias"
		+ " ON(productos.categoria=categorias.id) LEFT JOIN marca ON(productos.marca=marca.id) WHERE"
		+ " CONCAT(productos.codigoBarra, productos.nombre, categorias.nombre, marca.nombre)"
		+ " LIKE '%" + buscar + "%' ORDER BY productos.id DESC";
        String[] registros = new String[17];
        String[] titulos = {
		"Id",
		"Codigo Barra",
		"Nombre",
		"PrecioCompra",
		"Moneda",
		"PrecioVenta",
		"Moneda",
		"P. venta Min",
		"Descripción" ,
		"Fecha Vencimiento",
		"Stock",
		"Categoria",
		"Marca",
		"Ubicación",
		"Utlidad%",
		"Composición",
		"Precio $"
	};
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("codigoBarra");
                registros[2] = rs.getString("nombreProducto");
                registros[3] = rs.getString("precioCompra");
                registros[4] = rs.getString("monedaCompra");
                registros[5] = rs.getString("precioVenta");
                registros[6] = rs.getString("monedaVenta");
                registros[7] = rs.getString("precioMinimo");
                registros[8] = rs.getString("descripcion");
                registros[9] = rs.getString("fechaVencimiento");
                registros[10] = rs.getString("stock");
                registros[11] = rs.getString("nombreCategoria");
                registros[12] = rs.getString("nombreMarca");
                registros[13] = rs.getString("ubicacion");
                registros[14] = rs.getString("utilidad");
		registros[15] = rs.getString("composicion");
		registros[16] = rs.getString("preciocompradolar");
                this.modelo.addRow(registros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return modelo;
    }
    public DefaultTableModel Consulta(String buscar) {
        cn = Conexion();
        this.consulta = "SELECT productos.id, productos.codigoBarra, productos.nombre AS nombreProducto, precioVenta, monedaVenta, precioMinimo,"
		+ "fechaVencimiento, stock, ubicacion, productos.descripcion, categorias.nombre AS nombreCategoria, marca.nombre as nombreMarca,"
		+ " productos.utilidad, productos.composicion FROM productos LEFT JOIN categorias ON(productos.categoria=categorias.id) LEFT JOIN marca"
		+ " ON(productos.marca=marca.id) WHERE CONCAT(productos.codigoBarra, productos.nombre, categorias.nombre, marca.nombre)"
		+ " LIKE '%" + buscar + "%' ORDER BY productos.id DESC";
        String[] registros = new String[16];
        String[] titulos = {
		"Id",
		"Codigo Barra",
		"Nombre",
		"PrecioCompra",
		"Moneda",
		"PrecioVenta",
		"Moneda",
		"P. venta Min",
		"Descripción" ,
		"Fecha Vencimiento",
		"Stock",
		"Categoria",
		"Marca",
		"Ubicación",
		"Utlidad%",
		"Composición"
	};
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("codigoBarra");
                registros[2] = rs.getString("nombreProducto");
                registros[3] = "";
                registros[4] = "";
                registros[5] = rs.getString("precioVenta");
                registros[6] = rs.getString("monedaVenta");
                registros[7] = rs.getString("precioMinimo");
                registros[8] = rs.getString("descripcion");
                registros[9] = rs.getString("fechaVencimiento");
                registros[10] = rs.getString("stock");
                registros[11] = rs.getString("nombreCategoria");
                registros[12] = rs.getString("nombreMarca");
                registros[13] = rs.getString("ubicacion");
                registros[14] = "";
		registros[15] = rs.getString("composicion");
                this.modelo.addRow(registros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return modelo;
    }
    //Mostrar todas la categorias para agregra al producto
    public DefaultTableModel MostrarCategorias(String nombre) {
        cn = Conexion();
        this.consulta = "SELECT * FROM categorias WHERE nombre LIKE '%" + nombre + "%'";
        String[] resultados = new String[3];
        String[] titulos = {"Id", "Nombre", "Descripcion"};
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while (rs.next()) {

                resultados[0] = rs.getString("id");
                resultados[1] = rs.getString("nombre");
                resultados[2] = rs.getString("descripcion");
                this.modelo.addRow(resultados);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return modelo;
    }
    //Mostrar todas la Laboratorio para agregra al producto
    public DefaultTableModel MostrarMarca(String nombre) {
        cn = Conexion();
        this.consulta = "SELECT * FROM marca WHERE nombre LIKE '%" + nombre + "%'";
        String[] resultados = new String[3];
        String[] titulos = {"Id", "Nombre", "Descripcion"};
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(consulta);
            while (rs.next()) {

                resultados[0] = rs.getString("id");
                resultados[1] = rs.getString("nombre");
                resultados[2] = rs.getString("descripcion");
                modelo.addRow(resultados);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return modelo;
    }
    
    public String ObtenerIdMarca(String nombre)//metodo para obtener Id de laboratorio para modificar producto
    {
        String id = "";
        cn = Conexion();
        this.consulta = "SELECT id FROM marca WHERE nombre='" + nombre + "'";
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

    public String ObtenerIdCategoria(String nombre)//metodo para obtener Id de categoria para modificar producto
    {
        String id = "";
        cn = Conexion();
        this.consulta = "SELECT id FROM categorias WHERE nombre='" + nombre + "'";
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

    public void AgregarProductoStock(String id, String cantidad)//metodo para agregar producto al stock
    {
        cn = Conexion();
        float c = Float.parseFloat(cantidad);
        int idP = Integer.parseInt(id);
        this.consulta = "{CALL agregarProductoStock(?,?)}";
        try {
            CallableStatement cst = this.cn.prepareCall(this.consulta);
            cst.setInt(1, idP);
            cst.setFloat(2, c);
            this.banderin = cst.executeUpdate();
            if (banderin > 0) {
                //JOptionPane.showMessageDialog(null, "Se Agrego Exitosamente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public DefaultTableModel MinimoStock(String categoria, float cantidad) {
        cn = Conexion();
        //Agregar precioVenta y MonedaVenta a la consulta y al titulo de la tabla
        this.consulta = "SELECT productos.id, productos.codigoBarra, productos.nombre AS nombreProducto, precioVenta, monedaVenta, fechaVencimiento,stock, ubicacion, productos.descripcion, categorias.nombre AS nombreCategoria, marca.nombre as nombreMarca FROM productos INNER JOIN categorias ON(productos.categoria=categorias.id) INNER JOIN marca ON(productos.marca=marca.id) WHERE productos.stock < " + cantidad + " AND categorias.nombre LIKE '%" + categoria + "%' ORDER BY productos.stock";
        String[] titulos = {"Id", "Codigo Barra", "Nombre", "precioVenta", "Moneda", "Fecha Vencimiento", "Stock", "Categoría", "Marca", "Ubicación", "Descripción"};
        String[] registros = new String[12];
        modelo = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        try {
            Statement pst = this.cn.createStatement();
            //pst.setInt(1, cantidad);
            //pst.setString(2, categoria);
            ResultSet rs = pst.executeQuery(consulta);
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("codigoBarra");
                registros[2] = rs.getString("nombreProducto");
                registros[3] = rs.getString("precioVenta");
                registros[4] = rs.getString("monedaVenta");
                registros[5] = rs.getString("fechaVencimiento");
                registros[6] = rs.getString("stock");
                registros[7] = rs.getString("nombreCategoria");
                registros[8] = rs.getString("nombreMarca");
                registros[9] = rs.getString("ubicacion");
                registros[10] = rs.getString("descripcion");
                this.modelo.addRow(registros);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return this.modelo;
    }
    
    public void GenerarReporteStockMin(String categ, float cantidad) throws SQLException
    {
        try {
            this.cn = Conexion();
                JasperReport Reporte = null;
                String path = "/Reportes/minStock.jasper";
                Map parametros = new HashMap();
                parametros.put("cantidad", cantidad);
                parametros.put("categoria", categ);
                //Reporte = (JasperReport) JRLoader.loadObject(path);
                Reporte = (JasperReport) JRLoader.loadObject(getClass().getResource("/Reportes/minStock.jasper"));
                JasperPrint jprint = JasperFillManager.fillReport(Reporte, parametros, cn);
                JasperViewer vista = new JasperViewer(jprint,false);
                vista.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                vista.setVisible(true);
                cn.close();
            } catch (JRException ex) {
                Logger.getLogger(CtrlProducto.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
        //metodo para obtener el total de inversion en el negocio precio de compra
    public float inversion(){
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+"funcion inversion en modelo");
        }
        return inversion;
    }
    public float inversionCordobas()
    {
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos WHERE monedaCompra = 'Córdobas'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+"funcion inversion en modelo");
        }
        return inversion;
    }
    public float inversionDolar()
    {
        cn = Conexion();
        float inversion = 0;
        this.consulta = "SELECT SUM(precioCompra*stock) AS inversion FROM productos WHERE monedaCompra = 'Dolar'";
        try {
            PreparedStatement pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {                
                inversion = rs.getFloat("inversion");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+"funcion inversion en modelo");
        }
        return inversion;
    }

    public void ExitsCodBarra(String codBarra){
        String producto = "";
        this.cn = Conexion();
        this.consulta = "SELECT codigoBarra FROM productos WHERE codigoBarra = ?";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, codBarra);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                producto = rs.getString("codigoBarra");
            }
            if(producto.equals("")){
                setExiste(false);
            }else{
                setExiste(true);
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+" en la funcion ExistCodBarra en modelo productos");
        }
    }
    
    public void precioMinimo(String id){
        this.cn = Conexion();
        this.consulta = "SELECT precioMinimo FROM productos WHERE id = ?";
        try{
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                this.precioMinimo = rs.getFloat("precioMinimo");
            }
            this.cn.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e + "en la funcion precioMinimo en el modelo producto");
        }
    }
    
    public DefaultTableModel kardexSalidas(String id){
        this.cn = Conexion();
        this.consulta = "SELECT P.ID,P.CODIGOBARRA,P.NOMBRE,DF.CANTIDADPRODUCTO AS CANTIDADDESALIDA, F.FECHA AS FECHASALIDA, F.ID AS NUMEROFACTURA FROM"
                    + " PRODUCTOS AS P INNER JOIN DETALLEFACTURA AS DF ON(P.ID=DF.PRODUCTO) INNER JOIN FACTURAS AS F ON(DF.FACTURA=F.ID)"
                    + " WHERE P.ID = ? AND DF.CANTIDADPRODUCTO != 0";
        String[] titulos = {"ID","COD. BARRA","NOMBRE","CANTIDAD SALIDA","FECHA SALIDA","N. FACTURA"};
        String[] datos = new String[6];
        this.modelo = new DefaultTableModel(null, titulos){
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                datos[0] = rs.getString("ID");
                datos[1] = rs.getString("CODIGOBARRA");
                datos[2] = rs.getString("NOMBRE");
                datos[3] = rs.getString("CANTIDADDESALIDA");
                datos[4] = rs.getString("FECHASALIDA");
                datos[5] = rs.getString("NUMEROFACTURA");
                this.modelo.addRow(datos);
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo kardex en modelo productos");
        }
        return this.modelo;
    }
    
    public String kardexInicial(String id){
        String kardexInicial = "";
        this.consulta = "SELECT cantidad FROM kardexentradas WHERE producto = ? AND anotacion = 'inicial'";
        this.cn = Conexion();
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                kardexInicial = rs.getString("cantidad");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e+ " en la funcion kardexInicial en modelo productos");
        }
        return kardexInicial;
    }
    
    public String countKardexSalidas(String id){
        this.cn = Conexion();
        this.consulta = "SELECT SUM(DF.CANTIDADPRODUCTO) AS SALIDA FROM DETALLEFACTURA AS DF INNER JOIN PRODUCTOS AS P ON(DF.PRODUCTO=P.ID)"
                        + " WHERE P.ID = ?";
        String salidas = "";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                salidas = rs.getString("salida");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo kardex en modelo productos");
        }
        return salidas;
    }
    
    public DefaultTableModel kardexEntradas(String id){
        this.cn = Conexion();
        this.consulta = "SELECT P.ID,P.CODIGOBARRA,P.NOMBRE,K.CANTIDAD,K.FECHA, K.ANOTACION,K.USUARIO FROM PRODUCTOS AS P INNER JOIN"
                + " KARDEXENTRADAS AS K ON(P.ID=K.PRODUCTO) WHERE P.ID = ? AND (K.ANOTACION = 'add' OR K.ANOTACION = 'edicion stock')";
        String[] titulos = {"ID","COD. BARRA", "NOMBRE", "CANTIDAD ENTRADA", "FECHA ENTRADA", "ACCION", "USUARIO"};
        String[] datos = new String[7];
        this.modelo = new DefaultTableModel(null, titulos){
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            this.pst.setString(1, id);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                datos[0] = rs.getString("ID");
                datos[1] = rs.getString("CODIGOBARRA");
                datos[2] = rs.getString("NOMBRE");
                datos[3] = rs.getString("CANTIDAD");
                datos[4] = rs.getString("FECHA");
                datos[5] = rs.getString("ANOTACION");
                datos[6] = rs.getString("USUARIO");
                this.modelo.addRow(datos);
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo kardexEntradas en modelo productos");
        }
        return this.modelo;
    }
    
    public int ultimoRegistro(){
        int id = 0;
        this.cn = Conexion();
        this.consulta = "SELECT MAX(id) as ID FROM productos";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                id = rs.getInt("ID");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo ultimoRegistro en modelo productos");
        }
        return id;
    }
    
    public DefaultListModel productosMinStock(){
        this.cn = Conexion();
        this.consulta = "SELECT id,nombre,stock  FROM productos WHERE stock <= 5 ORDER BY stock DESC";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                 this.lista.add(rs.getString("nombre") + " Stock: " + rs.getString("stock"));
                 modeloLista.removeAllElements();
                 for (int l = 0; l < lista.size(); l++)//recorro la lista para ingresarla al modelo de Jlist
                    {
                        modeloLista.addElement(lista.get(l)); //add elemento a modeloLista   
                    }
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo productosMinStock en modelo productos");
        }
        return this.modeloLista;
    }
    public int countProductosMinStock(){
        int cont = 0;
        this.cn = Conexion();
        this.consulta = "SELECT COUNT(id) AS cont FROM productos WHERE stock <= 5;";
        try {
            this.pst = this.cn.prepareStatement(this.consulta);
            ResultSet rs = this.pst.executeQuery();
            while(rs.next()){
                 cont = rs.getInt("cont");
            }
            this.cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e + " en el metodo countProductosMinStock en modelo productos");
        }
        return cont;
    }

    public ArrayList<String> infoProducto(String id)
    {
	    ArrayList<String> info = new ArrayList<String>();
	    this.cn = Conexion();
	    this.consulta = "SELECT p.id,p.nombre,codigoBarra, precioCompra, monedaCompra,preciocompradolar, precioVenta,"
		    + " monedaVenta,precioMinimo,m.nombre AS marca,c.nombre AS categoria,p.descripcion, utilidad,"
		    + "composicion,fechaVencimiento,"
		    + " stock, ubicacion FROM productos AS p INNER JOIN categorias AS c ON(p.categoria=c.id)"
		    + " INNER JOIN marca AS m ON(p.marca=m.id) WHERE p.id = ?";
	    try {
		    this.pst = this.cn.prepareStatement(this.consulta);
		    this.pst.setString(1, id);
		    ResultSet rs = this.pst.executeQuery();
		    while(rs.next()){
			    info.add(rs.getString("id"));
			    info.add(rs.getString("codigoBarra"));
			    info.add(rs.getString("nombre"));
			    info.add(rs.getString("precioCompra"));
			    info.add(rs.getString("monedaCompra"));
			    info.add(rs.getString("preciocompradolar"));
			    info.add(rs.getString("precioVenta"));
			    info.add(rs.getString("monedaVenta"));
			    info.add(rs.getString("precioMinimo"));
			    info.add(rs.getString("marca"));
			    info.add(rs.getString("categoria"));
			    info.add(rs.getString("descripcion"));
			    info.add(rs.getString("utilidad"));
			    info.add(rs.getString("composicion"));
			    info.add(rs.getString("fechaVencimiento"));
			    info.add(rs.getString("stock"));
			    info.add(rs.getString("ubicacion"));
		    }
		    this.cn.close();
	    } catch (SQLException e) {
		    JOptionPane.showMessageDialog(null, e + "en el metodo infoProducto en modelo productos");
	    }
	    return info;
    }
}
