/*
 * CLASE QUE SE ENCARGA DE TODAS LAS GESTIONES DE FACTURACION 
 */
package controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import vista.IMenu;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlFacturacion implements ActionListener, CaretListener, MouseListener, KeyListener, ItemListener {

	IMenu menu;
	int permiso;
	Facturacion factura;
	Date fecha;
	DefaultTableModel modelo;
	CtrlProducto productos;
	CtrlCreditos creditos;
	CtrlReportes reportes;
	Productos modelProduct;
	//formato para los totales
	DecimalFormat formato;
	Reportes r;
	Creditos c;
	SpinnerNumberModel sModel;
	boolean dolarisado;
	JSpinner spiner;
	static float total;
	float subTotal, isv, descuento;
	String[] nD;

	public CtrlFacturacion(IMenu menu, Facturacion factura, int permiso) {
		this.fecha = new Date();
		this.total = 0;
		this.isv = 0;
		this.subTotal = 0;
		this.descuento = 0;
		this.permiso = permiso;
		this.menu = menu;
		this.factura = factura;
		this.menu.jcFechaVencimientoFactura.setDate(new Date());
		this.menu.cmbFormaPago.setModel(factura.FormasPago());
		this.formato = new DecimalFormat("#############.00");
		this.c = new Creditos();
		this.r = new Reportes();
		this.modelo = new DefaultTableModel();
		this.creditos = new CtrlCreditos(menu, c);
		this.reportes = new CtrlReportes(menu, r);
		this.modelProduct = new Productos();
		this.productos = new CtrlProducto(modelProduct, menu, 0);
		this.menu.btnActualizarFactura.setVisible(false);
		this.menu.btnGuardarFactura.addActionListener(this);
		this.menu.btnGuardarFactura.addKeyListener(this);
		this.menu.btnCobrarSinImprimir.addActionListener(this);
		this.menu.btnGuardarSalidaMoneda.addActionListener(this);
		this.menu.btnSalidaMonedas.addActionListener(this);
		this.menu.btnActualizarFactura.addActionListener(this);
		this.menu.btnEliminarFilaFactura.addActionListener(this);
		this.menu.btnNuevaFactura.addActionListener(this);
		this.menu.btnCreditoFactura.addActionListener(this);
		this.menu.tblAddCreditoFactura.addMouseListener(this);
		this.menu.tblAddProductoFactura.addMouseListener(this);
		this.menu.btnEditarImpuesto.addActionListener(this);
		this.menu.btnAgregarProductoFactura.addActionListener(this);
		this.menu.btnEditarFactura.addActionListener(this);
		this.menu.btnLimpiarCliente.addActionListener(this);
		this.menu.btnAgregar.addActionListener(this);
		this.menu.btnAgregar.addKeyListener(this);
		this.menu.btnDividirPago.addActionListener(this);
		this.menu.btnGuardarMonedasRecibidas.addActionListener(this);
		this.menu.subirPrecio.addActionListener(this);
		this.menu.txtCodBarraFactura.addKeyListener(this);
		this.menu.btnAgregarProductoFactura.addKeyListener(this);
		this.menu.addDescuento.addActionListener(this);
		this.menu.addDescuentoDirecto.addActionListener(this);
		this.menu.addDescuentoPorcentaje.addActionListener(this);
		this.menu.addMasProducto.addActionListener(this);
		this.menu.tblFactura.addKeyListener(this);
		this.menu.txtPagoCon.addCaretListener(this);
		this.menu.txtPagoCon.addKeyListener(this);
		this.menu.txtCambio.addCaretListener(this);
		this.menu.txtTotal.addCaretListener(this);
		this.menu.cmbPrecioCompraVentaAdmin.addItemListener(this);
		this.sModel = new SpinnerNumberModel();
		this.sModel.setMinimum(0.00);
		this.sModel.setValue(0.00);
		this.sModel.setStepSize(0.01);
		this.spiner = new JSpinner(sModel);
		EstiloTablaFacturacion();
		editarISV("");
		DeshabilitarBtnGuardarFactura();
		validarPermiso();
		this.menu.jcFechaFactura.setDate(fecha);
		menu.txtNumeroFactura.setText(factura.ObtenerIdFactura());
		menu.txtCodBarraFactura.requestFocus();
		mostrarCmbPrecioVenta();
	}

	public void mostrarCmbPrecioVenta() {
//		if (permiso == 2) {
//			menu.cmbPrecioCompraVentaAdmin.setVisible(false);
//		}
	}

	public void validacionAntesDeGuardarImprimir() {
		String totalF = menu.txtTotal.getText();
		String idCreditoL = menu.txtCreditoFactura.getText();
		float saldo, sumar, limite;
		if (!idCreditoL.equals("")) {
			saldo = this.creditos.creditoPorCliente(idCreditoL);
			limite = this.creditos.limiteCredito(idCreditoL);
			sumar = saldo + Float.parseFloat(totalF);
			if (sumar > limite) {
				JOptionPane.showMessageDialog(
					null,
					"Estaá excediendo el límite de crédito",
					"Advertencia",
					JOptionPane.WARNING_MESSAGE
				);
				menu.btnGuardarFactura.setEnabled(false);
			} else {
				guardarFactura();
			}
		} else {
			guardarFactura();
		}
	}

	public void validacionAntesDeGuardarSinImprimir() {
		String totalF = menu.txtTotal.getText();
		String idCreditoL = menu.txtCreditoFactura.getText();
		float saldo, sumar, limite;
		if (!idCreditoL.equals("")) {
			saldo = this.creditos.creditoPorCliente(idCreditoL);
			limite = this.creditos.limiteCredito(idCreditoL);
			sumar = saldo + Float.parseFloat(totalF);
			if (sumar > limite) {
				JOptionPane.showMessageDialog(
					null,
					"Estaá excediendo el límite de crédito",
					"Advertencia",
					JOptionPane.WARNING_MESSAGE
				);
				menu.btnGuardarFactura.setEnabled(false);
			} else {
				guardarFacturaSinImprimir();
			}
		} else {
			guardarFacturaSinImprimir();
		}
	}

	public void validacionDePermisoAntesDeAgregar() {
		String code = this.menu.txtCodBarraFactura.getText();
		switch (this.permiso) {
			case 1: {
				String precio = menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString();
				if (!code.equals("")) {
					if (precio.equals("Precio venta")) {
						AgregarProductoFacturaEnter("venta");
					} else if (precio.equals("Precio compra")) {
						AgregarProductoFacturaEnter("compra");
					} else if (precio.equals("Factura Dolarizada")) {
						AgregarProductoFacturaEnter("dolarizada");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Escriba un código de barras..");
				}
			}
			break;
			case 2: {
				String precio = menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString();
				if (!code.equals("")) {
					if (precio.equals("Precio venta")) {
						AgregarProductoFacturaEnter("venta");
					} else if (precio.equals("Factura Dolarizada")) {
						AgregarProductoFacturaEnter("dolarizada");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Escriba un código de barras..");
				}
			}
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		if (e.getSource() == menu.btnGuardarFactura) {
			this.validacionAntesDeGuardarImprimir();
		}
		if (e.getSource() == menu.btnCobrarSinImprimir) {
			this.validacionAntesDeGuardarSinImprimir();
		}
		if (e.getSource() == menu.btnActualizarFactura) {
			actualizarFactura();
		}
		if (e.getSource() == menu.btnEditarFactura) {
			editarFactura();
		}
		if (e.getSource() == menu.btnAgregarProductoFactura) {
			mostrarVentanaProductos();
		}
		if (e.getSource() == menu.btnEliminarFilaFactura) {
			eliminarFilaFactura();
		}
		if (e.getSource() == menu.btnNuevaFactura) {
			nuevaFactura();
		}
		if (e.getSource() == menu.btnEditarImpuesto) {
			actualizarIVA();
		}
		if (e.getSource() == menu.btnCreditoFactura) {
			mostrarVentanaCreditos();
		}
		if (e.getSource() == menu.btnLimpiarCliente) {
			limpiarFormularioClienteFactura();
		}
		if (e.getSource() == menu.btnAgregar) {
			this.validacionDePermisoAntesDeAgregar();
		}
		if (e.getSource() == menu.addDescuento) {
			addDescuento();
		}
		if (e.getSource() == menu.addDescuentoDirecto) {
			addDescuentoDirecto();
		}
		if (e.getSource() == menu.addDescuentoPorcentaje) {
			addDescuentoPorcentaje();
		}
		if (e.getSource() == menu.addMasProducto) {
			addMasProducto();
		}
		if (e.getSource() == menu.btnDividirPago) {
			this.menu.jdMonedasRecibidas.setSize(325, 260);
			this.menu.jdMonedasRecibidas.setLocationRelativeTo(null);
			this.menu.jdMonedasRecibidas.setVisible(true);
			this.menu.chexIngresoMonedasFactura.setSelected(true);
			this.menu.chexIngresoMonedasFactura.setEnabled(false);
			this.menu.chexIngresoMonedasPago.setSelected(false);
			this.menu.chexIngresoMonedasPago.setEnabled(false);
			this.menu.chexIngresoCompraDolar.setSelected(false);
			this.menu.jsFacturaPago.setValue(Integer.parseInt(menu.txtNumeroFactura.getText()));
		}
		if (e.getSource() == menu.btnSalidaMonedas) {
			this.menu.jdSalidaMonedas.setSize(325, 260);
			this.menu.jdSalidaMonedas.setLocationRelativeTo(null);
			this.menu.jdSalidaMonedas.setVisible(true);
			this.menu.chexEgresoMonedasFactura.setSelected(true);
			this.menu.chexEgresoMonedasFactura.setEnabled(false);
			this.menu.chexEgresoMonedasPago.setSelected(false);
			this.menu.chexEgresoMonedasPago.setEnabled(false);
		}
		if (e.getSource() == menu.subirPrecio) {
			this.subirPrecio();
		}
	}

	public void calculosParaCambio() {
		String total = menu.txtTotal.getText(), pagoCon = menu.txtPagoCon.getText();
		float cambio = 0;
		if (menu.isNumeric(pagoCon)) {
			cambio = Float.parseFloat(pagoCon) - Float.parseFloat(total);
			menu.txtCambio.setText(String.valueOf(cambio));
		} else if (pagoCon.equals("")) {
			menu.txtCambio.setText("");
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		if (e.getSource() == menu.txtPagoCon) {
			this.calculosParaCambio();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == menu.tblAddProductoFactura) {
			switch (this.permiso) {
				case 1: {
					String condicion = menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString();
					if (condicion.equals("Precio venta")) {
						addProductoFactura();
					} else if (condicion.equals("Precio compra")) {
						addProductoFacturaAdmin();
					} else if (condicion.equals("Factura Dolarizada")) {
						addProductoFacturaDolarizado();
					}
				}
				break;
				case 2: {
					String condicion = menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString();
					if (condicion.equals("Precio venta")) {
						addProductoFactura();
					} else if (condicion.equals("Factura Dolarizada")) {
						addProductoFacturaDolarizado();
					}
				}
				break;
			}

		}
		if (e.getSource() == menu.tblAddCreditoFactura) {
			if (e.getClickCount() == 2) {
				addCreditoFactura();
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e
	) {
	}

	@Override
	public void mouseReleased(MouseEvent e
	) {
	}

	@Override
	public void mouseEntered(MouseEvent e
	) {
	}

	@Override
	public void mouseExited(MouseEvent e
	) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.VK_ENTER == e.getKeyCode()) {
			this.validacionDePermisoAntesDeAgregar();
		}

		if (e.VK_F10 == e.getKeyCode()) {
			mostrarVentanaProductos();
		}
		if (e.VK_SPACE == e.getKeyCode()) {

			String totalF = menu.txtTotal.getText();
			String idCreditoL = menu.txtCreditoFactura.getText();
			float saldo, sumar, limite;
			if (!idCreditoL.equals("")) {
				saldo = this.creditos.creditoPorCliente(idCreditoL);
				limite = this.creditos.limiteCredito(idCreditoL);
				sumar = saldo + Float.parseFloat(totalF);
				System.out.println(sumar + " " + limite);
				if (sumar > limite) {
					JOptionPane.showMessageDialog(null, "Esta excediendo el limite de credito", "Advertencia", JOptionPane.WARNING_MESSAGE);
					menu.btnGuardarFactura.setEnabled(false);
				} else {
					guardarFactura();
				}
			} else {
				guardarFactura();
			}
			menu.txtCodBarraFactura.setText("");
			// menu.txtCodBarraFactura.requestFocus();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public void guardarFactura() {
		try {
			//obtengo el modelo de tabla factura y sus datos
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			int filas = this.modelo.getRowCount();//Cuento las filas de la tabla Factura
			//guardara la factura solo si el boton guardar factura esta habilatado
			if (menu.btnGuardarFactura.isEnabled()) {
				Date fecha, fechaVencimiento;
				int dolarizado;
				String[] ArregloImprimir = new String[filas];
				String factura = "",
					id,
					cantidad,
					precio,
					totalDetalle,
					idCredito,
					iva,
					totalFactura,
					formaPago,
					idFormaPago,
					comprador,
					cliente,
					subtotal,
					nombreProduct,
					tipoVenta,
					pagoCon,
					cambio,
					anotaciones,
					tasaCambio,
					bandera;//variables para capturar los datos a guardar
				pagoCon = menu.txtPagoCon.getText();
				bandera = (menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString().equals("Precio compra")) ? "c" : "v";
				cambio = menu.txtCambio.getText();
				subtotal = menu.txtSubTotal.getText();
				comprador = menu.txtCompradorFactura.getText();
				cliente = menu.txtNClienteFactura.getText() + " " + menu.txtAClienteFactura.getText();
				//capturo la fecha del dateshooser
				fecha = menu.jcFechaFactura.getDate();
				long fechaF = fecha.getTime();//
				fechaVencimiento = menu.jcFechaVencimientoFactura.getDate();
				//convertir la fecha obtenida a formato sql
				java.sql.Date fechaFactura = new java.sql.Date(fechaF);
				anotaciones = menu.txtNfacturaMembretada.getText();
				//obtengo el numero de credito al que pertenecera la factura
				idCredito = menu.txtCreditoFactura.getText();
				tipoVenta = (idCredito.equals("")) ? "Contado" : "Credito";
				iva = menu.txtImpuesto.getText();//obtengo el iva
				totalFactura = menu.txtTotal.getText();//obtengo total de factura
				//capturo el nombre de forma de pago
				formaPago = (String) menu.cmbFormaPago.getSelectedItem();
				//capturo el id de la forma de pago que retorna la funcion obtenerformapago 
				idFormaPago = this.factura.ObtenerFormaPago(formaPago);

				tasaCambio = this.menu.txtPrecioDolarVenta.getText();
				dolarizado = (this.dolarisado) ? 1 : 0;
				//envio los datos a guardar de la factura
				this.factura.setCaja(1);
				this.factura.setFecha(fechaFactura);
				this.factura.setNombreComprador(comprador);
				this.factura.setCredito(idCredito);
				this.factura.setPago(idFormaPago);
				this.factura.setIva(iva);
				this.factura.setTotal(totalFactura);
				this.factura.setAnotacion(anotaciones);
				this.factura.setDolarizado(dolarizado);
				this.factura.setFechaVencimiento(new java.sql.Date(fechaVencimiento.getTime()));
				this.factura.GuardarFactura();
				//for para recorrer la tabla factura
				for (int cont = 0; cont < filas; cont++) {
					//capturo el id de producto para guardar en detallefactura
					id = (String) this.modelo.getValueAt(cont, 0);
					cantidad = (String) this.modelo.getValueAt(cont, 2);
					nombreProduct = (String) this.modelo.getValueAt(cont, 3);
					precio = (String) this.modelo.getValueAt(cont, 4);
					totalDetalle = (String) this.modelo.getValueAt(cont, 5);
					factura = menu.txtNumeroFactura.getText();
					//envio los datos a guardar de los detalles
					this.factura.setFactura(factura);
					this.factura.setProductoDetalle(id);
					this.factura.setPrecio(precio);
					this.factura.setCantidad(cantidad);
					this.factura.setImporteDetalle(totalDetalle);
					this.factura.setBandera(bandera);
					this.factura.setPrecioDolar(Float.parseFloat(tasaCambio));
					this.factura.setDolarizado(dolarizado);
					this.factura.DetalleFactura();
//                    			validar si el nombre del producto es mayor de 10 caracteres
					if (nombreProduct.length() > 10) {
						nombreProduct = nombreProduct.substring(0, 10);
					}
					ArregloImprimir[cont] = nombreProduct
						+ " " + cantidad
						+ "   " + precio
						+ "  " + totalDetalle + "\n";
				}
				//Actualizo el campo numero de factura con la funcion obtenerIdFactura
				menu.txtNumeroFactura.setText(this.factura.ObtenerIdFactura());
				menu.txtCodBarraFactura.setText("");
				menu.txtCodBarraFactura.requestFocus();
				DeshabilitarBtnGuardarFactura();
				productos.MostrarProductos("");
				productos.MostrarProductosVender("");
				CtrlCreditos.cambiarEstado(
					(idCredito.equals("")) ? 0 : Integer.parseInt(idCredito)
				);
				LimpiarTablaFactura();//limpio la factura
//				creditos.ActualizarEstadoCreditoApendiente();
//				creditos.ActualizarEstadoCreditoAabierto();
				//creditos.MostrarCreditos("");
				creditos.MostrarCreditosCreados("");
				reportes.MostrarReportesDario(this.fecha);
				reportes.reportesDiarios(this.fecha);
				reportes.ReporteGlobal();
				reportes.SumaTotalFiltroReporte(this.fecha, this.fecha);
				reportes.inversion();
				reportes.mostrarProductosMasVendidios(this.fecha, this.fecha);
				creditos.MostrarCreditos("");
				creditos.MostrarCreditosAddFactura("");
				Imprimir(
					factura,
					comprador,
					cliente,
					tipoVenta,
					formaPago,
					ArregloImprimir,
					subtotal,
					iva,
					totalFactura,
					fechaFactura.toString(),
					pagoCon,
					cambio
				);
			} else {
				JOptionPane.showMessageDialog(null, "La factura esta vacia");
			}
		} catch (Exception err) {
			//JOptionPane.showMessageDialog(null, err);
		}
	}

	public void guardarFacturaSinImprimir() {
		try {
			//obtengo el modelo de tabla factura y sus datos
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			int filas = this.modelo.getRowCount();//Cuento las filas de la tabla Factura
			//guardara la factura solo si el boton guardar factura esta habilatado
			if (menu.btnGuardarFactura.isEnabled()) {
				Date fecha, fechaVencimiento;
				int dolarizado;
				String[] ArregloImprimir = new String[filas];
				String factura = "",
					id,
					cantidad,
					precio,
					totalDetalle,
					idCredito,
					iva,
					totalFactura,
					formaPago,
					idFormaPago,
					comprador,
					cliente,
					subtotal,
					nombreProduct,
					tipoVenta,
					pagoCon,
					cambio,
					anotaciones,
					tasaCambio,
					bandera;//variables para capturar los datos a guardar
				pagoCon = menu.txtPagoCon.getText();
				bandera = (menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString().equals("Precio compra")) ? "c" : "v";
				cambio = menu.txtCambio.getText();
				subtotal = menu.txtSubTotal.getText();
				comprador = menu.txtCompradorFactura.getText();
				cliente = menu.txtNClienteFactura.getText() + " " + menu.txtAClienteFactura.getText();
				//capturo la fecha del dateshooser
				fecha = menu.jcFechaFactura.getDate();
				long fechaF = fecha.getTime();//
				fechaVencimiento = menu.jcFechaVencimientoFactura.getDate();
				//convertir la fecha obtenida a formato sql
				java.sql.Date fechaFactura = new java.sql.Date(fechaF);
				anotaciones = menu.txtNfacturaMembretada.getText();
				//obtengo el numero de credito al que pertenecera la factura
				idCredito = menu.txtCreditoFactura.getText();
				tipoVenta = (idCredito.equals("")) ? "Contado" : "Credito";
				iva = menu.txtImpuesto.getText();//obtengo el iva
				totalFactura = menu.txtTotal.getText();//obtengo total de factura
				//capturo el nombre de forma de pago
				formaPago = (String) menu.cmbFormaPago.getSelectedItem();
				//capturo el id de la forma de pago que retorna la funcion obtenerformapago 
				idFormaPago = this.factura.ObtenerFormaPago(formaPago);
				tasaCambio = this.menu.txtPrecioDolarVenta.getText();
				dolarizado = (this.dolarisado) ? 1 : 0;
				//envio los datos a guardar de la factura
				this.factura.setCaja(1);
				this.factura.setFecha(fechaFactura);
				this.factura.setNombreComprador(comprador);
				this.factura.setCredito(idCredito);
				this.factura.setPago(idFormaPago);
				this.factura.setIva(iva);
				this.factura.setTotal(totalFactura);
				this.factura.setAnotacion(anotaciones);
				this.factura.setPrecioDolar(Float.parseFloat(tasaCambio));
				this.factura.setDolarizado(dolarizado);
				this.factura.setFechaVencimiento(new java.sql.Date(fechaVencimiento.getTime()));
				this.factura.GuardarFactura();
				//for para recorrer la tabla factura
				for (int cont = 0; cont < filas; cont++) {
					//capturo el id de producto para guardar en detallefactura
					id = (String) this.modelo.getValueAt(cont, 0);
					cantidad = (String) this.modelo.getValueAt(cont, 2);
					nombreProduct = (String) this.modelo.getValueAt(cont, 3);
					precio = (String) this.modelo.getValueAt(cont, 4);
					totalDetalle = (String) this.modelo.getValueAt(cont, 5);
					factura = menu.txtNumeroFactura.getText();
					//envio los datos a guardar de los detalles
					this.factura.setFactura(factura);
					this.factura.setProductoDetalle(id);
					this.factura.setPrecio(precio);
					this.factura.setCantidad(cantidad);
					this.factura.setImporteDetalle(totalDetalle);
					this.factura.setBandera(bandera);
					this.factura.setDolarizado(dolarizado);
					this.factura.DetalleFactura();
//                    			validar si el nombre del producto es mayor de 10 caracteres
					if (nombreProduct.length() > 10) {
						nombreProduct = nombreProduct.substring(0, 10);
					}
					ArregloImprimir[cont] = nombreProduct
						+ " " + cantidad
						+ "   " + precio
						+ "  " + totalDetalle + "\n";
				}
				//Actualizo el campo numero de factura con la funcion obtenerIdFactura
				menu.txtNumeroFactura.setText(this.factura.ObtenerIdFactura());
				menu.txtCodBarraFactura.setText("");
				menu.txtCodBarraFactura.requestFocus();
				DeshabilitarBtnGuardarFactura();
				productos.MostrarProductos("");
				productos.MostrarProductosVender("");
				CtrlCreditos.cambiarEstado(
					(idCredito.equals("")) ? 0 : Integer.parseInt(idCredito)
				);
				LimpiarTablaFactura();//limpio la factura
//				creditos.ActualizarEstadoCreditoApendiente();
//				creditos.ActualizarEstadoCreditoAabierto();
				//creditos.MostrarCreditos("");
				creditos.MostrarCreditosCreados("");
				reportes.MostrarReportesDario(this.fecha);
				reportes.reportesDiarios(this.fecha);
				reportes.ReporteGlobal();
				reportes.SumaTotalFiltroReporte(this.fecha, this.fecha);
				reportes.inversion();
				reportes.mostrarProductosMasVendidios(this.fecha, this.fecha);
				creditos.MostrarCreditos("");
				creditos.MostrarCreditosAddFactura("");

			} else {
				JOptionPane.showMessageDialog(null, "La factura esta vacia");
			}
		} catch (Exception err) {
			//JOptionPane.showMessageDialog(null, err);
		}
	}

	public void mostrarVentanaProductos() {
		menu.AddProductoFactura.setSize(1071, 456);
		menu.AddProductoFactura.setVisible(true);
		menu.AddProductoFactura.setLocationRelativeTo(null);
		if (menu.rbBuscarNombreCodBarra.isSelected() == true) {
			menu.txtBuscarPorNombre.setEnabled(true);
			menu.txtBuscarPorCategoria.setEnabled(false);
			menu.txtBuscarPorLaboratorio.setEnabled(false);
			menu.txtBuscarPorNombre.requestFocus();
			menu.txtBuscarPorNombre.selectAll();
		}
	}

	public void AgregarProductoFacturaEnter(String condicion) {
		String codBarra = menu.txtCodBarraFactura.getText();
		String precioDolar = menu.txtPrecioDolarVenta.getText(), id = "";
		int filas = 0;
		float totalImports = 0, sacarImpuesto = 0, porcentajeImp = 0, cantidadUpdate = 0, importeUpdate = 0, cantidadActual = 0, precio = 0;
		if (!menu.isNumeric(precioDolar) || precioDolar.equals("0")) {
			menu.txtPrecioDolarVenta.setText("1");
		} else {
			precioDolar = menu.txtPrecioDolarVenta.getText();
			this.factura.setPrecioDolar(Float.parseFloat(precioDolar));
			switch (condicion) {
				case "venta": {
					this.factura.obtenerPorCodBarra(codBarra);
				}
				break;
				case "compra": {
					this.factura.obtenerPorCodBarraPrecioCompra(codBarra);
				}
				break;
				case "dolarizada": {
					this.factura.obtenerPorCodBarraPrecioDolarisado(codBarra, Float.parseFloat(precioDolar));
				}
				break;
			}

			if (this.factura.getProducto()[0] != null) {
				if (this.factura.getStock() > 0) {
					this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
					this.modelo.addRow(this.factura.getProducto());
					filas = this.modelo.getRowCount();
					this.factura.Vender(this.factura.getProducto()[0], this.factura.getProducto()[2]);
					for (int i = 0; i < filas; i++) {
						totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
					}
					sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());
					porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
					this.total = totalImports;
					this.isv = ((this.total / sacarImpuesto) * porcentajeImp) / 100;
					this.subTotal = this.total - this.isv;
					menu.txtSubTotal.setText("" + formato.format(this.subTotal));
					menu.txtImpuesto.setText("" + formato.format(this.isv));
					menu.txtTotal.setText("" + formato.format(this.total));
					menu.txtCodBarraFactura.setText("");
					DeshabilitarBtnGuardarFactura();
					productos.MostrarProductosVender("");
				} else {
					JOptionPane.showMessageDialog(null, "No hay suficiente producto en stock para realizar la venta");
				}
			} else {
				JOptionPane.showMessageDialog(null, "El producto no esta ingresado...");
				//menu.txtCodBarraFactura.setText("");
			}
		}
	}

	public void Retornar() {
		this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
		menu.pnlVentas.setVisible(false);
		menu.pnlReportes.setVisible(true);
		menu.btnActualizarFactura.setVisible(false);
		try {
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			int filas = this.modelo.getRowCount();//numero de filas de la tabla factura
			for (int i = 0; i < filas; i++) {
				this.modelo.removeRow(0);//remover filas de la tabla factura
			}
			productos.MostrarProductosVender("");//acturalizar tabla que muestra productos a vender
			//limpiar
			menu.btnGuardarFactura.setEnabled(true);
			menu.txtNClienteFactura.setText("");
			menu.txtAClienteFactura.setText("");
			menu.lblIdClienteFactura.setText("");
			menu.txtCreditoFactura.setText("");
			menu.txtCompradorFactura.setText("");
			this.total = 0;
			this.subTotal = 0;
			this.isv = 0;
			//inicializar a 0.0
			menu.txtSubTotal.setText("" + this.total);
			menu.txtImpuesto.setText("" + this.subTotal);
			menu.txtTotal.setText("" + this.isv);

		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}
		menu.txtNumeroFactura.setText(factura.ObtenerIdFactura());//actualizar numero de factura
	}

	public void Imprimir(String Nfactura, String comprador, String cliente, String tipoVenta, String formaPago, String[] Datos, String subtotal, String isv, String total, String fecha, String recibido, String cambio) {
		InfoFactura info = new InfoFactura();
		info.obtenerInfoFactura();
		Ticket d = new Ticket(info.getNombre(), info.getDireccion(), info.getTelefono(), info.getRfc(), info.getRango(), "1", Nfactura, "Cajero", comprador, cliente, tipoVenta, formaPago, fecha, Datos, subtotal, isv, total, recibido, cambio);

		try {
			d.printInfo();
		} catch (Exception e) {

		}
	}

	//metodo para editar el impuesto de la factura
	public void editarISV(String isv) {
		if (isv.equals("")) {
			menu.lblImpuestoISV.setText("15");
		} else {
			menu.lblImpuestoISV.setText(isv);
		}
	}

	public void LimpiarTablaFactura() {
		try {
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			int filas = this.modelo.getRowCount();
			for (int i = 0; i < filas; i++) {
				this.modelo.removeRow(0);
			}
			productos.MostrarProductosVender("");
			menu.txtNClienteFactura.setText("");
			menu.txtAClienteFactura.setText("");
			menu.lblIdClienteFactura.setText("");
			menu.txtCreditoFactura.setText("");
			menu.lblIdClienteFactura.setText("");
			menu.txtCompradorFactura.setText("");
			menu.txtNfacturaMembretada.setText("");
			menu.cmbFormaPago.setSelectedItem("Efectivo");
			menu.cmbPrecioCompraVentaAdmin.setSelectedItem("Precio venta");
			menu.txtPagoCon.setText("");
			menu.txtCambio.setText("");
			this.total = 0;
			this.subTotal = 0;
			this.isv = 0;
			menu.txtSubTotal.setText("" + this.total);
			menu.txtImpuesto.setText("" + this.subTotal);
			menu.txtTotal.setText("" + this.isv);
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}

	}

	public void EstiloTablaFacturacion() {
		menu.tblFactura.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblFactura.getTableHeader().setOpaque(false);
		menu.tblFactura.getTableHeader().setBackground(new Color(69, 76, 89));
		menu.tblFactura.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblFactura.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 35));
	}

	public void DeshabilitarBtnGuardarFactura() {
		if (menu.tblFactura.getRowCount() > 0) {
			menu.btnGuardarFactura.setEnabled(true);
			menu.btnCobrarSinImprimir.setEnabled(true);
			menu.cmbPrecioCompraVentaAdmin.setEnabled(false);
		} else {
			menu.btnGuardarFactura.setEnabled(false);
			menu.btnCobrarSinImprimir.setEnabled(false);
			menu.cmbPrecioCompraVentaAdmin.setEnabled(true);
		}
	}

	public void addProductoFactura() {
		int filaseleccionada = menu.tblAddProductoFactura.getSelectedRow();
		try {
			String id, codigo, nombre, precio, cantidad, total, importe, stockA, monedaVenta = "";
			float imp = 0, calcula, impuesto, descProduct = 0, stock, cantidadPVender,
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
				sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText()),//concatenacion para sacar el valor 1.xx para sacar el iva
				porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"

			if (filaseleccionada == -1) {

			} else {
//              if (this.aumento == 0) {   //no se aplica aumento
				//capturar los datos de la tabla producto para mandarlos a tabla factura
				this.modelo = (DefaultTableModel) menu.tblAddProductoFactura.getModel();
				id = modelo.getValueAt(filaseleccionada, 0).toString();
				codigo = modelo.getValueAt(filaseleccionada, 1).toString();
				nombre = modelo.getValueAt(filaseleccionada, 2).toString();
				precio = modelo.getValueAt(filaseleccionada, 3).toString();
				monedaVenta = (String) modelo.getValueAt(filaseleccionada, 4);
				stock = Float.parseFloat(modelo.getValueAt(filaseleccionada, 6).toString());
				cantidad = JOptionPane.showInputDialog(null, "Cantidad:");
				//si cantidad no recibe la cantidad se le va asignar 0
				if (cantidad.equals("")) {
					cantidad = "0";
				}
				//convertir a flota la variable cantidad 1006195
				cantidadPVender = Float.parseFloat(cantidad);
				//validacion para la venta sugun lo que hay en stock osea no se pueda vender mas de lo que hay en stock
				if (cantidadPVender <= stock) {
					//si cantidadPVender es igual a 0 no realizar ninguna accion
					if (cantidadPVender == 0) {

					} else {
						if (monedaVenta.equals("Córdobas")) {
							imp = (Float.parseFloat(precio) * cantidadPVender);//importe total de compra de producto
						} else if (monedaVenta.equals("Dolar")) {
							imp = (Float.parseFloat(precio) * cantidadPVender) * precioDolar;//importe total de compra de producto
						}
						importe = formato.format(imp);
						//realizando los calculos de importe
						this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
						//pasar producto de tabla productos a tabla de factura
						String[] FilaElementos = {id, codigo, cantidad, nombre, precio, importe};
						this.modelo.addRow(FilaElementos);
//                                    calcula = (Float.parseFloat(importe));//convertir importe a float
						int filas = this.modelo.getRowCount();
						float totalImports = 0;
						for (int i = 0; i < filas; i++) {
							totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());//calcular el total de factura
						}
						this.total = totalImports;
						impuesto = ((this.total / sacarImpuesto) * porcentajeImp) / 100;//calcular el impuesto
						this.isv = impuesto;//impuesto
						this.subTotal = this.total - this.isv;//clacular subtotal de factura

						menu.txtImpuesto.setText("" + formato.format(this.isv));//establecer el valor impuesto en el campo impuesto de factura
						menu.txtSubTotal.setText("" + formato.format(this.subTotal));//establecer el valor impuesto en el campo sub total de factura
						menu.txtTotal.setText("" + formato.format(this.total));//establecer el valor impuesto en el campo Total de factura
						this.factura.Vender(id, cantidad);//llamar procedimiento sql para vender
						productos.MostrarProductosVender("");
						menu.txtBuscarPorNombre.selectAll();
						DeshabilitarBtnGuardarFactura();
					}

				} else {
					JOptionPane.showMessageDialog(null, "No hay suficiente producto en stock para realizar esta venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
				}

//                        } else if (this.aumento > 0) {   //aplicar aumento
//                            //capturar los datos de la tabla producto para mandarlos a tabla factura
//                            this.modelo = (DefaultTableModel) menu.tblAddProductoFactura.getModel();
//                            id = modelo.getValueAt(filaseleccionada, 0).toString();
//                            codigo = modelo.getValueAt(filaseleccionada, 1).toString();
//                            nombre = modelo.getValueAt(filaseleccionada, 2).toString();
//                            precio = modelo.getValueAt(filaseleccionada, 3).toString();
//                            monedaVenta = (String) modelo.getValueAt(filaseleccionada, 4).toString();
//                            stock = Float.parseFloat(modelo.getValueAt(filaseleccionada, 6).toString());
//                            //obtengo el precio restandole el aumento 
//                            descProduct = Float.parseFloat(precio) - this.aumento;
//                            //la cantidad que se va a vender
//                            cantidad = JOptionPane.showInputDialog(null, "Cantidad:");
//                            if (cantidad.equals("")) {
//                                cantidad = "0";
//                            }
//                            //convertir a float la variable cantidad
//                            cantidadPVender = Float.parseFloat(cantidad);
//                            //condicion para validar el estock con lo que se va a vender 
//                            if (cantidadPVender < stock || cantidadPVender == stock) {
//                                if (cantidadPVender == 0) {
//
//                                } else {
//                                    if (monedaVenta.equals("Córdobas")) {
//                                        imp = (descProduct * cantidadPVender);//importe total de compra de producto con aumento
//                                    } else if (monedaVenta.equals("Dolar")) {
//                                        float precioConDesc = (descProduct * cantidadPVender);//importe total de compra de producto con aumento
//                                        imp = precioConDesc * precioDolar;
//                                    }
//                                    DecimalFormat formato = new DecimalFormat("#############.##");
//                                    importe = String.valueOf(imp);
//                                    //realizando los calculos de importe
//                                    this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
//                                    //pasar producto de tabla productos a tabla de factura
//                                    String[] FilaElementos = {id, codigo, cantidad, nombre, String.valueOf(descProduct), importe};
//                                    this.modelo.addRow(FilaElementos);
////                                    calcula = (Float.parseFloat(importe));//convertir importe a float
//                                    int filas = this.modelo.getRowCount();
//                                    for (int i = 0; i < filas; i++) {
//                                        this.total += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());//calcular el total de factura
//                                    }
//                                    impuesto = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;//calcular el impuesto
//                                    this.isv = impuesto;//impuesto
//                                    this.subTotal = this.total - this.isv;//clacular subtotal de factura
//
//                                    menu.txtImpuesto.setText("" + formato.format(this.isv));//establecer el valor impuesto en el campo impuesto de factura
//                                    menu.txtSubTotal.setText("" + formato.format(this.subTotal));//establecer el valor impuesto en el campo sub total de factura
//                                    menu.txtTotal.setText("" + this.total);//establecer el valor impuesto en el campo Total de factura
//                                    this.aumento = 0;
//                                    this.factura.Vender(id, cantidad);
//                                    productos.MostrarProductosVender("");
//                                    menu.txtBuscarPorNombre.selectAll();
//                                    DeshabilitarBtnGuardarFactura();
//                                }
//
//                            } else {
//                                JOptionPane.showMessageDialog(null, "No hay suficiente producto en stock para realizar esta venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
//                                this.aumento = 0;//si no hay suficiente producto en stock inicializamos la variable aumento a 0
//                            }
//
//                        }
			}
		} catch (Exception err) {
			//JOptionPane.showMessageDialog(null, e);
		}
	}

	public void addProductoFacturaAdmin() {
		int filaseleccionada = menu.tblAddProductoFactura.getSelectedRow();
		try {
			String id, codigo, nombre, cantidad, total, importe, stockA, monedaVenta = "",
				precioAgregar = menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString();
			float imp = 0, calcula, impuesto, descProduct = 0, stock, cantidadPVender,
				precio,
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
				sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText()),//concatenacion para sacar el valor 1.xx para sacar el iva
				porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"

			if (filaseleccionada == -1) {

			} else {
//              if (this.aumento == 0) {   //no se aplica aumento
				//capturar los datos de la tabla producto para mandarlos a tabla factura
				this.modelo = (DefaultTableModel) menu.tblAddProductoFactura.getModel();
				id = modelo.getValueAt(filaseleccionada, 0).toString();
				codigo = modelo.getValueAt(filaseleccionada, 1).toString();
				nombre = modelo.getValueAt(filaseleccionada, 2).toString();
				precio = this.factura.precioCompraPordId(id);
				monedaVenta = this.factura.monedaCompraPordId(id);
				stock = Float.parseFloat(modelo.getValueAt(filaseleccionada, 6).toString());
				cantidad = JOptionPane.showInputDialog(null, "Cantidad:");
				//si cantidad no recibe la cantidad se le va asignar 0
				if (cantidad.equals("")) {
					cantidad = "0";
				}
				//convertir a flota la variable cantidad
				cantidadPVender = Float.parseFloat(cantidad);
				//validacion para la venta sugun lo que hay en stock osea no se pueda vender mas de lo que hay en stock
				if (cantidadPVender <= stock) {
					//si cantidadPVender es igual a 0 no realizar ninguna accion
					if (cantidadPVender == 0) {

					} else {
						if (monedaVenta.equals("Córdobas")) {
							imp = (precio * cantidadPVender);//importe total de compra de producto
						} else if (monedaVenta.equals("Dolar")) {
							imp = (precio * cantidadPVender) * precioDolar;//importe total de compra de producto
						}
						importe = formato.format(imp);
						//realizando los calculos de importe
						this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
						//pasar producto de tabla productos a tabla de factura
						String[] FilaElementos = {id, codigo, cantidad, nombre, String.valueOf(precio), importe};
						this.modelo.addRow(FilaElementos);
//                                    calcula = (Float.parseFloat(importe));//convertir importe a float
						int filas = this.modelo.getRowCount();
						float totalImports = 0;
						for (int i = 0; i < filas; i++) {
							totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());//calcular el total de factura
						}
						this.total = totalImports;
						impuesto = ((this.total / sacarImpuesto) * porcentajeImp) / 100;//calcular el impuesto
						this.isv = impuesto;//impuesto
						this.subTotal = this.total - this.isv;//clacular subtotal de factura

						menu.txtImpuesto.setText("" + formato.format(this.isv));//establecer el valor impuesto en el campo impuesto de factura
						menu.txtSubTotal.setText("" + formato.format(this.subTotal));//establecer el valor impuesto en el campo sub total de factura
						menu.txtTotal.setText("" + formato.format(this.total));//establecer el valor impuesto en el campo Total de factura
						this.factura.Vender(id, cantidad);//llamar procedimiento sql para vender
						productos.MostrarProductosVender("");
						menu.txtBuscarPorNombre.selectAll();
						DeshabilitarBtnGuardarFactura();
					}

				} else {
					JOptionPane.showMessageDialog(
						null,
						"No hay suficiente producto en stock para realizar esta venta",
						"Advertencia",
						JOptionPane.WARNING_MESSAGE
					);
				}
			}
		} catch (Exception err) {
			//JOptionPane.showMessageDialog(null, e);
		}
	}

	public void addCreditoFactura() {
		int filaseleccionada = menu.tblAddCreditoFactura.getSelectedRow();
		String nombre, apellido, credito;
		try {
			if (filaseleccionada == -1) {

			} else {
				this.modelo = (DefaultTableModel) menu.tblAddCreditoFactura.getModel();
				credito = (String) this.modelo.getValueAt(filaseleccionada, 0);
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 2);
				apellido = (String) this.modelo.getValueAt(filaseleccionada, 3);

				menu.txtCreditoFactura.setText(credito);
				menu.txtNClienteFactura.setText(nombre);
				menu.txtAClienteFactura.setText(apellido);
				menu.cmbFormaPago.setSelectedItem("Pendiente");
				menu.AddCreditoFactura.setVisible(false);
				menu.txtCodBarraFactura.requestFocus();
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}
	}

	public void mostrarVentanaCreditos() {
		menu.AddCreditoFactura.setSize(681, 363);
		menu.AddCreditoFactura.setVisible(true);
		menu.AddCreditoFactura.setLocationRelativeTo(null);
	}

	public void addMasProducto() {
		String nombre = "",
			id = "",
			codBarra = "",
			iu;
		float cantidadIngresar = 0,
			cantidadActual = 0,
			precio = 0,
			cantidadUpdate = 0,
			importeUpdate = 0,
			totalImports = 0,
			precioDolar = 0,
			sacarImpuesto = 0,
			porcentajeImp = 0;
		int filas = 0,
			filaseleccionada = 0;
		try {
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			filaseleccionada = menu.tblFactura.getSelectedRow();
			filas = this.modelo.getRowCount();
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {

				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
				id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 3);
				codBarra = (String) this.modelo.getValueAt(filaseleccionada, 1);
				factura.obtenerPorId(id);
				JOptionPane.showMessageDialog(null, spiner, "Cantidad de " + nombre + " a agregar:", JOptionPane.INFORMATION_MESSAGE);
				cantidadIngresar = Float.parseFloat(spiner.getValue().toString());
				if (cantidadIngresar <= factura.getStock()) {
					cantidadActual = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 2).toString());
					precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
					this.factura.monedaVentaProducto(id);
					cantidadUpdate = cantidadActual + cantidadIngresar;
					if (this.factura.getMonedaVenta().equalsIgnoreCase("Dolar")) {
						importeUpdate = (cantidadUpdate * precio) * precioDolar;
					} else if (this.factura.getMonedaVenta().equalsIgnoreCase("Córdobas")) {
						importeUpdate = (cantidadUpdate * precio);
					}
					this.modelo.setValueAt(String.valueOf(cantidadUpdate), filaseleccionada, 2);
					this.modelo.setValueAt(formato.format(importeUpdate), filaseleccionada, 5);
					this.factura.Vender(id, String.valueOf(cantidadIngresar));
					for (int i = 0; i < filas; i++) {
						totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
					}

					//formatreo de le inpuesto IVA
					sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
					//TOTAL DE FACTURA
					this.total = totalImports;
					//obtengo el IVA en entero "15" o cualquier que sea el impuesto
					porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
					this.isv = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;
					//calcular el subtotal de la factura
					this.subTotal = this.total - this.isv;
					//setear los campos total, subtotal, IVA
					menu.txtSubTotal.setText("" + formato.format(this.subTotal));
					menu.txtImpuesto.setText("" + formato.format(this.isv));
					menu.txtTotal.setText("" + formato.format(this.total));
					menu.txtCodBarraFactura.requestFocus();
					spiner.setValue(0.00);
					productos.MostrarProductosVender("");
				} else {
					JOptionPane.showMessageDialog(null, "No hay suficiente producto en stock para realizar la venta");
				}
			}
		} catch (Exception err) {
		}
	}

	public void addDescuento() {
		//variables para el nombre e id de producto
		String nombre = "", id = "";
		//variable para el numero de filas de la tabla factura
		int filaseleccionada = 0, filas = 0, confirmar = 0;
		//variables para las operaciones
		float cantidad = 0,
			precioUpdate = 0,
			precio = 0,
			importeUpdate = 0,
			totalImports = 0,
			sacarImpuesto = 0,
			porcentajeImp,
			precioDolar = 0,
			descuento = 0;
		//variable para obtener la filaseleccionada de la tabla factura
		filaseleccionada = menu.tblFactura.getSelectedRow();
		try {
			//modelo va a ser igual al modelo de la tabla factura
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			//obtener el nuemero de filas de la tabla factura
			filas = this.modelo.getRowCount();
			//validar si hay una fila seleccionada
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				//obtengo los valores de la tabla factura para las operaciones
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
				cantidad = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 2).toString());
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 3);
				precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
				id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				confirmar = JOptionPane.showConfirmDialog(null, spiner, "Agregar descuento a " + nombre, JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					descuento = Float.parseFloat(spiner.getValue().toString());
					//realiza el escuento
					precioUpdate = precio - descuento;
					//establecer precio minimo del producto seleccionado
					modelProduct.precioMinimo(id);
					if (precioUpdate < modelProduct.getPrecioMinimo()) {
						JOptionPane.showMessageDialog(
							null,
							"Esta excediendo el precio minimo ´" + modelProduct.getPrecioMinimo()
							+ "´ del producto con el descuento aplicado",
							"Advertencia",
							JOptionPane.WARNING_MESSAGE
						);
					} else {
						//obtengo desde el modelo facturta la moneda de venta de el producto na aplicarle el aumento
						this.factura.monedaVentaProducto(id);
						//validar que moneda de venta tiene el producto a aplicarse el aumento
						if (this.factura.getMonedaVenta().equals("Dolar") && !this.dolarisado) {
							importeUpdate = (precioUpdate * cantidad) * precioDolar;
						} else if (this.factura.getMonedaVenta().equals("Dolar") && this.dolarisado) {
							importeUpdate = precioUpdate * cantidad;
						} else if (this.factura.getMonedaVenta().equals("Córdobas")) {
							importeUpdate = precioUpdate * cantidad;
						}
						//actualizar el importe y el precio
						this.modelo.setValueAt(formato.format(precioUpdate), filaseleccionada, 4);
						this.modelo.setValueAt(formato.format(importeUpdate), filaseleccionada, 5);
						//recorrer la colummna importe para sacar el total de factura
						for (int i = 0; i < filas; i++) {
							totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
						}
						//formatreo de le inpuesto IVA
						sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
						//TOTAL DE FACTURA
						this.total = totalImports;
						//obtengo el IVA en entero "15" o cualquier que sea el impuesto
						porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
						this.isv = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;
						//calcular el subtotal de la factura
						this.subTotal = this.total - this.isv;
						//setear los campos total, subtotal, IVA
						menu.txtSubTotal.setText("" + formato.format(this.subTotal));
						menu.txtImpuesto.setText("" + formato.format(this.isv));
						menu.txtTotal.setText("" + formato.format(this.total));
						menu.txtCodBarraFactura.requestFocus();
						spiner.setValue(0.00);
					}
				}
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + " en Agregar descuento al producto en factura");
		}
	}

	public void addDescuentoDirecto() {
		//variables para el nombre e id de producto
		String nombre = "", id = "";
		//variable para el numero de filas de la tabla factura
		int filaseleccionada = 0, filas = 0, confirmar = 0;
		//variables para las operaciones
		float importeAdesminuir = 0, cantidad = 0, precioUpdate = 0, precio = 0, importeUpdate = 0, totalImports = 0, sacarImpuesto = 0, porcentajeImp, precioDolar = 0, descuento = 0;
		//variable para obtener la filaseleccionada de la tabla factura
		filaseleccionada = menu.tblFactura.getSelectedRow();
		try {
			//modelo va a ser igual al modelo de la tabla factura
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			//obtener el nuemero de filas de la tabla factura
			filas = this.modelo.getRowCount();
			//validar si hay una fila seleccionada
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				//obtengo los valores de la tabla factura para las operaciones
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
				cantidad = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 2).toString());
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 3);
				precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
				importeAdesminuir = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 5).toString());
				id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				this.factura.monedaVentaProducto(id);
				confirmar = JOptionPane.showConfirmDialog(null, spiner, "Agregar descuento a " + nombre, JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					descuento = Float.parseFloat(spiner.getValue().toString());
					//realiza el escuento
					if (this.factura.getMonedaVenta().equals("Dolar")) {
						precioUpdate = ((importeAdesminuir - descuento) / cantidad) / precioDolar;
					} else {
						precioUpdate = (importeAdesminuir - descuento) / cantidad;
					}
					//obtengo desde el modelo facturta la moneda de venta de el producto na aplicarle el aumento
					this.factura.monedaVentaProducto(id);
					//validar que moneda de venta tiene el producto a aplicarse el aumento
					/*if (this.factura.getMonedaVenta().equals("Dolar")) {
                        importeUpdate = (precioUpdate * cantidad) * precioDolar;
                    } else if (this.factura.getMonedaVenta().equals("Córdobas")) {
                        importeUpdate = precioUpdate * cantidad;
                    }*/
					importeUpdate = importeAdesminuir - descuento;
					//actualizar el importe y el precio
					this.modelo.setValueAt(formato.format(precioUpdate), filaseleccionada, 4);
					this.modelo.setValueAt(formato.format(importeUpdate), filaseleccionada, 5);
					//recorrer la colummna importe para sacar el total de factura
					for (int i = 0; i < filas; i++) {
						totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
					}
					//formatreo de le inpuesto IVA
					sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
					//TOTAL DE FACTURA
					this.total = totalImports;
					//obtengo el IVA en entero "15" o cualquier que sea el impuesto
					porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
					this.isv = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;
					//calcular el subtotal de la factura
					this.subTotal = this.total - this.isv;
					//setear los campos total, subtotal, IVA
					menu.txtSubTotal.setText("" + formato.format(this.subTotal));
					menu.txtImpuesto.setText("" + formato.format(this.isv));
					menu.txtTotal.setText("" + formato.format(this.total));
					menu.txtCodBarraFactura.requestFocus();
					spiner.setValue(0.00);
				}
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + " en Agregar descuento al producto en factura");
		}
	}

	public void addDescuentoPorcentaje() {
		//variables para el nombre e id de producto
		String nombre = "", id = "";
		//variable para el numero de filas de la tabla factura
		int filaseleccionada = 0, filas = 0, confirmar = 0;
		//variables para las operaciones
		float importeAdesminuir = 0, cantidad = 0, precioUpdate = 0, precio = 0, importeUpdate = 0, totalImports = 0, sacarImpuesto = 0, porcentajeImp, precioDolar = 0, descuento = 0;
		//variable para obtener la filaseleccionada de la tabla factura
		filaseleccionada = menu.tblFactura.getSelectedRow();
		try {
			//modelo va a ser igual al modelo de la tabla factura
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			//obtener el nuemero de filas de la tabla factura
			filas = this.modelo.getRowCount();
			//validar si hay una fila seleccionada
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				//obtengo los valores de la tabla factura para las operaciones
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
				cantidad = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 2).toString());
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 3);
				precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
				importeAdesminuir = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 5).toString());
				id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				this.factura.monedaVentaProducto(id);
				confirmar = JOptionPane.showConfirmDialog(null, spiner, "Agregar descuento a " + nombre, JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					descuento = Float.parseFloat(spiner.getValue().toString());
					importeUpdate = importeAdesminuir - (importeAdesminuir * descuento / 100);
					//realiza el escuentocesar  
					if (this.factura.getMonedaVenta().equals("Dolar")) {
						precioUpdate = (importeUpdate / cantidad) / precioDolar;
					} else {
						precioUpdate = importeUpdate / cantidad;
					}
					//obtengo desde el modelo facturta la moneda de venta de el producto na aplicarle el aumento
					this.factura.monedaVentaProducto(id);
					//validar que moneda de venta tiene el producto a aplicarse el aumento
					/*if (this.factura.getMonedaVenta().equals("Dolar")) {
                        importeUpdate = (precioUpdate * cantidad) * precioDolar;
                    } else if (this.factura.getMonedaVenta().equals("Córdobas")) {
                        importeUpdate = precioUpdate * cantidad;
                    }*/

					//actualizar el importe y el precio
					this.modelo.setValueAt(formato.format(precioUpdate), filaseleccionada, 4);
					this.modelo.setValueAt(formato.format(importeUpdate), filaseleccionada, 5);
					//recorrer la colummna importe para sacar el total de factura
					for (int i = 0; i < filas; i++) {
						totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
					}
					//formatreo de le inpuesto IVA
					sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
					//TOTAL DE FACTURA
					this.total = totalImports;
					//obtengo el IVA en entero "15" o cualquier que sea el impuesto
					porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
					this.isv = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;
					//calcular el subtotal de la factura
					this.subTotal = this.total - this.isv;
					//setear los campos total, subtotal, IVA
					menu.txtSubTotal.setText("" + formato.format(this.subTotal));
					menu.txtImpuesto.setText("" + formato.format(this.isv));
					menu.txtTotal.setText("" + formato.format(this.total));
					menu.txtCodBarraFactura.requestFocus();
					spiner.setValue(0.00);
				}
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + " en Agregar descuento al producto en factura");
		}
	}

	public void agregarProductoBoton() {
		String codBarra = menu.txtCodBarraFactura.getText();
		String precioDolar = menu.txtPrecioDolarVenta.getText(), id = "";
		int filas = 0;
		float totalImports = 0, sacarImpuesto = 0, porcentajeImp = 0, cantidadUpdate = 0, importeUpdate = 0, cantidadActual = 0, precio = 0;
		if (!menu.isNumeric(precioDolar) || precioDolar.equals("0")) {
			menu.txtPrecioDolarVenta.setText("1");
		} else {
			precioDolar = menu.txtPrecioDolarVenta.getText();
			this.factura.setPrecioDolar(Float.parseFloat(precioDolar));
			this.factura.obtenerPorCodBarra(codBarra);
			if (this.factura.getProducto()[0] != null) {
				this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
				this.modelo.addRow(this.factura.getProducto());
				filas = this.modelo.getRowCount();
				this.factura.Vender(this.factura.getProducto()[0], this.factura.getProducto()[2]);
				for (int i = 0; i < filas; i++) {
					totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
				}
				sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
				//obtengo el IVA en entero "15" o cualquier que sea el impuesto
				porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
				this.total = totalImports;
				this.isv = ((this.total / sacarImpuesto) * porcentajeImp) / 100;
				this.subTotal = this.total - this.isv;
				menu.txtSubTotal.setText("" + formato.format(this.subTotal));
				menu.txtImpuesto.setText("" + formato.format(this.isv));
				menu.txtTotal.setText("" + formato.format(this.total));
				menu.txtCodBarraFactura.setText("");
				DeshabilitarBtnGuardarFactura();
				menu.txtCodBarraFactura.requestFocus();
			} else {
				JOptionPane.showMessageDialog(null, "El producto no esta ingresado...");
				menu.txtCodBarraFactura.setText("");
			}
		}
	}

	public void actualizarFactura() {
		try {
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();//obtengo el modelo de tabla factura y sus datos
			int filas = this.modelo.getRowCount();//Cuento las filas de la tabla Factura
			if (filas == nD.length)//nD quiere decir numero de detalles condicion para guardar solo los cambios de las filas de la factura actual no se pueda agregar mas productos ni quitar solo cambiar ya que solo es edicio de la facura
			{
				Date fecha, fechaVencimiento;
				String factura,
					id,
					cantidad,
					precio,
					totalDetalle,
					idCredito,
					iva,
					totalFactura,
					formaPago,
					idFormaPago,
					nombreComprador,
					anotaciones;//variables para capturar los datos a guardar
				fecha = menu.jcFechaFactura.getDate();//capturo la fecha del dateshooser
				fechaVencimiento = menu.jcFechaVencimientoFactura.getDate();
				long fechaF = fecha.getTime();//
				java.sql.Date fechaFactura = new java.sql.Date(fechaF);//convertir la fecha obtenida a formato sql
				nombreComprador = menu.txtCompradorFactura.getText();
				idCredito = menu.txtCreditoFactura.getText();//obtengo el credito
				iva = menu.txtImpuesto.getText();//obtengo el iva
				totalFactura = menu.txtTotal.getText();//obtengo total de factura
				formaPago = (String) menu.cmbFormaPago.getSelectedItem();//capturo el nombre de forma de pago 
				idFormaPago = this.factura.ObtenerFormaPago(formaPago);//capturo el id de la forma de pago que retorna la funcion obtenerformapago de la clase facturacion
				anotaciones = menu.txtNfacturaMembretada.getText();
				factura = menu.txtNumeroFactura.getText();//capturo id de factura ala que pertenece el detalle de factura
				this.factura.ActualizarFactura(
					1,
					factura,
					fechaFactura,
					nombreComprador,
					idCredito,
					idFormaPago,
					iva,
					totalFactura,
					anotaciones,
					new java.sql.Date(fechaVencimiento.getTime())
				);//envio los datos a actualizar de la factura
				for (int cont = 0; cont < filas; cont++)//for para recorrer la tabla factura
				{
					id = (String) this.modelo.getValueAt(cont, 0);//capturo el id de producto para guardar en detallefactura
					cantidad = (String) this.modelo.getValueAt(cont, 2);//capturo la cantidad de producto de la columna dos y la paso a String para guardar en detallefactura
					precio = (String) this.modelo.getValueAt(cont, 4);//capturo el precio de producto para guardar en detallefactura
					totalDetalle = (String) this.modelo.getValueAt(cont, 5);//capturo el total de detalle compra de producto para guardar en detallefactura
					this.factura.ActualizarDetalle(nD[cont], id, precio, cantidad, totalDetalle);//envio los datos para actualizar los detalles de la factura
					//this.factura.Vender(id, cantidad);//funcion para diminuir el stock segun la cantidad que se venda
				}
				menu.txtNumeroFactura.setText(this.factura.ObtenerIdFactura());//Actualizo el campo numero de factura con la funcion obtenerIdFactura
				LimpiarTablaFactura();//limpio la factura
				menu.txtCreditoFactura.setText("");
				DeshabilitarBtnGuardarFactura();//deshabilito el boton guadar factura
				menu.pnlVentas.setVisible(false);
				menu.pnlReportes.setVisible(true);
				menu.btnActualizarFactura.setVisible(false);
				//
				menu.btnVentas.setVisible(true);
				menu.btnReportes.setVisible(true);
				menu.btnCerrarSesion.setVisible(true);
				menu.btnUsuarios.setVisible(true);
				menu.btnClientes.setVisible(true);
				menu.btnNotificaciones.setVisible(true);
				menu.btnTransacciones.setVisible(true);
				menu.btnInventario.setVisible(true);
				menu.btnInfoFactura.setVisible(true);
				menu.btnAgregar.setEnabled(true);
				menu.txtCodBarraFactura.setEnabled(true);
				//menu.btnGuardarFactura.setEnabled(true);//deshabilitar boton guardarFactura
				menu.btnAgregarProductoFactura.setEnabled(true);//deshabilitar boton AgregarProducto a factura
				menu.btnNuevaFactura.setEnabled(true);//deshabilitar boton Nueva Factura
				menu.btnEliminarFilaFactura.setEnabled(true);//deshabilitar boton EliminarFila Factura
				menu.jcFechaFactura.setDate(this.fecha);
				//
				productos.MostrarProductos("");//actualizar la tabla de productos inventario
				productos.MostrarProductosVender("");//actualizar la tabla de productos a vender
				creditos.ActualizarEstadoCreditoAabierto();//Actualizar Credito 
				creditos.ActualizarEstadoCreditoApendiente();//Actualizar Credito
				reportes.reportesDiarios(this.fecha);
				reportes.MostrarReportesDario(this.fecha);//actualizar reportes
				reportes.ReporteGlobal();
				reportes.SumaTotalFiltroReporte(this.fecha, this.fecha);//actualizar datos de reportes
				reportes.inversion();//actualizar Dato de Inversion
				creditos.MostrarCreditos("");//Actualizar creditos
				creditos.MostrarCreditosAddFactura("");//actualizar los creditos en factura
			} else {
				JOptionPane.showMessageDialog(null, "La factura depende de " + nD.length + " filas");
			}

		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}
	}

	public void editarFactura() {
		int filaF = menu.tblFactura.getRowCount();
		String cP, idProd;
		for (int i = 0; i < filaF; i++) {
			idProd = menu.tblFactura.getValueAt(i, 0).toString();
			cP = menu.tblFactura.getValueAt(i, 2).toString();
			modelProduct.AgregarProductoStock(idProd, cP);
		}
		LimpiarTablaFactura();
		String nombre = "", apellido = "";
		//obtengo la fila seleccionda de la tabla reporte diario    obtengo el numero las filas de la tabla detalleFactura
		int filaseleccionada = menu.tblReporte.getSelectedRow(), filas = menu.tblMostrarDetalleFactura.getRowCount();
		//la variable modelo va a tomar el modelo de la tabla factura
		this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
		//variables para obtener los valores que se ocupan para la actualizacion
		String idFactura = "",
			idP = "",
			codBarra = "",
			nombreP = "",
			precioP = "",
			cantidadP = "",
			importe = "",
			pago = "",
			detalle = "",
			comprador = "",
			fecha = "",
			credito = "",
			anotaciones = "",
			fechaVencimiento = "";
		//convertir el formato sql a Date con simpleDateFormat
		SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
		//nD quiere decir numero de detalles es la variable que guarda el numero de detalles que van en la factura a editar
		this.nD = new String[filas];
		//variables float para hacer la operaciones 
		float totalFactura, iva, subTotal;
		try {
			//idFactura obtiene el id de factura de la tabla reporte diario
			idFactura = menu.tblReporte.getValueAt(filaseleccionada, 0).toString();
			//obtengo la fecha de la factura
			fecha = menu.tblReporte.getValueAt(filaseleccionada, 1).toString();
			fechaVencimiento = (String) menu.tblReporte.getValueAt(filaseleccionada, 10);
			//obtengo el nombre comprador
			comprador = menu.tblReporte.getValueAt(filaseleccionada, 4).toString();//antes 4
			//obtengo el impuesto
			iva = Float.parseFloat(menu.tblReporte.getValueAt(filaseleccionada, 2).toString());
			//obtengo el total de factura
			totalFactura = Float.parseFloat(menu.tblReporte.getValueAt(filaseleccionada, 3).toString());//antes 3
			//obtengo el credito
			credito = (String) menu.tblReporte.getValueAt(filaseleccionada, 6);//antes 6
			nombre = r.nombreCliente(credito);//obtengo el nombre de credito
			apellido = r.apellidoCliente(credito);//obtengo el apellido de credito
			//obtengo la forma de pago
			pago = menu.tblReporte.getValueAt(filaseleccionada, 5).toString();//antes 5
			anotaciones = (String) menu.tblReporte.getValueAt(filaseleccionada, 8);

			//realiza el calculo para obtener el subtotal
			subTotal = totalFactura - iva;
			//validacion de lo que estoy obteniendo en la variable credito
			if (credito == null) {
				credito = "";
			}
			//lleno los campos del formulario factura
			menu.txtCreditoFactura.setText(credito);
			menu.jcFechaFactura.setDate(spf.parse(fecha));
			if (fechaVencimiento == null || fechaVencimiento.equals("")) {
				menu.jcFechaVencimientoFactura.setDate(new Date());
			} else {
				menu.jcFechaVencimientoFactura.setDate(spf.parse(fechaVencimiento));
			}
			menu.txtSubTotal.setText("" + subTotal);
			menu.txtTotal.setText("" + totalFactura);
			menu.txtImpuesto.setText("" + iva);
			menu.txtNumeroFactura.setText(idFactura);
			menu.txtCompradorFactura.setText(comprador);
			menu.txtNClienteFactura.setText(nombre);
			menu.txtAClienteFactura.setText(apellido);
			menu.cmbFormaPago.setSelectedItem(pago);
			menu.txtNfacturaMembretada.setText(anotaciones);
			//for para recorrer la tabla detalleFactura
			for (int i = 0; i < filas; i++) {
				detalle = menu.tblMostrarDetalleFactura.getValueAt(i, 0).toString();//obtengo numero de detalle
				idP = menu.tblMostrarDetalleFactura.getValueAt(i, 1).toString();//obtengo id de producto
				codBarra = menu.tblMostrarDetalleFactura.getValueAt(i, 2).toString();//obtengo cod barra del producto
				nombreP = menu.tblMostrarDetalleFactura.getValueAt(i, 3).toString();//obtengo nombre del producto
				cantidadP = menu.tblMostrarDetalleFactura.getValueAt(i, 4).toString();//obtengo cantidad de producto vendido en la factura
				precioP = menu.tblMostrarDetalleFactura.getValueAt(i, 5).toString();//obtengo precio del producto
				importe = menu.tblMostrarDetalleFactura.getValueAt(i, 6).toString();//obtengo total de venta del producto

				nD[i] = detalle;//lleno el array los id correspondiente a cada detalles
				String[] addFila = {idP, codBarra, cantidadP, nombreP, precioP, importe};//creo el arreglo con los datos obtenidos de la tabla detalle
				this.modelo.addRow(addFila);//agrego la fila de array creado anteriormente a la tabla factura para la edicion
			}
			//System.out.println(nD[0]+" "+nD[1]);  
			menu.pnlVentas.setVisible(true);//mostrar panel de ventas 
			menu.btnActualizarFactura.setVisible(true);//mostrar boton actualizar
			menu.btnGuardarFactura.setEnabled(false);//deshabilitar boton guardarFactura
			menu.btnCobrarSinImprimir.setEnabled(false);
			menu.btnAgregarProductoFactura.setEnabled(false);//deshabilitar boton AgregarProducto a factura
			menu.btnNuevaFactura.setEnabled(false);//deshabilitar boton Nueva Factura
			menu.btnEliminarFilaFactura.setEnabled(false);//deshabilitar boton EliminarFila Factura
			menu.pnlReportes.setVisible(false);//ocultar panel Reportes
			menu.btnInfoFactura.setVisible(false);
			menu.vistaDetalleFacturas.setVisible(false);//ocultar la ventana de detalle de factura de reportes
			menu.btnVentas.setVisible(false);
			menu.btnReportes.setVisible(false);
			menu.btnCerrarSesion.setVisible(false);
			menu.btnUsuarios.setVisible(false);
			menu.btnClientes.setVisible(false);
			menu.btnAgregar.setEnabled(false);
			menu.txtCodBarraFactura.setEnabled(false);
			menu.btnNotificaciones.setVisible(false);
			menu.btnTransacciones.setVisible(false);
			menu.btnInventario.setVisible(false);
		} catch (Exception err) {
			err.printStackTrace();
			JOptionPane.showMessageDialog(null, err + "guardar facturas");
		}
	}

	public void eliminarFilaFactura() {
		int filaseleccionada = menu.tblFactura.getSelectedRow();
		float importe, totalActual, sacarImpuesto, porcentajeImp;
		String cantidad, id;
		try {
			if (filaseleccionada == -1) {

			} else {
				this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
				id = (String) modelo.getValueAt(filaseleccionada, 0);
				cantidad = (String) modelo.getValueAt(filaseleccionada, 2);
				importe = Float.parseFloat(modelo.getValueAt(filaseleccionada, 5).toString());
				totalActual = Float.parseFloat(menu.txtTotal.getText());
				//formatreo de le inpuesto IVA
				sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
				//obtengo el IVA en entero "15" o cualquier que sea el impuesto
				porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
				this.total = totalActual - importe;
				this.isv = ((this.total / sacarImpuesto) * porcentajeImp) / 100;
				this.subTotal = this.total - isv;
				menu.txtTotal.setText("" + formato.format(this.total));
				menu.txtSubTotal.setText("" + formato.format(this.subTotal));
				menu.txtImpuesto.setText("" + formato.format(this.isv));
				modelProduct.AgregarProductoStock(id, cantidad);
				productos.MostrarProductosVender("");
				this.modelo.removeRow(filaseleccionada);
				DeshabilitarBtnGuardarFactura();
				menu.txtCodBarraFactura.requestFocus();
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}
	}

	public void nuevaFactura() {
		DeshabilitarBtnGuardarFactura();
		try {
			String id, cantidad;
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			int filas = this.modelo.getRowCount();
			for (int i = 0; i < filas; i++) {
				id = (String) this.modelo.getValueAt(i, 0);
				cantidad = (String) this.modelo.getValueAt(i, 2);
				modelProduct.AgregarProductoStock(id, cantidad);
			}
			LimpiarTablaFactura();
			DeshabilitarBtnGuardarFactura();
			menu.txtCodBarraFactura.requestFocus();
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err);
		}
	}

	public void actualizarIVA() {
		int confirmar = JOptionPane.showConfirmDialog(null, spiner, "Valor de Impuesto IVA:", JOptionPane.OK_CANCEL_OPTION);
		if (confirmar == JOptionPane.YES_OPTION) {
			menu.lblImpuestoISV.setText(spiner.getValue().toString());
			spiner.setValue(0.00);
		}
	}

	public void limpiarFormularioClienteFactura() {
		menu.txtNClienteFactura.setText("");
		menu.txtAClienteFactura.setText("");
		menu.lblIdClienteFactura.setText("");
		menu.txtCreditoFactura.setText("");
		menu.cmbFormaPago.setSelectedItem("Efectivo");
	}

	public void validarPermiso() {
		if (this.permiso == 1) {
//            menu.addDescuentoDirecto.setEnabled(false);
//            menu.addDescuentoPorcentaje.setEnabled(false);
		} else if (this.permiso == 2) {
			menu.addDescuentoDirecto.setEnabled(false);
			menu.addDescuentoPorcentaje.setEnabled(false);
		}
	}

	public void updateNumberFactura(String number) {
		this.menu.txtNumeroFactura.setText(number);
	}

	public void subirPrecio() {
		//variables para el nombre e id de producto
		String nombre = "", id = "";
		//variable para el numero de filas de la tabla factura
		int filaseleccionada = 0, filas = 0, confirmar = 0;
		//variables para las operaciones
		float cantidad = 0,
			precioUpdate = 0,
			precio = 0,
			importeUpdate = 0,
			totalImports = 0,
			sacarImpuesto = 0,
			porcentajeImp,
			precioDolar = 0,
			aumento = 0;
		//variable para obtener la filaseleccionada de la tabla factura
		filaseleccionada = menu.tblFactura.getSelectedRow();
		try {
			//modelo va a ser igual al modelo de la tabla factura
			this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
			//obtener el nuemero de filas de la tabla factura
			filas = this.modelo.getRowCount();
			//validar si hay una fila seleccionada
			if (filaseleccionada == -1) {
				JOptionPane.showMessageDialog(null, "Seleccione un producto.", "Advertencia", JOptionPane.WARNING_MESSAGE);
			} else {
				//obtengo los valores de la tabla factura para las operaciones
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
				cantidad = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 2).toString());
				nombre = (String) this.modelo.getValueAt(filaseleccionada, 3);
				precio = Float.parseFloat(this.modelo.getValueAt(filaseleccionada, 4).toString());
				id = (String) this.modelo.getValueAt(filaseleccionada, 0);
				confirmar = JOptionPane.showConfirmDialog(null, spiner, "Agregar descuento a " + nombre, JOptionPane.OK_CANCEL_OPTION);
				if (confirmar == JOptionPane.YES_OPTION) {
					aumento = Float.parseFloat(spiner.getValue().toString());
					//realiza el escuento
					precioUpdate = precio + aumento;
					//establecer precio minimo del producto seleccionado
					modelProduct.precioMinimo(id);
					if (precioUpdate < modelProduct.getPrecioMinimo()) {
						JOptionPane.showMessageDialog(
							null,
							"Esta excediendo el precio minimo ´" + modelProduct.getPrecioMinimo()
							+ "´ del producto con el descuento aplicado",
							"Advertencia",
							JOptionPane.WARNING_MESSAGE
						);
					} else {
						//obtengo desde el modelo facturta la moneda de venta de el producto na aplicarle el aumento
						this.factura.monedaVentaProducto(id);
						//validar que moneda de venta tiene el producto a aplicarse el aumento
						if (this.factura.getMonedaVenta().equals("Dolar")) {
							importeUpdate = (precioUpdate * cantidad) * precioDolar;
						} else if (this.factura.getMonedaVenta().equals("Córdobas")) {
							importeUpdate = precioUpdate * cantidad;
						}
						//actualizar el importe y el precio
						this.modelo.setValueAt(formato.format(precioUpdate), filaseleccionada, 4);
						this.modelo.setValueAt(formato.format(importeUpdate), filaseleccionada, 5);
						//recorrer la colummna importe para sacar el total de factura
						for (int i = 0; i < filas; i++) {
							totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());
						}
						//formatreo de le inpuesto IVA
						sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText());//concatenacion para sacar el valor 1.xx para sacar el iva
						//TOTAL DE FACTURA
						this.total = totalImports;
						//obtengo el IVA en entero "15" o cualquier que sea el impuesto
						porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"
						this.isv = (float) ((this.total / sacarImpuesto) * porcentajeImp) / 100;
						//calcular el subtotal de la factura
						this.subTotal = this.total - this.isv;
						//setear los campos total, subtotal, IVA
						menu.txtSubTotal.setText("" + formato.format(this.subTotal));
						menu.txtImpuesto.setText("" + formato.format(this.isv));
						menu.txtTotal.setText("" + formato.format(this.total));
						menu.txtCodBarraFactura.requestFocus();
						spiner.setValue(0.00);
					}
				}
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + " en Agregar descuento al producto en factura");
		}
	}

	public void addProductoFacturaDolarizado() {
		int filaseleccionada = menu.tblAddProductoFactura.getSelectedRow();
		try {
			String id, codigo, nombre, total, importe, stockA, monedaVenta = "", cantidadString = "";
			float imp = 0, calcula, precio, impuesto, descProduct = 0, stock, cantidadPVender, precioDolarizado = 0, setPrecio = 0,
				cantidad,
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
				sacarImpuesto = Float.parseFloat(1 + "." + menu.lblImpuestoISV.getText()),
				porcentajeImp = Float.parseFloat(menu.lblImpuestoISV.getText());// "descProduct = aumento de producto"

			if (filaseleccionada != -1) {
				this.modelo = (DefaultTableModel) menu.tblAddProductoFactura.getModel();
				id = modelo.getValueAt(filaseleccionada, 0).toString();
				codigo = modelo.getValueAt(filaseleccionada, 1).toString();
				nombre = modelo.getValueAt(filaseleccionada, 2).toString();
				precio = Float.parseFloat(modelo.getValueAt(filaseleccionada, 3).toString());

				monedaVenta = (String) modelo.getValueAt(filaseleccionada, 4);
				stock = Float.parseFloat(modelo.getValueAt(filaseleccionada, 6).toString());
				cantidadString = JOptionPane.showInputDialog(null, "Cantidad:");
				cantidad = (cantidadString.equals("") || cantidadString == null) ? 0 : Float.parseFloat(cantidadString);
				//convertir a flota la variable cantidad 1006195
				//validacion para la venta sugun lo que hay en stock osea no se pueda vender mas de lo que hay en stock
				if (cantidad > 0 && cantidad <= stock) {
					if (monedaVenta.equals("Córdobas")) {
						precioDolarizado = precio / precioDolar;
						precioDolarizado = Float.parseFloat(this.formato.format(precioDolarizado));
						imp = (precioDolarizado * cantidad);//importe total de compra de producto
						setPrecio = precioDolarizado;
					} else if (monedaVenta.equals("Dolar")) {
						imp = precio * cantidad;//importe total de compra de producto
						setPrecio = precio;
					}
					importe = formato.format(imp);
					//realizando los calculos de importe
					this.modelo = (DefaultTableModel) menu.tblFactura.getModel();
					//pasar producto de tabla productos a tabla de factura
					String[] FilaElementos = {
						id,
						codigo,
						this.formato.format(cantidad),
						nombre,
						this.formato.format(setPrecio),
						importe
					};
					this.modelo.addRow(FilaElementos);
					this.factura.Vender(id, String.valueOf(cantidad));//llamar procedimiento sql para vender
//                                    calcula = (Float.parseFloat(importe));//convertir importe a float
					int filas = this.modelo.getRowCount();
					float totalImports = 0;
					for (int i = 0; i < filas; i++) {
						totalImports += Float.parseFloat(this.modelo.getValueAt(i, 5).toString());//calcular el total de factura
					}
					this.total = totalImports;
					impuesto = ((this.total / sacarImpuesto) * porcentajeImp) / 100;//calcular el impuesto
					this.isv = impuesto;//impuesto
					this.subTotal = this.total - this.isv;//clacular subtotal de factura

					menu.txtImpuesto.setText("" + formato.format(this.isv));//establecer el valor impuesto en el campo impuesto de factura
					menu.txtSubTotal.setText("" + formato.format(this.subTotal));//establecer el valor impuesto en el campo sub total de factura
					menu.txtTotal.setText("" + formato.format(this.total));//establecer el valor impuesto en el campo Total de factura
					productos.MostrarProductosVender("");
					menu.txtBuscarPorNombre.selectAll();
					DeshabilitarBtnGuardarFactura();
				} else {
					JOptionPane.showMessageDialog(
						null,
						"No hay suficiente producto en stock para realizar esta venta",
						"Advertencia",
						JOptionPane.WARNING_MESSAGE
					);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	/* 
		funcion para no repetir los productos en la tabla factura 
		-- en proceso
	 */
	public boolean actualizarProductoTableFactura(String id, float cant, float precioDolar, String moneda) {
		boolean isAdd = true;
		int filas = this.menu.tblFactura.getRowCount();
		String producto;
		float precio, cantidad, importe = 0;
		this.modelo = (DefaultTableModel) this.menu.tblFactura.getModel();
		if (filas > 0) {
			for (int i = 0; i < filas; i++) {
				producto = (String) this.modelo.getValueAt(i, 0);
				if (producto.equals(id)) {
					precio = Float.parseFloat(this.modelo.getValueAt(i, 4).toString());
					cantidad = Float.parseFloat(this.modelo.getValueAt(i, 2).toString());
					if (moneda.equals("Dolar")) {
						importe = (cantidad + cant) * precio;
						importe = importe * precioDolar;
					} else {
						importe = (cantidad + cant) * precio;
					}
					this.modelo.setValueAt(this.formato.format(importe), i, 5);
					isAdd = true;
				} else {
					isAdd = false;
				}
			}
		} else {
			isAdd = false;
		}
		return isAdd;
	}

	public void sumarTotalFactura() {

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == this.menu.cmbPrecioCompraVentaAdmin) {
			if (this.menu.cmbPrecioCompraVentaAdmin.getSelectedItem().toString().equals("Factura Dolarizada")) {
				this.dolarisado = true;
			} else {
				this.dolarisado = false;
			}

		}
	}

}
