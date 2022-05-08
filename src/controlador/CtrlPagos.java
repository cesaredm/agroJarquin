/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import com.github.anastaciocintra.escpos.EscPos;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import modelo.*;
import vista.IMenu;

/**
 *
 * @author CESAR DIAZ MARADIAGA
 */
public class CtrlPagos extends CtrlImprimir implements ActionListener, CaretListener, MouseListener {

	IMenu menu;
	PagosCreditos pagos;
	CtrlReportes ctrlR;
	CtrlCreditos ctrlC;
	Creditos creditos;
	Reportes reportes;
	PrintReportes print;
	InfoFactura info;
	DefaultTableModel modelo;
	String id;
	Date fecha;
	DecimalFormat formato;
	CmbFormaPago formaPagoModel;
	float precioDolar;

	public CtrlPagos(IMenu menu, PagosCreditos pagos) {
		this.menu = menu;
		this.pagos = pagos;
		this.formato = new DecimalFormat("############0.00");
		this.reportes = new Reportes();
		this.creditos = new Creditos();
		this.ctrlR = new CtrlReportes(menu, reportes);
		this.ctrlC = new CtrlCreditos(menu, creditos);
		this.modelo = new DefaultTableModel();
		this.print = new PrintReportes();
		this.info = new InfoFactura();
		this.fecha = new Date();
		this.menu.cmbFormaPagoCredito.setModel(pagos.FormasPago());
		this.menu.btnGuardarPago.addActionListener(this);
		this.menu.btnActualizarPago.addActionListener(this);
		this.menu.btnNuevoPago.addActionListener(this);
		this.menu.EditarPago.addActionListener(this);
		this.menu.BorrarPago.addActionListener(this);
		this.menu.txtBuscarPago.addCaretListener(this);
		this.menu.btnMostrarPagosRegistrados.addActionListener(this);
		this.menu.tblPagos.addMouseListener(this);
		this.showFormasPago();
		MostrarPagos("");
		DeshabilitarPagos();
		UltimoPago();
		this.menu.jcFechaPago.setDate(fecha);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		if (e.getSource() == menu.btnGuardarPago) {
			guardarPago();
		}
		if (e.getSource() == menu.btnActualizarPago) {
			int c, factura = (int) menu.jsFacturaPagoU.getValue();
			float montoPago,
				precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText()),
				utilidadPago;
				this.formaPagoModel = (CmbFormaPago) menu.cmbFormaPagoCredito.getSelectedItem();
			String credito = menu.txtCreditoPago.getText(),
				monto = menu.txtMontoPago.getText(),
				anotaciones = menu.txtAreaAnotacionPagos.getText();
			Date f = menu.jcFechaPago.getDate();
			int idFormaPago = this.formaPagoModel.getId();
			long fecha = f.getTime();
			java.sql.Date fechaPago = new java.sql.Date(fecha);
			if (!credito.equals("") && !monto.equals("")) {
				if (isNumeric(credito) && isNumeric(monto)) {
					c = Integer.parseInt(credito);
					montoPago = Float.parseFloat(monto);
					utilidadPago = this.pagos.utilidadXfactura(factura, precioDolar, montoPago);
					pagos.Actualizar(this.id, c, montoPago, fechaPago, idFormaPago, anotaciones, utilidadPago);
					MostrarPagos("");
					ctrlC.MostrarCreditos("");
					ctrlC.ActualizarEstadoCreditoApendiente();
					ctrlC.ActualizarEstadoCreditoAabierto();
					LimpiarPago();
					ctrlR.reportesDiarios(this.fecha);
					ctrlR.MostrarReportesDario(this.fecha);
					ctrlR.ReporteGlobal();
					ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
					ctrlC.MostrarCreditosCreados("");
					menu.btnGuardarPago.setEnabled(true);
					menu.btnActualizarPago.setEnabled(false);
				}
			} else {

			}
		}
		if (e.getSource() == menu.btnNuevoPago) {
			HabilitarPago();
			LimpiarPago();
			menu.txtMontoPago.requestFocus();
		}
		if (e.getSource() == menu.EditarPago) {
			int filaseleccionada = menu.tblPagos.getSelectedRow();
			String monto, credito, formaPago, anotaciones;
			Date fecha;
			int id;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if (filaseleccionada == -1) {

				} else {
					this.modelo = (DefaultTableModel) menu.tblPagos.getModel();
					id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
					this.pagos.editar(id);
					this.id = String.valueOf(id);

					HabilitarPago();
					menu.txtMontoPago.setText(""+this.pagos.getMonto());
					menu.txtCreditoPago.setText(""+this.pagos.getCredito());
					menu.jcFechaPago.setDate(this.pagos.getFecha());
					menu.cmbFormaPagoCredito.setSelectedItem(new CmbFormaPago(this.pagos.getFormaPago(), ""));
					menu.txtAreaAnotacionPagos.setText(""+this.pagos.getAnotacion());
					menu.cmbMonedaPago.setSelectedItem(this.pagos.getMoneda());
					menu.btnGuardarPago.setEnabled(false);
					menu.btnActualizarPago.setEnabled(true);
				}
			} catch (Exception err) {
				JOptionPane.showMessageDialog(null, err);
			}
		}
		if (e.getSource() == menu.BorrarPago) {
			this.eliminar();
		}
		if (e.getSource() == menu.btnMostrarPagosRegistrados) {
			menu.pagosAcreditos.setSize(860, 550);
			menu.pagosAcreditos.setVisible(true);
			menu.pagosAcreditos.setLocationRelativeTo(null);
		}
	}

	public void eliminar() {
		int filaseleccionada = menu.tblPagos.getSelectedRow(), id = 0;
		try {
			if (filaseleccionada == -1) {

			} else {
				this.modelo = (DefaultTableModel) menu.tblPagos.getModel();
				id = Integer.parseInt(this.modelo.getValueAt(filaseleccionada, 0).toString());
				this.pagos.Eliminar(id);
				MostrarPagos("");
				this.ctrlC.MostrarCreditos("");
				ctrlR.reportesDiarios(this.fecha);
				ctrlR.MostrarReportesDario(this.fecha);
				ctrlR.ReporteGlobal();
				this.ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
				ctrlC.ActualizarEstadoCreditoApendiente();
				ctrlC.ActualizarEstadoCreditoAabierto();
			}
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, err + " en la funcion Borrar Pago", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void MostrarPagos(String buscar) {
		menu.jcFechaPago.setDate(this.fecha);
		menu.tblPagos.getTableHeader().setFont(new Font("Sugoe UI", Font.PLAIN, 14));
		menu.tblPagos.getTableHeader().setOpaque(false);
		menu.tblPagos.getTableHeader().setBackground(new Color(69, 76, 89));
		menu.tblPagos.getTableHeader().setForeground(new Color(255, 255, 255));
		menu.tblPagos.setModel(this.pagos.Mostrar(buscar));
	}

	public boolean isNumeric(String cadena) {//metodo para la validacion de campos numericos
		try {
			Float.parseFloat(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	//metodo para limpiar el formulario Pagos
	public void LimpiarPago() {
		menu.txtMontoPago.setText("");
		menu.cmbFormaPagoCredito.setSelectedItem("Efectivo");
		menu.txtAreaAnotacionPagos.setText("");
		menu.jsFacturaPagoU.setValue(0);
	}

	public void HabilitarPago() {
		menu.txtAreaAnotacionPagos.setEnabled(true);
		menu.jsFacturaPagoU.setEnabled(true);
		menu.btnActualizarPago.setEnabled(false);
		menu.btnGuardarPago.setEnabled(true);
	}
	//funcion para deshabilitar componentes de el formulario de pago

	public void showFormasPago(){
		this.pagos.getFormasPago();
		this.menu.cmbFormaPagoCredito.setModel(this.pagos.combo);
	}
	
	public void DeshabilitarPagos() {
		menu.btnActualizarPago.setEnabled(false);
		menu.btnGuardarPago.setEnabled(false);
		menu.txtAreaAnotacionPagos.setEnabled(false);
		menu.jsFacturaPagoU.setEnabled(false);
	}//lbls para hacer visible el pago en la ventana pago

	//mostrar el id del pago actual
	public void UltimoPago() {
		this.menu.lblNumeroPago.setText("" + creditos.obtenerUltimoPago());
	}

	public void guardarPago() {
		this.precioDolar = Float.parseFloat(menu.txtPrecioDolarVenta.getText());
		int c, factura = (int) menu.jsFacturaPagoU.getValue();
		float montoPago,
			utilidadPago = 0,
			saldo = 0,
			saldoActual = 0;
		String fechaString = "",
			credito = menu.txtCreditoPago.getText(),
			monto = menu.txtMontoPago.getText(),
			anotaciones = menu.txtAreaAnotacionPagos.getText(),
			moneda = this.menu.cmbMonedaPago.getSelectedItem().toString();

			this.formaPagoModel = (CmbFormaPago) menu.cmbFormaPagoCredito.getSelectedItem();
		int idFormaPago = this.formaPagoModel.getId();
		Date f = menu.jcFechaPago.getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
		long fecha = f.getTime();
		java.sql.Date fechaPago = new java.sql.Date(fecha);
		fechaString = sdf.format(fecha);
		if (isNumeric(credito) && isNumeric(monto) && !anotaciones.equals("") && factura > 0) {
			try {
				c = Integer.parseInt(credito);
				montoPago = Float.parseFloat(monto);
				saldo = pagos.deuda(credito) - pagos.PagosSegunCredito(credito);
				utilidadPago = this.pagos.utilidadXfactura(factura, this.precioDolar, montoPago);
				/* VALIDAR QUE EL MONTO INGRESADO NO EXCEDA EL SALDO */
				if (this.isExcedeSaldo(saldo, montoPago, credito)) {
					pagos.Guardar(c, montoPago, fechaPago, idFormaPago, anotaciones, utilidadPago, moneda);
					saldoActual = pagos.deuda(credito) - pagos.PagosSegunCredito(credito);
					UltimoPago();
					info.obtenerInfoFactura();
					imprimir(
						info.getNombre(),
						menu.lblNumeroPago.getText(),
						fechaString,
						this.pagos.cliente(credito),
						credito,
						this.formato.format(saldo),
						monto,
						this.formato.format(saldoActual)
					);
					MostrarPagos("");
					LimpiarPago();
					ctrlC.MostrarCreditos("");
					ctrlC.ActualizarEstadoCreditoApendiente();
					ctrlC.ActualizarEstadoCreditoAabierto();
					ctrlC.MostrarCreditos("");
					ctrlR.reportesDiarios(this.fecha);
					ctrlR.MostrarReportesDario(this.fecha);
					ctrlR.ReporteGlobal();
					ctrlR.SumaTotalFiltroReporte(this.fecha, this.fecha);
					MostrarPagos("");
					ctrlC.MostrarCreditosCreados("");
					ctrlC.MostrarCreditosAddFactura("");
					menu.btnGuardarPago.setEnabled(true);
					menu.btnActualizarPago.setEnabled(false);
				} else {
					JOptionPane.showMessageDialog(null, "Oops.. el monto ingresado excede al saldo actual.!");
				}

			} catch (Exception e) {

			}
		}
	}

	/* VALIDAR SI EL PAGO ESTA EXCEDIENDO EL SALDO DEL CREDITO */
	public boolean isExcedeSaldo(float saldo, float monto, String credito) {
		if (monto <= saldo) {
			return true;
		} else {
			return false;
		}
	}

	//IMPRIMIR TICKET COMPOBANTE DE PAGO
	public void imprimir(String tienda, String idPago, String fecha, String cliente, String credito, String totalCredito, String monto, String saldo) {
		try {

			reiniciar();
			escpos.write(imageWrapper, escposImage).feed(1);

			escpos.writeLF(boldCenter, tienda)
				.feed(1)
				.writeLF(boldCenter, "COMPROBANTE DE PAGO")
				.feed(2)
				.writeLF("N° crédito:" + credito)
				.writeLF("N° pago:" + idPago)
				.writeLF("Cliente:" + cliente)
				.writeLF("Fecha:" + fecha)
				.writeLF("Total crédito:" + totalCredito)
				.writeLF("Monto de abono:" + monto)
				.write("Saldo:").writeLF(bold, saldo)
				.feed(4)
				.writeLF(centrar, "_____________________________________")
				.writeLF(centrar, "Firma vendedor")
				.feed(8)
				.writeLF(centrar, "_____________________________________")
				.writeLF(centrar, "Firma cliente")
				.feed(3)
				.cut(EscPos.CutMode.FULL);

			escpos.close();

		} catch (Exception e) {

		}
	}

	;
    
    
    @Override
	public void caretUpdate(CaretEvent e) {
		if (e.getSource() == menu.txtBuscarPago) {
			String valor = menu.txtBuscarPago.getText();
			MostrarPagos(valor);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == menu.tblPagos) {
			int filaseleccionada = menu.tblPagos.getSelectedRow();
			String anotacion;
			if (e.getClickCount() == 2) {
				try {
					if (filaseleccionada != -1) {
						anotacion = (String) menu.tblPagos.getValueAt(filaseleccionada, 7);
						this.menu.txtAreaAnotacionPagoJD.setText(anotacion);
						this.menu.jdAnotacionPagos.setLocationRelativeTo(null);
						this.menu.jdAnotacionPagos.setSize(555, 284);
						this.menu.jdAnotacionPagos.setVisible(true);
					}
				} catch (Exception err) {
					JOptionPane.showMessageDialog(null, err + " en la funcion mouseClicked en el ctrl pagos");
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
