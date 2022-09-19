/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import vista.IMenu;
import modelo.Reportes;
import modelo.Facturacion;
import modelo.Productos;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlDevoluciones implements ActionListener, WindowListener {

	IMenu menu;
	Reportes reportes;
	Facturacion factura;
	Productos producto;
	DefaultTableModel modelo;
	JSpinner spiner;
	SpinnerNumberModel sModel;
	Date fecha;
	DecimalFormat formato;

	public CtrlDevoluciones(IMenu menu, Reportes reportes) {
		this.menu = menu;
		this.reportes = reportes;
		formato = new DecimalFormat("#############.##");
		this.factura = new Facturacion();
		this.producto = new Productos();
		this.modelo = new DefaultTableModel();
		menu.btnDevolverProducto.addActionListener(this);
		this.menu.vistaDetalleFacturas.addWindowListener(this);
		this.sModel = new SpinnerNumberModel();
		this.sModel.setMinimum(0.00);
		this.sModel.setValue(0.00);
		this.sModel.setStepSize(0.01);
		this.spiner = new JSpinner(sModel);
		this.fecha = new Date();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menu.btnDevolverProducto) {
			DevolverProducto();
		}
	}
	//funcion para devolver productos

	public void DevolverProducto() {
		Date fecha = menu.jcFacturasEmitidas.getDate();
		int idProducto = 0, filaseleccionada = 0, filaseleccionadaR = 0, idDetalle = 0, idFactura = 0, filas, credito = 0;
		String precioDolar = menu.txtPrecioDolarVenta.getText(), bandera, restarString;
		float precio = 0,
			cantidadActual = 0,
			total = 0,
			totalUpdate = 0,
			ivaUpdate = 0,
			subTotalUpdate = 0,
			cantidadUpdate = 0,
			cantidadDevolver = 0,
			sacarImpuesto = 0,
			porcentajeImp = 0,
			importe = 0,
			importeActual = 0,
			tasaCambio = 0,
			restar = 0;
		this.modelo = (DefaultTableModel) menu.tblMostrarDetalleFactura.getModel();
		try {
			//numero de filas
			filas = this.modelo.getRowCount();
			//fila seleccionada de la tabla DetalleFactura
			filaseleccionada = menu.tblMostrarDetalleFactura.getSelectedRow();
			//fila seleccionada de la tabla reportes
			filaseleccionadaR = menu.tblReporte.getSelectedRow();
			sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
			//obtengo el IVA en entero "15" o cualquier que sea el impuesto
			porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = descuento de producto"

			if (filaseleccionada == -1) {

			} else {
				int confirmar = JOptionPane.showConfirmDialog(null, spiner, "Cantidad a devolver:", JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					importeActual = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 6).toString());
					cantidadDevolver = Float.parseFloat(spiner.getValue().toString());
					idDetalle = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
					idProducto = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 1).toString());
					precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 5).toString());
					cantidadActual = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
					bandera = (String) this.modelo.getValueAt(filaseleccionada, 7);
					idFactura = Integer.parseInt(menu.tblReporte.getValueAt(filaseleccionadaR, 0).toString());
					credito = (menu.tblReporte.getValueAt(filaseleccionadaR, 6) == null) ? 0 :
						Integer.parseInt(menu.tblReporte.getValueAt(filaseleccionadaR, 6).toString());
					total = this.factura.obtenerTotalFacturaSeleccionada(idFactura);//Float.parseFloat(menu.tblReporte.getValueAt(filaseleccionadaR, 3).toString());
					if (bandera.equals("v")) {
						this.factura.monedaVentaProducto(String.valueOf(idProducto));
					} else if (bandera.equals("c")) {
						this.factura.monedaCompraProducto(String.valueOf(idProducto));
					}
					//validar que lo que se va a devolver sea menor o igual que lo que compro
					if (cantidadDevolver <= cantidadActual) {
						cantidadUpdate = cantidadActual - cantidadDevolver;
						//validar que moneda
						if (this.factura.getMonedaVenta().equals("Dolar")) {
							//validar que precioDolar sea numerico
							tasaCambio = (importeActual / cantidadActual) / precio;
							importe = (cantidadUpdate * precio) * tasaCambio;
							restar = (cantidadDevolver * precio) * tasaCambio;
							restarString = this.formato.format(restar);
							restar = Float.parseFloat(restarString);
							totalUpdate = total - restar;
						} else {
							importe = cantidadUpdate * precio;
							totalUpdate = total - (cantidadDevolver * precio);
						}
						//calcular el nuevo impuesto
						ivaUpdate = ((totalUpdate / sacarImpuesto) * porcentajeImp) / 100;
						//System.out.println(ivaUpdate);
						//calcular el nuevo subtotal
						subTotalUpdate = totalUpdate - ivaUpdate;
						//llamar las funciones para actualizar los datos correpondientes
						this.factura.ActualizarDetalle(
							String.valueOf(idDetalle),
							String.valueOf(idProducto),
							String.valueOf(precio),
							String.valueOf(cantidadUpdate),
							String.valueOf(importe)
						);
						this.factura.ActualizarDevolucion(
							idFactura,
							ivaUpdate,
							totalUpdate
						);
						this.producto.AgregarProductoStock(
							String.valueOf(idProducto),
							String.valueOf(cantidadDevolver)
						);
						MostrarDetalleFactura(idFactura);
						CtrlCreditos.cambiarEstado(credito);
						MostrarProductos("");
						MostrarProductosVender("");
					} else {
						JOptionPane.showMessageDialog(
							null,
							"Error.. excede a la cantidad vendida.",
							"Advertencia",
							JOptionPane.WARNING_MESSAGE);
					}
				} else {

				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex + " en la funcion DevolverProducto en ctrlDevoluciones");
		}
	}

	public void MostrarDetalleFactura(int id)//metodo para llenar la tabla que muestra el detalle de las facturas de reportes
	{
		menu.tblMostrarDetalleFactura.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblMostrarDetalleFactura.getTableHeader().setOpaque(false);
		menu.tblMostrarDetalleFactura.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblMostrarDetalleFactura.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblMostrarDetalleFactura.setModel(reportes.DetalleFactura(id));
	}

	public void MostrarReportesDario(Date fecha1)//metodo para llenar la tabla de reortes por rango o mensual del menu reportes
	{
		long f1 = fecha1.getTime();//
		java.sql.Date fechaInicio = new java.sql.Date(f1);
		menu.tblReporte.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblReporte.getTableHeader().setOpaque(false);
		menu.tblReporte.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblReporte.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblReporte.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
		try {
			menu.tblReporte.setModel(reportes.ReporteDiario(fechaInicio));

		} catch (Exception err) {

		}

	}

	public void MostrarProductos(String buscar) {
		menu.tblProductos.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblProductos.getTableHeader().setOpaque(false);
		menu.tblProductos.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblProductos.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblProductos.setModel(this.producto.ConsultaAdmin(buscar));
	}

	public void MostrarProductosVender(String Buscar) {
		menu.tblAddProductoFactura.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblAddProductoFactura.getTableHeader().setOpaque(false);
		menu.tblAddProductoFactura.getTableHeader().setBackground(new Color(100, 100, 100));
		menu.tblAddProductoFactura.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblAddProductoFactura.setModel(factura.BusquedaGeneralProductoVender(Buscar));
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		MostrarReportesDario(this.fecha);

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
